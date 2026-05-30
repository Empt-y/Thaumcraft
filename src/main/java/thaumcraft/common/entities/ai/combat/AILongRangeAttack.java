package thaumcraft.common.entities.ai.combat;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;


public class AILongRangeAttack extends RangedAttackGoal
{
    private Mob wielder;
    double minDistance;
    
    public AILongRangeAttack(RangedAttackMob par1IRangedAttackMob, double min, double speedMod, int intervalMin, int intervalMax, float attackRadius) {
        super(par1IRangedAttackMob, speedMod, intervalMin, intervalMax, attackRadius);
        minDistance = min;
        wielder = (Mob)par1IRangedAttackMob;
    }
    
    @Override
        public boolean canUse() {
        boolean ex = super.canUse();
        if (ex) {
            LivingEntity var1 = wielder.getTarget();
            if (var1 == null) {
                return false;
            }
            if (var1.isDeadOrDying()) {
                wielder.setTarget(null);
                return false;
            }
            double ra = wielder.distanceToSqr(var1.getX(), var1.getBoundingBox().minY, var1.getZ());
            if (ra < minDistance * minDistance) {
                return false;
            }
        }
        return ex;
    }
}
