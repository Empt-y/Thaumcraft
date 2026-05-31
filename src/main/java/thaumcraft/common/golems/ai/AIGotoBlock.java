package thaumcraft.common.golems.ai;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.tasks.TaskHandler;


public class AIGotoBlock extends AIGoto
{
    public AIGotoBlock(EntityThaumcraftGolem g) {
        super(g, (byte)0);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (golem.getLookControl() != null) {
            golem.getLookControl().setLookAt(golem.getTask().getPos().getX() + 0.5, golem.getTask().getPos().getY() + 0.5, golem.getTask().getPos().getZ() + 0.5, 10.0f, (float) 40);
        }
    }
    
    @Override
    protected void moveTo() {
        if (targetBlock != null) {
            golem.getNavigation().moveTo(targetBlock.getX() + 0.5, targetBlock.getY() + 0.5, targetBlock.getZ() + 0.5, golem.getGolemMoveSpeed());
        }
        else {
            golem.getNavigation().moveTo(golem.getTask().getPos().getX() + 0.5, golem.getTask().getPos().getY() + 0.5, golem.getTask().getPos().getZ() + 0.5, golem.getGolemMoveSpeed());
        }
    }
    
    @Override
    protected boolean findDestination() {
        ArrayList<Task> list = TaskHandler.getBlockTasksSorted((golem.level() instanceof net.minecraft.server.level.ServerLevel ? ((golem.level() instanceof net.minecraft.server.level.ServerLevel) ? ((net.minecraft.server.level.ServerLevel)golem.level()).dimension().identifier().hashCode() : 0) : 0), golem.getUUID(), golem);
        for (Task ticket : list) {
            if (areGolemTagsValidForTask(ticket) && ticket.canGolemPerformTask(golem) && golem.isWithinHome(ticket.getPos()) && isValidDestination(golem.level(), ticket.getPos()) && canEasilyReach(ticket.getPos())) {
                targetBlock = getAdjacentSpace(ticket.getPos());
                golem.setTask(ticket);
                golem.getTask().setReserved(true);
                if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                    golem.level().broadcastEntityEvent(golem, (byte)5);
                }
                return true;
            }
        }
        return false;
    }
    
    private BlockPos getAdjacentSpace(BlockPos pos) {
        double d = Double.MAX_VALUE;
        BlockPos closest = null;
        for (Direction face : Direction.Plane.HORIZONTAL) {
            BlockState block = golem.level().getBlockState(pos.relative(face));
            if (!block.isSolid()) {
                double dist = pos.relative(face).getCenter().distanceTo(golem.position());
                if (dist < d) {
                    closest = pos.relative(face);
                    d = dist;
                }
            }
        }
        return closest;
    }
    
    private boolean canEasilyReach(BlockPos pos) {
        if (golem.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < minDist) {
            return true;
        }
        Path pathentity = golem.getNavigation().createPath(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1);
        if (pathentity == null) {
            return false;
        }
        Node pathpoint = pathentity.getEndNode();
        if (pathpoint == null) {
            return false;
        }
        int i = pathpoint.x - Mth.floor((float)pos.getX());
        int j = pathpoint.z - Mth.floor((float)pos.getZ());
        int k = pathpoint.y - Mth.floor((float)pos.getY());
        if (i == 0 && j == 0 && k == 2) {
            --k;
        }
        return i * i + j * j + k * k < 2.25;
    }
}
