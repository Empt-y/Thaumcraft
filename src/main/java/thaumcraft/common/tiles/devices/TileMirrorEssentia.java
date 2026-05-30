package thaumcraft.common.tiles.devices;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
// removed: import net.minecraftforge.common.DimensionManager;
// FML FMLCommonHandler removed
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileMirrorEssentia extends TileThaumcraft implements IAspectSource
{
    public boolean linked;
    public int linkX;
    public int linkY;
    public int linkZ;
    public int linkDim;
    public Direction linkedFacing;
    public int instability;
    int count;
    int inc;
    
    public TileMirrorEssentia(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        linked = false;
        linkedFacing = Direction.DOWN;
        count = 0;
        inc = 40;
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        linked = nbttagcompound.getBooleanOr("linked", false);
        linkX = nbttagcompound.getIntOr("linkX", 0);
        linkY = nbttagcompound.getIntOr("linkY", 0);
        linkZ = nbttagcompound.getIntOr("linkZ", 0);
        linkDim = nbttagcompound.getIntOr("linkDim", 0);
        instability = nbttagcompound.getIntOr("instability", 0);
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        nbttagcompound.putBoolean("linked", linked);
        nbttagcompound.putInt("linkX", linkX);
        nbttagcompound.putInt("linkY", linkY);
        nbttagcompound.putInt("linkZ", linkZ);
        nbttagcompound.putInt("linkDim", linkDim);
        nbttagcompound.putInt("instability", instability);
        return nbttagcompound;
    }
    
    protected void addInstability(Level targetWorld, int amt) {
        instability += amt;
        setChanged();
        if (targetWorld != null) {
            BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
            if (te != null && te instanceof TileMirrorEssentia) {
                TileMirrorEssentia tileMirrorEssentia = (TileMirrorEssentia)te;
                tileMirrorEssentia.instability += amt;
                if (((TileMirrorEssentia)te).instability < 0) {
                    ((TileMirrorEssentia)te).instability = 0;
                }
                te.setChanged();
            }
        }
    }
    
    public void restoreLink() {
        if (isDestinationValid()) {
            Level targetWorld = null /* FMLCommonHandler removed */;
            if (targetWorld == null) {
                return;
            }
            BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
            if (te != null && te instanceof TileMirrorEssentia) {
                TileMirrorEssentia tm = (TileMirrorEssentia)te;
                tm.linked = true;
                tm.linkX = getBlockPos().getX();
                tm.linkY = getBlockPos().getY();
                tm.linkZ = getBlockPos().getZ();
                tm.linkDim = (this.level instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)this.level).dimension().identifier().hashCode() : 0);
                tm.syncTile(false);
                linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(linkX, linkY, linkZ)));
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
        if (te != null && te instanceof TileMirrorEssentia) {
            TileMirrorEssentia tm = (TileMirrorEssentia)te;
            tm.linked = false;
            tm.linkedFacing = Direction.DOWN;
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
        if (te == null || !(te instanceof TileMirrorEssentia)) {
            linked = false;
            setChanged();
            syncTile(false);
            return false;
        }
        TileMirrorEssentia tm = (TileMirrorEssentia)te;
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
        if (te == null || !(te instanceof TileMirrorEssentia)) {
            return false;
        }
        TileMirrorEssentia tm = (TileMirrorEssentia)te;
        return tm.linked && tm.linkX == getBlockPos().getX() && tm.linkY == getBlockPos().getY() && tm.linkZ == getBlockPos().getZ() && tm.linkDim == (this.level instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)this.level).dimension().identifier().hashCode() : 0);
    }
    
    public boolean isDestinationValid() {
        Level targetWorld = null /* TODO: DimensionManager removed */;
        if (targetWorld == null) {
            return false;
        }
        BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
        if (te == null || !(te instanceof TileMirrorEssentia)) {
            linked = false;
            setChanged();
            syncTile(false);
            return false;
        }
        TileMirrorEssentia tm = (TileMirrorEssentia)te;
        return !tm.isLinkValid();
    }
    
    public AspectList getAspects() {
        return null;
    }
    
    public void setAspects(AspectList aspects) {
    }
    
    public boolean doesContainerAccept(Aspect tag) {
        Level targetWorld = null /* TODO: DimensionManager removed */;
        if (linkedFacing == Direction.DOWN && targetWorld != null) {
            linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(linkX, linkY, linkZ)));
        }
        BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
        return te == null || !(te instanceof TileMirrorEssentia) || EssentiaHandler.canAcceptEssentia(te, tag, linkedFacing, 8, true);
    }
    
    public int addToContainer(Aspect tag, int amount) {
        if (!isLinkValid() || amount > 1) {
            return amount;
        }
        Level targetWorld = null /* TODO: DimensionManager removed */;
        if (linkedFacing == Direction.DOWN && targetWorld != null) {
            linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(linkX, linkY, linkZ)));
        }
        BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
        if (te != null && te instanceof TileMirrorEssentia) {
            boolean b = EssentiaHandler.addEssentia(te, tag, linkedFacing, 8, true, 5);
            if (b) {
                addInstability(null, amount);
            }
            return b ? 0 : 1;
        }
        return amount;
    }
    
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (!isLinkValid() || amount > 1) {
            return false;
        }
        Level targetWorld = null /* TODO: DimensionManager removed */;
        if (linkedFacing == Direction.DOWN && targetWorld != null) {
            linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(linkX, linkY, linkZ)));
        }
        BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
        if (te != null && te instanceof TileMirrorEssentia) {
            boolean b = EssentiaHandler.drainEssentia(te, tag, linkedFacing, 8, true, 5);
            if (b) {
                addInstability(null, amount);
            }
            return b;
        }
        return false;
    }
    
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        if (!isLinkValid() || amount > 1) {
            return false;
        }
        Level targetWorld = null /* TODO: DimensionManager removed */;
        if (linkedFacing == Direction.DOWN && targetWorld != null) {
            linkedFacing = BlockStateUtils.getFacing(targetWorld.getBlockState(new BlockPos(linkX, linkY, linkZ)));
        }
        BlockEntity te = targetWorld.getBlockEntity(new BlockPos(linkX, linkY, linkZ));
        return te != null && te instanceof TileMirrorEssentia && EssentiaHandler.findEssentia(te, tag, linkedFacing, 8, true);
    }
    
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }
    
    public int containerContains(Aspect tag) {
        return 0;
    }
    
    public void update() {
        if (!getLevel().isClientSide()) {
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
        if (instability > 64) {
            AuraHelper.polluteAura(level, getBlockPos(), 1.0f, true);
            instability -= 64;
            setChanged();
        }
        if (instability > 0 && count % 100 == 0) {
            --instability;
        }
    }
    
    @Override
    public boolean isBlocked() {
        return false;
    }
}
