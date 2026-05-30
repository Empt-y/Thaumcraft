package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.LiquidBlock;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.redstone.Orientation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTCTile;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.crafting.TilePedestal;


public class BlockPedestal extends BlockTCTile implements IInfusionStabiliserExt
{
    public static BlockPedestal instance;

    public BlockPedestal(String name) {
        super(null /*  null   Material removed    */, TilePedestal.class, name);
        setSoundType(SoundType.STONE);
        BlockPedestal.instance = this;
        registerDefaultState(defaultBlockState().setValue(BlockInlay.CHARGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockInlay.CHARGE);
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

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (world.isClientSide()) {
            return true;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile != null && tile instanceof TilePedestal) {
            TilePedestal ped = (TilePedestal)tile;
            if (ped.getItem(0).isEmpty() && !player.getItemInHand(hand).isEmpty() && player.getItemInHand(hand).getCount() > 0) {
                ItemStack i = player.getItemInHand(hand).copy();
                i.setCount(1);
                ped.setItem(0, i);
                player.getItemInHand(hand).shrink(1);
                if (player.getItemInHand(hand).getCount() == 0) {
                    player.setItemInHand(hand, ItemStack.EMPTY);
                }
                player.getInventory().setChanged();
                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2f, ((world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.7f + 1.0f) * 1.6f);
                return true;
            }
            if (!ped.getItem(0).isEmpty()) {
                InventoryUtils.dropItemsAtEntity(world, pos, player);
                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2f, ((world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.7f + 1.0f) * 1.5f);
                return true;
            }
        }
        return false;
    }

    public BlockState getStateFromMeta(int meta) {
        return defaultBlockState().setValue(BlockInlay.CHARGE, meta);
    }

    public int getMetaFromState(BlockState state) {
        return (int)state.getValue(LiquidBlock.LEVEL);
    }

    /* createBlockState() removed */

    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
        int charge = (int)stateIn.getValue(LiquidBlock.LEVEL);
        if (charge > 0) {
            FXDispatcher.INSTANCE.blockRunes2(pos.getX(), pos.getY() - 0.375, pos.getZ(), 1.0f, 0.0f, 0.0f, 10, 0.0f);
        }
    }

    public void onBlockAdded(Level worldIn, BlockPos pos, BlockState state) {
        if (!worldIn.isClientSide()) {
            BlockInlay.updateSurroundingInlay(worldIn, pos, state);
            for (Direction enumfacing1 : Direction.Plane.HORIZONTAL) {
                BlockInlay.notifyInlayNeighborsOfStateChange(worldIn, pos.relative(enumfacing1));
            }
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel worldIn, BlockPos pos, boolean isMoving) {
        super.affectNeighborsAfterRemoval(state, worldIn, pos, isMoving);
        for (Direction enumfacing : Direction.Plane.HORIZONTAL) {
            worldIn.updateNeighborsAt(pos.relative(enumfacing), this);
        }
        BlockInlay.updateSurroundingInlay(worldIn, pos, state);
        for (Direction enumfacing2 : Direction.Plane.HORIZONTAL) {
            BlockInlay.notifyInlayNeighborsOfStateChange(worldIn, pos.relative(enumfacing2));
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, Orientation orientation, boolean isMoving) {
        if (!worldIn.isClientSide()) {
            BlockInlay.updateSurroundingInlay(worldIn, pos, state);
        }
    }

    public boolean canStabaliseInfusion(Level world, BlockPos pos) {
        return true;
    }

    @Override
    public float getStabilizationAmount(Level world, BlockPos pos) {
        Block b = world.getBlockState(pos).getBlock();
        return (b == BlocksTC.pedestalEldritch) ? 0.1f : 0.0f;
    }

    @Override
    public boolean hasSymmetryPenalty(Level world, BlockPos pos1, BlockPos pos2) {
        BlockEntity te1 = world.getBlockEntity(pos1);
        BlockEntity te2 = world.getBlockEntity(pos2);
        if (world.isClientSide()) {
            if (te1 != null && te2 != null && te1 instanceof TilePedestal && te2 instanceof TilePedestal) {
                return ((TilePedestal)te1).getSyncedStackInSlot(0).isEmpty() != ((TilePedestal)te2).getSyncedStackInSlot(0).isEmpty();
            }
        }
        else if (te1 != null && te2 != null && te1 instanceof TilePedestal && te2 instanceof TilePedestal) {
            return ((TilePedestal)te1).getItem(0).isEmpty() != ((TilePedestal)te2).getItem(0).isEmpty();
        }
        return false;
    }

    @Override
    public float getSymmetryPenalty(Level world, BlockPos pos) {
        return 0.1f;
    }
}
