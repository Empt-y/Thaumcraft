package thaumcraft.common.container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.container.slot.SlotOutput;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;


public class ContainerVoidSiphon extends AbstractContainerMenu
{
    private TileVoidSiphon siphon;

    public ContainerVoidSiphon(Inventory par1InventoryPlayer, TileVoidSiphon tileEntity) {
        super(null, 0);
        siphon = tileEntity;
        addSlot(new SlotOutput(tileEntity, 0, 80, 32));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
        addDataSlots(new ContainerData() {
            @Override public int get(int i) { return i == 0 ? siphon.progress : 0; }
            @Override public void set(int i, int v) { if (i == 0) siphon.progress = v; }
            @Override public int getCount() { return 1; }
        });
    }

    @Override
    public boolean stillValid(Player par1Player) {
        return siphon.stillValid(par1Player);
    }

    @Override
    public ItemStack quickMoveStack(Player par1Player, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = slots.get(slot);
        if (slotObject != null && slotObject.hasItem()) {
            ItemStack stackInSlot = slotObject.getItem();
            stack = stackInSlot.copy();
            if (slot == 0) {
                if (!siphon.isItemValidForSlot(slot, stackInSlot) || !moveItemStackTo(stackInSlot, 1, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!siphon.isItemValidForSlot(slot, stackInSlot) || !moveItemStackTo(stackInSlot, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
            if (stackInSlot.getCount() == 0) {
                slotObject.set(ItemStack.EMPTY);
            }
            else {
                slotObject.setChanged();
            }
        }
        return stack;
    }
}
