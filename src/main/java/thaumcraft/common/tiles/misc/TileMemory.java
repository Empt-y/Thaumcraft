package thaumcraft.common.tiles.misc;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;


public class TileMemory extends BlockEntity
{
    public BlockState oldblock;
    public CompoundTag tileEntityCompound;
    
    public TileMemory(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        oldblock = Blocks.AIR.defaultBlockState();
    }
    
    public TileMemory(BlockState bi) {
        oldblock = Blocks.AIR.defaultBlockState();
        oldblock = bi;
    }
    
    public void loadAdditional(CompoundTag nbttagcompound) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
        Block b = net.minecraft.core.registries.BuiltInRegistries.BLOCK.byId(nbttagcompound.getIntOr("oldblock", 0));
        int meta = nbttagcompound.getIntOr("oldmeta", 0);
        oldblock = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState() /* getStateFromMeta removed */;
    }
    
    public CompoundTag saveAdditional(CompoundTag nbttagcompound) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
        nbttagcompound.putInt("oldblock", net.minecraft.core.registries.BuiltInRegistries.BLOCK.getId(oldblock.getBlock()));
        nbttagcompound.putInt("oldmeta", oldblock.getBlock());
        return nbttagcompound;
    }
}
