package thaumcraft.common.items.tools;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.items.ItemTCBase;


public class ItemSanityChecker extends ItemTCBase
{
    public ItemSanityChecker() {
        super("sanity_checker");
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
}
