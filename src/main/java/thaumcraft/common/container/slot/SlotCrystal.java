package thaumcraft.common.container.slot;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.resources.ItemCrystalEssence;


public class SlotCrystal extends Slot
{
    private Aspect aspect;
    
    public SlotCrystal(Aspect aspect, Container par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
        this.aspect = aspect;
    }
    
    public boolean isItemValid(ItemStack stack) {
        return isValidCrystal(stack, aspect);
    }
    
    public static boolean isValidCrystal(ItemStack stack, Aspect aspect) {
        return stack != null && !stack.isEmpty() && !stack.isEmpty() && stack.getItem() instanceof ItemCrystalEssence && ((ItemCrystalEssence)stack.getItem()).getAspects(stack).getAspects()[0] == aspect;
    }
}
