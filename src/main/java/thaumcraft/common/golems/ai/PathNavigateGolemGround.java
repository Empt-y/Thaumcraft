package thaumcraft.common.golems.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;


public class PathNavigateGolemGround extends GroundPathNavigation {

    public PathNavigateGolemGround(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new GolemNodeProcessor();
        return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
    }
}
