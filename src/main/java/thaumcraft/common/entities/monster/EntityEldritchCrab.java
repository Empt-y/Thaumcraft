package thaumcraft.common.entities.monster;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.lib.SoundsTC;


public class EntityEldritchCrab extends Monster implements IEldritchMob
{
    private static final EntityDataAccessor<Boolean> HELM =
        SynchedEntityData.defineId(EntityEldritchCrab.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> RIDING =
        SynchedEntityData.defineId(EntityEldritchCrab.class, EntityDataSerializers.INT);
    private int attackTime;

    public EntityEldritchCrab(net.minecraft.world.entity.EntityType<? extends EntityEldritchCrab> type, Level world) {
        super(type, world);
        attackTime = 0;
        xpReward = 6;
        ((GroundPathNavigation) getNavigation()).setCanOpenDoors(true);
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.63f));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EntityCultist.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HELM, false);
        builder.define(RIDING, -1);
    }

    public int getRiding() {
        return entityData.get(RIDING);
    }

    public void setRiding(int s) {
        entityData.set(RIDING, s);
    }

    public boolean hasHelm() {
        return entityData.get(HELM);
    }

    public void setHelm(boolean par1) {
        entityData.set(HELM, par1);
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor levelAccessor, DifficultyInstance diff, net.minecraft.world.entity.EntitySpawnReason reason, SpawnGroupData data) {
        setHelm(level().getDifficulty() == Difficulty.HARD || getRandom().nextFloat() < 0.33f);
        return super.finalizeSpawn(levelAccessor, diff, reason, data);
    }

    @Override
    public void tick() {
        super.tick();
        --attackTime;
        if (tickCount < 20) fallDistance = 0.0f;
        if (!level().isClientSide()) {
            Entity riding = getVehicle();
            if (riding == null && getTarget() != null && !getTarget().isPassenger() && onGround() && !hasHelm() && !getTarget().isDeadOrDying() && getY() - getTarget().getY() >= getTarget().getBbHeight() / 2.0f && distanceToSqr(getTarget()) < 4.0) {
                startRiding(getTarget());
                setRiding(getTarget().getId());
            }
            if (getVehicle() != null && !isDeadOrDying() && attackTime <= 0 && level() instanceof net.minecraft.server.level.ServerLevel sl) {
                attackTime = 10 + getRandom().nextInt(10);
                doHurtTarget(sl, getVehicle());
                if (getRandom().nextFloat() < 0.2f) {
                    stopRiding();
                    setRiding(-1);
                }
            }
        }
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel sl, DamageSource source, float damage) {
        boolean b = super.hurtServer(sl, source, damage);
        if (hasHelm() && getHealth() / getMaxHealth() <= 0.5f) {
            setHelm(false);
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3);
        }
        return b;
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        setHelm(nbt.getBooleanOr("helm", false));
        if (!hasHelm()) getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("helm", hasHelm());
    }

    @Override
    public int getAmbientSoundInterval() {
        return 160;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.crabtalk;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsTC.crabdeath;
    }
}
