package thaumcraft.common.blocks.misc;
import net.minecraft.world.level.block.RenderShape;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.tiles.misc.TileBarrierStone;


public class BlockBarrier extends BlockTC
{
    public BlockBarrier() {
        super(BlockBehaviour.Properties.of());
        setLightOpacity(0);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null;
    }

    public void getSubBlocks(CreativeModeTab tab, NonNullList<ItemStack> list) {
    }

    public ItemStack getPickBlock(BlockState state, HitResult target, Level world, BlockPos pos, Player player) {
        return ItemStack.EMPTY;
    }

    public boolean isSideSolid(BlockState state, BlockGetter world, BlockPos pos, Direction o) {
        return false;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public void addCollisionBoxToList(BlockState state, Level world, BlockPos pos, AABB mask, List list, Entity collidingEntity, boolean isActualState) {
        if (collidingEntity != null && collidingEntity instanceof LivingEntity && !(collidingEntity instanceof Player) && collidingEntity.getPassengers().stream().noneMatch(p -> p instanceof Player)) {
            int a = 1;
            if (world.getBlockState(pos.below(a)).getBlock() != BlocksTC.pavingStoneBarrier) {
                ++a;
            }
            if (world.getBestNeighborSignal(pos.below(a)) == 0) {
                list.add(FULL_BLOCK_AABB.move(pos));
            }
        }
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos pos2) {
        if (world.getBlockState(pos.below(1)) != BlocksTC.pavingStoneBarrier.defaultBlockState() && world.getBlockState(pos.below(1)) != defaultBlockState()) {
            world.removeBlock(pos, false);
        }
    }

    public boolean isPassable(BlockGetter worldIn, BlockPos pos) {
        for (int a = 1; a < 3; ++a) {
            BlockEntity te = worldIn.getBlockEntity(pos.below(a));
            if (te instanceof TileBarrierStone) {
                return te.world.getBestNeighborSignal(pos.below(a)) > 0;
            }
        }
        return true;
    }

    public boolean isReplaceable(BlockGetter worldIn, BlockPos pos) {
        return true;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    public boolean isAir(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }
}
