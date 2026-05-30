package thaumcraft.common.entities.construct;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.Utils;


public class EntityTurretCrossbowAdvanced extends EntityTurretCrossbow
{
    private static final EntityDataAccessor<Byte> FLAGS =
        SynchedEntityData.defineId(EntityTurretCrossbowAdvanced.class, EntityDataSerializers.BYTE);

    @SuppressWarnings("unchecked")
    public EntityTurretCrossbowAdvanced(Level worldIn, BlockPos pos) {
        this((EntityType<? extends EntityTurretCrossbowAdvanced>) null, worldIn, pos);
    }

    public EntityTurretCrossbowAdvanced(EntityType<? extends EntityTurretCrossbowAdvanced> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityTurretCrossbowAdvanced(EntityType<? extends EntityTurretCrossbowAdvanced> type, Level worldIn, BlockPos pos) {
        this(type, worldIn);
        setPos(pos.getX() + 0.5, (double)pos.getY(), pos.getZ() + 0.5);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 40.0)
            .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FLAGS, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new RangedAttackGoal(this, 0.0, 20, 40, 24.0f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true));
        setTargetMob(true);
    }

    public boolean getTargetAnimal() {
        return Utils.getBit((byte) (int) this.entityData.get(FLAGS), 0);
    }

    public void setTargetAnimal(boolean val) {
        byte b = this.entityData.get(FLAGS);
        this.entityData.set(FLAGS, val ? (byte) Utils.setBit(b, 0) : (byte) Utils.clearBit(b, 0));
        setTarget(null);
    }

    public boolean getTargetMob() {
        return Utils.getBit((byte) (int) this.entityData.get(FLAGS), 1);
    }

    public void setTargetMob(boolean val) {
        byte b = this.entityData.get(FLAGS);
        this.entityData.set(FLAGS, val ? (byte) Utils.setBit(b, 1) : (byte) Utils.clearBit(b, 1));
        setTarget(null);
    }

    public boolean getTargetPlayer() {
        return Utils.getBit((byte) (int) this.entityData.get(FLAGS), 2);
    }

    public void setTargetPlayer(boolean val) {
        byte b = this.entityData.get(FLAGS);
        this.entityData.set(FLAGS, val ? (byte) Utils.setBit(b, 2) : (byte) Utils.clearBit(b, 2));
        setTarget(null);
    }

    public boolean getTargetFriendly() {
        return Utils.getBit((byte) (int) this.entityData.get(FLAGS), 3);
    }

    public void setTargetFriendly(boolean val) {
        byte b = this.entityData.get(FLAGS);
        this.entityData.set(FLAGS, val ? (byte) Utils.setBit(b, 3) : (byte) Utils.clearBit(b, 3));
        setTarget(null);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && getTarget() instanceof Player p && p != getOwnerEntity()) {
            setTarget(null);
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putByte("targets", this.entityData.get(FLAGS));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(FLAGS, input.getByteOr("targets", (byte)0));
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource cause, boolean recentlyHit) {
        if (random.nextFloat() < 0.2f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.mind));
        if (random.nextFloat() < 0.5f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.mechanismSimple));
        if (random.nextFloat() < 0.5f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(BlocksTC.plankGreatwood));
        if (random.nextFloat() < 0.5f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(BlocksTC.plankGreatwood));
        if (random.nextFloat() < 0.3f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.plate));
        if (random.nextFloat() < 0.4f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.plate));
        if (random.nextFloat() < 0.4f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.plate));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isRemoved()) return InteractionResult.PASS;
        if (player.getMainHandItem().getItem() instanceof net.minecraft.world.item.NameTagItem) {
            return InteractionResult.PASS;
        }
        if (isOwner(player)) {
            if (player.isCrouching()) {
                if (level() instanceof ServerLevel sl) {
                    playSound(SoundsTC.zap, 1.0f, 1.0f);
                    if (!getMainHandItem().isEmpty()) {
                        ItemEntity ie = new ItemEntity(sl, getX(), getY(), getZ(), getMainHandItem().copy());
                        sl.addFreshEntity(ie);
                    }
                    spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.turretPlacer));
                    discard();
                }
                player.swing(hand);
                return InteractionResult.SUCCESS;
            }
            // TODO: open advanced turret GUI
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }
}
