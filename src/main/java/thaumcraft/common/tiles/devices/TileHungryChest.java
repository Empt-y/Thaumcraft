package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blocks.devices.BlockHungryChest;


public class TileHungryChest extends ChestBlockEntity
{
    public TileHungryChest(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public TileHungryChest(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public boolean canRenderBreaking() {
        return true;
    }

    public boolean shouldRefresh(Level world, BlockPos pos, BlockState oldState, BlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
