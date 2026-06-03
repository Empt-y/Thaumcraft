package thaumcraft.common.container;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.container.slot.SlotTurretBasic;
import thaumcraft.common.entities.construct.EntityTurretCrossbowAdvanced;


public class ContainerTurretAdvanced extends AbstractContainerMenu
{
    public EntityTurretCrossbowAdvanced turret;
    private Player player;

    public ContainerTurretAdvanced(int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        this(inv, inv.player.level(), (EntityTurretCrossbowAdvanced) inv.player.level().getEntity(buf.readInt()));
    }

    public ContainerTurretAdvanced(Inventory par1InventoryPlayer, Level par3World, EntityTurretCrossbowAdvanced ent) {
        super(null, 0);
        turret = ent;
        player = par1InventoryPlayer.player;
        addSlot(new SlotTurretBasic(turret, 0, 42, 29));
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
    public boolean clickMenuButton(Player par1Player, int par2) {
        if (par2 == 1) { turret.setTargetAnimal(!turret.getTargetAnimal()); return true; }
        if (par2 == 2) { turret.setTargetMob(!turret.getTargetMob()); return true; }
        if (par2 == 3) { turret.setTargetPlayer(!turret.getTargetPlayer()); return true; }
        if (par2 == 4) { turret.setTargetFriendly(!turret.getTargetFriendly()); return true; }
        return super.clickMenuButton(par1Player, par2);
    }

    @Override
    public boolean stillValid(Player par1Player) {
        return true;
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
