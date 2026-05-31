package thaumcraft.common.tiles.devices;
import net.minecraft.world.Container;
import java.util.ArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
// removed: import net.minecraftforge.common.DimensionManager;
// FML FMLCommonHandler removed
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileMirror extends TileThaumcraft implements Container
{
    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) { return true; }
    @Override
    public int getContainerSize() { return 0; }
    @Override
    public boolean isEmpty() { return true; }
    @Override
    public net.minecraft.world.item.ItemStack getItem(int slot) { return net.minecraft.world.item.ItemStack.EMPTY; }
    @Override
    public net.minecraft.world.item.ItemStack removeItem(int slot, int count) { return net.minecraft.world.item.ItemStack.EMPTY; }
    @Override
    public net.minecraft.world.item.ItemStack removeItemNoUpdate(int slot) { return net.minecraft.world.item.ItemStack.EMPTY; }
    @Override
    public void setItem(int slot, net.minecraft.world.item.ItemStack itemStack) {}
    @Override
    public void clearContent() {}

    public boolean linked;
    public int linkX;
    public int linkY;
    public int linkZ;
    public int linkDim;
    public int instability;
    int count;
    int inc;
    private ArrayList<ItemStack> outputStacks;
    
    public TileMirror(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        linked = false;
        count = 0;
        inc = 40;
        outputStacks = new ArrayList<ItemStack>();
    }
    
    public void restoreLink() {
        if (isDestinationValid()) {
            Level targetWorld = null /* FMLCommonHandler removed */;
            if (targetWorld == null) {
                return;
            }
            BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
            if (te != null && te instanceof TileMirror) {
                TileMirror tm = (TileMirror)te;
                tm.linked = true;
                tm.linkX = getBlockPos().getX();
                tm.linkY = getBlockPos().getY();
                tm.linkZ = getBlockPos().getZ();
                tm.linkDim = (this.level instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)this.level).dimension().identifier().hashCode() : 0);
                tm.syncTile(false);
                linked = true;
                setChanged();
                tm.setChanged();
                syncTile(false);
            }
        }
    }
    
    public void invalidateLink() {
        Level targetWorld = null /* TODO: DimensionManager removed */;
        if (targetWorld == null) {
            return;
        }
        if (!Utils.isChunkLoaded(targetWorld, linkX, linkZ)) {
            return;
        }
        BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
        if (te != null && te instanceof TileMirror) {
            TileMirror tm = (TileMirror)te;
            tm.linked = false;
            setChanged();
            tm.setChanged();
            tm.syncTile(false);
        }
    }
    
    public boolean isLinkValid() {
        if (!linked) {
            return false;
        }
        Level targetWorld = null /* TODO: DimensionManager removed */;
        if (targetWorld == null) {
            return false;
        }
        BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
        if (te == null || !(te instanceof TileMirror)) {
            linked = false;
            setChanged();
            syncTile(false);
            return false;
        }
        TileMirror tm = (TileMirror)te;
        if (!tm.linked) {
            linked = false;
            setChanged();
            syncTile(false);
            return false;
        }
        if (tm.linkX != getBlockPos().getX() || tm.linkY != getBlockPos().getY() || tm.linkZ != getBlockPos().getZ() || tm.linkDim != (this.level instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)this.level).dimension().identifier().hashCode() : 0)) {
            linked = false;
            setChanged();
            syncTile(false);
            return false;
        }
        return true;
    }
    
    public boolean isLinkValidSimple() {
        if (!linked) {
            return false;
        }
        Level targetWorld = null /* TODO: DimensionManager removed */;
        if (targetWorld == null) {
            return false;
        }
        BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
        if (te == null || !(te instanceof TileMirror)) {
            return false;
        }
        TileMirror tm = (TileMirror)te;
        return tm.linked && tm.linkX == getBlockPos().getX() && tm.linkY == getBlockPos().getY() && tm.linkZ == getBlockPos().getZ() && tm.linkDim == (this.level instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)this.level).dimension().identifier().hashCode() : 0);
    }
    
    public boolean isDestinationValid() {
        Level targetWorld = null /* TODO: DimensionManager removed */;
        if (targetWorld == null) {
            return false;
        }
        BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
        if (te == null || !(te instanceof TileMirror)) {
            linked = false;
            setChanged();
            syncTile(false);
            return false;
        }
        TileMirror tm = (TileMirror)te;
        return !tm.isLinkValid();
    }
    
    public boolean transport(ItemEntity ie) {
        ItemStack items = ie.getItem();
        if (!linked || !isLinkValid()) {
            return false;
        }
        Level world = null /* FMLCommonHandler removed */;
        BlockEntity target = getLevel().getBlockEntity(new BlockPos(linkX, linkY, linkZ));
        if (target != null && target instanceof TileMirror) {
            ((TileMirror)target).addStack(items);
            addInstability(null, items.getCount());
            ie.discard();
            setChanged();
            target.setChanged();
            getLevel().blockEvent(getBlockPos(), getBlockState().getBlock(), 1, 0);
            return true;
        }
        return false;
    }
    
    public boolean transportDirect(ItemStack items) {
        if (items == null || items.isEmpty() || items.getCount() <= 0) {
            return false;
        }
        addStack(items.copy());
        setChanged();
        return true;
    }
    
    public void eject() {
        if (outputStacks.size() > 0 && count > 20) {
            int i = this.level.getRandom().nextInt(outputStacks.size());
            if (outputStacks.get(i) != null && !outputStacks.get(i).isEmpty()) {
                ItemStack outItem = outputStacks.get(i);
                outItem.setCount(1);
                if (spawnItem(outItem)) {
                    outputStacks.get(i).shrink(1);
                    addInstability(null, 1);
                    getLevel().blockEvent(getBlockPos(), getBlockState().getBlock(), 1, 0);
                    if (outputStacks.get(i).getCount() <= 0) {
                        outputStacks.remove(i);
                    }
                }
            }
            else {
                outputStacks.remove(i);
            }
            setChanged();
        }
    }
    
    public boolean spawnItem(ItemStack stack) {
        try {
            Direction face = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
            ItemEntity ie2 = new ItemEntity(level, getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.25, getBlockPos().getZ() + 0.5, stack);
            ie2.setDeltaMovement(face.getStepX() * 0.15f, ie2.getDeltaMovement().y, ie2.getDeltaMovement().z);
            ie2.setDeltaMovement(ie2.getDeltaMovement().x, face.getStepY() * 0.15f, ie2.getDeltaMovement().z);
            ie2.setDeltaMovement(ie2.getDeltaMovement().x, ie2.getDeltaMovement().y, face.getStepZ() * 0.15f);
            
            getLevel().addFreshEntity(ie2);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    protected void addInstability(Level targetWorld, int amt) {
        instability += amt;
        setChanged();
        if (targetWorld != null) {
            BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
            if (te != null && te instanceof TileMirror) {
                TileMirror tileMirror = (TileMirror)te;
                tileMirror.instability += amt;
                if (((TileMirror)te).instability < 0) {
                    ((TileMirror)te).instability = 0;
                }
                te.setChanged();
            }
        }
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        linked = nbttagcompound.getBooleanOr("linked", false);
        linkX = nbttagcompound.getIntOr("linkX", 0);
        linkY = nbttagcompound.getIntOr("linkY", 0);
        linkZ = nbttagcompound.getIntOr("linkZ", 0);
        linkDim = nbttagcompound.getIntOr("linkDim", 0);
        instability = nbttagcompound.getIntOr("instability", 0);
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        super.writeSyncNBT(nbttagcompound);
        nbttagcompound.putBoolean("linked", linked);
        nbttagcompound.putInt("linkX", linkX);
        nbttagcompound.putInt("linkY", linkY);
        nbttagcompound.putInt("linkZ", linkZ);
        nbttagcompound.putInt("linkDim", linkDim);
        nbttagcompound.putInt("instability", instability);
        return nbttagcompound;
    }
    
    public boolean triggerEvent(int i, int j) {
        if (i == 1) {
            if (getLevel().isClientSide()) {
                Direction face = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
                double xx = getBlockPos().getX() + 0.33 + net.minecraft.util.RandomSource.create().nextFloat() * 0.33f - face.getStepX() / 2.0;
                double yy = getBlockPos().getY() + 0.33 + net.minecraft.util.RandomSource.create().nextFloat() * 0.33f - face.getStepY() / 2.0;
                double zz = getBlockPos().getZ() + 0.33 + net.minecraft.util.RandomSource.create().nextFloat() * 0.33f - face.getStepZ() / 2.0;
                FXDispatcher.INSTANCE.drawWispyMotes(xx, yy, zz, face.getStepX() / 50.0 + this.level.getRandom().nextGaussian() * 0.01, face.getStepY() / 50.0 + this.level.getRandom().nextGaussian() * 0.01, face.getStepZ() / 50.0 + this.level.getRandom().nextGaussian() * 0.01, (this.level.getRandom().nextInt(21) + 10), net.minecraft.util.RandomSource.create().nextFloat() / 3.0f, 0.0f, net.minecraft.util.RandomSource.create().nextFloat() / 2.0f, (float)(this.level.getRandom().nextGaussian() * 0.01));
            }
            return true;
        }
        return super.triggerEvent(i, j);
    }
    
    public void update() {
        if (!getLevel().isClientSide()) {
            eject();
            checkInstability();
            if (count++ % inc == 0) {
                if (!isLinkValidSimple()) {
                    if (inc < 600) {
                        inc += 20;
                    }
                    restoreLink();
                }
                else {
                    inc = 40;
                }
            }
        }
    }
    
    public void checkInstability() {
        if (instability > 128) {
            AuraHelper.polluteAura(level, getBlockPos(), 1.0f, true);
            instability -= 128;
            setChanged();
        }
        if (instability > 0 && count % 100 == 0) {
            --instability;
        }
    }
    
        public void loadAdditional(CompoundTag nbtCompound) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
        ListTag nbttaglist = nbtCompound.getListOrEmpty("Items");
        outputStacks = new ArrayList<ItemStack>();
        for (int i = 0; i < nbttaglist.size(); ++i) {
            CompoundTag nbttagcompound1 = nbttaglist.getCompoundOrEmpty(i);
            byte b0 = nbttagcompound1.getByteOr("Slot", (byte)0);
            outputStacks.add(ItemStack.OPTIONAL_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, nbttagcompound1).result().orElse(ItemStack.EMPTY));
        }
    }
    
        public CompoundTag saveAdditional(CompoundTag nbtCompound) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
        ListTag nbttaglist = new ListTag();
        for (int i = 0; i < outputStacks.size(); ++i) {
            if (outputStacks.get(i) != null && outputStacks.get(i).getCount() > 0) {
                CompoundTag nbttagcompound1 = new CompoundTag();
                nbttagcompound1.putByte("Slot", (byte)i);
                { net.minecraft.nbt.Tag _tag = ItemStack.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, outputStacks.get(i)).getOrThrow(); if (_tag instanceof net.minecraft.nbt.CompoundTag _ct) nbttagcompound1.merge(_ct); }
                nbttaglist.add(nbttagcompound1);
            }
        }
        nbtCompound.put("Items", nbttaglist);
        return nbtCompound;
    }
    
    public int getSizeInventory() {
        return 1;
    }
    
    public ItemStack getStackInSlot(int par1) {
        return ItemStack.EMPTY;
    }
    
    public ItemStack decrStackSize(int par1, int par2) {
        return ItemStack.EMPTY;
    }
    
    public ItemStack removeStackFromSlot(int par1) {
        return ItemStack.EMPTY;
    }
    
    public void addStack(ItemStack stack) {
        outputStacks.add(stack);
        setChanged();
    }
    
    public void setInventorySlotContents(int par1, ItemStack stack2) {
        Level world = null /* FMLCommonHandler removed */;
        BlockEntity target = this.level.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
        if (target != null && target instanceof TileMirror) {
            ((TileMirror)target).addStack(stack2.copy());
            addInstability(null, stack2.getCount());
            this.level.blockEvent(getBlockPos(), getBlockState().getBlock(), 1, 0);
        }
        else {
            spawnItem(stack2.copy());
        }
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUsableByPlayer(Player var1) {
        return false;
    }
    
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        Level world = null /* FMLCommonHandler removed */;
        BlockEntity target = this.level.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
        return target != null && target instanceof TileMirror;
    }
    
    public String getName() {
        return null;
    }
    
    public boolean hasCustomName() {
        return false;
    }
    
    public Component getDisplayName() {
        return null;
    }
    
    public void openInventory(Player player) {
    }
    
    public void closeInventory(Player player) {
    }
    
    public int getField(int id) {
        return 0;
    }
    
    public void setField(int id, int value) {
    }
    
    public int getFieldCount() {
        return 0;
    }
    
}

