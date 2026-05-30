package thaumcraft.common.entities.ai.combat;
import net.minecraft.world.entity.TamableAnimal;
import java.util.Iterator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.phys.AABB;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import net.minecraft.world.entity.animal.Animal;


public class AICultistHurtByTarget extends TargetGoal
{
    boolean entityCallsForHelp;
    private int revengeTimerOld;
    
    public AICultistHurtByTarget(PathfinderMob owner, boolean callsHelp) {
        super(owner, false);
        entityCallsForHelp = callsHelp;
        // FIXME: setMutexBits removed - use setFlags(EnumSet.of(Goal.Flag.MOVE))
    }
    
    @Override
        public boolean canUse() {
        int i = mob.getLastHurtByMobTimestamp();
        LivingEntity entitylivingbase = mob.getLastHurtByMob();
        return i != revengeTimerOld && entitylivingbase != null && canAttack(entitylivingbase, net.minecraft.world.entity.ai.targeting.TargetingConditions.DEFAULT);
    }
    
    @Override
        public void start() {
        mob.setTarget(mob.getLastHurtByMob());
        ;
        revengeTimerOld = mob.getLastHurtByMobTimestamp();
        // FIXME: TargetGoal no longer has setUnseenMemoryTicks - use constructor
        if (entityCallsForHelp) {
            alertOthers();
        }
        super.start();
    }
    
    protected void alertOthers() {
        double d0 = 16.0;
        for (PathfinderMob entitycreature : mob.level().getEntitiesOfClass(EntityCultist.class, new AABB(mob.getX(), mob.getY(), mob.getZ(), mob.getX() + 1.0, mob.getY() + 1.0, mob.getZ() + 1.0).inflate(d0, 10.0, d0))) {
            if (mob != entitycreature && entitycreature.getTarget() == null && (!(mob instanceof TamableAnimal) || ((TamableAnimal) mob).getOwner() == ((TamableAnimal)entitycreature).getOwner()) && !entitycreature.isAlliedTo(mob.getLastHurtByMob())) {
                setEntityAttackTarget(entitycreature, mob.getLastHurtByMob());
            }
        }
    }
    
    protected void setEntityAttackTarget(PathfinderMob creatureIn, LivingEntity entityLivingBaseIn) {
        creatureIn.setTarget(entityLivingBaseIn);
    }
}
