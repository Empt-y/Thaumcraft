package thaumcraft.common.container.slot;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;


public class SlotOutput extends Slot
{
    public SlotOutput(Container par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
    }
    
    public boolean isItemValid(ItemStack stack1) {
        return false;
    }
}
