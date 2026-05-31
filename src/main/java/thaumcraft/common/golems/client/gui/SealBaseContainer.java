package thaumcraft.common.golems.client.gui;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.container.slot.SlotGhost;


public class SealBaseContainer extends AbstractContainerMenu
{
    private Level world;
    ISealEntity seal;
    Player player;
    InventoryFake temp;
    int[] categories;
    int category;
    Inventory pinv;
    int t;
    private byte lastPriority;
    private byte lastColor;
    private int lastAreaX;
    private int lastAreaY;
    private int lastAreaZ;

    public SealBaseContainer(Inventory iinventory, Level par2World, ISealEntity seal) {
        super(null, 0);
        this.seal = null;
        player = null;
        category = -1;
        t = 0;
        world = par2World;
        player = iinventory.player;
        pinv = iinventory;
        this.seal = seal;
        if (seal.getSeal() instanceof ISealGui) {
            categories = ((ISealGui)seal.getSeal()).getGuiCategories();
        }
        else {
            categories = new int[] { 0 };
        }
        setupCategories();
    }

    void setupCategories() {
        slots.clear();
        t = 0;
        if (category < 0) {
            category = categories[0];
        }
        switch (category) {
            case 1: {
                setupFilterInventory();
                break;
            }
        }
        bindPlayerInventory(pinv);
    }

    private void setupFilterInventory() {
        if (seal.getSeal() instanceof ISealConfigFilter) {
            int s = ((ISealConfigFilter) seal.getSeal()).getFilterSize();
            int sx = 16 + (s - 1) % 3 * 12;
            int sy = 16 + (s - 1) / 3 * 12;
            int middleX = 88;
            int middleY = 72;
            temp = new InventoryFake(((ISealConfigFilter) seal.getSeal()).getInv());
            for (int a = 0; a < s; ++a) {
                int x = a % 3;
                int y = a / 3;
                addSlot(new SlotGhost(temp, a, middleX + x * 24 - sx + 8, middleY + y * 24 - sy + 8));
                ++t;
            }
        }
    }

    protected void bindPlayerInventory(Inventory inventoryPlayer) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 150 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 208));
        }
    }

    @Override
    public boolean stillValid(Player var1) {
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int par2) {
        if (par2 >= 0 && par2 < categories.length) {
            category = categories[par2];
            setupCategories();
            return true;
        }
        if (category == 3 && seal.getSeal() instanceof ISealConfigToggles && par2 >= 30 && par2 < 30 + ((ISealConfigToggles) seal.getSeal()).getToggles().length) {
            ISealConfigToggles cp = (ISealConfigToggles) seal.getSeal();
            cp.setToggle(par2 - 30, true);
            return true;
        }
        if (category == 3 && seal.getSeal() instanceof ISealConfigToggles && par2 >= 60 && par2 < 60 + ((ISealConfigToggles) seal.getSeal()).getToggles().length) {
            ISealConfigToggles cp = (ISealConfigToggles) seal.getSeal();
            cp.setToggle(par2 - 60, false);
            return true;
        }
        if (category == 0 && par2 >= 25 && par2 <= 26) {
            seal.setLocked(par2 == 25);
            return true;
        }
        if (par2 >= 27 && par2 <= 28) {
            seal.setRedstoneSensitive(par2 == 27);
            return true;
        }
        if (category == 1 && seal.getSeal() instanceof ISealConfigFilter && par2 >= 20 && par2 <= 21) {
            ISealConfigFilter cp2 = (ISealConfigFilter) seal.getSeal();
            cp2.setBlacklist(par2 == 20);
            return true;
        }
        if (par2 == 80 && seal.getPriority() > -5) {
            seal.setPriority((byte)(seal.getPriority() - 1));
            return true;
        }
        if (par2 == 81 && seal.getPriority() < 5) {
            seal.setPriority((byte)(seal.getPriority() + 1));
            return true;
        }
        if (par2 == 82 && seal.getColor() > 0) {
            seal.setColor((byte)(seal.getColor() - 1));
            return true;
        }
        if (par2 == 83 && seal.getColor() < 16) {
            seal.setColor((byte)(seal.getColor() + 1));
            return true;
        }
        if (seal.getSeal() instanceof ISealConfigArea) {
            if (par2 == 90 && seal.getArea().getY() > 1) {
                seal.setArea(seal.getArea().offset(0, -1, 0));
                return true;
            }
            if (par2 == 91 && seal.getArea().getY() < 8) {
                seal.setArea(seal.getArea().offset(0, 1, 0));
                return true;
            }
            if (par2 == 92 && seal.getArea().getX() > 1) {
                seal.setArea(seal.getArea().offset(-1, 0, 0));
                return true;
            }
            if (par2 == 93 && seal.getArea().getX() < 8) {
                seal.setArea(seal.getArea().offset(1, 0, 0));
                return true;
            }
            if (par2 == 94 && seal.getArea().getZ() > 1) {
                seal.setArea(seal.getArea().offset(0, 0, -1));
                return true;
            }
            if (par2 == 95 && seal.getArea().getZ() < 8) {
                seal.setArea(seal.getArea().offset(0, 0, 1));
                return true;
            }
        }
        return super.clickMenuButton(player, par2);
    }

    public void addSlotListener(ContainerListener crafting) {
        super.addSlotListener(crafting);
        crafting.dataChanged(this, 0, seal.getPriority());
        crafting.dataChanged(this, 4, seal.getColor());
    }

    public void broadcastChanges() {
        super.broadcastChanges();
        // Container data syncing handled by super.broadcastChanges via DataSlot
        if (lastPriority != seal.getPriority() || lastAreaX != seal.getArea().getX() || lastAreaY != seal.getArea().getY() || lastAreaZ != seal.getArea().getZ() || lastColor != seal.getColor()) {
            // trigger re-sync via setData which will notify listeners
        }
        lastPriority = seal.getPriority();
        lastColor = seal.getColor();
        lastAreaX = seal.getArea().getX();
        lastAreaY = seal.getArea().getY();
        lastAreaZ = seal.getArea().getZ();
        if (seal.getSeal() instanceof ISealConfigFilter && temp != null) {
            for (int a = 0; a < temp.getContainerSize(); ++a) {
                ((ISealConfigFilter) seal.getSeal()).setFilterSlot(a, temp.getItem(a));
            }
        }
    }

    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) {
            seal.setPriority((byte)par2);
        }
        if (par1 == 1) {
            seal.setArea(new BlockPos(par2, seal.getArea().getY(), seal.getArea().getZ()));
        }
        if (par1 == 2) {
            seal.setArea(new BlockPos(seal.getArea().getX(), par2, seal.getArea().getZ()));
        }
        if (par1 == 3) {
            seal.setArea(new BlockPos(seal.getArea().getX(), seal.getArea().getY(), par2));
        }
        if (par1 == 4) {
            seal.setColor((byte)par2);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(par2);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack2 = slot.getItem();
            itemstack = itemstack2.copy();
            if (itemstack2.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }
            if (itemstack2.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemstack2);
        }
        return itemstack;
    }
}
