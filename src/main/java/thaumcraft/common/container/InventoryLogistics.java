package thaumcraft.common.container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;


public class InventoryLogistics extends SimpleContainer
{
    public InventoryLogistics() {
        super(81);
    }

    @Override
    public int getMaxStackSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Integer.MAX_VALUE;
    }
}
