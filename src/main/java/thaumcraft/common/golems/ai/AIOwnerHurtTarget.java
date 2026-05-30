package thaumcraft.common.golems.ai;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;


public class AIOwnerHurtTarget extends TargetGoal
{
    EntityOwnedConstruct theEntityTameable;
    LivingEntity theTarget;
    private int timestamp;
    
    public AIOwnerHurtTarget(EntityOwnedConstruct p_i1668_1_) {
        super(p_i1668_1_, false);
        theEntityTameable = p_i1668_1_;
        setFlags(java.util.EnumSet.of(net.minecraft.world.entity.ai.goal.Goal.Flag.MOVE));
    }
    
    public boolean canUse() {
        if (!theEntityTameable.isOwned()) {
            return false;
        }
        LivingEntity entitylivingbase = theEntityTameable.getOwnerEntity();
        if (entitylivingbase == null) {
            return false;
        }
        theTarget = entitylivingbase.getLastAttackedEntity();
        int i = entitylivingbase.getLastAttackedEntityTime();
        return i != timestamp && isSuitableTarget(theTarget, false);
    }
    
    public void start() {
        mob.setTarget(theTarget);
        LivingEntity entitylivingbase = theEntityTameable.getOwnerEntity();
        if (entitylivingbase != null) {
            timestamp = entitylivingbase.getLastAttackedEntityTime();
        }
        super.startExecuting();
    }
}
