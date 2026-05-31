package thaumcraft.common.entities.monster.tainted;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.blocks.world.taint.BlockTaintFibre;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.utils.BlockUtils;


public class EntityTaintCrawler extends Monster implements ITaintedMob
{
    BlockPos lastPos;
    
    @SuppressWarnings("unchecked")
    public EntityTaintCrawler(Level par1World) {
        super(null, par1World);
        // Entity requires EntityType; use factory method
    }

    public EntityTaintCrawler(net.minecraft.world.entity.EntityType<? extends EntityTaintCrawler> type, Level par1World) {
        super(type, par1World);
        lastPos = new BlockPos(0, 0, 0);
        xpReward = 3;
    }
    
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    
    @Override
    public boolean canAttack(LivingEntity entity) {
        return !ITaintedMob.class.isAssignableFrom(entity.getClass());
    }

    // isOnSameTeam(Entity) is final in Entity - omit override
    
    // getEyeHeight() is final in Entity - not overridable
    
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
        .add(Attributes.MAX_HEALTH, 8.0) .add(Attributes.MOVEMENT_SPEED, 0.275) .add(Attributes.ATTACK_DAMAGE, 2.0);
    }
    
    protected float getSoundPitch() {
        return 0.7f;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SILVERFISH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.SILVERFISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SILVERFISH_DEATH;
    }

    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.Block block) {
        playSound(SoundEvents.SILVERFISH_STEP, 0.15f, 1.0f);
    }
    
    public boolean isIgnoringBlockTriggers() { return true; }
    
    @Override
    public void tick() {
        if (!level().isClientSide() && isAlive() && tickCount % 40 == 0 && !lastPos.equals(blockPosition())) {
            lastPos = blockPosition();
            // TODO: taint fibre spreading - ThaumcraftMaterials removed
            if (level().isEmptyBlock(blockPosition()) && BlockUtils.isAdjacentToSolidBlock(level(), blockPosition())) {
                level().setBlock(blockPosition(), BlocksTC.taintFibre.defaultBlockState(), 3);
            }
        }
        super.tick();
    }
    
    protected boolean checkAmbientSpawningRequirements() {
        return true;
    }
    
    public Object /* MobType removed */ getCreatureAttribute() {
        return null; // MobType removed
    }
    
    protected Item getDropItem() {
        return net.minecraft.world.item.Items.AIR;
    }
    
    protected void dropFewItems(boolean flag, int i) {
        if (level().getRandom().nextInt(8) == 0) {
            net.minecraft.world.entity.item.ItemEntity drop = new net.minecraft.world.entity.item.ItemEntity(level(), getX(), getY(), getZ(), ConfigItems.FLUX_CRYSTAL.copy());
            level().addFreshEntity(drop);
        }
    }
    
    public SpawnGroupData finalizeSpawn(DifficultyInstance p_180482_1_, SpawnGroupData p_180482_2_) {
        return p_180482_2_;
    }
    
    @Override
    public boolean doHurtTarget(net.minecraft.server.level.ServerLevel sl, Entity victim) {
        if (super.doHurtTarget(sl, victim)) {
            if (victim instanceof LivingEntity lv) {
                byte b0 = 0;
                if (level().getDifficulty() == Difficulty.NORMAL) b0 = 3;
                else if (level().getDifficulty() == Difficulty.HARD) b0 = 6;
                if (b0 > 0 && getRandom().nextInt(b0 + 1) > 2) {
                    lv.addEffect(new MobEffectInstance(net.minecraft.core.Holder.direct(PotionFluxTaint.instance), b0 * 20, 0));
                }
            }
            return true;
        }
        return false;
    }
}
