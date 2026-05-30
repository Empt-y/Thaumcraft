package thaumcraft.common.container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import thaumcraft.common.container.slot.SlotFocus;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;


public class ContainerFocalManipulator extends AbstractContainerMenu
{
    private TileFocalManipulator table;
    private int lastBreakTime;

    public ContainerFocalManipulator(Inventory inventoryPlayer, TileFocalManipulator tileEntity) {
        super(null, 0);
        table = tileEntity;
        addSlot(new SlotFocus(tileEntity, 0, 31, 191));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, i * 18 - 62, 64 + j * 18));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                addSlot(new Slot(inventoryPlayer, i + j * 3, i * 18 - 62, j * 18 + 7));
            }
        }
    }

    @Override
    public boolean clickMenuButton(Player p, int button) {
        if (button == 0 && !table.startCraft(button, p)) {
            net.minecraft.world.level.Level level = table.level();
            if (level != null) {
                level.playSound(p, table.getBlockPos(), SoundsTC.craftfail, SoundSource.BLOCKS, 0.33f, 1.0f);
            }
        }
        return false;
    }

    @Override
    public boolean stillValid(Player par1Player) {
        net.minecraft.world.level.Level level = table.level();
        if (level == null) return false;
        return level.getBlockEntity(table.getBlockPos()) == table &&
               par1Player.distanceToSqr(table.getBlockPos().getCenter()) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player par1Player, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(par2);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack2 = slot.getItem();
            itemstack = itemstack2.copy();
            if (par2 != 0) {
                if (itemstack2.getItem() instanceof ItemFocus) {
                    if (!moveItemStackTo(itemstack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (par2 >= 1 && par2 < 28) {
                    if (!moveItemStackTo(itemstack2, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (par2 >= 28 && par2 < 37 && !moveItemStackTo(itemstack2, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemstack2, 1, 37, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack2.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
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
