package thaumcraft.common.container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.container.slot.SlotOutput;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;


public class ContainerGolemBuilder extends AbstractContainerMenu
{
    private TileGolemBuilder builder;
    public static boolean redo;
    private int lastCost;
    private int lastMaxCost;
    private final java.util.List<ContainerListener> myListeners = new java.util.ArrayList<>();

    public ContainerGolemBuilder(Inventory par1InventoryPlayer, TileGolemBuilder tileEntity) {
        super(null, 0);
        builder = tileEntity;
        addSlot(new SlotOutput(tileEntity, 0, 160, 104));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(par1InventoryPlayer, j + i * 9 + 9, 24 + j * 18, 142 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(par1InventoryPlayer, i, 24 + i * 18, 200));
        }
    }

    @Override
    public boolean clickMenuButton(Player p, int button) {
        if (button == 99) {
            ContainerGolemBuilder.redo = true;
        }
        return false;
    }

    @Override
    public void addSlotListener(ContainerListener par1ICrafting) {
        super.addSlotListener(par1ICrafting);
        myListeners.add(par1ICrafting);
        par1ICrafting.dataChanged(this, 0, builder.cost);
    }

    @Override
    public void removeSlotListener(ContainerListener par1ICrafting) {
        super.removeSlotListener(par1ICrafting);
        myListeners.remove(par1ICrafting);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        for (ContainerListener icrafting : myListeners) {
            if (lastCost != builder.cost) {
                icrafting.dataChanged(this, 0, builder.cost);
            }
            if (lastMaxCost != builder.maxCost) {
                icrafting.dataChanged(this, 1, builder.maxCost);
            }
        }
        lastCost = builder.cost;
        lastMaxCost = builder.maxCost;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setData(int par1, int par2) {
        if (par1 == 0) {
            builder.cost = par2;
        }
        if (par1 == 1) {
            builder.maxCost = par2;
        }
    }

    @Override
    public boolean stillValid(Player par1Player) {
        net.minecraft.world.level.Level level = builder.getLevel();
        if (level == null) return false;
        return level.getBlockEntity(builder.getBlockPos()) == builder &&
               par1Player.distanceToSqr(builder.getBlockPos().getCenter()) <= 64.0;
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

    static {
        ContainerGolemBuilder.redo = false;
    }
}
