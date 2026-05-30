package thaumcraft.common.items.baubles;
// baubles import removed
// baubles import removed
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.items.ItemTCBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;


public class ItemVoidseerCharm extends ItemTCBase implements IVisDiscountGear, IWarpingGear
{
    public ItemVoidseerCharm() {
        super("voidseer_charm");
        // maxStackSize removed - set in Item.Properties
        // canRepair field removed
        /* setMaxDamage removed - use Item.Properties */;
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }
    
    public Object /* BaubleType removed */ getBaubleType(ItemStack itemstack) {
        return null /* nested removed */;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        tooltip.accept(net.minecraft.network.chat.Component.literal("" + ChatFormatting.DARK_BLUE + "" + ChatFormatting.ITALIC + I18n.get("item.voidseer_charm.text")));
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
    }
    
    public int getVisDiscount(ItemStack stack, Player player) {
        int q = 0;
        IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);
        if (warp != null) {
            int pw = Math.min(100, warp.get(IPlayerWarp.EnumWarpType.PERMANENT));
            q = (int)(pw / 100.0f * 25.0f);
        }
        return q;
    }
    
    public int getWarp(ItemStack itemstack, Player player) {
        return getVisDiscount(itemstack, player) / 5;
    }
}
