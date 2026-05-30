package thaumcraft.common.container.slot;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;


public class SlotLimitedByItemstack extends Slot
{
    ItemStack limitItem;
    
    public SlotLimitedByItemstack(ItemStack item, Container par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
        limitItem = null;
        limitItem = item;
    }
    
    @Override
    public boolean mayPlace(ItemStack stack1) {
        return ItemStack.isSameItem(stack1, limitItem);
    }
}
