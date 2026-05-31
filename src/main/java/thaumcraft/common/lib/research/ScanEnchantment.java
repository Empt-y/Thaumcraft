package thaumcraft.common.lib.research;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;


public class ScanEnchantment implements IScanThing
{
    Enchantment enchantment;

    public ScanEnchantment(Enchantment ench) {
        enchantment = ench;
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        return getEnchantment(player, obj) != null;
    }

    private Enchantment getEnchantment(Player player, Object obj) {
        if (obj == null) {
            return null;
        }
        ItemStack is = ScanningManager.getItemFromParms(player, obj);
        if (is != null && !is.isEmpty()) {
            ItemEnchantments enchantments = is.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            for (Holder<Enchantment> holder : enchantments.keySet()) {
                if (holder.value() == enchantment) {
                    return enchantment;
                }
            }
        }
        return null;
    }

    @Override
    public String getResearchKey(Player player, Object obj) {
        return "!" + enchantment.description().getString();
    }
}
