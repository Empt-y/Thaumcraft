package thaumcraft.common.container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


public class ContainerThaumatorium extends AbstractContainerMenu
{
    private TileThaumatorium thaumatorium;
    private Player player;

    public ContainerThaumatorium(Inventory par1InventoryPlayer, TileThaumatorium tileEntity) {
        super(null, 0);
        player = par1InventoryPlayer.player;
        thaumatorium = tileEntity;
        thaumatorium.eventHandler = this;
        addSlot(new Slot(tileEntity, 0, 55, 24));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 135 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(par1InventoryPlayer, i, 8 + i * 18, 193));
        }
        thaumatorium.updateRecipes(player);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        thaumatorium.updateRecipes(player);
    }

    @Override
    public void removed(Player par1Player) {
        super.removed(par1Player);
        if (!thaumatorium.world.isClientSide()) {
            thaumatorium.eventHandler = null;
        }
    }

    @Override
    public boolean stillValid(Player par1Player) {
        return thaumatorium.stillValid(par1Player);
    }

    @Override
    public ItemStack quickMoveStack(Player par1Player, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(par2);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack2 = slot.getItem();
            itemstack = itemstack2.copy();
            if (par2 != 0) {
                if (!moveItemStackTo(itemstack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (par2 >= 1 && par2 < 28) {
                if (!moveItemStackTo(itemstack2, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (par2 >= 28 && par2 < 37 && !moveItemStackTo(itemstack2, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
                if (!moveItemStackTo(itemstack2, 1, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack2.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }
            if (itemstack2.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(par1Player, itemstack2);
        }
        return itemstack;
    }
}
