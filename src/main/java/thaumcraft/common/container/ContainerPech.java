package thaumcraft.common.container;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.container.slot.SlotOutput;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.lib.SoundsTC;


public class ContainerPech extends AbstractContainerMenu
{
    private EntityPech pech;
    private InventoryPech inventory;
    private Player player;
    private Level theWorld;
    
    public ContainerPech(Inventory par1InventoryPlayer, Level par3World, EntityPech par2IMerchant) {
        super(null, 0);
        pech = par2IMerchant;
        theWorld = par3World;
        player = par1InventoryPlayer.player;
        inventory = new InventoryPech(par1InventoryPlayer.player, par2IMerchant);
        pech.trading = true;
        addSlot(new Slot(inventory, 0, 36, 29));
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                addSlot(new SlotOutput(inventory, 1 + j + i * 2, 106 + 18 * j, 20 + 18 * i));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }
    
    public InventoryPech getMerchantInventory() {
        return inventory;
    }
    
    @Override
    public boolean clickMenuButton(Player par1Player, int par2) {
        if (par2 == 0) {
            generateContents();
            return true;
        }
        return super.clickMenuButton(par1Player, par2);
    }
    
    private boolean hasStuffInPack() {
        for (ItemStack stack : pech.loot) {
            if (stack != null && !stack.isEmpty() && stack.getCount() > 0) {
                return true;
            }
        }
        return false;
    }
    
    private void generateContents() {
        if (!theWorld.isClientSide() && !inventory.getItem(0).isEmpty() && inventory.getItem(1).isEmpty() && inventory.getItem(2).isEmpty() && inventory.getItem(3).isEmpty() && inventory.getItem(4).isEmpty() && pech.isValued(inventory.getItem(0))) {
            int value = pech.getValue(inventory.getItem(0));
            if (theWorld.getRandom().nextInt(100) <= value / 2) {
                pech.setTamed(false);
                pech.playSound(SoundsTC.pech_trade, 0.4f, 1.0f);
            }
            if (theWorld.getRandom().nextInt(5) == 0) {
                value += theWorld.getRandom().nextInt(3);
            }
            else if (theWorld.getRandom().nextBoolean()) {
                value -= theWorld.getRandom().nextInt(3);
            }
            EntityPech pech = this.pech;
            ArrayList<List> pos = EntityPech.tradeInventory.get(this.pech.getPechType());
            while (value > 0) {
                int am = Math.min(5, Math.max((value + 1) / 2, theWorld.getRandom().nextInt(value) + 1));
                value -= am;
                if (am == 1 && theWorld.getRandom().nextBoolean() && hasStuffInPack()) {
                    ArrayList<Integer> loot = new ArrayList<Integer>();
                    for (int a = 0; a < this.pech.loot.size(); ++a) {
                        if (this.pech.loot.get(a) != null && !this.pech.loot.get(a).isEmpty() && this.pech.loot.get(a).getCount() > 0) {
                            loot.add(a);
                        }
                    }
                    int r = loot.get(theWorld.getRandom().nextInt(loot.size()));
                    ItemStack is = this.pech.loot.get(r);
                    is.setCount(1);
                    addStack(is);
                    this.pech.loot.get(r).shrink(1);
                    if (this.pech.loot.get(r).getCount() > 0) {
                        continue;
                    }
                    this.pech.loot.set(r, ItemStack.EMPTY);
                }
                else {
                    if (am >= 4 && theWorld.getRandom().nextBoolean()) {
                        continue;
                    }
                    List it = null;
                    do {
                        it = pos.get(theWorld.getRandom().nextInt(pos.size()));
                    } while ((int)it.get(0) != am);
                    ItemStack is2 = ((ItemStack)it.get(1)).copy();
                    // onCrafting removed in 1.21
                    addStack(is2);
                }
            }
            inventory.removeItem(0, 1);
        }
    }
    
    private void addStack(ItemStack s) {
        for (int a = 1; a < 5; ++a) {
            if (inventory.getItem(a).isEmpty()) {
                inventory.setItem(a, s);
                break;
            }
            if (inventory.getItem(a).is(s.getItem()) && inventory.getItem(a).getCount() + s.getCount() < inventory.getItem(a).getMaxStackSize()) {
                inventory.getItem(a).grow(s.getCount());
            }
        }
    }
    
    public void updateProgressBar(int par1, int par2) {
    }
    
    @Override
    public boolean stillValid(Player par1Player) {
        return pech.isTamed();
    }

    @Override
    public ItemStack quickMoveStack(Player par1Player, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(par2);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack2 = slot.getItem();
            itemstack = itemstack2.copy();
            if (par2 == 0) {
                if (!moveItemStackTo(itemstack2, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (par2 >= 1 && par2 < 5) {
                if (!moveItemStackTo(itemstack2, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (par2 != 0 && par2 >= 5 && par2 < 41 && !moveItemStackTo(itemstack2, 0, 1, true)) {
                return ItemStack.EMPTY;
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
    
    @Override
    public void removed(Player par1Player) {
        super.removed(par1Player);
        pech.trading = false;
        if (!theWorld.isClientSide()) {
            for (int a = 0; a < 5; ++a) {
                ItemStack itemstack = inventory.removeItemNoUpdate(a);
                if (!itemstack.isEmpty()) {
                    par1Player.drop(itemstack, false);
                }
            }
        }
    }
}
