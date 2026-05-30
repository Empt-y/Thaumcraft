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
    
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, net.minecraft.world.item.context.UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        InteractionHand hand = context.getHand();
        if (player == null || world.isClientSide()) {
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
        return InteractionResult.PASS;
    }
    
    @Override
    public boolean ignoreContainedAspects() {
        return true;
    }
}
