package thaumcraft.common.entities.monster.tainted;
import java.util.ArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.fx.particles.FXSwarm;
import thaumcraft.common.blocks.world.taint.TaintHelper;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.SoundsTC;


public class EntityTaintSwarm extends Monster implements ITaintedMob
{
    private BlockPos currentFlightTarget;
    private static final EntityDataAccessor<Boolean> SUMMONED =
        SynchedEntityData.defineId(EntityTaintSwarm.class, EntityDataSerializers.BOOLEAN);
    public int damBonus;
    public ArrayList<FXSwarm> swarm;
    private int attackTime;

    public EntityTaintSwarm(net.minecraft.world.entity.EntityType<? extends EntityTaintSwarm> type, Level world) {
        super(type, world);
        damBonus = 0;
        swarm = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public EntityTaintSwarm(net.minecraft.world.entity.EntityType<? extends EntityTaintSwarm> type, Level world, int db) {
        this(type, world);
        this.damBonus = db;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0)
            .add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SUMMONED, false);
    }

    public boolean getIsSummoned() {
        return entityData.get(SUMMONED);
    }

    public void setIsSummoned(boolean par1) {
        entityData.set(SUMMONED, par1);
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return !ITaintedMob.class.isAssignableFrom(entity.getClass());
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(getDeltaMovement().multiply(1.0, 0.6, 1.0));
        if (level().isClientSide()) {
            swarm.removeIf(p -> p == null || !p.isAlive());
            if (swarm.size() < 30) {
                swarm.add(FXDispatcher.INSTANCE.swarmParticleFX(this, 0.22f, 15.0f, 0.08f));
            }
        } else {
            if (attackTime > 0) --attackTime;
            LivingEntity target = getTarget();
            if (target == null) {
                if (getIsSummoned() && level() instanceof net.minecraft.server.level.ServerLevel sl) {
                    hurtServer(sl, sl.damageSources().generic(), 5.0f);
                }
                if (currentFlightTarget == null || getRandom().nextInt(30) == 0 || distanceToSqr(currentFlightTarget.getX() + 0.5, currentFlightTarget.getY(), currentFlightTarget.getZ() + 0.5) < 4.0) {
                    currentFlightTarget = new BlockPos((int)getX() + getRandom().nextInt(7) - getRandom().nextInt(7), (int)getY() + getRandom().nextInt(6) - 2, (int)getZ() + getRandom().nextInt(7) - getRandom().nextInt(7));
                }
                double vx = (currentFlightTarget.getX() + 0.5 - getX());
                double vy = (currentFlightTarget.getY() + 0.1 - getY());
                double vz = (currentFlightTarget.getZ() + 0.5 - getZ());
                setDeltaMovement(
                    getDeltaMovement().x + (Math.signum(vx) * 0.5 - getDeltaMovement().x) * 0.015,
                    getDeltaMovement().y + (Math.signum(vy) * 0.7 - getDeltaMovement().y) * 0.1,
                    getDeltaMovement().z + (Math.signum(vz) * 0.5 - getDeltaMovement().z) * 0.015);
            } else if (isAlive() && target.isAlive()) {
                double vx = target.getX() - getX();
                double vy = target.getY() + target.getEyeHeight() - getY();
                double vz = target.getZ() - getZ();
                setDeltaMovement(
                    getDeltaMovement().x + (Math.signum(vx) * 0.5 - getDeltaMovement().x) * 0.025,
                    getDeltaMovement().y + (Math.signum(vy) * 0.7 - getDeltaMovement().y) * 0.1,
                    getDeltaMovement().z + (Math.signum(vz) * 0.5 - getDeltaMovement().z) * 0.025);
                float dist = distanceTo(target);
                if (attackTime <= 0 && dist < 3.0f && getSensing().hasLineOfSight(target)) {
                    attackTime = 15 + getRandom().nextInt(10);
                    if (level() instanceof net.minecraft.server.level.ServerLevel sl) {
                        if (doHurtTarget(sl, target)) {
                            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
                        }
                    }
                    playSound(SoundsTC.swarmattack, 0.3f, 0.9f + level().getRandom().nextFloat() * 0.2f);
                }
            } else {
                setTarget(null);
            }
        }
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        setIsSummoned(nbt.getBooleanOr("summoned", false));
        damBonus = nbt.getByteOr("damBonus", (byte)0);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("summoned", getIsSummoned());
        nbt.putByte("damBonus", (byte) damBonus);
    }

    @Override
    public float getSoundVolume() {
        return 0.1f;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.swarm;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return SoundsTC.swarmattack;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsTC.swarmattack;
    }
}
