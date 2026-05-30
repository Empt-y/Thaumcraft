package thaumcraft.common.items.baubles;
// baubles import removed
// baubles import removed
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;


public class ItemCharmUndying extends ItemTCBase 
{
    public ItemCharmUndying() {
        super("charm_undying");
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
}
