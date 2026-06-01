package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.Orientation;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.utils.BlockStateUtils;


public class BlockBrainBox extends BlockTC implements IBlockFacingHorizontal, IBlockEnabled
{
    public BlockBrainBox() {
        super(null /*  null   Material removed    */, "brain_box", true);

        setSoundType(SoundType.METAL);
        setHardness(1.0f);
        setResistance(10.0f);
        registerDefaultState(defaultBlockState().setValue(IBlockFacing.FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IBlockFacing.FACING);
    }

    public boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player) {
        return true;
    }

    public int damageDropped(BlockState state) {
        return 0;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, Orientation orientation, boolean isMoving) {
        Direction facing = BlockStateUtils.getFacing(state);
        if (worldIn.getBlockState(pos.relative(facing)).getBlock() != BlocksTC.thaumatorium
                && worldIn.getBlockState(pos.relative(facing)).getBlock() != BlocksTC.thaumatoriumTop) {
            Block.popResource(worldIn, pos, new ItemStack(BlocksTC.brainBox));
            worldIn.removeBlock(pos, false);
        }
    }

    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        return defaultBlockState().setValue(IBlockFacing.FACING, facing.getOpposite());
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
    }
}
