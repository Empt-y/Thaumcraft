package thaumcraft.common.tiles.essentia;
import thaumcraft.api.aspects.Aspect;


public class TileTubeRestrict extends TileTube
{
    public TileTubeRestrict(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
    }

    @Override
    void calculateSuction(Aspect filter, boolean restrict, boolean dir) {
        super.calculateSuction(filter, true, dir);
    }
}
