package thaumcraft.common.container;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.Level;
import thaumcraft.common.container.slot.SlotLimitedByClass;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.ItemFocusPouch;


public class ContainerFocusPouch extends AbstractContainerMenu implements ContainerListener
{
    public Container input;
    ItemStack pouch;
    Player player;

    public ContainerFocusPouch(int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        this(TCMenuTypes.FOCUS_POUCH.get(), id, inv, inv.player.level());
    }

    public ContainerFocusPouch(MenuType<ContainerFocusPouch> type, int id, Inventory iinventory, Level level) {
        super(type, id);
        input = new InventoryFocusPouch();
        pouch = null;
        player = iinventory.player;
        pouch = iinventory.getSelectedItem();
        for (int a = 0; a < 18; ++a) {
            addSlot(new SlotLimitedByClass(ItemFocus.class, input, a, 37 + a % 6 * 18, 51 + a / 6 * 18));
        }
        bindPlayerInventory(iinventory);
        if (!level.isClientSide()) {
            try {
                NonNullList<ItemStack> list = ((ItemFocusPouch) pouch.getItem()).getInventory(pouch);
                for (int a2 = 0; a2 < list.size(); ++a2) {
                    input.setItem(a2, list.get(a2));
                }
            } catch (Exception ex) {}
        }
        slotsChanged(input);
    }

    @Override
    public void slotChanged(AbstractContainerMenu menu, int slot, ItemStack stack) {
        broadcastChanges();
    }

    @Override
    public void dataChanged(AbstractContainerMenu menu, int id, int value) {}

    protected void bindPlayerInventory(Inventory inventoryPlayer) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 151 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 209));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player par1Player, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = slots.get(slot);
        if (slotObject != null && slotObject.hasItem()) {
            ItemStack stackInSlot = slotObject.getItem();
            stack = stackInSlot.copy();
            if (slot < 18) {
                if (!moveItemStackTo(stackInSlot, 18, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stackInSlot, 0, 18, false)) {
                return ItemStack.EMPTY;
            }
            if (stackInSlot.getCount() == 0) {
                slotObject.set(ItemStack.EMPTY);
            } else {
                slotObject.setChanged();
            }
        }
        return stack;
    }

    @Override
    public boolean stillValid(Player var1) {
        return true;
    }

    @Override
    public void removed(Player par1Player) {
        super.removed(par1Player);
        if (!par1Player.level().isClientSide()) {
            NonNullList<ItemStack> list = NonNullList.withSize(18, ItemStack.EMPTY);
            for (int a = 0; a < list.size(); ++a) {
                list.set(a, input.getItem(a));
            }
            if (pouch != null && pouch.getItem() instanceof ItemFocusPouch) {
                ((ItemFocusPouch) pouch.getItem()).setInventory(pouch, list);
            }
        }
    }
}
