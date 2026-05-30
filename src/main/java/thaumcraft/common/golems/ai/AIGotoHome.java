package thaumcraft.common.golems.ai;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;


public class AIGotoHome extends Goal
{
    protected EntityThaumcraftGolem golem;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    protected int idleCounter;
    
    public AIGotoHome(EntityThaumcraftGolem g) {
        idleCounter = 10;
        golem = g;
        setFlags(java.util.EnumSet.of(net.minecraft.world.entity.ai.goal.Goal.Flag.MOVE, net.minecraft.world.entity.ai.goal.Goal.Flag.JUMP));
    }
    
    public boolean canUse() {
        if (idleCounter > 0) {
            --idleCounter;
            return false;
        }
        idleCounter = 50;
        double dd = golem.distanceToSqr(golem.getHomePosition().getX() + 0.5, golem.getHomePosition().getY() + 0.5, golem.getHomePosition().getZ() + 0.5);
        if (dd < 5.0) {
            return false;
        }
        if (dd <= 1024.0) {
            movePosX = golem.getHomePosition().getX();
            movePosY = golem.getHomePosition().getY();
            movePosZ = golem.getHomePosition().getZ();
            return true;
        }
        Vec3 vec3 = null; /* DefaultRandomPos call removed */
        if (vec3 == null) {
            return false;
        }
        movePosX = vec3.x;
        movePosY = vec3.y;
        movePosZ = vec3.z;
        return true;
    }
    
    public void start() {
        golem.getNavigation().moveTo(movePosX, movePosY, movePosZ, golem.getGolemMoveSpeed());
    }
    
    public boolean canContinueToUse() {
        return golem.getTask() == null && !golem.getNavigation().isDone() && golem.distanceToSqr(golem.getHomePosition().getX() + 0.5, golem.getHomePosition().getY() + 0.5, golem.getHomePosition().getZ() + 0.5) > 3.0;
    }
    
    public void stop() {
        idleCounter = 50;
        golem.getNavigation().stop();
    }
}
