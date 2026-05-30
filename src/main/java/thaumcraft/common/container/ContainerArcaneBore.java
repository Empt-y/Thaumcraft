package thaumcraft.common.container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.container.slot.SlotArcaneBorePickaxe;
import thaumcraft.common.entities.construct.EntityArcaneBore;


public class ContainerArcaneBore extends AbstractContainerMenu
{
    private EntityArcaneBore turret;
    private Player player;
    private Level theWorld;

    public ContainerArcaneBore(Inventory par1InventoryPlayer, Level par3World, EntityArcaneBore ent) {
        super(null, 0);
        turret = ent;
        theWorld = par3World;
        player = par1InventoryPlayer.player;
        addSlot(new SlotArcaneBorePickaxe(turret, 0, 80, 29));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player par1Player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player par1Player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = slots.get(slotIndex);
        if (slotObject != null && slotObject.hasItem()) {
            ItemStack stackInSlot = slotObject.getItem();
            stack = stackInSlot.copy();
            if (slotIndex == 0) {
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
