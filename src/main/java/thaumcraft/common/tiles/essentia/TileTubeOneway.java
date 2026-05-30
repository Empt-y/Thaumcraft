package thaumcraft.common.tiles.essentia;
import thaumcraft.api.aspects.Aspect;


public class TileTubeOneway extends TileTube
{
    public TileTubeOneway(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
    }

    @Override
    void calculateSuction(Aspect filter, boolean restrict, boolean directional) {
        super.calculateSuction(filter, restrict, true);
    }
    
    @Override
    void equalizeWithNeighbours(boolean directional) {
        super.equalizeWithNeighbours(true);
    }
}
