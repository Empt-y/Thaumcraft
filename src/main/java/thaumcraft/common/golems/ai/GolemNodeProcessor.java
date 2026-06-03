package thaumcraft.common.golems.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.Target;


public class GolemNodeProcessor extends NodeEvaluator {

    @Override
    public void prepare(PathNavigationRegion level, Mob mob) {
        super.prepare(level, mob);
    }

    @Override
    public void done() {
        super.done();
    }

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
        // Cardinal directions: check same level, then step-up, then fall
        int[][] dirs = {{0, 0, 1}, {0, 0, -1}, {1, 0, 0}, {-1, 0, 0}};
        for (int[] d : dirs) {
            Node n = findGroundNode(node.x + d[0], node.y, node.z + d[2]);
            if (n != null && !n.closed) {
                neighbors[count++] = n;
            }
        }
        return count;
    }

    private Node findGroundNode(int x, int y, int z) {
        PathType type = getPathType(currentContext, x, y, z);
        if (type != PathType.BLOCKED) {
            return getNode(x, y, z);
        }
        // Try stepping up one block
        type = getPathType(currentContext, x, y + 1, z);
        if (type != PathType.BLOCKED) {
            return getNode(x, y + 1, z);
        }
        // Try falling up to 4 blocks
        for (int fallY = y - 1; fallY >= y - 4; fallY--) {
            type = getPathType(currentContext, x, fallY, z);
            if (type != PathType.BLOCKED) {
                return getNode(x, fallY, z);
            }
        }
        return null;
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
