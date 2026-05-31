package thaumcraft.common.entities.monster;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;


public class EntityFireBat extends Monster
{
    private BlockPos currentFlightTarget;
    public LivingEntity owner;
    private static final EntityDataAccessor<Boolean> HANGING =
        SynchedEntityData.defineId(EntityFireBat.class, EntityDataSerializers.BOOLEAN);
    public int damBonus;
    private int attackTime;

    public EntityFireBat(net.minecraft.world.entity.EntityType<? extends EntityFireBat> type, Level world) {
        super(type, world);
        owner = null;
        damBonus = 0;
        setIsBatHanging(true);
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 5.0)
            .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HANGING, false);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    public boolean getIsBatHanging() {
        return entityData.get(HANGING);
    }

    public void setIsBatHanging(boolean par1) {
        entityData.set(HANGING, par1);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (getIsBatHanging()) {
            setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
        } else {
            setDeltaMovement(getDeltaMovement().multiply(1.0, 0.6, 1.0));
            if (attackTime > 0) --attackTime;

            BlockPos blockpos = blockPosition();
            BlockPos blockpos2 = blockpos.above();

            if (getTarget() == null) {
                if (currentFlightTarget == null || getRandom().nextInt(30) == 0 || distanceToSqr(currentFlightTarget.getX() + 0.5, currentFlightTarget.getY(), currentFlightTarget.getZ() + 0.5) < 4.0) {
                    currentFlightTarget = new BlockPos((int)getX() + getRandom().nextInt(7) - getRandom().nextInt(7), (int)getY() + getRandom().nextInt(6) - 2, (int)getZ() + getRandom().nextInt(7) - getRandom().nextInt(7));
                }
                double vx = currentFlightTarget.getX() + 0.5 - getX();
                double vy = currentFlightTarget.getY() + 0.1 - getY();
                double vz = currentFlightTarget.getZ() + 0.5 - getZ();
                setDeltaMovement(
                    getDeltaMovement().x + (Math.signum(vx) * 0.5 - getDeltaMovement().x) * 0.1,
                    getDeltaMovement().y + (Math.signum(vy) * 0.7 - getDeltaMovement().y) * 0.1,
                    getDeltaMovement().z + (Math.signum(vz) * 0.5 - getDeltaMovement().z) * 0.1);
            } else {
                LivingEntity target = getTarget();
                double vx = target.getX() - getX();
                double vy = target.getY() + target.getEyeHeight() * 0.66f - getY();
                double vz = target.getZ() - getZ();
                setDeltaMovement(
                    getDeltaMovement().x + (Math.signum(vx) * 0.5 - getDeltaMovement().x) * 0.1,
                    getDeltaMovement().y + (Math.signum(vy) * 0.7 - getDeltaMovement().y) * 0.1,
                    getDeltaMovement().z + (Math.signum(vz) * 0.5 - getDeltaMovement().z) * 0.1);
                if (attackTime <= 0 && isAlive() && getSensing().hasLineOfSight(target) && level() instanceof net.minecraft.server.level.ServerLevel sl) {
                    float dist = distanceTo(target);
                    if (dist < 3.0f) {
                        attackTime = 20 + level().getRandom().nextInt(20);
                        if (level().getRandom().nextInt(10) == 0) {
                            sl.explode(this, getX(), getY(), getZ(), 1.5f, Level.ExplosionInteraction.NONE);
                            discard();
                        }
                        doHurtTarget(sl, target);
                        playSound(SoundEvents.BAT_HURT, 0.5f, 0.9f + level().getRandom().nextFloat() * 0.2f);
                    }
                }
            }
        }
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel sl, DamageSource source, float damage) {
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE) || source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)) {
            return false;
        }
        if (getIsBatHanging()) setIsBatHanging(false);
        return super.hurtServer(sl, source, damage);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        setIsBatHanging(nbt.getBooleanOr("hang", false));
        damBonus = nbt.getByteOr("damBonus", (byte)0);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("hang", getIsBatHanging());
        nbt.putByte("damBonus", (byte) damBonus);
    }

    @Override
    public float getSoundVolume() {
        return 0.1f;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return (getIsBatHanging() && getRandom().nextInt(4) != 0) ? null : SoundEvents.BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
    }
}
