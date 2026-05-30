package thaumcraft.common.container.slot;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;


public class SlotLimitedByClass extends Slot
{
    Class clazz;
    int limit;
    
    public SlotLimitedByClass(Class clazz, Container par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
        this.clazz = Object.class;
        limit = 64;
        this.clazz = clazz;
    }
    
    public SlotLimitedByClass(Class clazz, int limit, Container par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
        this.clazz = Object.class;
        this.limit = 64;
        this.clazz = clazz;
        this.limit = limit;
    }
    
    public boolean isItemValid(ItemStack stack) {
        return !stack.isEmpty() && clazz.isAssignableFrom(stack.getItem().getClass());
    }
    
    public int getSlotStackLimit() {
        return limit;
    }
}
