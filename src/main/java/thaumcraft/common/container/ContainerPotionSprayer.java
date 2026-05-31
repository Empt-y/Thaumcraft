package thaumcraft.common.container;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.container.slot.SlotPotion;
import thaumcraft.common.tiles.devices.TilePotionSprayer;


public class ContainerPotionSprayer extends AbstractContainerMenu
{
    private TilePotionSprayer sprayer;

    public ContainerPotionSprayer(int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        this(TCMenuTypes.POTION_SPRAYER.get(), id, inv,
            (TilePotionSprayer) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public ContainerPotionSprayer(MenuType<ContainerPotionSprayer> type, int id, Inventory par1InventoryPlayer, TilePotionSprayer tilePotionSprayer) {
        super(type, id);
        sprayer = tilePotionSprayer;
        addSlot(new SlotPotion(tilePotionSprayer, 0, 56, 64));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(par1InventoryPlayer, j + i * 9 + 9, 16 + j * 18, 151 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(par1InventoryPlayer, i, 16 + i * 18, 209));
        }
    }

    @Override
    public boolean stillValid(Player par1Player) {
        Level level = sprayer.getLevel();
        if (level == null) return false;
        return level.getBlockEntity(sprayer.getBlockPos()) == sprayer
            && par1Player.distanceToSqr(sprayer.getBlockPos().getCenter()) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player par1Player, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = slots.get(slot);
        if (slotObject != null && slotObject.hasItem()) {
            ItemStack stackInSlot = slotObject.getItem();
            stack = stackInSlot.copy();
            if (slot == 0) {
                if (!moveItemStackTo(stackInSlot, 1, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (sprayer.isItemValidForSlot(slot, stackInSlot) && !moveItemStackTo(stackInSlot, 0, 1, false)) {
                return ItemStack.EMPTY;
            } else if (!moveItemStackTo(stackInSlot, 1, slots.size(), false)) {
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

    public TilePotionSprayer getTile() { return sprayer; }
}
