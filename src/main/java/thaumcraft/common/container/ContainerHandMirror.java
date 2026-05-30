package thaumcraft.common.container;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.items.tools.ItemHandMirror;


public class ContainerHandMirror extends AbstractContainerMenu
{
    private Level worldObj;
    public InventoryHandMirror input;
    ItemStack mirror;
    Player player;

    public ContainerHandMirror(Inventory iinventory, Level par2World, int par3, int par4, int par5) {
        super(null, 0);
        worldObj = par2World;
        player = iinventory.player;
        mirror = iinventory.getSelectedItem();
        input = new InventoryHandMirror(this);

        addSlot(new Slot(input, 0, 80, 24));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(iinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(iinventory, i, 8 + i * 18, 142));
        }
        slotsChanged(input);
    }

    @Override
    public void slotsChanged(Container par1IInventory) {
        ItemStack slotItem = input.getItem(0);
        if (!slotItem.isEmpty() && ItemStack.isSameItemSameComponents(slotItem, mirror)) {
            if (player instanceof ServerPlayer sp) {
                sp.closeContainer();
            }
        } else if (!worldObj.isClientSide() && !slotItem.isEmpty() && player != null) {
            ItemStack is = slotItem.copy();
            input.setItem(0, ItemStack.EMPTY);
            if (!ItemHandMirror.transport(mirror, is, player, worldObj)) {
                input.setItem(0, is);
            }
            broadcastChanges();
        }
    }

    @Override
    public boolean stillValid(Player par1Player) {
        return true;
    }

    @Override
    public void removed(Player par1Player) {
        super.removed(par1Player);
        if (!worldObj.isClientSide()) {
            ItemStack var3 = input.getItem(0);
            if (!var3.isEmpty()) {
                input.setItem(0, ItemStack.EMPTY);
                par1Player.drop(var3, false);
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player par1Player, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = slots.get(slot);
        if (slotObject != null && slotObject.hasItem() && !(slotObject.getItem().getItem() instanceof ItemHandMirror)) {
            ItemStack stackInSlot = slotObject.getItem();
            stack = stackInSlot.copy();
            if (slot == 0) {
                if (!moveItemStackTo(stackInSlot, 1, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stackInSlot, 0, 1, false)) {
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
}
