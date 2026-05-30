package thaumcraft.common.golems.parts;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.sounds.SoundEvents;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.common.entities.projectile.EntityGolemDart;
import thaumcraft.common.golems.ai.AIArrowAttack;


public class GolemArmDart implements GolemArm.IArmFunction
{
    @Override
    public void onMeleeAttack(IGolemAPI golem, Entity target) {
    }
    
    @Override
    public void onRangedAttack(IGolemAPI golem, LivingEntity target, float range) {
        EntityGolemDart entityarrow = new EntityGolemDart(golem.getGolemWorld(), golem.getGolemEntity());
        float dmg = (float)golem.getGolemEntity().getAttribute(Attributes.ATTACK_DAMAGE).getValue() / 3.0f;
        entityarrow.setDamage(dmg + range + golem.getGolemWorld().getRandom().nextGaussian() * 0.25);
        double d0 = target.getX() - golem.getGolemEntity().getX();
        double d2 = target.getBoundingBox().minY + target.getEyeHeight() + range * range - entityarrow.getY();
        double d3 = target.getZ() - golem.getGolemEntity().getZ();
        entityarrow.shoot(d0, d2, d3, 1.6f, 3.0f);
        golem.getGolemWorld().spawnEntity(entityarrow);
        golem.getGolemEntity().playSound(SoundEvents.ARROW_SHOOT, 1.0f, 1.0f / (golem.getGolemWorld().getRandom().nextFloat() * 0.4f + 0.8f));
    }
    
    @Override
    public RangedAttackGoal getRangedAttackAI(RangedAttackMob golem) {
        return new AIArrowAttack(golem, 1.0, 20, 25, 16.0f);
    }
    
    @Override
    public void onUpdateTick(IGolemAPI golem) {
    }
}
