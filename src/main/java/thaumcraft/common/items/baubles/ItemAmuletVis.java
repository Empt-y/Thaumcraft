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
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.items.ItemTCBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;


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
        if (player instanceof Player p && !player.level().isClientSide() && player.tickCount % ((itemstack.getDamageValue() == 0) ? 40 : 5) == 0) {
            Inventory inv = p.getInventory();
            for (int a = 0; a < Inventory.getSelectionSize(); ++a) {
                if (RechargeHelper.rechargeItem(player.level(), inv.getItem(a), player.blockPosition(), p, 1) > 0.0f) {
                    return;
                }
            }
            for (net.minecraft.world.entity.EquipmentSlot slot : net.minecraft.world.entity.EquipmentSlot.values()) {
                if (slot.getType() == net.minecraft.world.entity.EquipmentSlot.Type.HUMANOID_ARMOR) {
                    if (RechargeHelper.rechargeItem(player.level(), p.getItemBySlot(slot), player.blockPosition(), p, 1) > 0.0f) {
                        return;
                    }
                }
            }
        }
    }
    
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        tooltip.accept(net.minecraft.network.chat.Component.literal("" + ChatFormatting.AQUA + I18n.get("item.amulet_vis.text")));
    }
}
