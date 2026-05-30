package thaumcraft.common.entities.ai.pech;
import net.minecraft.world.entity.ai.goal.Goal;
import thaumcraft.common.entities.monster.EntityPech;


public class AIPechTradePlayer extends Goal
{
    private EntityPech villager;
    
    public AIPechTradePlayer(EntityPech par1EntityVillager) {
        villager = par1EntityVillager;
        // FIXME: setMutexBits removed - use setFlags(EnumSet.of(Goal.Flag.MOVE))
    }
    
    @Override
        public boolean canUse() {
        return villager.isAlive() && !villager.isInWater() && villager.isTamed() && villager.onGround() && villager.trading;
    }
    
    @Override
        public void start() {
        villager.getNavigation().stop();
    }
    
    @Override
        public void stop() {
        villager.trading = false;
    }
}
