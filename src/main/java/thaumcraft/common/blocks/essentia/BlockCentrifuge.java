package thaumcraft.common.blocks.essentia;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.tiles.essentia.TileCentrifuge;


public class BlockCentrifuge extends BlockTCDevice
{
    public BlockCentrifuge() {
        super(null /*  null   Material removed    */, TileCentrifuge.class, "centrifuge");
        setSoundType(SoundType.WOOD);
    }
    
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }
    
    public boolean isFullCube(BlockState state) {
        return false;
    }
    
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
    
    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null; // Object /* BlockFaceShape removed */ removed
    }
}
