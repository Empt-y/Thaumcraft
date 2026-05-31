package thaumcraft.common.golems;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.EquipmentSlot;
import java.nio.ByteBuffer;
import java.util.Iterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
// removed: import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
// FML FMLCommonHandler removed
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.golems.ai.AIFollowOwner;
import thaumcraft.common.golems.ai.AIGotoBlock;
import thaumcraft.common.golems.ai.AIGotoEntity;
import thaumcraft.common.golems.ai.AIGotoHome;
import thaumcraft.common.golems.ai.AIOwnerHurtByTarget;
import thaumcraft.common.golems.ai.AIOwnerHurtTarget;
import thaumcraft.common.golems.ai.PathNavigateGolemAir;
import thaumcraft.common.golems.ai.PathNavigateGolemGround;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.Utils;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import io.netty.buffer.ByteBuf;


public class EntityThaumcraftGolem extends EntityOwnedConstruct implements IGolemAPI, RangedAttackMob
{
    int rankXp;
    private static EntityDataAccessor<Integer> PROPS1;
    private static EntityDataAccessor<Integer> PROPS2;
    private static EntityDataAccessor<Integer> PROPS3;
    private static EntityDataAccessor<Byte> CLIMBING;
    public boolean redrawParts;
    private boolean firstRun;
    protected Task task;
    protected int taskID;
    public static int XPM = 1000;
    
    public EntityThaumcraftGolem(net.minecraft.world.entity.EntityType<? extends EntityThaumcraftGolem> type, Level worldIn) {
        super(type, worldIn);
        rankXp = 0;
        redrawParts = false;
        firstRun = true;
        task = null;
        taskID = Integer.MAX_VALUE;
        xpReward = 5;
    }

    @Override
    public void performRangedAttack(net.minecraft.world.entity.LivingEntity target, float distanceFactor) {
        // Golem ranged attack - delegated to arm function
    }
    
