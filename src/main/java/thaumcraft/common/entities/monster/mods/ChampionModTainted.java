package thaumcraft.common.entities.monster.mods;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.ai.combat.EntityCritterAIAttackMelee;


public class ChampionModTainted implements IChampionModifierEffect
{
    @Override
    public float performEffect(LivingEntity boss, LivingEntity target, DamageSource source, float amount) {
        resetAI((PathfinderMob) boss);
        return amount;
    }

    public static void resetAI(PathfinderMob critter) {
        if (!(critter instanceof Monster)) {
            try {
                critter.goalSelector.removeAllGoals(g -> true);
                critter.targetSelector.removeAllGoals(g -> true);
                critter.goalSelector.addGoal(0, new FloatGoal(critter));
                critter.goalSelector.addGoal(2, new EntityCritterAIAttackMelee(critter, 1.2, false));
                critter.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(critter, 1.0));
                critter.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(critter, 1.0));
                critter.goalSelector.addGoal(8, new LookAtPlayerGoal(critter, Player.class, 8.0f));
                critter.goalSelector.addGoal(8, new RandomLookAroundGoal(critter));
                critter.targetSelector.addGoal(1, new HurtByTargetGoal(critter));
                critter.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(critter, Player.class, true));
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void preRender(LivingEntity boss, Object renderLivingBase) {
        // TODO: taint layer rendering
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void showFX(LivingEntity boss) {
        float w = boss.level().getRandom().nextFloat() * boss.getBbWidth();
        float d = boss.level().getRandom().nextFloat() * boss.getBbWidth();
        float h = boss.level().getRandom().nextFloat() * boss.getBbHeight();
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getBoundingBox().minX + w, boss.getBoundingBox().minY + h, boss.getBoundingBox().minZ + d, 0.0, -0.01, 0.0, 0.1f + boss.level().getRandom().nextFloat() * 0.2f, 0.0f, 0.1f + boss.level().getRandom().nextFloat() * 0.1f, 0.25f, false, 1, 5, 1, 6 + boss.level().getRandom().nextInt(6), 0, 2.0f + boss.level().getRandom().nextFloat(), 0.5f, 1);
    }
}
