package thaumcraft.common.golems.ai;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.util.Mth;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.tasks.TaskHandler;


public class AIGotoEntity extends AIGoto
{
    public AIGotoEntity(EntityThaumcraftGolem g) {
        super(g, (byte)1);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (golem.getLookControl() != null && golem.getTask() != null && golem.getTask().getEntity() != null) {
            golem.getLookControl().setLookAt(golem.getTask().getEntity(), 10.0f, (float) 40);
        }
    }
    
    @Override
    protected void moveTo() {
        if (golem.getNavigation() != null && golem.getTask() != null && golem.getTask().getEntity() != null) {
            golem.getNavigation().moveTo(golem.getTask().getEntity(), golem.getGolemMoveSpeed());
        }
    }
    
    @Override
    protected boolean findDestination() {
        ArrayList<Task> list = TaskHandler.getEntityTasksSorted((golem.level() instanceof net.minecraft.server.level.ServerLevel ? ((golem.level() instanceof net.minecraft.server.level.ServerLevel) ? ((net.minecraft.server.level.ServerLevel)golem.level()).dimension().identifier().hashCode() : 0) : 0), golem.getUUID(), golem);
        for (Task ticket : list) {
            if (areGolemTagsValidForTask(ticket) && ticket.canGolemPerformTask(golem) && golem.isWithinHome(ticket.getEntity().blockPosition()) && isValidDestination(golem.level(), ticket.getEntity().blockPosition()) && canEasilyReach(ticket.getEntity())) {
                golem.setTask(ticket);
                golem.getTask().setReserved(true);
                minDist = 3.5 + golem.getTask().getEntity().getBbWidth() / 2.0f * (golem.getTask().getEntity().getBbWidth() / 2.0f);
                if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                    golem.level().broadcastEntityEvent(golem, (byte)5);
                }
                return true;
            }
        }
        return false;
    }
    
    private boolean canEasilyReach(Entity e) {
        if (golem.distanceToSqr(e.getX() + 0.5, e.getY() + 0.5, e.getZ() + 0.5) < minDist) {
            return true;
        }
        Path pathentity = golem.getNavigation().createPath(e);
        if (pathentity == null) {
            return false;
        }
        Node pathpoint = pathentity.getFinalPathPoint();
        if (pathpoint == null) {
            return false;
        }
        int i = pathpoint.x - Mth.floor(e.getX());
        int j = pathpoint.z - Mth.floor(e.getZ());
        return i * i + j * j < minDist;
    }
}
