package thaumcraft.common.container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;


public class InventoryHandMirror extends SimpleContainer
{
    private ContainerHandMirror container;

    public InventoryHandMirror(ContainerHandMirror containerIn) {
        super(1);
        container = containerIn;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (container != null) {
            container.slotsChanged(this);
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        super.setItem(index, stack);
    }
}
