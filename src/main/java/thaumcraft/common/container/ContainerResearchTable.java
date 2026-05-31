package thaumcraft.common.container;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.container.slot.SlotLimitedByClass;
import thaumcraft.common.container.slot.SlotLimitedByItemstack;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.crafting.TileResearchTable;


public class ContainerResearchTable extends AbstractContainerMenu
{
    public TileResearchTable tileEntity;
    String[] aspects;
    Player player;
    static HashMap<Integer, Long> antiSpam;

    public ContainerResearchTable(int id, Inventory inv, RegistryFriendlyByteBuf buf) {
        this(TCMenuTypes.RESEARCH_TABLE.get(), id, inv,
            (TileResearchTable) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public ContainerResearchTable(MenuType<ContainerResearchTable> type, int id, Inventory iinventory, TileResearchTable iinventory1) {
        super(type, id);
        player = iinventory.player;
        tileEntity = iinventory1;
        aspects = Aspect.aspects.keySet().toArray(new String[0]);
        addSlot(new SlotLimitedByClass(IScribeTools.class, iinventory1, 0, 16, 15));
        addSlot(new SlotLimitedByItemstack(new ItemStack(Items.PAPER), iinventory1, 1, 224, 16));
        bindPlayerInventory(iinventory);
    }

    protected void bindPlayerInventory(Inventory inventoryPlayer) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 77 + j * 18, 190 + i * 18));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                addSlot(new Slot(inventoryPlayer, i + j * 3, 20 + i * 18, 190 + j * 18));
            }
        }
    }
    
    public boolean enchantItem(Player playerIn, int button) {
        if (button == 1) {
            if (tileEntity.data.lastDraw != null) {
                tileEntity.data.savedCards.add(tileEntity.data.lastDraw.card.getSeed());
            }
            for (ResearchTableData.CardChoice cc : tileEntity.data.cardChoices) {
                if (cc.selected) {
                    tileEntity.data.lastDraw = cc;
                    break;
                }
            }
            tileEntity.data.cardChoices.clear();
            tileEntity.syncTile(false);
            return true;
        }
        if (button == 4 || button == 5 || button == 6) {
            long tn = System.currentTimeMillis();
            long to = 0L;
            if (ContainerResearchTable.antiSpam.containsKey(playerIn.getId())) {
                to = ContainerResearchTable.antiSpam.get(playerIn.getId());
            }
            if (tn - to < 333L) {
                return false;
            }
            ContainerResearchTable.antiSpam.put(playerIn.getId(), tn);
            try {
                TheorycraftCard card = tileEntity.data.cardChoices.get(button - 4).card;
                if (card.getRequiredItems() != null) {
                    for (ItemStack stack : card.getRequiredItems()) {
                        if (stack != null && !stack.isEmpty() && !InventoryUtils.isPlayerCarryingAmount(player, stack, true)) {
                            return false;
                        }
                    }
                    if (card.getRequiredItemsConsumed() != null && card.getRequiredItemsConsumed().length == card.getRequiredItems().length) {
                        for (int a = 0; a < card.getRequiredItems().length; ++a) {
                            if (card.getRequiredItemsConsumed()[a] && card.getRequiredItems()[a] != null && !card.getRequiredItems()[a].isEmpty()) {
                                InventoryUtils.consumePlayerItem(player, card.getRequiredItems()[a], true, true);
                            }
                        }
                    }
                }
                if (card.activate(playerIn, tileEntity.data)) {
                    tileEntity.consumeInkFromTable();
                    tileEntity.data.cardChoices.get(button - 4).selected = true;
                    tileEntity.data.addInspiration(-card.getInspirationCost());
                    tileEntity.syncTile(false);
                    return true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (button == 7 && tileEntity.data.isComplete()) {
            tileEntity.finishTheory(playerIn);
            tileEntity.syncTile(false);
            return true;
        }
        if (button == 9 && !tileEntity.data.isComplete()) {
            tileEntity.data = null;
            tileEntity.syncTile(false);
            return true;
        }
        if (button == 2 || button == 3) {
            if (tileEntity.data != null && !tileEntity.data.isComplete() && tileEntity.consumepaperFromTable()) {
                tileEntity.data.drawCards(button, playerIn);
                tileEntity.syncTile(false);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public ItemStack quickMoveStack(Player par1Player, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = slots.get(slot);
        if (slotObject != null && slotObject.hasItem()) {
            ItemStack stackInSlot = slotObject.getItem();
            stack = stackInSlot.copy();
            if (slot < 2) {
                if (!tileEntity.isItemValidForSlot(slot, stackInSlot) || !moveItemStackTo(stackInSlot, 2, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!tileEntity.isItemValidForSlot(slot, stackInSlot) || !moveItemStackTo(stackInSlot, 0, 2, false)) {
                return ItemStack.EMPTY;
            }
            if (stackInSlot.getCount() == 0) {
                slotObject.set(ItemStack.EMPTY);
            }
            else {
                slotObject.setChanged();
            }
        }
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return tileEntity.stillValid(player);
    }
    
    static {
        ContainerResearchTable.antiSpam = new HashMap<Integer, Long>();
    }
}
