package thaumcraft.common.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SlotPotion extends Slot
{
    int limit;

    public SlotPotion(Container par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
        limit = 64;
    }

    public SlotPotion(int limit, Container par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
        this.limit = limit;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack != null && !stack.isEmpty() && isValidPotion(stack);
    }

    public static boolean isValidPotion(ItemStack stack) {
        // FIXME: PotionType API removed; check for potion items broadly
        return stack.getItem() == Items.POTION
                || stack.getItem() == Items.LINGERING_POTION
                || stack.getItem() == Items.SPLASH_POTION;
    }

    @Override
    public int getMaxStackSize() {
        return limit;
    }
}
