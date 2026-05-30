package thaumcraft.common.container;

import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.items.casters.ItemFocus;

public class InventoryFocusPouch extends SimpleContainer
{
    public InventoryFocusPouch(ContainerListener listener) {
        super(18);
        // FIXME: SimpleContainer no longer supports listeners directly; wire via menu
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemstack) {
        return !itemstack.isEmpty() && itemstack.getItem() instanceof ItemFocus;
    }
}
