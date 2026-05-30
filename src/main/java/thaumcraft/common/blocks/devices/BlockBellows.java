package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.tiles.devices.TileBellows;


public class BlockBellows extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    public BlockBellows() {
        super(null /*  null   Material removed    */, TileBellows.class, "bellows");
        setSoundType(SoundType.WOOD);
        setHardness(1.0f);
    }

    public int damageDropped(BlockState state) {
        return 0;
    }

    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null;
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

    @Override
    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        BlockState bs = defaultBlockState();
        if (this instanceof IBlockFacing) {
            bs = bs.setValue((Property)IBlockFacing.FACING, facing.getOpposite());
        }
        if (this instanceof IBlockEnabled) {
            bs = bs.setValue((Property)IBlockEnabled.ENABLED, true);
        }
        return bs;
    }
}
