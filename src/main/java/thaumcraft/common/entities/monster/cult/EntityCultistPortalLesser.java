package thaumcraft.common.entities.monster.cult;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;


public class EntityCultistPortalLesser extends Monster
{
    private static final EntityDataAccessor<Boolean> ACTIVE =
        SynchedEntityData.defineId(EntityCultistPortalLesser.class, EntityDataSerializers.BOOLEAN);
    int stagecounter;
    public int activeCounter;
    public int pulse;

    public EntityCultistPortalLesser(net.minecraft.world.entity.EntityType<? extends EntityCultistPortalLesser> type, Level world) {
        super(type, world);
        stagecounter = 100;
        activeCounter = 0;
        pulse = 0;
        xpReward = 10;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 100.0)
            .add(Attributes.ATTACK_DAMAGE, 0.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ACTIVE, false);
    }

    public boolean isActive() {
        return entityData.get(ACTIVE);
    }

    public void setActive(boolean active) {
        entityData.set(ACTIVE, active);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("active", isActive());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        setActive(nbt.getBooleanOr("active", false));
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void move(MoverType mt, Vec3 vec) {}

    @Override
    public boolean shouldRenderAtSqrDistance(double dist) {
        return dist < 4096.0;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (isActive()) ++activeCounter;
        if (!level().isClientSide()) {
            if (!isActive()) {
                if (tickCount % 10 == 0) {
                    Player p = level().getNearestPlayer(getX(), getY(), getZ(), 32.0, false);
                    if (p != null) {
                        setActive(true);
                        playSound(SoundsTC.craftstart, 1.0f, 1.0f);
                    }
                }
            } else if (stagecounter-- <= 0) {
                Player p = level().getNearestPlayer(getX(), getY(), getZ(), 32.0, false);
                if (p != null && getSensing().hasLineOfSight(p)) {
                    int count = (level().getDifficulty() == Difficulty.HARD) ? 6 : ((level().getDifficulty() == Difficulty.NORMAL) ? 4 : 2);
                    List<EntityCultist> l = level().getEntitiesOfClass(EntityCultist.class, getBoundingBox().inflate(32.0, 32.0, 32.0));
                    if (l != null) count -= l.size();
                    if (count > 0) {
                        level().broadcastEntityEvent(this, (byte) 16);
                        spawnMinions();
                    }
                }
                stagecounter = 50 + getRandom().nextInt(50);
            }
        }
        if (pulse > 0) --pulse;
    }

    void spawnMinions() {
        EntityCultist cultist;
        if (getRandom().nextFloat() > 0.33f) {
            cultist = new EntityCultistKnight(level());
        } else {
            cultist = new EntityCultistCleric(level());
        }
        cultist.setPos(getX() + getRandom().nextFloat() - getRandom().nextFloat(), getY() + 0.25, getZ() + getRandom().nextFloat() - getRandom().nextFloat());
        level().addFreshEntity(cultist);
        cultist.spawnExplosionParticle();
        cultist.playSound(SoundsTC.wandfail, 1.0f, 1.0f);
        if (level() instanceof net.minecraft.server.level.ServerLevel sl) {
            hurtServer(sl, sl.damageSources().fellOutOfWorld(), (float)(5 + getRandom().nextInt(5)));
        }
    }

    @Override
    public void playerTouch(Player p) {
        if (distanceToSqr(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5) < 3.0 && level() instanceof net.minecraft.server.level.ServerLevel sl) {
            p.hurtServer(sl, sl.damageSources().indirectMagic(this, this), 4.0f);
            playSound(SoundsTC.zap, 1.0f, (getRandom().nextFloat() - getRandom().nextFloat()) * 0.1f + 1.0f);
        }
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, Entity source) {
        return false;
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide() && level() instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.explode(this, getX(), getY(), getZ(), 1.5f, Level.ExplosionInteraction.NONE);
        }
        super.die(source);
    }

    @Override
    public float getSoundVolume() {
        return 0.75f;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 540;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.monolith;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return SoundsTC.zap;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsTC.shock;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte msg) {
        if (msg == 16) {
            pulse = 10;
        } else {
            super.handleEntityEvent(msg);
        }
    }
}
