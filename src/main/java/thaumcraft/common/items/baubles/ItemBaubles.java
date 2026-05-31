package thaumcraft.common.items.baubles;
// baubles import removed
// baubles import removed
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.items.ItemTCBase;


public class ItemBaubles extends ItemTCBase implements IVisDiscountGear
{
    public ItemBaubles() {
        super("baubles", "amulet_mundane", "ring_mundane", "girdle_mundane", "ring_apprentice", "amulet_fancy", "ring_fancy", "girdle_fancy");
        // maxStackSize removed - set in Item.Properties
        /* setMaxDamage removed - use Item.Properties */;
    }
    
    public Object /* BaubleType removed */ getBaubleType(ItemStack itemstack) {
        switch (itemstack.getDamageValue()) {
            case 1:
            case 3:
            case 5: {
                return null /* nested removed */;
            }
            case 2:
            case 6: {
                return null /* nested removed */;
            }
            default: {
                return null /* nested removed */;
            }
        }
    }
    
    // getRarity(ItemStack) overrides NeoForge extension; vanilla rarity is set via Item.Properties
    public Rarity getRarity(ItemStack stack) {
        if (stack.getDamageValue() >= 3) {
            return Rarity.UNCOMMON;
        }
        return Rarity.COMMON;
    }
    
    public int getVisDiscount(ItemStack stack, Player player) {
        if (stack.getDamageValue() == 3) {
            return 5;
        }
        return 0;
    }
}
