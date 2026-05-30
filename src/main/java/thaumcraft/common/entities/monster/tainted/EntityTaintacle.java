package thaumcraft.common.entities.monster.tainted;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.world.biomes.BiomeHandler;


public class EntityTaintacle extends Monster implements ITaintedMob
{
    public float flailIntensity;

    public EntityTaintacle(net.minecraft.world.entity.EntityType<? extends EntityTaintacle> type, Level world) {
        super(type, world);
        flailIntensity = 1.0f;
        xpReward = 8;
    }

    @SuppressWarnings("unchecked")
    public EntityTaintacle(Level world) {
        super(null, world);
        // Entity requires EntityType; use factory method
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 50.0)
            .add(Attributes.ATTACK_DAMAGE, 7.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, false));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0f));
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean canAttack(net.minecraft.world.entity.LivingEntity entity) {
        return !ITaintedMob.class.isAssignableFrom(entity.getClass());
    }

    @Override
    public void move(MoverType mt, Vec3 vec) {
        super.move(mt, new Vec3(0, Math.min(0, vec.y), 0));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && tickCount % 20 == 0) {
            // TODO: taint block check - ThaumcraftMaterials removed
            if (!(this instanceof EntityTaintacleSmall) && tickCount % 40 == 0 && getTarget() != null && distanceToSqr(getTarget()) > 16.0 && distanceToSqr(getTarget()) < 256.0 && getSensing().hasLineOfSight(getTarget())) {
                spawnTentacles(getTarget());
            }
        }
        if (level().isClientSide()) {
            if (flailIntensity > 1.0f) flailIntensity -= 0.01f;
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
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte event) {
        if (event == 16) {
            flailIntensity = 3.0f;
        } else {
            super.handleEntityEvent(event);
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.server.level.ServerLevel sl, Entity target) {
        level().broadcastEntityEvent(this, (byte) 16);
        playSound(SoundsTC.tentacle, getSoundVolume(), getSoundPitch());
        return super.doHurtTarget(sl, target);
    }
}
