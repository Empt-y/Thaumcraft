package thaumcraft.common.items.consumables;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCEssentiaContainer;


public class ItemLabel extends ItemTCEssentiaContainer
{
    public ItemLabel() {
        super("label", 1, "blank", "filled");
    }
    
    
    
    public void getSubItems(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == null /* SEARCH tab removed */) {
            items.add(new ItemStack(this, 1));
        }
    }
    
    public InteractionResult onItemUseFirst(Player player, Level world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, InteractionHand hand) {
        if (world.isClientSide()) {
            return InteractionResult.PASS;
        }
        BlockState bs = world.getBlockState(pos);
        if (bs.getBlock() instanceof ILabelable) {
            if (((ILabelable)bs.getBlock()).applyLabel(player, pos, side, player.getItemInHand(hand))) {
                player.getItemInHand(hand).shrink(1);
                player.containerMenu.broadcastChanges();
            }
            return InteractionResult.SUCCESS;
        }
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ILabelable) {
            if (((ILabelable)te).applyLabel(player, pos, side, player.getItemInHand(hand))) {
                player.getItemInHand(hand).shrink(1);
                player.containerMenu.broadcastChanges();
            }
            return InteractionResult.SUCCESS;
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int par4, boolean par5) {
    }
    
    @Override
    public void onCreated(ItemStack stack, Level world, Player player) {
    }
    
    @Override
    public boolean ignoreContainedAspects() {
        return true;
    }
}
