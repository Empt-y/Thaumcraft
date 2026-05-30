package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
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
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.devices.TileRechargePedestal;


public class BlockRechargePedestal extends BlockTCDevice
{
    public BlockRechargePedestal() {
        super(null /*  null   Material removed    */, TileRechargePedestal.class, "recharge_pedestal");
        setSoundType(SoundType.STONE);
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
        if (tile != null && tile instanceof TileRechargePedestal) {
            TileRechargePedestal ped = (TileRechargePedestal)tile;
            if (ped.getItem(0).isEmpty() && player.getItemInHand(hand).getItem() instanceof IRechargable) {
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
}
