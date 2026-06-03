package thaumcraft.common.entities.monster.cult;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.ai.misc.AIAltarFocus;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import thaumcraft.api.entities.EntitiesTC;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.common.lib.SoundsTC;


public class EntityCultistCleric extends EntityCultist implements RangedAttackMob
{
    public int rage;
    private static final EntityDataAccessor<Boolean> RITUALIST =
        SynchedEntityData.defineId(EntityCultistCleric.class, EntityDataSerializers.BOOLEAN);

    @SuppressWarnings("unchecked")
    public EntityCultistCleric(Level level) {
        super(null, level);
        // Entity requires EntityType; use factory method
    }

    public EntityCultistCleric(net.minecraft.world.entity.EntityType<? extends EntityCultistCleric> type, Level world) {
        super(type, world);
        rage = 0;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 24.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new AIAltarFocus(this));
        goalSelector.addGoal(2, new AILongRangeAttack(this, 2.0, 1.0, 20, 40, 24.0f));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.8));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new AICultistHurtByTarget(this, true));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EntityEldritchGuardian.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractIllager.class, true));
    }

    @Override
    protected void setLoot(DifficultyInstance diff) {
        setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonRobeHelm));
        setItemSlot(EquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonRobeChest));
        setItemSlot(EquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonRobeLegs));
        if (getRandom().nextFloat() < ((level().getDifficulty() == Difficulty.HARD) ? 0.3f : 0.1f)) {
            setItemSlot(EquipmentSlot.FEET, new ItemStack(ItemsTC.crimsonBoots));
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float f) {
        double d0 = target.getX() - getX();
        double d2 = target.getBoundingBox().minY + target.getBbHeight() / 2.0f - (getY() + getBbHeight() / 2.0f);
        double d3 = target.getZ() - getZ();
        swing(InteractionHand.MAIN_HAND);
        float rf = getRandom().nextFloat();
        if (rf > 0.66f) {
            EntityGolemOrb blast = EntityGolemOrb.create(EntitiesTC.GOLEM_ORB.get(), level(), this, target, true);
            Vec3 v = target.position().add(target.getDeltaMovement().scale(10.0)).subtract(position()).normalize();
            blast.shoot(v.x, v.y, v.z, 0.66f, 3.0f);
            playSound(SoundsTC.egattack, 1.0f, 1.0f + getRandom().nextFloat() * 0.1f);
            level().addFreshEntity(blast);
        } else {
            float f2 = Mth.sqrt(f) * 0.5f;
            level().levelEvent(1009, blockPosition(), 0);
            for (int i = 0; i < 3; ++i) {
                SmallFireball fireball = new SmallFireball(level(), this, new Vec3(
                    d0 + getRandom().nextGaussian() * f2, d2, d3 + getRandom().nextGaussian() * f2));
                fireball.setPos(fireball.getX(), getY() + getBbHeight() / 2.0f + 0.5, fireball.getZ());
                level().addFreshEntity(fireball);
            }
        }
    }

    @Override
    public boolean requiresCustomPersistence() {
        return !getIsRitualist();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RITUALIST, false);
    }

    public boolean getIsRitualist() {
        return entityData.get(RITUALIST);
    }

    public void setIsRitualist(boolean par1) {
        entityData.set(RITUALIST, par1);
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel serverLevel, DamageSource source, float amount) {
        setIsRitualist(false);
        return super.hurtServer(serverLevel, source, amount);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        setIsRitualist(nbt.getBooleanOr("ritualist", false));
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("ritualist", getIsRitualist());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && getIsRitualist() && rage >= 5) {
            setIsRitualist(false);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.chant;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 500;
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 19) {
            for (int i = 0; i < 3; ++i) {
                double d0 = getRandom().nextGaussian() * 0.02;
                double d2 = getRandom().nextGaussian() * 0.02;
                double d3 = getRandom().nextGaussian() * 0.02;
                level().addParticle(ParticleTypes.ANGRY_VILLAGER,
                    getX() + getRandom().nextFloat() * getBbWidth() * 2.0f - getBbWidth(),
                    getY() + 0.5 + getRandom().nextFloat() * getBbHeight(),
                    getZ() + getRandom().nextFloat() * getBbWidth() * 2.0f - getBbWidth(),
                    d0, d2, d3);
            }
        } else {
            super.handleEntityEvent(event);
        }
    }

    public void setSwingingArms(boolean swingingArms) {}
}