    protected void initEntityAI() {
        targetSelector.removeAllGoals(g -> true);
        goalSelector.addGoal(2, new AIGotoEntity(this));
        goalSelector.addGoal(3, new AIGotoBlock(this));
        goalSelector.addGoal(4, new AIGotoHome(this));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }
    
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(EntityThaumcraftGolem.PROPS1, 0);
        builder.define(EntityThaumcraftGolem.PROPS2, 0);
        builder.define(EntityThaumcraftGolem.PROPS3, 0);
        builder.define((EntityDataAccessor)EntityThaumcraftGolem.CLIMBING, 0);
    }
    
    @Override
    public IGolemProperties getProperties() {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putInt((int) entityData.get((EntityDataAccessor)EntityThaumcraftGolem.PROPS1));
        bb.putInt((int) entityData.get((EntityDataAccessor)EntityThaumcraftGolem.PROPS2));
        return GolemProperties.fromLong(bb.getLong(0));
    }
    
    @Override
    public void setProperties(IGolemProperties prop) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(prop.toLong());
        bb.rewind();
        entityData.set(EntityThaumcraftGolem.PROPS1, bb.getInt());
        entityData.set(EntityThaumcraftGolem.PROPS2, bb.getInt());
    }
    
    @Override
    public float maxUpStep() {
        return getProperties().hasTrait(thaumcraft.api.golems.EnumGolemTrait.WHEELED) ? 0.5f : 0.6f;
    }

    @Override
    public byte getGolemColor() {
        byte[] ba = Utils.intToByteArray((int) entityData.get((EntityDataAccessor)EntityThaumcraftGolem.PROPS3));
        return ba[0];
    }
    
    public void setGolemColor(byte b) {
        byte[] ba = Utils.intToByteArray((int) entityData.get((EntityDataAccessor)EntityThaumcraftGolem.PROPS3));
        ba[0] = b;
        entityData.set(EntityThaumcraftGolem.PROPS3, Utils.byteArraytoInt(ba));
    }
    
    public byte getFlags() {
        byte[] ba = Utils.intToByteArray((int) entityData.get((EntityDataAccessor)EntityThaumcraftGolem.PROPS3));
        return ba[1];
    }
    
    public void setFlags(byte b) {
        byte[] ba = Utils.intToByteArray((int) entityData.get((EntityDataAccessor)EntityThaumcraftGolem.PROPS3));
        ba[1] = b;
        entityData.set(EntityThaumcraftGolem.PROPS3, Utils.byteArraytoInt(ba));
    }
    
    // getEyeHeight() is final in Entity - cannot override
    // public float getEyeHeight() { return 0.7f; }
    
    // applyEntityAttributes() removed - use createAttributes() static builder
    // @Override protected void applyEntityAttributes() {}
    
    private void updateEntityAttributes() {
        int mh = 10 + getProperties().getMaterial().healthMod;
        if (getProperties().hasTrait(EnumGolemTrait.FRAGILE)) {
            mh *= (int)0.75;
        }
        mh += getProperties().getRank();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(mh);
        // stepHeight is now via maxUpStep() override below
        setHomeTo((getHomePosition() == BlockPos.ZERO) ? blockPosition() : getHomePosition(), getProperties().hasTrait(EnumGolemTrait.SCOUT) ? 48 : 32);
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(getProperties().hasTrait(EnumGolemTrait.SCOUT) ? 56.0 : 40.0);
        /* navigator field removed; navigation is set via constructor */;
        if (getProperties().hasTrait(EnumGolemTrait.FLYER)) {
            // moveHelper is private - override getMoveControl() or use setMoveControl if available
            // moveHelper = new FlyingMoveControl(this);
        }
        if (getProperties().hasTrait(EnumGolemTrait.FIGHTER)) {
            double da = getProperties().getMaterial().damage;
            if (getProperties().hasTrait(EnumGolemTrait.BRUTAL)) {
                da = Math.max(da * 1.5, da + 1.0);
            }
            da += getProperties().getRank() * 0.25;
            getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(da);
        }
        else {
            getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.0);
        }
        createAI();
    }
    
    private void createAI() {
        goalSelector.removeAllGoals(g -> true);
        targetSelector.removeAllGoals(g -> true);
        if (isFollowingOwner()) {
            goalSelector.addGoal(4, new AIFollowOwner(this, 1.0, 10.0f, 2.0f));
        }
        else {
            goalSelector.addGoal(3, new AIGotoEntity(this));
            goalSelector.addGoal(4, new AIGotoBlock(this));
            goalSelector.addGoal(5, new AIGotoHome(this));
        }
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        if (getProperties().hasTrait(EnumGolemTrait.FIGHTER)) {
            if (getNavigation() instanceof GroundPathNavigation) {
                goalSelector.addGoal(0, new FloatGoal(this));
            }
            if (getProperties().hasTrait(EnumGolemTrait.RANGED)) {
                RangedAttackGoal aa = null;
                if (getProperties().getArms().function != null) {
                    Object aaObj = getProperties().getArms().function.getRangedAttackAI(this);
                    if (aaObj instanceof RangedAttackGoal) aa = (RangedAttackGoal) aaObj;
                }
                if (aa != null) {
                    goalSelector.addGoal(1, aa);
                }
            }
            goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, false));
            if (isFollowingOwner()) {
                targetSelector.addGoal(1, new AIOwnerHurtByTarget(this));
                targetSelector.addGoal(2, new AIOwnerHurtTarget(this));
            }
            targetSelector.addGoal(3, new HurtByTargetGoal(this));
        }
    }
    
    public boolean isOnLadder() {
        return isBesideClimbableBlock();
    }
    
    public SpawnGroupData finalizeSpawn(DifficultyInstance diff, SpawnGroupData ld) {
        setHomeTo(blockPosition(), 32);
        updateEntityAttributes();
        return ld;
    }
    
    public int getTotalArmorValue() {
        int armor = getProperties().getMaterial().armor;
        if (getProperties().hasTrait(EnumGolemTrait.ARMORED)) {
            armor = (int)Math.max(armor * 1.5, armor + 1);
        }
        if (getProperties().hasTrait(EnumGolemTrait.FRAGILE)) {
            armor = (int)(armor * 0.75);
        }
        return armor;
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
    }
    
    @Override
    public void tick() {
        super.tick();
        if (getProperties().hasTrait(EnumGolemTrait.FLYER)) {
            setNoGravity(true);
        }
        if (!level().isClientSide()) {
            if (firstRun) {
                firstRun = false;
                if (hasHome() && !blockPosition().equals(getHomePosition())) {
                    goHome();
                }
            }
            if (task != null && task.isSuspended()) {
                task = null;
            }
            if (getTarget() != null && getTarget().isDeadOrDying()) {
                setTarget(null);
            }
            if (getTarget() != null && getProperties().hasTrait(EnumGolemTrait.RANGED) && distanceToSqr(getTarget()) > 1024.0) {
                setTarget(null);
            }
            if (getTarget() != null && getTarget() instanceof Player) {
                setTarget(null);
            }
            if (tickCount % (getProperties().hasTrait(EnumGolemTrait.REPAIR) ? 40 : 100) == 0) {
                heal(1.0f);
            }
            if (getProperties().hasTrait(EnumGolemTrait.CLIMBER)) {
                setBesideClimbableBlock(horizontalCollision);
            }
        }
        else if (tickCount < 20 || tickCount % 20 == 0) {
            redrawParts = true;
        }
        if (getProperties().getHead().function != null) {
            getProperties().getHead().function.onUpdateTick(this);
        }
        if (getProperties().getArms().function != null) {
            getProperties().getArms().function.onUpdateTick(this);
        }
        if (getProperties().getLegs().function != null) {
            getProperties().getLegs().function.onUpdateTick(this);
        }
        if (getProperties().getAddon().function != null) {
            getProperties().getAddon().function.onUpdateTick(this);
        }
    }
    
    public void handleEntityEvent(byte par1) {
        if (par1 == 5) {
            FXDispatcher.INSTANCE.drawGenericParticles(getX(), getY() + getBbHeight() + 0.1, getZ(), 0.0, 0.0, 0.0, 1.0f, 1.0f, 1.0f, 0.5f, false, 704 + (getRandom().nextBoolean() ? 0 : 3), 3, 1, 6, 0, 2.0f, 0.0f, 1);
        }
        else if (par1 == 6) {
            FXDispatcher.INSTANCE.drawGenericParticles(getX(), getY() + getBbHeight() + 0.1, getZ(), 0.0, 0.025, 0.0, 0.1f, 1.0f, 1.0f, 0.5f, false, 15, 1, 1, 10, 0, 2.0f, 0.0f, 1);
        }
        else if (par1 == 7) {
            FXDispatcher.INSTANCE.drawGenericParticles(getX(), getY() + getBbHeight() + 0.1, getZ(), 0.0, 0.05, 0.0, 1.0f, 1.0f, 1.0f, 0.5f, false, 640, 10, 1, 10, 0, 2.0f, 0.0f, 1);
        }
        else if (par1 == 8) {
            FXDispatcher.INSTANCE.drawGenericParticles(getX(), getY() + getBbHeight() + 0.1, getZ(), 0.0, 0.01, 0.0, 1.0f, 1.0f, 0.1f, 0.5f, false, 14, 1, 1, 20, 0, 2.0f, 0.0f, 1);
        }
        else if (par1 == 9) {
            for (int a = 0; a < 5; ++a) {
                FXDispatcher.INSTANCE.drawGenericParticles(getX(), getY() + getBbHeight(), getZ(), getRandom().nextGaussian() * 0.009999999776482582, getRandom().nextFloat() * 0.02, getRandom().nextGaussian() * 0.009999999776482582, 1.0f, 1.0f, 1.0f, 0.5f, false, 13, 1, 1, 20 + getRandom().nextInt(20), 0, 0.3f + getRandom().nextFloat() * 0.4f, 0.0f, 1);
            }
        }
        else {
            super.handleEntityEvent(par1);
        }
    }
    
    public float getGolemMoveSpeed() {
        return 1.0f + getProperties().getRank() * 0.025f + (getProperties().hasTrait(EnumGolemTrait.LIGHT) ? 0.2f : 0.0f) + (getProperties().hasTrait(EnumGolemTrait.HEAVY) ? -0.175f : 0.0f) + (getProperties().hasTrait(EnumGolemTrait.FLYER) ? -0.33f : 0.0f) + (getProperties().hasTrait(EnumGolemTrait.WHEELED) ? 0.25f : 0.0f);
    }
    
    public PathNavigation getGolemNavigator() {
        return getProperties().hasTrait(EnumGolemTrait.FLYER) ? new PathNavigateGolemAir(this, level()) : new PathNavigateGolemGround(this, level());
    }
    
    protected boolean canTriggerWalking() {
        return getProperties().hasTrait(EnumGolemTrait.HEAVY) && !getProperties().hasTrait(EnumGolemTrait.FLYER);
    }
    
    public boolean causeFallDamage(double distance, float damageMultiplier, net.minecraft.world.damagesource.DamageSource source) {
        if (!getProperties().hasTrait(EnumGolemTrait.FLYER) && !getProperties().hasTrait(EnumGolemTrait.CLIMBER)) {
            return super.causeFallDamage(distance, damageMultiplier, damageSources().fall());
        }
        return false;
    }
    
    private void goHome() {
        double d0 = getX();
        double d2 = getY();
        double d3 = getZ();
        setPos(getHomePosition().getX() + 0.5, getY(), getZ());
        setPos(getX(), getHomePosition().getY(), getZ());
        setPos(getX(), getY(), getHomePosition().getZ() + 0.5);
        boolean flag = false;
        BlockPos blockpos = this.blockPosition();
        boolean flag2 = false;
        while (!flag2 && blockpos.getY() < 256) {
            BlockPos blockpos2 = blockpos.above();
            BlockState iblockstate = level().getBlockState(blockpos2);
            if (iblockstate.isSolid()) {
                flag2 = true;
            }
            else {
                blockpos = blockpos2;
            }
        }
        if (flag2) {
            teleportTo(getX(), getY(), getZ());
            if (level().noCollision(this, getBoundingBox())) {
                flag = true;
            }
        }
        if (!flag) {
            teleportTo(d0, d2, d3);
        }
        else if (this instanceof PathfinderMob) {
            getNavigation().stop();
        }
    }
    
    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        setProperties(GolemProperties.fromLong(nbt.getLongOr("props", 0L)));
        setHomeTo(BlockPos.of(nbt.getLongOr("homepos", 0L)), 32);
        setFlags(nbt.getByteOr("gflags", (byte)0));
        rankXp = nbt.getIntOr("rankXP", 0);
        setGolemColor(nbt.getByteOr("color", (byte)0));
        updateEntityAttributes();
    }
    
    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putLong("props", getProperties().toLong());
        nbt.putLong("homepos", getHomePosition().asLong());
        nbt.putByte("gflags", getFlags());
        nbt.putInt("rankXP", rankXp);
        nbt.putByte("color", getGolemColor());
    }
    
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource ds, float damage) {
        if (ds.is(net.minecraft.tags.DamageTypeTags.IS_FIRE) && getProperties().hasTrait(EnumGolemTrait.FIREPROOF)) {
            return false;
        }
        if (ds.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION) && getProperties().hasTrait(EnumGolemTrait.BLASTPROOF)) {
            damage = Math.min(getMaxHealth() / 2.0f, damage * 0.3f);
        }
        if (ds == this.damageSources().cactus()) {
            return false;
        }
        if (hasHome() && (ds == this.damageSources().inWall() || ds == this.damageSources().fellOutOfWorld())) {
            goHome();
        }
        return super.hurtServer(level, ds, damage);
    }
    
    @Override
    protected net.minecraft.world.InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isDeadOrDying()) {
            return net.minecraft.world.InteractionResult.PASS;
        }
        if (player.getItemInHand(hand).getItem() instanceof net.minecraft.world.item.NameTagItem) {
            return net.minecraft.world.InteractionResult.PASS;
        }
        if (!level().isClientSide() && isOwner(player) && !isDeadOrDying()) {
            if (player.isCrouching()) {
                playSound(SoundsTC.zap, 1.0f, 1.0f);
                if (task != null) {
                    task.setReserved(false);
                }
                dropCarried();
                ItemStack placer = new ItemStack(ItemsTC.golemPlacer);
                net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, placer, t -> t.put("props", net.minecraft.nbt.LongTag.valueOf(getProperties().toLong())));
                net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, placer, t -> t.putInt("xp", rankXp));
                if (level() instanceof net.minecraft.server.level.ServerLevel _sl) spawnAtLocation(_sl, placer);
                discard();
                player.swing(hand);
            }
            else if (player.getItemInHand(hand).getItem() instanceof ItemGolemBell && ThaumcraftCapabilities.getKnowledge(player).isResearchKnown("GOLEMDIRECT")) {
                if (task != null) {
                    task.setReserved(false);
                }
                playSound(SoundsTC.scan);
                setFollowingOwner(!isFollowingOwner());
                if (isFollowingOwner()) {
                    player.sendSystemMessage(Component.translatable("golem.follow", ""));
                    if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                        level().broadcastEntityEvent(this, (byte)5);
                    }
                    clearHome();
                }
                else {
                    player.sendSystemMessage(Component.translatable("golem.stay", ""));
                    if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                        level().broadcastEntityEvent(this, (byte)8);
                    }
                    setHomeTo(blockPosition(), getProperties().hasTrait(EnumGolemTrait.SCOUT) ? 48 : 32);
                }
                updateEntityAttributes();
                player.swing(hand);
            }
            // dye coloring disabled (OreDictionary removed)
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }
    
    @Override
    public void die(DamageSource cause) {
        if (task != null) {
            task.setReserved(false);
        }
        super.die(cause);
        if (!level().isClientSide()) {
            dropCarried();
        }
    }
    
    protected void dropCarried() {
        for (ItemStack s : getCarrying()) {
            if (s != null && !s.isEmpty()) {
                if (level() instanceof net.minecraft.server.level.ServerLevel _sl) spawnAtLocation(_sl, s);
            }
        }
    }
    
    protected void dropFewItems(boolean p_70628_1_) {
        float b = 0.15f; // drop bonus
        for (ItemStack stack : getProperties().generateComponents()) {
            ItemStack s = stack.copy();
            if (getRandom().nextFloat() < 0.3f + b) {
                if (s.getCount() > 0) {
                    s.shrink(getRandom().nextInt(s.getCount()));
                }
                if (level() instanceof net.minecraft.server.level.ServerLevel _sl) spawnAtLocation(_sl, s);
            }
        }
    }
    
    public boolean isBesideClimbableBlock() {
        return ((byte) entityData.get((EntityDataAccessor)EntityThaumcraftGolem.CLIMBING) & 0x1) != 0x0;
    }
    
    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = (byte) entityData.get((EntityDataAccessor)EntityThaumcraftGolem.CLIMBING);
        if (climbing) {
            b0 |= 0x1;
        }
        else {
            b0 &= 0xFFFFFFFE;
        }
        entityData.set(EntityThaumcraftGolem.CLIMBING, b0);
    }
    
    public boolean isFollowingOwner() {
        return Utils.getBit(getFlags(), 1);
    }
    
    public void setFollowingOwner(boolean par1) {
        byte var2 = getFlags();
        if (par1) {
            setFlags((byte)Utils.setBit(var2, 1));
        }
        else {
            setFlags((byte)Utils.clearBit(var2, 1));
        }
    }
    
    public void setTarget(LivingEntity entitylivingbaseIn) {
        super.setTarget(entitylivingbaseIn);
        setInCombat(getTarget() != null);
    }
    
    @Override
    public boolean isInCombat() {
        return Utils.getBit(getFlags(), 3);
    }
    
    public void setInCombat(boolean par1) {
        byte var2 = getFlags();
        if (par1) {
            setFlags((byte)Utils.setBit(var2, 3));
        }
        else {
            setFlags((byte)Utils.clearBit(var2, 3));
        }
    }
    
    public boolean attackEntityAsMob(Entity ent) {
        float dmg = (float) getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        int kb = 0;
        if (ent instanceof LivingEntity) {
            // getModifierForCreature removed in 1.17+
            kb += 0 /* knockback modifier removed */;
        }
        ent.hurt(damageSources().mobAttack(this), dmg);
        boolean flag = true;
        if (flag) {
            if (ent instanceof LivingEntity && getProperties().hasTrait(EnumGolemTrait.DEFT)) {
                // recentlyHit removed - was preventing drops, not needed
            }
            if (kb > 0) {
                ent.setDeltaMovement(ent.getDeltaMovement().add(-Mth.sin(getYRot() * 3.1415927f / 180.0f) * kb * 0.5f, 0.1, Mth.cos(getYRot() * 3.1415927f / 180.0f) * kb * 0.5f));
                setDeltaMovement(getDeltaMovement().x * 0.6, getDeltaMovement().y, getDeltaMovement().z);
                setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z * 0.6);
            }
            int j = 0 /* fire aspect removed */;
            if (j > 0) {
                ent.igniteForSeconds(j);
            }
            /* applyEnchantments removed */ ;
            if (getProperties().getArms().function != null) {
                getProperties().getArms().function.onMeleeAttack(this, ent);
            }
            if (ent instanceof Mob && !ent.isAlive()) {
                addRankXp(8);
            }
        }
        return flag;
    }
    
    public Task getTask() {
        if (task == null && taskID != Integer.MAX_VALUE) {
            task = TaskHandler.getTask((level() instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)level()).dimension().identifier().hashCode() : 0), taskID);
            taskID = Integer.MAX_VALUE;
        }
        return task;
    }
    
    public void setTask(Task task) {
        this.task = task;
    }
    
    @Override
    public void addRankXp(int xp) {
        if (!getProperties().hasTrait(EnumGolemTrait.SMART) || level().isClientSide()) {
            return;
        }
        int rank = getProperties().getRank();
        if (rank < 10) {
            rankXp += xp;
            int xn = (rank + 1) * (rank + 1) * 1000;
            if (rankXp >= xn) {
                rankXp -= xn;
                IGolemProperties props = getProperties();
                props.setRank(rank + 1);
                setProperties(props);
                if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                    level().broadcastEntityEvent(this, (byte)9);
                    playSound(SoundEvents.PLAYER_LEVELUP, 0.25f, 1.0f);
                }
            }
        }
    }
    
    @Override
    public ItemStack holdItem(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getCount() <= 0) {
            return stack;
        }
        for (int a = 0; a < (getProperties().hasTrait(EnumGolemTrait.HAULER) ? 2 : 1); ++a) {
            if (getItemBySlot(EquipmentSlot.values()[a]) == null || getItemBySlot(EquipmentSlot.values()[a]).isEmpty()) {
                setItemSlot(EquipmentSlot.values()[a], stack);
                return ItemStack.EMPTY;
            }
            if (getItemBySlot(EquipmentSlot.values()[a]).getCount() < getItemBySlot(EquipmentSlot.values()[a]).getMaxStackSize() && ItemStack.isSameItem(getItemBySlot(EquipmentSlot.values()[a]), stack) && ItemStack.isSameItemSameComponents(getItemBySlot(EquipmentSlot.values()[a]), stack)) {
                int d = Math.min(stack.getCount(), getItemBySlot(EquipmentSlot.values()[a]).getMaxStackSize() - getItemBySlot(EquipmentSlot.values()[a]).getCount());
                stack.shrink(d);
                getItemBySlot(EquipmentSlot.values()[a]).grow(d);
                if (stack.getCount() <= 0) {
                    stack = ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }
    
    @Override
    public ItemStack dropItem(ItemStack stack) {
        ItemStack out = ItemStack.EMPTY;
        for (int a = 0; a < (getProperties().hasTrait(EnumGolemTrait.HAULER) ? 2 : 1); ++a) {
            if (getItemBySlot(EquipmentSlot.values()[a]) != null) {
                if (!getItemBySlot(EquipmentSlot.values()[a]).isEmpty()) {
                    if (stack == null || stack.isEmpty()) {
                        out = getItemBySlot(EquipmentSlot.values()[a]).copy();
                        setItemSlot(EquipmentSlot.values()[a], ItemStack.EMPTY);
                    }
                    else if (ItemStack.isSameItem(getItemBySlot(EquipmentSlot.values()[a]), stack) && ItemStack.isSameItemSameComponents(getItemBySlot(EquipmentSlot.values()[a]), stack)) {
                        out = getItemBySlot(EquipmentSlot.values()[a]).copy();
                        out.setCount(Math.min(stack.getCount(), out.getCount()));
                        getItemBySlot(EquipmentSlot.values()[a]).shrink(stack.getCount());
                        if (getItemBySlot(EquipmentSlot.values()[a]).getCount() <= 0) {
                            setItemSlot(EquipmentSlot.values()[a], ItemStack.EMPTY);
                        }
                    }
                    if (out != null && !out.isEmpty()) {
                        break;
                    }
                }
            }
        }
        if (getProperties().hasTrait(EnumGolemTrait.HAULER) && (getItemBySlot(EquipmentSlot.values()[0]) == null || getItemBySlot(EquipmentSlot.values()[0]).isEmpty()) && getItemBySlot(EquipmentSlot.values()[1]) != null && !getItemBySlot(EquipmentSlot.values()[1]).isEmpty()) {
            setItemSlot(EquipmentSlot.values()[0], getItemBySlot(EquipmentSlot.values()[1]).copy());
            setItemSlot(EquipmentSlot.values()[1], ItemStack.EMPTY);
        }
        return out;
    }
    
    @Override
    public int canCarryAmount(ItemStack stack) {
        int ss = 0;
        for (int a = 0; a < (getProperties().hasTrait(EnumGolemTrait.HAULER) ? 2 : 1); ++a) {
            if (getItemBySlot(EquipmentSlot.values()[a]) == null || getItemBySlot(EquipmentSlot.values()[a]).isEmpty()) {
                ss += getItemBySlot(EquipmentSlot.values()[a]).getMaxStackSize();
            }
            if (ItemStack.isSameItem(getItemBySlot(EquipmentSlot.values()[a]), stack) && ItemStack.isSameItemSameComponents(getItemBySlot(EquipmentSlot.values()[a]), stack)) {
                ss += getItemBySlot(EquipmentSlot.values()[a]).getMaxStackSize() - getItemBySlot(EquipmentSlot.values()[a]).getCount();
            }
        }
        return ss;
    }
    
    @Override
    public boolean canCarry(ItemStack stack, boolean partial) {
        int ca = canCarryAmount(stack);
        if (ca > 0) {
            if (!partial) {
                if (ca < stack.getCount()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isCarrying(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        for (int a = 0; a < (getProperties().hasTrait(EnumGolemTrait.HAULER) ? 2 : 1); ++a) {
            if (getItemBySlot(EquipmentSlot.values()[a]) != null && !getItemBySlot(EquipmentSlot.values()[a]).isEmpty() && getItemBySlot(EquipmentSlot.values()[a]).getCount() > 0 && ItemStack.isSameItem(getItemBySlot(EquipmentSlot.values()[a]), stack) && ItemStack.isSameItemSameComponents(getItemBySlot(EquipmentSlot.values()[a]), stack)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public NonNullList<ItemStack> getCarrying() {
        if (getProperties().hasTrait(EnumGolemTrait.HAULER)) {
            NonNullList<ItemStack> stacks = NonNullList.withSize(2, ItemStack.EMPTY);
            stacks.set(0, getItemBySlot(EquipmentSlot.values()[0]));
            stacks.set(1, getItemBySlot(EquipmentSlot.values()[1]));
            return stacks;
        }
        return NonNullList.withSize(1, getItemBySlot(EquipmentSlot.values()[0]));
    }
    
    @Override
    public LivingEntity getGolemEntity() {
        return this;
    }
    
    @Override
    public Level getGolemWorld() {
        return level();
    }
    
    @Override
    public void swing() {
        swing(net.minecraft.world.InteractionHand.MAIN_HAND);
    }
    
    public void attackEntityWithRangedAttack(LivingEntity target, float range) {
        if (getProperties().getArms().function != null) {
            getProperties().getArms().function.onRangedAttack(this, target, range);
        }
    }
    
    public void setSwingingArms(boolean swingingArms) {
    }
    
    static {
        PROPS1 = SynchedEntityData.defineId(EntityThaumcraftGolem.class, EntityDataSerializers.INT);
        PROPS2 = SynchedEntityData.defineId(EntityThaumcraftGolem.class, EntityDataSerializers.INT);
        PROPS3 = SynchedEntityData.defineId(EntityThaumcraftGolem.class, EntityDataSerializers.INT);
        CLIMBING = SynchedEntityData.defineId(EntityThaumcraftGolem.class, EntityDataSerializers.BYTE);
    }
    
    class FlyingMoveControl extends MoveControl
    {
        public FlyingMoveControl(EntityThaumcraftGolem vex) {
            super(vex);
        }
        
        public void onUpdateMoveHelper() {
            if (operation == MoveControl.Operation.MOVE_TO) {
                double d0 = wantedX - EntityThaumcraftGolem.this.getX();
                double d2 = wantedY - EntityThaumcraftGolem.this.getY();
                double d3 = wantedZ - EntityThaumcraftGolem.this.getZ();
                double d4 = d0 * d0 + d2 * d2 + d3 * d3;
                d4 = Mth.sqrt((float)d4);
                if (d4 < getBoundingBox().getSize()) {
                    operation = MoveControl.Operation.WAIT;
                    EntityThaumcraftGolem this$0 = EntityThaumcraftGolem.this;
                    this$0.setDeltaMovement(this$0.getDeltaMovement().x * 0.5, this$0.getDeltaMovement().y, this$0.getDeltaMovement().z);
                    EntityThaumcraftGolem this$2 = EntityThaumcraftGolem.this;
                    this$2.setDeltaMovement(this$2.getDeltaMovement().x, this$2.getDeltaMovement().y * 0.5, this$2.getDeltaMovement().z);
                    EntityThaumcraftGolem this$3 = EntityThaumcraftGolem.this;
                    this$3.setDeltaMovement(this$3.getDeltaMovement().x, this$3.getDeltaMovement().y, this$3.getDeltaMovement().z * 0.5);
                }
                else {
                    EntityThaumcraftGolem this$4 = EntityThaumcraftGolem.this;
                    this$4.setDeltaMovement(this$4.getDeltaMovement().x + d0 / d4 * 0.033 * speedModifier, this$4.getDeltaMovement().y, this$4.getDeltaMovement().z);
                    EntityThaumcraftGolem this$5 = EntityThaumcraftGolem.this;
                    this$5.setDeltaMovement(this$5.getDeltaMovement().x, this$5.getDeltaMovement().y + d2 / d4 * 0.0125 * speedModifier, this$5.getDeltaMovement().z);
                    EntityThaumcraftGolem this$6 = EntityThaumcraftGolem.this;
                    this$6.setDeltaMovement(this$6.getDeltaMovement().x, this$6.getDeltaMovement().y, this$6.getDeltaMovement().z + d3 / d4 * 0.033 * speedModifier);
                    if (getTarget() == null) {
                        setYRot(-(float)Mth.atan2(getDeltaMovement().x, getDeltaMovement().z) * 57.295776f);
                        yBodyRotO = getYRot();
                    }
                    else {
                        double d5 = getTarget().getX() - EntityThaumcraftGolem.this.getX();
                        double d6 = getTarget().getZ() - EntityThaumcraftGolem.this.getZ();
                        EntityThaumcraftGolem.this.setYRot(-(float)Mth.atan2(d5, d6) * 57.295776f);

                        yBodyRotO = EntityThaumcraftGolem.this.getYRot();
                    }
                }
            }
        }
    }
}
