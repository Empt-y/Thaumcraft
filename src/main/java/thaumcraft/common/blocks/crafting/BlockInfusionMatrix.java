package thaumcraft.common.blocks.crafting;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;


public class BlockInfusionMatrix extends BlockTCDevice
{
    public BlockInfusionMatrix() {
        super(null /*  null   Material removed    */, TileInfusionMatrix.class, "infusion_matrix");
        setSoundType(SoundType.STONE);
    }
    
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }
    
    public boolean isFullCube(BlockState state) {
        return false;
    }
    
    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null; // Object /* BlockFaceShape removed */ removed
    }
    
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
