package thaumcraft.common.tiles;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;


public class TileThaumcraftInventory extends TileThaumcraft implements WorldlyContainer
{
    private NonNullList<ItemStack> stacks;
    protected int[] syncedSlots;
    private NonNullList<ItemStack> syncedStacks;
    protected String customName;
    private int[] faceSlots;
    boolean initial;
    IItemHandler handlerTop;
    IItemHandler handlerBottom;
    IItemHandler handlerWest;
    IItemHandler handlerEast;
    IItemHandler handlerNorth;
    IItemHandler handlerSouth;

    public TileThaumcraftInventory(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
        super(type, pos, state);
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        syncedSlots = new int[0];
        syncedStacks = NonNullList.withSize(size, ItemStack.EMPTY);
        initial = true;
        handlerTop = new SidedInvWrapper(this, Direction.UP);
        handlerBottom = new SidedInvWrapper(this, Direction.DOWN);
        handlerWest = new SidedInvWrapper(this, Direction.WEST);
        handlerEast = new SidedInvWrapper(this, Direction.EAST);
        handlerNorth = new SidedInvWrapper(this, Direction.NORTH);
        handlerSouth = new SidedInvWrapper(this, Direction.SOUTH);
        faceSlots = new int[size];
        for (int a = 0; a < size; ++a) {
            faceSlots[a] = a;
        }
    }

    protected TileThaumcraftInventory(int size) {
        this(null, BlockPos.ZERO, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), size);
    }

    public int getSizeInventory() {
        return stacks.size();
    }

    @Override
    public int getContainerSize() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : stacks) {
            if (!itemstack.isEmpty()) return false;
        }
        return true;
    }

    protected NonNullList<ItemStack> getItems() {
        return stacks;
    }

    public ItemStack getSyncedStackInSlot(int index) {
        return syncedStacks.get(index);
    }

    @Override
    public ItemStack getItem(int index) {
        return getItems().get(index);
    }

    public ItemStack getStackInSlot(int index) {
        return getItem(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = net.minecraft.world.ContainerHelper.removeItem(getItems(), index, count);
        if (!itemstack.isEmpty() && isSyncedSlot(index)) syncSlots(null);
        setChanged();
        return itemstack;
    }

    public ItemStack decrStackSize(int index, int count) {
        return removeItem(index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack s = net.minecraft.world.ContainerHelper.takeItem(getItems(), index);
        if (isSyncedSlot(index)) syncSlots(null);
        setChanged();
        return s;
    }

    public ItemStack removeStackFromSlot(int index) {
        return removeItemNoUpdate(index);
    }

    @Override
    public void setItem(int index, @Nullable ItemStack stack) {
        if (stack == null) stack = ItemStack.EMPTY;
        getItems().set(index, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
        if (isSyncedSlot(index)) syncSlots(null);
    }

    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        setItem(index, stack);
    }

    public boolean hasCustomName() {
        return customName != null && customName.length() > 0;
    }

    private boolean isSyncedSlot(int slot) {
        for (int s : syncedSlots) {
            if (s == slot) return true;
        }
        return false;
    }

    protected void syncSlots(ServerPlayer player) {
        // sync stubbed — ItemStack serialization requires HolderLookup.Provider
    }

    @Override
    public void syncTile(boolean rerender) {
        super.syncTile(rerender);
        syncSlots(null);
    }

    @Override
    public void messageFromClient(CompoundTag nbt, ServerPlayer player) {
        super.messageFromClient(nbt, player);
        if (nbt.contains("requestSync")) syncSlots(player);
    }

    @Override
    public void messageFromServer(CompoundTag nbt) {
        super.messageFromServer(nbt);
        // sync stubbed — ItemStack deserialization requires HolderLookup.Provider
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
        net.minecraft.world.ContainerHelper.loadAllItems(input,
            stacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
        net.minecraft.world.ContainerHelper.saveAllItems(output, stacks);
    }

    @Override
    public boolean stillValid(Player par1Player) {
        if (level == null) return false;
        return getLevel().getBlockEntity(worldPosition) == this
            && par1Player.distanceToSqr(worldPosition.getCenter()) <= 64.0;
    }

    public boolean isItemValidForSlot(int par1, ItemStack stack2) {
        return true;
    }

    @Override
    public int[] getSlotsForFace(Direction par1) {
        return faceSlots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int par1, ItemStack stack2, @Nullable Direction par3) {
        return isItemValidForSlot(par1, stack2);
    }

    @Override
    public boolean canTakeItemThroughFace(int par1, ItemStack stack2, Direction par3) {
        return true;
    }

    public boolean canInsertItem(int par1, ItemStack stack2, Direction par3) {
        return isItemValidForSlot(par1, stack2);
    }

    public boolean canExtractItem(int par1, ItemStack stack2, Direction par3) {
        return true;
    }

    @Override
    public void clearContent() {
        stacks.replaceAll(s -> ItemStack.EMPTY);
    }

    public void update() {
        if (initial) {
            initial = false;
            if (level != null && !getLevel().isClientSide()) {
                syncSlots(null);
            } else if (level != null) {
                CompoundTag nbt = new CompoundTag();
                nbt.putBoolean("requestSync", true);
                sendMessageToServer(nbt);
            }
        }
    }

    public void openInventory(Player player) { }

    public void closeInventory(Player player) { }
}
