package thaumcraft.common.golems.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;


public class AINearestValidTarget<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    public AINearestValidTarget(Mob mob, Class<T> targetClass, boolean mustSee) {
        super(mob, targetClass, mustSee);
    }

    public AINearestValidTarget(Mob mob, Class<T> targetClass, boolean mustSee, boolean mustReach) {
        super(mob, targetClass, mustSee);
    }
}
