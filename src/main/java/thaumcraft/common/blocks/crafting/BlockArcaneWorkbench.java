package thaumcraft.common.blocks.crafting;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;


public class BlockArcaneWorkbench extends BlockTCDevice
{
    public BlockArcaneWorkbench() {
        super(null /*  null   Material removed    */, TileArcaneWorkbench.class, "arcane_workbench");
        setSoundType(SoundType.WOOD);
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (world.isClientSide()) {
            return true;
        }
        // TODO: open GUI id=13
        return true;
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (worldIn instanceof Level level) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof TileArcaneWorkbench) {
                Containers.dropContents(level, pos, ((TileArcaneWorkbench)tileEntity).inventoryCraft);
            }
        }
        super.destroy(worldIn, pos, state);
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
