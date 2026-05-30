package thaumcraft.common.container;

import java.util.ArrayList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

public class InventoryFake extends SimpleContainer
{
    public InventoryFake(int size) {
        super(size);
    }

    public InventoryFake(NonNullList<ItemStack> inv) {
        super(inv.size());
        for (int a = 0; a < inv.size(); ++a) {
            setItem(a, inv.get(a));
        }
    }

    public InventoryFake(ItemStack... stacks) {
        super(stacks);
    }

    public InventoryFake(ArrayList<ItemStack> inv) {
        super(inv.size());
        for (int a = 0; a < inv.size(); ++a) {
            setItem(a, inv.get(a));
        }
    }
}
