package thaumcraft.common.items.baubles;
// baubles import removed
// baubles import removed
// baubles import removed
// baubles import removed
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.items.ItemTCBase;
import net.minecraft.world.item.TooltipFlag;


public class ItemAmuletVis extends ItemTCBase 
{
    public ItemAmuletVis() {
        super("amulet_vis", "found", "crafted");
        // maxStackSize removed - set in Item.Properties
        /* setMaxDamage removed - use Item.Properties */;
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return (itemstack.getDamageValue() == 0) ? Rarity.UNCOMMON : Rarity.RARE;
    }
    
    public Object /* BaubleType removed */ getBaubleType(ItemStack itemstack) {
        return null /* nested removed */;
    }
    
    public void onWornTick(ItemStack itemstack, LivingEntity player) {
        if (player instanceof Player && !player.level().isClientSide() && player.tickCount % ((itemstack.getDamageValue() == 0) ? 40 : 5) == 0) {
            NonNullList<ItemStack> inv = ((Player)player).getInventory().items;
            int a = 0;
            while (true) {
                int n = a;
                Inventory inventory = ((Player)player).inventory;
                if (n >= Inventory.getHotbarSize()) {
                    Object /* IBaublesItemHandler removed */ baubles = null /* call removed */;
                    for (int a2 = 0; a2 < baubles.getSlots(); ++a2) {
                        if (RechargeHelper.rechargeItem(player.level(), baubles.getItem(a2), player.blockPosition(), (Player)player, 1) > 0.0f) {
                            return;
                        }
                    }
                    inv = ((Player)player).getInventory().armor;
                    for (int a2 = 0; a2 < inv.size(); ++a2) {
                        if (RechargeHelper.rechargeItem(player.level(), inv.get(a2), player.blockPosition(), (Player)player, 1) > 0.0f) {
                            return;
                        }
                    }
                    break;
                }
                if (RechargeHelper.rechargeItem(player.level(), inv.get(a), player.blockPosition(), (Player)player, 1) > 0.0f) {
                    return;
                }
                ++a;
            }
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(net.minecraft.network.chat.Component.literal("" + ChatFormatting.AQUA + I18n.get("item.amulet_vis.text")));
    }
}
