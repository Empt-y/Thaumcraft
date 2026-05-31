package thaumcraft.common.entities.monster;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.lib.SoundsTC;


public class EntityEldritchGuardian extends Monster implements RangedAttackMob, IEldritchMob
{
    public float armLiftL;
    public float armLiftR;
    boolean lastBlast;

    public EntityEldritchGuardian(net.minecraft.world.entity.EntityType<? extends EntityEldritchGuardian> type, Level world) {
        super(type, world);
        armLiftL = 0.0f;
        armLiftR = 0.0f;
        lastBlast = false;
        ((GroundPathNavigation) getNavigation()).setCanOpenDoors(true);
        xpReward = 20;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 50.0)
            .add(Attributes.FOLLOW_RANGE, 40.0)
            .add(Attributes.MOVEMENT_SPEED, 0.28)
            .add(Attributes.ATTACK_DAMAGE, 7.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new AILongRangeAttack(this, 8.0, 1.0, 20, 40, 24.0f));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.8));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EntityCultist.class, true));
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel sl, DamageSource source, float damage) {
        if (source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR)) {
            damage /= 2.0f;
        }
        return super.hurtServer(sl, source, damage);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            if (armLiftL > 0.0f) armLiftL -= 0.05f;
            if (armLiftR > 0.0f) armLiftR -= 0.05f;
            float x = (float)(getX() + (getRandom().nextFloat() - getRandom().nextFloat()) * 0.2f);
            float z = (float)(getZ() + (getRandom().nextFloat() - getRandom().nextFloat()) * 0.2f);
            FXDispatcher.INSTANCE.wispFXEG(x, getY() + 0.22 * getBbHeight(), z, this);
        }
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        if (getHomePosition() != null && getHomeRadius() > 0) {
            nbt.putInt("HomeD", getHomeRadius());
            nbt.putInt("HomeX", getHomePosition().getX());
            nbt.putInt("HomeY", getHomePosition().getY());
            nbt.putInt("HomeZ", getHomePosition().getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.getIntOr("HomeD", 0) != Integer.MIN_VALUE) {
            setHomeTo(new BlockPos(nbt.getIntOr("HomeX", 0), nbt.getIntOr("HomeY", 0), nbt.getIntOr("HomeZ", 0)), nbt.getIntOr("HomeD", 0));
        }
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor levelAccessor, DifficultyInstance diff, net.minecraft.world.entity.EntitySpawnReason reason, SpawnGroupData data) {
        return super.finalizeSpawn(levelAccessor, diff, reason, data);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float f) {
        // TODO: EntityEldritchOrb / sonic attack
        if (getRandom().nextFloat() <= 0.15f && getSensing().hasLineOfSight(target)) {
            try {
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 0));
            } catch (Exception ignored) {}
            playSound(SoundsTC.egscreech, 3.0f, 1.0f + getRandom().nextFloat() * 0.1f);
        }
    }

    @Override
    protected void customServerAiStep(net.minecraft.server.level.ServerLevel sl) {
        super.customServerAiStep(sl);
        if (invulnerableTime <= 0 && tickCount % 25 == 0) {
            int bh = (int) getAttribute(Attributes.MAX_HEALTH).getBaseValue() / 2;
            if (getAbsorptionAmount() < bh) {
                setAbsorptionAmount(getAbsorptionAmount() + 1.0f);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 15) {
            armLiftL = 0.5f;
        } else if (event == 16) {
            armLiftR = 0.5f;
        } else if (event == 17) {
            armLiftL = 0.9f;
            armLiftR = 0.9f;
        } else {
            super.handleEntityEvent(event);
        }
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public float getSoundVolume() {
        return 1.5f;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.egidle;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsTC.egdeath;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 500;
    }

    public void setSwingingArms(boolean swingingArms) {}
}
