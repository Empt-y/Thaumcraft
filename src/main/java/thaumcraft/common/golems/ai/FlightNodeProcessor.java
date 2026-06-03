package thaumcraft.common.golems.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.Target;


public class FlightNodeProcessor extends NodeEvaluator {

    @Override
    public Node getStart() {
        return this.getNode(mob.blockPosition());
    }

    @Override
    public Target getTarget(double x, double y, double z) {
        return this.getTargetNodeAt(x, y, z);
    }

    @Override
    public int getNeighbors(Node[] neighbors, Node node) {
        int count = 0;
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            Node n = getNode(node.x + dir.getStepX(), node.y + dir.getStepY(), node.z + dir.getStepZ());
            if (n != null && !n.closed) {
                neighbors[count++] = n;
            }
        }
        return count;
    }

    @Override
    public PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) {
        return PathType.OPEN;
    }

    @Override
    public PathType getPathType(PathfindingContext context, int x, int y, int z) {
        return PathType.OPEN;
    }
}
