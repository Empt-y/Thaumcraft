package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.redstone.Orientation;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileLampArcane;
import thaumcraft.common.tiles.devices.TileLampFertility;
import thaumcraft.common.tiles.devices.TileLampGrowth;


public class BlockLamp extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    public BlockLamp(Class tc, String name) {
        super(null /*  null   Material removed    */, tc, name, true);
        setSoundType(SoundType.METAL);
        setHardness(1.0f);
        registerDefaultState(defaultBlockState()
            .setValue(IBlockFacing.FACING, Direction.DOWN)
            .setValue(IBlockEnabled.ENABLED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder); // super adds IBlockFacing.FACING and IBlockEnabled.ENABLED
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public int damageDropped(BlockState state) {
        return 0;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return (boolean)state.getValue(IBlockEnabled.ENABLED) ? 15 : 0;
    }

    @Override
    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        return defaultBlockState()
            .setValue(IBlockFacing.FACING, facing.getOpposite())
            .setValue(IBlockEnabled.ENABLED, false);
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (worldIn instanceof Level level) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileLampArcane) {
                ((TileLampArcane)te).removeLights();
            }
        }
        super.destroy(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, Orientation orientation, boolean isMoving) {
        Direction facing = BlockStateUtils.getFacing(state);
        if (worldIn.getBlockState(pos.relative(facing)).isAir()) {
            Block.popResource(worldIn, pos, new ItemStack(this));
            worldIn.removeBlock(pos, false);
            return;
        }
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof TileLampArcane && (boolean)state.getValue(IBlockEnabled.ENABLED) && worldIn.hasNeighborSignal(pos)) {
            ((TileLampArcane)te).removeLights();
        }
        boolean checkUpdate = true;
        if (te instanceof TileLampGrowth && ((TileLampGrowth)te).charges <= 0) {
            checkUpdate = false;
        }
        if (te instanceof TileLampFertility && ((TileLampFertility)te).charges <= 0) {
            checkUpdate = false;
        }
        if (checkUpdate) {
            super.neighborChanged(state, worldIn, pos, blockIn, orientation, isMoving);
        }
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.25, 0.125, 0.25, 0.75, 0.875, 0.75);
    }
}
