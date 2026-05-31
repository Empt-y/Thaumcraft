package thaumcraft.common.items.curios;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import thaumcraft.common.items.ItemTCBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;


public class ItemEnchantmentPlaceholder extends ItemTCBase
{
    public ItemEnchantmentPlaceholder() {
        super("enchanted_placeholder");
    }
    
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
    
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }
    
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
        tooltip.accept(net.minecraft.network.chat.Component.literal("" + ChatFormatting.ITALIC + "" + ChatFormatting.DARK_AQUA + I18n.get("item.enchanted_placeholder.text")));
    }
    
    public boolean canHarvestBlock(BlockState blockIn) {
        return true;
    }
    
    public boolean canHarvestBlock(BlockState state, ItemStack stack) {
        return true;
    }
    
    public int getHarvestLevel(ItemStack stack, String toolClass, Player player, BlockState blockState) {
        return 99;
    }
}
