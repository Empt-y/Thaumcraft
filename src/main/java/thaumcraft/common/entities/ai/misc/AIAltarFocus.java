package thaumcraft.common.entities.ai.misc;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;

public class AIAltarFocus extends Goal
{
    private final EntityCultistCleric entity;
    private final Level world;

    public AIAltarFocus(EntityCultistCleric par1Mob) {
        entity = par1Mob;
        world = par1Mob.level();
    }

    @Override
    public boolean canUse() {
        return entity.getIsRitualist() && entity.getRestrictCenter() != null;
    }

    @Override
    public void start() {}

    @Override
    public void stop() {}

    @Override
    public boolean canContinueToUse() {
        return entity.getIsRitualist() && entity.getRestrictCenter() != null;
    }

    @Override
    public void tick() {
        if (entity.getRestrictCenter() != null && entity.tickCount % 40 == 0
                && (entity.getRestrictCenter().distSqr(entity.blockPosition()) > 16.0
                        || entity.level().getBlockState(entity.getRestrictCenter()).getBlock() != BlocksTC.eldritch)) {
            entity.setIsRitualist(false);
        }
    }
}
