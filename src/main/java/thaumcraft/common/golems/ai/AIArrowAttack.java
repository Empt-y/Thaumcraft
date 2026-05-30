package thaumcraft.common.golems.ai;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;


public class AIArrowAttack extends RangedAttackGoal
{
    private Mob entityHost;
    private RangedAttackMob rangedAttackEntityHost;
    private int rangedAttackTime;
    private double entityMoveSpeed;
    private int seeTime;
    private int attackIntervalMin;
    private int maxRangedAttackTime;
    private float attackRadius;
    private float maxAttackDistance;
    
    public AIArrowAttack(RangedAttackMob attacker, double movespeed, int p_i1650_4_, int maxAttackTime, float maxAttackDistanceIn) {
        super(attacker, movespeed, p_i1650_4_, maxAttackDistanceIn);
        rangedAttackTime = -1;
        if (!(attacker instanceof LivingEntity)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        rangedAttackEntityHost = attacker;
        entityHost = (Mob)attacker;
        entityMoveSpeed = movespeed;
        attackIntervalMin = p_i1650_4_;
        maxRangedAttackTime = maxAttackTime;
        attackRadius = maxAttackDistanceIn;
        maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        setFlags(java.util.EnumSet.of(net.minecraft.world.entity.ai.goal.Goal.Flag.MOVE, net.minecraft.world.entity.ai.goal.Goal.Flag.LOOK));
    }
    
    public boolean canUse() {
        return entityHost.getTarget() != null;
    }
    
    public boolean canContinueToUse() {
        return canUse() || !entityHost.getNavigation().isDone();
    }
    
    public void stop() {
        seeTime = 0;
        rangedAttackTime = -1;
    }
    
    @Override
    public void tick() {
        if (entityHost.getTarget() == null) {
            return;
        }
        double d0 = entityHost.distanceToSqr(entityHost.getTarget().getX(), entityHost.getTarget().getBoundingBox().minY, entityHost.getTarget().getZ());
        boolean flag = entityHost.getSensing().hasLineOfSight(entityHost.getTarget());
        if (flag) {
            ++seeTime;
        }
        else {
            seeTime = 0;
        }
        if (d0 <= maxAttackDistance && seeTime >= 20) {
            entityHost.getNavigation().stop();
        }
        else {
            entityHost.getNavigation().moveTo(entityHost.getTarget(), entityMoveSpeed);
        }
        entityHost.getLookControl().setLookAt(entityHost.getTarget(), 10.0f, 30.0f);
        int rangedAttackTime = this.rangedAttackTime - 1;
        this.rangedAttackTime = rangedAttackTime;
        if (rangedAttackTime == 0) {
            if (d0 > maxAttackDistance || !flag) {
                return;
            }
            float f = Mth.sqrt((float)d0) / attackRadius;
            float lvt_5_1_ = Mth.clamp(f, 0.1f, 1.0f);
            rangedAttackEntityHost.performRangedAttack(entityHost.getTarget(), lvt_5_1_);
            this.rangedAttackTime = Mth.floor(f * (maxRangedAttackTime - attackIntervalMin) + attackIntervalMin);
        }
        else if (this.rangedAttackTime < 0) {
            float f2 = Mth.sqrt((float)d0) / attackRadius;
            this.rangedAttackTime = Mth.floor(f2 * (maxRangedAttackTime - attackIntervalMin) + attackIntervalMin);
        }
    }
}
