package thaumcraft.common.items.consumables;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.items.ItemTCBase;


public class ItemBathSalts extends ItemTCBase
{
    public ItemBathSalts() {
        super("bath_salts");
    }
    
    public int getEntityLifespan(ItemStack itemStack, Level world) {
        return 200;
    }
}
