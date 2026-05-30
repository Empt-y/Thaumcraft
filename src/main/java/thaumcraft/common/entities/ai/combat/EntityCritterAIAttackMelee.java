package thaumcraft.common.entities.ai.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class EntityCritterAIAttackMelee extends MeleeAttackGoal
{
    public EntityCritterAIAttackMelee(PathfinderMob creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (canPerformAttack(target)) {
            resetAttackCooldown();
            if (mob.level() instanceof net.minecraft.server.level.ServerLevel sl) {
                mob.doHurtTarget(sl, target, creature);
            }
        }
    }
}
