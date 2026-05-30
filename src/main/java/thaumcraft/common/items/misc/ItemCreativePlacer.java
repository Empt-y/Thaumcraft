package thaumcraft.common.items.misc;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.items.ItemTCBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;


public class ItemCreativePlacer extends ItemTCBase
{
    public ItemCreativePlacer() {
        super("creative_placer", "obelisk", "node", "caster");
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
        tooltip.accept(net.minecraft.network.chat.Component.literal("" + ChatFormatting.DARK_PURPLE + "Creative only"));
    }
    
    public InteractionResult onItemUseFirst(Player player, Level world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, InteractionHand hand) {
        BlockState bs = world.getBlockState(pos);
        if (!bs.isSolid()) {
            return InteractionResult.FAIL;
        }
        if (world.isClientSide()) {
            return InteractionResult.PASS;
        }
        pos = pos.relative(side);
        bs = world.getBlockState(pos);
        if (!player.mayUseItemAt(pos, side, player.getItemInHand(hand))) {
            return InteractionResult.FAIL;
        }
        if (!bs.getBlock().canBeReplaced()) {
            return InteractionResult.FAIL;
        }
        if (player.getItemInHand(hand).getDamageValue() == 0 && !world.getBlockState(pos.below()).isSolid()) {
            return InteractionResult.FAIL;
        }
        world.removeBlock(pos, false);
        player.getItemInHand(hand).getDamageValue();
        return InteractionResult.SUCCESS;
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.EPIC;
    }
}
