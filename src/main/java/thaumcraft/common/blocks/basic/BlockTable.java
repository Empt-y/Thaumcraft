package thaumcraft.common.blocks.basic;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.tiles.crafting.TileResearchTable;


public class BlockTable extends BlockTC
{
    public BlockTable(Object mat, String name, SoundType st) {
        super(mat, name, st);
    }

    public boolean isSideSolid(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return side == Direction.UP;
    }

    public Object getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null;
    }

    public boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player) {
        return true;
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
        if (this == BlocksTC.tableWood && player.getItemInHand(hand).getItem() instanceof IScribeTools) {
            BlockState bs = BlocksTC.researchTable.defaultBlockState();
            bs = bs.setValue((Property)IBlockFacingHorizontal.FACING, player.getDirection());
            world.setBlock(pos, bs, 3);
            TileResearchTable tile = (TileResearchTable)world.getBlockEntity(pos);
            if (tile != null) {
                tile.setItem(0, player.getItemInHand(hand).copy());
                player.setItemInHand(hand, ItemStack.EMPTY);
                player.getInventory().setChanged();
                tile.setChanged();
                world.sendBlockUpdated(pos, bs, bs, 3);
            }
        }
        return true;
    }
}
