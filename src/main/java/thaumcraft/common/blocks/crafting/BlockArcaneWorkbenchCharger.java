package thaumcraft.common.blocks.crafting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.Orientation;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;


public class BlockArcaneWorkbenchCharger extends BlockTC
{
    public BlockArcaneWorkbenchCharger() {
        super(null /*  null   Material removed    */, "arcane_workbench_charger");
        setSoundType(SoundType.WOOD);
        setHardness(1.25f);
        setResistance(10.0f);
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public boolean canPlaceBlockAt(Level worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.below()).getBlock() == BlocksTC.arcaneWorkbench
            || worldIn.getBlockState(pos.below()).getBlock() == BlocksTC.wandWorkbench;
    }

    public Object getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null;
    }

    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        BlockEntity te = worldIn.getBlockEntity(pos.below());
        if (te instanceof TileArcaneWorkbench) {
            ((TileArcaneWorkbench)te).syncTile(true);
        }
        if (te instanceof TileFocalManipulator) {
            ((TileFocalManipulator)te).syncTile(true);
        }
        return defaultBlockState();
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, Orientation orientation, boolean isMoving) {
        if (worldIn.getBlockState(pos.below()).getBlock() != BlocksTC.arcaneWorkbench
                && worldIn.getBlockState(pos.below()).getBlock() != BlocksTC.wandWorkbench) {
            Block.popResource(worldIn, pos, new ItemStack(this));
            worldIn.removeBlock(pos, false);
        }
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (world.isClientSide()) {
            return true;
        }
        if (world.getBlockState(pos.below()).getBlock() == BlocksTC.arcaneWorkbench) {
            // TODO: open GUI id=13
        }
        if (world.getBlockState(pos.below()).getBlock() == BlocksTC.wandWorkbench) {
            // TODO: open GUI id=7
        }
        return true;
    }
}
