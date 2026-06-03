package thaumcraft.common.entities.monster.boss;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.api.entities.EntitiesTC;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.common.lib.SoundsTC;


public class EntityEldritchGolem extends EntityThaumcraftBoss implements IEldritchMob, RangedAttackMob
{
    private static final EntityDataAccessor<Boolean> HEADLESS =
        SynchedEntityData.defineId(EntityEldritchGolem.class, EntityDataSerializers.BOOLEAN);
    int beamCharge;
    boolean chargingBeam;
    int arcing;
    int ax, ay, az;
    private int attackTimer;

    public EntityEldritchGolem(net.minecraft.world.entity.EntityType<? extends EntityEldritchGolem> type, Level world) {
        super(type, world);
        beamCharge = 0;
        chargingBeam = false;
        arcing = 0;
        ax = ay = az = 0;
        // fire immunity handled by fireImmune() override
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.ATTACK_DAMAGE, 10.0)
            .add(Attributes.MAX_HEALTH, 400.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1, false));
        goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.8));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HEADLESS, false);
    }

    @Override
    public void generateName() {
        setCustomName(net.minecraft.network.chat.Component.literal("Eldritch Golem"));
        bossInfo.setName(getDisplayName());
    }

    public boolean isHeadless() {
        return entityData.get(HEADLESS);
    }

    public void setHeadless(boolean par1) {
        entityData.set(HEADLESS, par1);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("headless", isHeadless());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        setHeadless(nbt.getBooleanOr("headless", false));
        if (isHeadless()) {
            makeHeadless();
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor levelAccessor, DifficultyInstance diff, net.minecraft.world.entity.EntitySpawnReason reason, SpawnGroupData data) {
        spawnTimer = 100;
        return super.finalizeSpawn(levelAccessor, diff, reason, data);
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel serverLevel, DamageSource source, float damage) {
        if (damage > getHealth() && !isHeadless()) {
            setHeadless(true);
            spawnTimer = 100;
            makeHeadless();
            return false;
        }
        return super.hurtServer(serverLevel, source, damage);
    }

    void makeHeadless() {
        goalSelector.addGoal(2, new AILongRangeAttack(this, 3.0, 1.0, 5, 5, 24.0f));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float f) {
        if (getSensing().hasLineOfSight(target) && !chargingBeam && beamCharge > 0) {
            beamCharge -= 15 + getRandom().nextInt(5);
            Vec3 look = getLookAngle();
            EntityGolemOrb blast = EntityGolemOrb.create(EntitiesTC.GOLEM_ORB.get(), level(), this, target, false);
            blast.setPos(blast.getX() + look.x, blast.getY(), blast.getZ() + look.z);
            double d0 = target.getX() + target.getDeltaMovement().x - getX();
            double d2 = target.getY() - getY() - target.getBbHeight() / 2.0f;
            double d3 = target.getZ() + target.getDeltaMovement().z - getZ();
            blast.shoot(d0, d2, d3, 0.66f, 5.0f);
            playSound(SoundsTC.egattack, 1.0f, 1.0f + getRandom().nextFloat() * 0.1f);
            level().addFreshEntity(blast);
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 4) {
            attackTimer = 10;
            playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0f, 1.0f);
        } else if (event == 18) {
            spawnTimer = 150;
        } else if (event == 19) {
            // arc lighting
        } else {
            super.handleEntityEvent(event);
        }
    }

    @Override
    public void tick() {
        if (getSpawnTimer() == 150) {
            level().broadcastEntityEvent(this, (byte) 18);
        }
        if (getSpawnTimer() > 0) {
            heal(2.0f);
        }
        super.tick();
        if (!level().isClientSide()) {
            if (attackTimer > 0) --attackTimer;
            if (isHeadless() && beamCharge <= 0) chargingBeam = true;
            if (isHeadless() && chargingBeam) {
                ++beamCharge;
                level().broadcastEntityEvent(this, (byte) 19);
                if (beamCharge == 150) chargingBeam = false;
            }
        }
    }

    public int getAttackTimer() {
        return attackTimer;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    public void setSwingingArms(boolean swingingArms) {}
}
