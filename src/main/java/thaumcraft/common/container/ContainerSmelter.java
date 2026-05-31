package thaumcraft.common.container;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.container.slot.SlotLimitedHasAspects;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.essentia.TileSmelter;


public class ContainerSmelter extends AbstractContainerMenu
{
    private TileSmelter furnace;

    public ContainerSmelter(int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        this(TCMenuTypes.SMELTER.get(), id, inv,
            (TileSmelter) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public ContainerSmelter(MenuType<ContainerSmelter> type, int id, Inventory par1InventoryPlayer, TileSmelter tileEntity) {
        super(type, id);
        furnace = tileEntity;
        addSlot(new SlotLimitedHasAspects(tileEntity, 0, 80, 8));
        addSlot(new Slot(tileEntity, 1, 80, 48));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
        addDataSlots(new ContainerData() {
            @Override public int get(int i) {
                return switch (i) {
                    case 0 -> furnace.furnaceCookTime;
                    case 1 -> furnace.furnaceBurnTime;
                    case 2 -> furnace.currentItemBurnTime;
                    case 3 -> furnace.vis;
                    case 4 -> furnace.smeltTime;
                    default -> 0;
                };
            }
            @Override public void set(int i, int v) {
                switch (i) {
                    case 0 -> furnace.furnaceCookTime = v;
                    case 1 -> furnace.furnaceBurnTime = v;
                    case 2 -> furnace.currentItemBurnTime = v;
                    case 3 -> furnace.vis = v;
                    case 4 -> furnace.smeltTime = v;
                }
            }
            @Override public int getCount() { return 5; }
        });
    }

    @Override
    public boolean stillValid(Player par1Player) {
        return furnace.stillValid(par1Player);
    }

    @Override
    public ItemStack quickMoveStack(Player par1Player, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(par2);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack2 = slot.getItem();
            itemstack = itemstack2.copy();
            if (par2 != 1 && par2 != 0) {
                AspectList al = null /* CraftingManager removed */;
                if (furnace.isItemFuel(itemstack2)) {
                    if (!moveItemStackTo(itemstack2, 1, 2, false) && !moveItemStackTo(itemstack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (al != null && al.size() > 0) {
                    if (!moveItemStackTo(itemstack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (par2 >= 2 && par2 < 29) {
                    if (!moveItemStackTo(itemstack2, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (par2 >= 29 && par2 < 38 && !moveItemStackTo(itemstack2, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!moveItemStackTo(itemstack2, 2, 38, false)) {
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

    public TileSmelter getTile() { return furnace; }
}
