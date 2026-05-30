package thaumcraft.common.entities.ai.combat;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;


public class AILongRangeAttack extends RangedAttackGoal
{
    private Mob wielder;
    double minDistance;
    
    public AILongRangeAttack(RangedAttackMob par1IRangedAttackMob, double min, double p_i1650_2_, int p_i1650_4_, int p_i1650_5_, float p_i1650_6_) {
        super(new net.minecraft.world.item.Item.Properties());
        minDistance = 0.0;
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
