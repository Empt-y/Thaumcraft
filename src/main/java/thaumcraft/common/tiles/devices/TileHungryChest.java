package thaumcraft.common.tiles.devices;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.common.blocks.devices.BlockHungryChest;


public class TileHungryChest extends ChestBlockEntity
{
    public void checkForAdjacentChests() {
        if (!adjacentChestChecked) {
            adjacentChestChecked = true;
        }
    }
    
    public boolean canRenderBreaking() {
        return true;
    }
    
    public void closeInventory(Player player) {
        if (!player.isSpectator() && getBlockState().getBlock() instanceof BlockHungryChest) {
            --numPlayersUsing;
            getLevel().blockEvent(this.worldPosition, getBlockState().getBlock(), 1, numPlayersUsing);
            getLevel().updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
            getLevel().updateNeighborsAt(this.worldPosition.below(), getBlockState().getBlock());
        }
    }
    
    public boolean shouldRefresh(Level world, BlockPos pos, BlockState oldState, BlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
