package thaumcraft.common.container;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.common.container.slot.SlotGhostFull;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.seals.SealProvide;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketItemToClientContainer;


public class ContainerLogistics extends AbstractContainerMenu
{
    private Level worldObj;
    Player player;
    public InventoryLogistics input;
    TreeMap<String, ItemStack> items;
    int lastTotal;
    public int start;
    public int end;
    public String searchText;
    int lastStart;
    int lastEnd;
    public boolean updated;
    private final List<ContainerListener> myListeners = new ArrayList<>();

    public ContainerLogistics(Inventory iinventory, Level par2World) {
        super(null, 0);
        player = null;
        input = new InventoryLogistics();
        items = new TreeMap<String, ItemStack>();
        lastTotal = 0;
        start = 0;
        end = 0;
        searchText = "";
        lastStart = 0;
        lastEnd = 0;
        updated = false;
        worldObj = par2World;
        player = iinventory.player;
        for (int a = 0; a < input.getContainerSize(); ++a) {
            addSlot(new SlotGhostFull(input, a, 19 + a % 9 * 19, 19 + a / 9 * 19));
        }
        refreshItemList(true);
    }

    public void refreshItemList(boolean full) {
        int newTotal = lastTotal;
        TreeMap<String, ItemStack> ti = new TreeMap<String, ItemStack>();
        if (full) {
            newTotal = 0;
            CopyOnWriteArrayList<SealEntity> seals = SealHandler.getSealsInRange(worldObj, player.blockPosition(), 32);
            for (SealEntity seal : seals) {
                if (seal.getSeal() instanceof SealProvide && seal.getOwner().equals(player.getUUID().toString())) {
                    IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(worldObj, seal.getSealPos().pos, seal.getSealPos().face);
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        ItemStack stack = handler.getStackInSlot(slot).copy();
                        if (((SealProvide)seal.getSeal()).matchesFilters(stack)) {
                            String displayName = stack.getHoverName().getString();
                            if (searchText.isEmpty() || displayName.toLowerCase().contains(searchText.toLowerCase())) {
                                String key = displayName + "/" + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()) + "/" + stack.getDamageValue();
                                if (ti.containsKey(key)) {
                                    stack.grow(ti.get(key).getCount());
                                }
                                ti.put(key, stack);
                                newTotal += stack.getCount();
                            }
                        }
                    }
                }
            }
        }
        if (lastTotal != newTotal || start != lastStart) {
            lastTotal = newTotal;
            if (full) {
                items = ti;
            }
            input.clearContent();
            int j = 0;
            int q = 0;
            for (String key2 : items.keySet()) {
                if (++j <= start * 9) {
                    continue;
                }
                input.setItem(q, items.get(key2));
                if (++q >= input.getContainerSize()) {
                    break;
                }
            }
            end = items.size() / 9 - 8;
        }
    }

    @Override
    public void addSlotListener(ContainerListener listener) {
        super.addSlotListener(listener);
        myListeners.add(listener);
        listener.dataChanged(this, 0, start);
    }

    @Override
    public void removeSlotListener(ContainerListener listener) {
        super.removeSlotListener(listener);
        myListeners.remove(listener);
    }

    @Override
    public void broadcastChanges() {
        sendLargeSlotsToClient();
        super.broadcastChanges();
        for (ContainerListener icrafting : myListeners) {
            if (lastStart != start) {
                icrafting.dataChanged(this, 0, start);
            }
            if (lastEnd != end) {
                icrafting.dataChanged(this, 1, end);
            }
        }
        lastStart = start;
        lastEnd = end;
    }

    private void sendLargeSlotsToClient() {
        for (int i = 0; i < slots.size(); ++i) {
            if (getSlot(i) instanceof SlotGhostFull) {
                ItemStack itemstack = slots.get(i).getItem();
                if (itemstack.getCount() > itemstack.getMaxStackSize()) {
                    for (ContainerListener l : myListeners) {
                        if (l instanceof ServerPlayer p) {
                            PacketHandler.sendToPlayer(new PacketItemToClientContainer(containerId, i, itemstack), p);
                        }
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setData(int par1, int par2) {
        if (par1 == 0) {
            start = par2;
            updated = true;
        }
        if (par1 == 1) {
            end = par2;
            updated = true;
        }
    }

    @Override
    public boolean clickMenuButton(Player par1Player, int par2) {
        if (par2 == 22) {
            refreshItemList(true);
            return true;
        }
        if (par2 == 0) {
            if (start < items.size() / 9 - 8) {
                ++start;
                refreshItemList(false);
            }
            return true;
        }
        if (par2 == 1) {
            if (start > 0) {
                --start;
                refreshItemList(false);
            }
            return true;
        }
        if (par2 >= 100) {
            int s = par2 - 100;
            if (s >= 0 && s <= items.size() / 9 - 8) {
                start = s;
                refreshItemList(false);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean stillValid(Player var1) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player par1Player, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = slots.get(slot);
        if (slotObject != null && slotObject.hasItem()) {
            ItemStack stackInSlot = slotObject.getItem();
            stack = stackInSlot.copy();
            if (slot < input.getContainerSize()) {
                if (!moveItemStackTo(stackInSlot, input.getContainerSize(), slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stackInSlot, 0, input.getContainerSize(), false)) {
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
