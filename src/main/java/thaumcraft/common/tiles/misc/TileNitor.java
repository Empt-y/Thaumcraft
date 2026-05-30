package thaumcraft.common.tiles.misc;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.client.fx.FXDispatcher;


public class TileNitor extends BlockEntity 
{
    int count;
    
    public TileNitor(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        count = 0;
    }
    
    public boolean shouldRefresh(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
    
    public void update() {
        if (getLevel().isClientSide()) {
            BlockState state = getLevel().getBlockState(getBlockPos());
            FXDispatcher.INSTANCE.drawNitorFlames(this.worldPosition.getX() + 0.5f + this.level.getRandom().nextGaussian() * 0.025, this.worldPosition.getY() + 0.45f + this.level.getRandom().nextGaussian() * 0.025, this.worldPosition.getZ() + 0.5f + this.level.getRandom().nextGaussian() * 0.025, this.level.getRandom().nextGaussian() * 0.0025, this.level.getRandom().nextFloat() * 0.06, this.level.getRandom().nextGaussian() * 0.0025, state.getMapColor(level, getBlockPos()).colorValue, 0);
            if (count++ % 10 == 0) {
                FXDispatcher.INSTANCE.drawNitorCore(this.worldPosition.getX() + 0.5f, this.worldPosition.getY() + 0.49f, this.worldPosition.getZ() + 0.5f, 0.0, 0.0, 0.0);
            }
        }
    }
}
