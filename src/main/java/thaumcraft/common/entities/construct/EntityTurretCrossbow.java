package thaumcraft.common.entities.construct;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.SoundsTC;


public class EntityTurretCrossbow extends EntityOwnedConstruct implements RangedAttackMob
{
    int loadProgressInt;
    boolean isLoadInProgress;
    float loadProgress;
    float prevLoadProgress;
    public float loadProgressForRender;
    boolean attackedLastTick;
    int attackCount;

    @SuppressWarnings("unchecked")
    public EntityTurretCrossbow(Level worldIn, BlockPos pos) {
        this((EntityType<? extends EntityTurretCrossbow>) null, worldIn, pos);
    }

    public EntityTurretCrossbow(EntityType<? extends EntityTurretCrossbow> type, Level worldIn) {
        super(type, worldIn);
        loadProgressInt = 0;
        isLoadInProgress = false;
        loadProgress = 0.0f;
        prevLoadProgress = 0.0f;
        loadProgressForRender = 0.0f;
        attackedLastTick = false;
        attackCount = 0;
    }

    public EntityTurretCrossbow(EntityType<? extends EntityTurretCrossbow> type, Level worldIn, BlockPos pos) {
        this(type, worldIn);
        setPos(pos.getX() + 0.5, (double)pos.getY(), pos.getZ() + 0.5);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 30.0)
            .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new RangedAttackGoal(this, 0.0, 20, 60, 24.0f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float range) {
        if (!getMainHandItem().isEmpty()) {
            Arrow arrow = new Arrow(level(), this, getMainHandItem(), null);
            arrow.setBaseDamage(2.25 + range * 2.0f + getRandom().nextGaussian() * 0.25);
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            double dx = target.getX() - getX();
            double dy = target.getBoundingBox().minY + target.getEyeHeight() - arrow.getY();
            double dz = target.getZ() - getZ();
            arrow.shoot(dx, dy, dz, 2.0f, 2.0f);
            level().addFreshEntity(arrow);
            level().broadcastEntityEvent(this, (byte) 16);
            playSound(SoundEvents.ARROW_SHOOT, 1.0f, 1.0f / (getRandom().nextFloat() * 0.4f + 0.8f));
            getMainHandItem().shrink(1);
            if (getMainHandItem().getCount() <= 0) {
                setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 16 || id == 17) {
            if (!isLoadInProgress) {
                loadProgressInt = -1;
                isLoadInProgress = true;
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getLoadProgress(float pt) {
        float f1 = loadProgress - prevLoadProgress;
        if (f1 < 0.0f) ++f1;
        return prevLoadProgress + f1 * pt;
    }

    private void updateArmSwingProgress() {
        if (isLoadInProgress) {
            ++loadProgressInt;
            if (loadProgressInt >= 10) {
                loadProgressInt = 0;
                isLoadInProgress = false;
            }
        } else {
            loadProgressInt = 0;
        }
        loadProgress = loadProgressInt / 10.0f;
    }

    @Override
    public void tick() {
        prevLoadProgress = loadProgress;
        super.tick();
        if (!level().isClientSide()) {
            setYRot(getYHeadRot());
            if (tickCount % 80 == 0) {
                heal(1.0f);
            }
        } else {
            updateArmSwingProgress();
        }
    }

    public int getVerticalFaceSpeed() {
        return 20;
    }

    public void setSwingingArms(boolean swingingArms) {}

    @Override
    public void move(MoverType type, Vec3 movement) {
        super.move(type, new Vec3(movement.x / 20.0, movement.y, movement.z / 20.0));
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (level() instanceof ServerLevel sl && !getMainHandItem().isEmpty()) {
            ItemEntity ie = new ItemEntity(sl, getX(), getY(), getZ(), getMainHandItem().copy());
            sl.addFreshEntity(ie);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource cause, boolean recentlyHit) {
        if (getRandom().nextFloat() < 0.2f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.mind));
        if (getRandom().nextFloat() < 0.5f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.mechanismSimple));
        if (getRandom().nextFloat() < 0.5f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(BlocksTC.plankGreatwood));
        if (getRandom().nextFloat() < 0.5f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(BlocksTC.plankGreatwood));
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
            // TODO: open basic turret GUI
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }
}
