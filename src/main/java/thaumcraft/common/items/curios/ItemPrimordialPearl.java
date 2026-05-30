package thaumcraft.common.items.curios;
import javax.annotation.Nullable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.items.ItemTCBase;


public class ItemPrimordialPearl extends ItemTCBase
{
    public ItemPrimordialPearl() {
        super("primordial_pearl");
        // maxStackSize removed - set in Item.Properties
        /* setMaxDamage removed - use Item.Properties */;
        /* addPropertyOverride removed */

        
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return false;
    }
    
    public boolean isRepairable() {
        return false;
    }
    
    
    public ItemStack getContainerItem(ItemStack itemStack) {
        if (!hasContainerItem(itemStack)) {
            return ItemStack.EMPTY;
        }
        ItemStack result = new ItemStack(itemStack.getItem(), itemStack.getCount());
        result.setDamageValue(itemStack.getDamageValue() + 1);
        return result;
    }
    
    public boolean hasContainerItem(ItemStack stack) {
        return stack.getDamageValue() < 7;
    }
    
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }
    
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }
}
