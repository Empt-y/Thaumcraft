package thaumcraft.common.golems.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;


public class PathNavigateGolemAir extends FlyingPathNavigation {

    public PathNavigateGolemAir(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new FlightNodeProcessor();
        return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
    }
}
