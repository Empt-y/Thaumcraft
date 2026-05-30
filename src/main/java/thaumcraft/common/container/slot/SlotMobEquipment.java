package thaumcraft.common.container.slot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;


public class SlotMobEquipment extends Slot
{
    Mob entity;

    public SlotMobEquipment(Mob entity, int par3, int par4, int par5) {
        super(new SimpleContainer(1), par3, par4, par5);
        this.entity = entity;
    }

    @Override
    public ItemStack getItem() {
        return entity.getItemInHand(InteractionHand.MAIN_HAND);
    }

    @Override
    public void set(ItemStack stack) {
        entity.setItemInHand(InteractionHand.MAIN_HAND, stack);
        if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public void setChanged() {
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public ItemStack remove(int amount) {
        ItemStack current = getItem();
        if (current.isEmpty()) return ItemStack.EMPTY;
        if (current.getCount() <= amount) {
            set(ItemStack.EMPTY);
            return current;
        }
        ItemStack split = current.split(amount);
        if (current.getCount() == 0) set(ItemStack.EMPTY);
        return split;
    }
}
