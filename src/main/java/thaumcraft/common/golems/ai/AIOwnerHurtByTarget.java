package thaumcraft.common.golems.ai;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;


public class AIOwnerHurtByTarget extends TargetGoal
{
    EntityOwnedConstruct theDefendingTameable;
    LivingEntity theOwnerAttacker;
    private int timestamp;
    
    public AIOwnerHurtByTarget(EntityOwnedConstruct p_i1667_1_) {
        super(p_i1667_1_, false);
        theDefendingTameable = p_i1667_1_;
        setFlags(java.util.EnumSet.of(net.minecraft.world.entity.ai.goal.Goal.Flag.MOVE));
    }
    
    public boolean canUse() {
        if (!theDefendingTameable.isOwned()) {
            return false;
        }
        LivingEntity entitylivingbase = theDefendingTameable.getOwnerEntity();
        if (entitylivingbase == null) {
            return false;
        }
        theOwnerAttacker = entitylivingbase.getLastHurtByMob();
        int i = entitylivingbase.getLastHurtByMobTimestamp();
        return i != timestamp && theOwnerAttacker != null && mob.canAttack(theOwnerAttacker);
    }
    
    public void start() {
        mob.setTarget(theOwnerAttacker);
        LivingEntity entitylivingbase = theDefendingTameable.getOwnerEntity();
        if (entitylivingbase != null) {
            timestamp = entitylivingbase.getLastHurtByMobTimestamp();
        }
        super.start();
    }
}
