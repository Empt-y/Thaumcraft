package thaumcraft.common.golems.seals;
import java.util.Iterator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;


public abstract class SealFiltered implements ISeal, ISealGui, ISealConfigFilter
{
    NonNullList<ItemStack> filter;
    NonNullList<Integer> filterSize;
    boolean blacklist;
    
    public SealFiltered() {
        filter = NonNullList.withSize(getFilterSize(), ItemStack.EMPTY);
        filterSize = NonNullList.withSize(getFilterSize(), 0);
        blacklist = true;
    }
    
    @Override
    public void readCustomNBT(CompoundTag nbt) {
        filter = NonNullList.withSize(getFilterSize(), ItemStack.EMPTY);
        net.minecraft.nbt.ListTag filterItems = nbt.getListOrEmpty("FilterItems");
        for (int i = 0; i < filterItems.size(); i++) {
            net.minecraft.nbt.CompoundTag slotTag = filterItems.getCompoundOrEmpty(i);
            int slot = slotTag.getByteOr("Slot", (byte)0) & 255;
            if (slot >= 0 && slot < filter.size()) {
                filter.set(slot, net.minecraft.world.item.ItemStack.OPTIONAL_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, slotTag).result().orElse(ItemStack.EMPTY));
            }
        }
        for (ItemStack s : filter) {
            if (s.getCount() > 1) {
                s.setCount(1);
            }
        }
        blacklist = nbt.getBooleanOr("bl", false);
        filterSize = NonNullList.withSize(getFilterSize(), 0);
        ListTag nbttaglist = nbt.getListOrEmpty("Sizes");
        for (int i = 0; i < nbttaglist.size(); ++i) {
            CompoundTag nbttagcompound = nbttaglist.getCompoundOrEmpty(i);
            int j = nbttagcompound.getByteOr("Slot", (byte)0) & 0xFF;
            if (j >= 0 && j < filterSize.size()) {
                filterSize.set(j, nbttagcompound.getIntOr("Size", 0));
            }
        }
    }
    
    @Override
    public void writeCustomNBT(CompoundTag nbt) {
        net.minecraft.nbt.ListTag filterItems = new net.minecraft.nbt.ListTag();
        for (int i = 0; i < filter.size(); i++) {
            ItemStack s = filter.get(i);
            if (!s.isEmpty()) {
                net.minecraft.nbt.CompoundTag slotTag = new net.minecraft.nbt.CompoundTag();
                slotTag.putByte("Slot", (byte)i);
                net.minecraft.world.item.ItemStack.OPTIONAL_CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, s).result().ifPresent(tag -> { if (tag instanceof net.minecraft.nbt.CompoundTag ct) slotTag.merge(ct); });
                filterItems.add(slotTag);
            }
        }
        nbt.put("FilterItems", filterItems);
        nbt.putBoolean("bl", blacklist);
        ListTag nbttaglist = new ListTag();
        for (int i = 0; i < filterSize.size(); ++i) {
            int size = filterSize.get(i);
            if (size != 0) {
                CompoundTag nbttagcompound = new CompoundTag();
                nbttagcompound.putByte("Slot", (byte)i);
                nbttagcompound.putInt("Size", size);
                nbttaglist.add(nbttagcompound);
            }
        }
        nbt.put("Sizes", nbttaglist);
    }
    
    @Override
    public Object returnContainer(Level world, Player player, BlockPos pos, Direction side, ISealEntity seal) {
        return new SealBaseContainer(player.getInventory(), world, seal);
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public Object returnGui(Level world, Player player, BlockPos pos, Direction side, ISealEntity seal) {
        return new thaumcraft.common.golems.client.gui.SealBaseGUI(new thaumcraft.common.golems.client.gui.SealBaseContainer(player.getInventory(), world, seal), player.getInventory(), net.minecraft.network.chat.Component.empty());
    }
    
    @Override
    public int[] getGuiCategories() {
        return new int[] { 0 };
    }
    
    @Override
    public int getFilterSize() {
        return 1;
    }
    
    @Override
    public NonNullList<ItemStack> getInv() {
        return filter;
    }
    
    @Override
    public NonNullList<Integer> getSizes() {
        return filterSize;
    }
    
    @Override
    public ItemStack getFilterSlot(int i) {
        return filter.get(i);
    }
    
    @Override
    public int getFilterSlotSize(int i) {
        return filterSize.get(i);
    }
    
    @Override
    public void setFilterSlot(int i, ItemStack stack) {
        filter.set(i, stack.copy());
    }
    
    @Override
    public void setFilterSlotSize(int i, int size) {
        filterSize.set(i, size);
    }
    
    @Override
    public boolean isBlacklist() {
        return blacklist;
    }
    
    @Override
    public void setBlacklist(boolean black) {
        blacklist = black;
    }
    
    @Override
    public boolean hasStacksizeLimiters() {
        return false;
    }
}
