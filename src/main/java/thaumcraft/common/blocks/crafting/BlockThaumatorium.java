package thaumcraft.common.blocks.crafting;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.redstone.Orientation;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import thaumcraft.common.tiles.crafting.TileThaumatoriumTop;


public class BlockThaumatorium extends BlockTCDevice implements IBlockFacingHorizontal
{
    boolean top;

    public BlockThaumatorium(boolean top) {
        super(null /*  null   Material removed    */, null, top ? "thaumatorium_top" : "thaumatorium");
        setSoundType(SoundType.METAL);
        this.top = top;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return top ? new TileThaumatoriumTop(null, pos, state) : new TileThaumatorium(null, pos, state);
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public RenderShape getRenderShape(BlockState state) {
        return top ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    public int damageDropped(BlockState state) {
        return 0;
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (worldIn instanceof Level level) {
            if (top && level.getBlockState(pos.below()).getBlock() == BlocksTC.thaumatorium) {
                level.setBlock(pos.below(), BlocksTC.metalAlchemical.defaultBlockState(), 3);
            }
            if (!top && level.getBlockState(pos.above()).getBlock() == BlocksTC.thaumatoriumTop) {
                level.setBlock(pos.above(), BlocksTC.metalAlchemical.defaultBlockState(), 3);
            }
        }
        super.destroy(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, Orientation orientation, boolean isMoving) {
        if (!top && worldIn.getBlockState(pos.below()).getBlock() != BlocksTC.crucible) {
            worldIn.setBlock(pos, BlocksTC.metalAlchemical.defaultBlockState(), 3);
            if (worldIn.getBlockState(pos.above()).getBlock() == BlocksTC.thaumatoriumTop) {
                worldIn.setBlock(pos.above(), BlocksTC.metalAlchemical.defaultBlockState(), 3);
            }
        }
    }

    public boolean hasComparatorInputOverride(BlockState state) {
        return !top;
    }

    public int getComparatorInputOverride(BlockState state, Level world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileThaumatorium) {
            return 0; // Container.calcRedstoneFromInventory removed
        }
        return 0;
    }

    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hit) {
        if (!world.isClientSide() && !player.isShiftKeyDown()) {
            net.minecraft.world.level.block.entity.BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof net.minecraft.world.MenuProvider) {
                ((net.minecraft.server.level.ServerPlayer) player).openMenu((net.minecraft.world.MenuProvider) te, buf -> buf.writeBlockPos(pos));
            }
        }
        return net.minecraft.world.InteractionResult.SUCCESS;
    }
}
