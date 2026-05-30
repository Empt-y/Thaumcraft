package thaumcraft.common.blocks;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.common.lib.utils.BlockStateUtils;


public class BlockTCDevice extends BlockTCTile
{
    public BlockTCDevice(Object mat, Class tc, String name) {
        super(mat, tc, name);
        // Block state registration is done via createBlockStateDefinition in modern MC
    }

    public boolean rotateBlock(Level world, BlockPos pos, Direction axis) {
        BlockState state = world.getBlockState(pos);
        for (Property<?> prop : state.getProperties()) {
            if (prop.getName().equals("facing")) {
                world.setBlock(pos, state.cycle((Property)prop), 3);
                return true;
            }
        }
        return false;
    }

    public void onBlockAdded(Level worldIn, BlockPos pos, BlockState state, BlockState oldState, boolean isMoving) {
        updateState(worldIn, pos, state);
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
        updateState(worldIn, pos, state);
        super.neighborChanged(state, worldIn, pos, blockIn, orientation, isMoving);
    }

    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        BlockState bs = defaultBlockState();
        if (this instanceof IBlockFacingHorizontal) {
            bs = bs.setValue((Property)IBlockFacingHorizontal.FACING, placer.isCrouching() ? placer.getDirection() : placer.getDirection().getOpposite());
        }
        if (this instanceof IBlockFacing) {
            bs = bs.setValue((Property)IBlockFacing.FACING, placer.isCrouching() ? facing.getOpposite() : facing);
        }
        if (this instanceof IBlockEnabled) {
            bs = bs.setValue((Property)IBlockEnabled.ENABLED, true);
        }
        return bs;
    }

    protected void updateState(Level worldIn, BlockPos pos, BlockState state) {
        if (this instanceof IBlockEnabled) {
            boolean flag = !worldIn.hasNeighborSignal(pos);
            if (flag != (boolean)state.getValue((Property)IBlockEnabled.ENABLED)) {
                worldIn.setBlock(pos, state.setValue((Property)IBlockEnabled.ENABLED, flag), 3);
            }
        }
    }

    public void updateFacing(Level world, BlockPos pos, Direction face) {
        if (this instanceof IBlockFacing || this instanceof IBlockFacingHorizontal) {
            BlockState state = world.getBlockState(pos);
            if (face == BlockStateUtils.getFacing(state)) return;
            if (this instanceof IBlockFacingHorizontal && face.getAxis().isHorizontal()) {
                world.setBlock(pos, state.setValue((Property)IBlockFacingHorizontal.FACING, face), 3);
            }
            if (this instanceof IBlockFacing) {
                world.setBlock(pos, state.setValue((Property)IBlockFacing.FACING, face), 3);
            }
        }
    }

    public BlockState getStateFromMeta(int meta) {
        BlockState bs = defaultBlockState();
        try {
            if (this instanceof IBlockFacingHorizontal) {
                bs = bs.setValue((Property)IBlockFacingHorizontal.FACING, BlockStateUtils.getFacing(meta));
            }
            if (this instanceof IBlockFacing) {
                bs = bs.setValue((Property)IBlockFacing.FACING, BlockStateUtils.getFacing(meta));
            }
            if (this instanceof IBlockEnabled) {
                bs = bs.setValue((Property)IBlockEnabled.ENABLED, BlockStateUtils.isEnabled(meta));
            }
        } catch (Exception ex) {}
        return bs;
    }

    public int getMetaFromState(BlockState state) {
        byte b0 = 0;
        int i = (this instanceof IBlockFacingHorizontal)
            ? (b0 | ((Direction)state.getValue((Property)IBlockFacingHorizontal.FACING)).get2DDataValue())
            : ((this instanceof IBlockFacing)
                ? (b0 | ((Direction)state.getValue((Property)IBlockFacing.FACING)).get3DDataValue())
                : b0);
        if (this instanceof IBlockEnabled && !(boolean)state.getValue((Property)IBlockEnabled.ENABLED)) {
            i |= 0x8;
        }
        return i;
    }
}
