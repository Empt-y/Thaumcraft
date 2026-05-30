package thaumcraft.common.container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.container.slot.SlotLimitedByClass;
import thaumcraft.common.items.consumables.ItemBathSalts;
import thaumcraft.common.tiles.devices.TileSpa;


public class ContainerSpa extends AbstractContainerMenu
{
    private TileSpa spa;
    private int lastBreakTime;
    
    public ContainerSpa(Inventory par1InventoryPlayer, TileSpa tileEntity) {
        super(null, 0);
        spa = tileEntity;
        addSlot(new SlotLimitedByClass(ItemBathSalts.class, tileEntity, 0, 65, 31));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }
    
    public boolean enchantItem(Player p, int button) {
        if (button == 1) {
            spa.toggleMix();
        }
        return false;
    }
    
    @Override
    public boolean stillValid(Player par1Player) {
        return spa.stillValid(par1Player);
    }

    @Override
    public ItemStack quickMoveStack(Player par1Player, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = slots.get(slot);
        if (slotObject != null && slotObject.hasItem()) {
            ItemStack stackInSlot = slotObject.getItem();
            stack = stackInSlot.copy();
            if (slot == 0) {
                if (!spa.isItemValidForSlot(slot, stackInSlot) || !moveItemStackTo(stackInSlot, 1, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!spa.isItemValidForSlot(slot, stackInSlot) || !moveItemStackTo(stackInSlot, 0, 1, false)) {
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
