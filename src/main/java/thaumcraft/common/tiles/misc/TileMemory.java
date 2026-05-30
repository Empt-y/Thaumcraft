package thaumcraft.common.tiles.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


public class TileMemory extends BlockEntity
{
    public BlockState oldblock;
    public CompoundTag tileEntityCompound;

    public TileMemory(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        oldblock = Blocks.AIR.defaultBlockState();
    }

    public TileMemory(BlockState bi) {
        super(net.minecraft.world.level.block.entity.BlockEntityType.SIGN, BlockPos.ZERO, bi);
        oldblock = bi;
    }

    public void loadAdditional(CompoundTag nbttagcompound) {
        Block b = net.minecraft.core.registries.BuiltInRegistries.BLOCK.byId(nbttagcompound.getIntOr("oldblock", 0));
        oldblock = b.defaultBlockState();
    }

    public CompoundTag saveAdditional(CompoundTag nbttagcompound) {
        nbttagcompound.putInt("oldblock", net.minecraft.core.registries.BuiltInRegistries.BLOCK.getId(oldblock.getBlock()));
        return nbttagcompound;
    }
}
