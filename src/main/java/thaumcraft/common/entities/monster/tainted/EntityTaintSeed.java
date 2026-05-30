package thaumcraft.common.entities.monster.tainted;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.world.taint.TaintHelper;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.world.aura.AuraHandler;


public class EntityTaintSeed extends Monster implements ITaintedMob
{
    public int boost;
    boolean firstRun;
    public float attackAnim;

    @SuppressWarnings("unchecked")
    public EntityTaintSeed(Level world) {
        super(null, world);
        // Entity requires EntityType; use factory method
    }

    public EntityTaintSeed(net.minecraft.world.entity.EntityType<? extends EntityTaintSeed> type, Level world) {
        super(type, world);
        boost = 0;
        firstRun = false;
        attackAnim = 0.0f;
    }

    protected int getArea() {
        return 1;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, false));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        boost = nbt.getIntOr("boost", 0);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("boost", boost);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.server.level.ServerLevel sl, Entity target) {
        level().broadcastEntityEvent(this, (byte) 16);
        playSound(SoundsTC.tentacle, getSoundVolume(), getSoundPitch());
        return super.doHurtTarget(sl, target);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte par1) {
        if (par1 == 16) {
            attackAnim = 0.5f;
        } else {
            super.handleEntityEvent(par1);
        }
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return !ITaintedMob.class.isAssignableFrom(entity.getClass());
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 75.0)
            .add(Attributes.ATTACK_DAMAGE, 4.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    @Override
    public void die(DamageSource cause) {
        TaintHelper.removeTaintSeed(level(), blockPosition());
        super.die(cause);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            if (!firstRun || tickCount % 1200 == 0) {
                TaintHelper.removeTaintSeed(level(), blockPosition());
                TaintHelper.addTaintSeed(level(), blockPosition());
                firstRun = true;
            }
            if (isAlive()) {
                boolean tickFlag = tickCount % 20 == 0;
                if (boost > 0 || tickFlag) {
                    float mod = (boost > 0) ? 1.0f : AuraHandler.getFluxSaturation(level(), blockPosition());
                    if (boost > 0) --boost;
                    if (mod <= 0.0f) {
                        if (level() instanceof net.minecraft.server.level.ServerLevel sl) {
                            hurtServer(sl, sl.damageSources().starve(), 0.5f);
                        }
                        AuraHelper.polluteAura(level(), blockPosition(), 0.1f, false);
                    } else {
                        TaintHelper.spreadFibres(level(), blockPosition().offset(Mth.randomBetweenInclusive(getRandom(), -getArea() * 3, getArea() * 3), Mth.randomBetweenInclusive(getRandom(), -getArea(), getArea()), Mth.randomBetweenInclusive(getRandom(), -getArea() * 3, getArea() * 3)), true);
                    }
                }
                if (tickFlag) {
                    if (getTarget() != null && distanceToSqr(getTarget()) < getArea() * 256 && getSensing().hasLineOfSight(getTarget())) {
                        spawnTentacles(getTarget());
                    }
                    List<LivingEntity> list = EntityUtils.getEntitiesInRange(level(), getX(), getY(), getZ(), this, LivingEntity.class, getArea() * 4);
                    for (LivingEntity elb : list) {
                        elb.addEffect(new MobEffectInstance(net.minecraft.core.Holder.direct(PotionFluxTaint.instance), 100, getArea() - 1, false, true));
                    }
                }
            }
        } else {
            if (attackAnim > 0.0f) attackAnim *= 0.75f;
            if (attackAnim < 0.001) attackAnim = 0.0f;
            float xx = Mth.sin(tickCount * 0.05f - 0.5f) / 5.0f;
            float zz = Mth.sin(tickCount * 0.06f - 0.5f) / 5.0f + hurtTime / 200.0f + attackAnim;
            if (getRandom().nextFloat() < 0.033) {
                FXDispatcher.INSTANCE.drawLightningFlash((float) getX() + xx, (float) getY() + getBbHeight() + 0.25f, (float) getZ() + zz, 0.7f, 0.1f, 0.9f, 0.5f, 1.5f + getRandom().nextFloat());
            } else {
                FXDispatcher.INSTANCE.drawTaintParticles((float) getX() + xx, (float) getY() + getBbHeight() + 0.25f, (float) getZ() + zz, (float) getRandom().nextGaussian() * 0.05f, 0.1f + 0.01f * getRandom().nextFloat(), (float) getRandom().nextGaussian() * 0.05f, 2.0f);
            }
        }
    }

    protected void spawnTentacles(Entity entity) {
        EntityTaintacleSmall taintlet = new EntityTaintacleSmall(level());
        taintlet.setPos(entity.getX() + level().getRandom().nextFloat() - level().getRandom().nextFloat(), entity.getY(), entity.getZ() + level().getRandom().nextFloat() - level().getRandom().nextFloat());
        level().addFreshEntity(taintlet);
        playSound(SoundsTC.tentacle, getSoundVolume(), getSoundPitch());
    }

    @Override
    public int getAmbientSoundInterval() {
        return 200;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CHORUS_FLOWER_DEATH;
    }

    protected float getSoundPitch() {
        return 1.3f - getBbHeight() / 10.0f;
    }

    @Override
    public float getSoundVolume() {
        return getBbHeight() / 8.0f;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return SoundsTC.tentacle;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsTC.tentacle;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void move(MoverType mt, Vec3 vec) {
        super.move(mt, new Vec3(0, Math.min(0, vec.y), 0));
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return false;
    }
}
