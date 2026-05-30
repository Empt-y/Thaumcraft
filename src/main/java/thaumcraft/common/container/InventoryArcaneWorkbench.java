package thaumcraft.common.container;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.api.crafting.IArcaneWorkbench;


public class InventoryArcaneWorkbench implements CraftingContainer, IArcaneWorkbench
{
    private static final int WIDTH = 5;
    private static final int HEIGHT = 3;
    private static final int SIZE = WIDTH * HEIGHT;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    public AbstractContainerMenu eventHandler;
    private final BlockEntity workbench;

    public InventoryArcaneWorkbench(BlockEntity tileEntity, AbstractContainerMenu container) {
        this.workbench = tileEntity;
        this.eventHandler = container;
    }

    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }
    @Override public List<ItemStack> getItems() { return items; }
    @Override public int getContainerSize() { return SIZE; }
    @Override public int size() { return SIZE; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack result = net.minecraft.world.ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        return net.minecraft.world.ContainerHelper.takeItem(items, slot);
    }
    @Override public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }
    @Override public void setChanged() {
        if (eventHandler != null) eventHandler.slotsChanged(this);
        if (workbench != null) workbench.setChanged();
    }
    @Override public boolean stillValid(Player player) { return true; }
    @Override public void clearContent() { items.replaceAll(s -> ItemStack.EMPTY); }

    @Override
    public void fillStackedContents(net.minecraft.world.entity.player.StackedItemContents contents) {
        for (ItemStack stack : items) {
            contents.accountStack(stack);
        }
    }
}
