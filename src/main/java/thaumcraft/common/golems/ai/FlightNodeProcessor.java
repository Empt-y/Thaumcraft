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
        return 0; // FIXME: stub
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
