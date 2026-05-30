package thaumcraft.common.entities.monster;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.fx.PacketFXWispZap;


public class EntityWisp extends Mob {
    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    private int aggroCooldown;
    public int prevAttackCounter;
    public int attackCounter;
    private BlockPos currentFlightTarget;

    private static final EntityDataAccessor<String> WISP_TYPE =
        SynchedEntityData.defineId(EntityWisp.class, EntityDataSerializers.STRING);

    public EntityWisp(net.minecraft.world.entity.EntityType<? extends EntityWisp> type, Level world) {
        super(type, world);
        xpReward = 5;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 22.0)
            .add(Attributes.ATTACK_DAMAGE, 3.0);
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WISP_TYPE, "");
    }

    public String getWispType() {
        return entityData.get(WISP_TYPE);
    }

    public void setWispType(String t) {
        entityData.set(WISP_TYPE, t);
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (level().isClientSide()) {
            FXDispatcher.INSTANCE.burst(getX(), getY() + 0.45, getZ(), 1.0f);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide() && tickCount <= 1) {
            FXDispatcher.INSTANCE.burst(getX(), getY(), getZ(), 10.0f);
        }
        if (level().isClientSide() && random.nextBoolean() && Aspect.getAspect(getWispType()) != null) {
            FXDispatcher.INSTANCE.drawWispParticles(
                getX() + (random.nextFloat() - random.nextFloat()) * 0.7f,
                getY() + (random.nextFloat() - random.nextFloat()) * 0.7f,
                getZ() + (random.nextFloat() - random.nextFloat()) * 0.7f,
                0.0, 0.0, 0.0, Aspect.getAspect(getWispType()).getColor(), 0);
        }
        setDeltaMovement(getDeltaMovement().multiply(1.0, 0.6, 1.0));
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);

        if (Aspect.getAspect(getWispType()) == null) {
            if (random.nextInt(10) != 0) {
                ArrayList<Aspect> as = Aspect.getPrimalAspects();
                setWispType(as.get(random.nextInt(as.size())).getTag());
            } else {
                ArrayList<Aspect> as = Aspect.getCompoundAspects();
                setWispType(as.get(random.nextInt(as.size())).getTag());
            }
        }
        if (serverLevel.getDifficulty() == Difficulty.PEACEFUL) {
            discard();
            return;
        }

        prevAttackCounter = attackCounter;
        double attackrange = 16.0;
        LivingEntity target = getTarget();

        if (target == null || !getSensing().hasLineOfSight(target)) {
            if (currentFlightTarget != null && (!level().isEmptyBlock(currentFlightTarget)
                    || currentFlightTarget.getY() < 1
                    || currentFlightTarget.getY() > level().getMaxY() - 8)) {
                currentFlightTarget = null;
            }
            if (currentFlightTarget == null || random.nextInt(30) == 0
                    || distanceToSqr(currentFlightTarget.getX() + 0.5, currentFlightTarget.getY() + 0.5, currentFlightTarget.getZ() + 0.5) < 4.0) {
                currentFlightTarget = new BlockPos(
                    (int) getX() + random.nextInt(7) - random.nextInt(7),
                    (int) getY() + random.nextInt(6) - 2,
                    (int) getZ() + random.nextInt(7) - random.nextInt(7));
            }
            double vx = currentFlightTarget.getX() + 0.5 - getX();
            double vy = currentFlightTarget.getY() + 0.1 - getY();
            double vz = currentFlightTarget.getZ() + 0.5 - getZ();
            Vec3 m = getDeltaMovement();
            setDeltaMovement(
                m.x + (Math.signum(vx) * 0.5 - m.x) * 0.1,
                m.y + (Math.signum(vy) * 0.7 - m.y) * 0.1,
                m.z + (Math.signum(vz) * 0.5 - m.z) * 0.1);
            Vec3 m2 = getDeltaMovement();
            float yaw = (float)(Math.atan2(m2.z, m2.x) * 180.0 / Math.PI) - 90.0f;
            setYRot(getYRot() + Mth.wrapDegrees(yaw - getYRot()));
            zza = 0.15f;
        } else if (distanceToSqr(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5) > attackrange * attackrange / 2.0 && getSensing().hasLineOfSight(target)) {
            double vx = target.getX() - getX();
            double vy = target.getY() + target.getEyeHeight() * 0.66f - getY();
            double vz = target.getZ() - getZ();
            Vec3 m = getDeltaMovement();
            setDeltaMovement(
                m.x + (Math.signum(vx) * 0.5 - m.x) * 0.1,
                m.y + (Math.signum(vy) * 0.7 - m.y) * 0.1,
                m.z + (Math.signum(vz) * 0.5 - m.z) * 0.1);
            Vec3 m2 = getDeltaMovement();
            float yaw = (float)(Math.atan2(m2.z, m2.x) * 180.0 / Math.PI) - 90.0f;
            setYRot(getYRot() + Mth.wrapDegrees(yaw - getYRot()));
            zza = 0.5f;
        }

        if (target instanceof Player p && p.getAbilities().invulnerable) {
            setTarget(null);
            target = null;
        }
        if (target != null && target.isDeadOrDying()) {
            setTarget(null);
            target = null;
        }

        --aggroCooldown;
        if (random.nextInt(1000) == 0 && (getTarget() == null || aggroCooldown-- <= 0)) {
            setTarget(serverLevel.getNearestPlayer(this, 16.0));
            if (getTarget() != null) aggroCooldown = 50;
        }

        target = getTarget();
        if (isAlive() && target != null && target.distanceToSqr(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5) < attackrange * attackrange) {
            double d5 = target.getX() - getX();
            double d7 = target.getZ() - getZ();
            float n = -(float) Math.atan2(d5, d7) * 180.0f / (float) Math.PI;
            setYRot(n);
            yBodyRot = n;
            if (getSensing().hasLineOfSight(target)) {
                ++attackCounter;
                if (attackCounter == 20) {
                    playSound(SoundsTC.zap, 1.0f, 1.1f);
                    PacketDistributor.sendToPlayersNear(serverLevel, null, getX(), getY(), getZ(), 32.0,
                        new PacketFXWispZap(getId(), target.getId()));
                    float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
                    Vec3 tm = target.getDeltaMovement();
                    if (Math.abs(tm.x) > 0.1 || Math.abs(tm.y) > 0.1 || Math.abs(tm.z) > 0.1) {
                        if (random.nextFloat() < 0.4f) {
                            target.hurt(serverLevel.damageSources().mobAttack(this), damage);
                        }
                    } else if (random.nextFloat() < 0.66f) {
                        target.hurt(serverLevel.damageSources().mobAttack(this), damage + 1.0f);
                    }
                    attackCounter = -20 + random.nextInt(20);
                }
            } else if (attackCounter > 0) {
                --attackCounter;
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.wisplive;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.LAVA_EXTINGUISH;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsTC.wispdead;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        if (Aspect.getAspect(getWispType()) != null) {
            spawnAtLocation(level, ThaumcraftApiHelper.makeCrystal(Aspect.getAspect(getWispType())));
        }
    }

    @Override
    protected float getSoundVolume() {
        return 0.25f;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, EntitySpawnReason spawnType) {
        int count = 0;
        try {
            List<EntityWisp> l = level.getEntitiesOfClass(EntityWisp.class, getBoundingBox().inflate(16.0, 16.0, 16.0));
            count = l.size();
        } catch (Exception ex) {}
        return count < 8 && level.getDifficulty() != Difficulty.PEACEFUL && super.checkSpawnRules(level, spawnType);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        BlockPos pos = new BlockPos((int) getX(), (int) getBoundingBox().minY, (int) getZ());
        if (level.getBrightness(LightLayer.SKY, pos) > random.nextInt(32)) {
            return false;
        }
        return level.getMaxLocalRawBrightness(pos) <= net.minecraft.util.RandomSource.create().nextInt(8);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("Type", getWispType());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        setWispType(nbt.getStringOr("Type", ""));
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 2;
    }
}
