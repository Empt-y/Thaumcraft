package thaumcraft.common.tiles.devices;
import java.util.ArrayList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.devices.BlockCondenserLattice;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileCondenser extends TileThaumcraft implements IEssentiaTransport
{
    private int essentia;
    private int flux;
    private int MAX;
    private int count;
    private ArrayList<Long> history;
    private ArrayList<Long> blockList;
    private ArrayList<Long> uncloggedList;
    public float latticeCount;
    public int interval;
    public int cost;
    
    public TileCondenser(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        MAX = 100;
        count = 0;
        history = new ArrayList<Long>();
        blockList = new ArrayList<Long>();
        uncloggedList = new ArrayList<Long>();
        latticeCount = -1.0f;
        interval = 0;
        cost = 0;
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        essentia = nbttagcompound.getShortOr("essentia", (short)0);
        flux = nbttagcompound.getShortOr("flux", (short)0);
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        nbttagcompound.putShort("essentia", (short) essentia);
        nbttagcompound.putShort("flux", (short) flux);
        return nbttagcompound;
    }
    
    public void update() {
        if (latticeCount < 0.0f) {
            triggerCheck();
        }
        ++count;
        if (BlockStateUtils.isEnabled(getBlockState()) && latticeCount > 0.0f) {
            if (getLevel().isClientSide()) {
                if (essentia > 0 && uncloggedList.size() > 0 && count % Math.max(3, interval / 50) == 0) {
                    BlockPos p = BlockPos.of(uncloggedList.get(this.level.getRandom().nextInt(uncloggedList.size())));
                    if (p != null) {
                        FXDispatcher.INSTANCE.spark(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, 4.5f + net.minecraft.util.RandomSource.create().nextFloat(), 0.33f + net.minecraft.util.RandomSource.create().nextFloat() * 0.66f, 0.33f + net.minecraft.util.RandomSource.create().nextFloat() * 0.66f, 0.33f + net.minecraft.util.RandomSource.create().nextFloat() * 0.66f, 0.8f);
                    }
                }
            }
            else {
                if (count % 5 == 0 && essentia < MAX) {
                    fill();
                }
                if (interval > 0 && essentia >= cost && flux < MAX && count % interval == 0 && AuraHelper.getFlux(getLevel(), getBlockPos()) >= 1.0f) {
                    AuraHelper.drainFlux(getLevel(), getBlockPos(), 1.0f, false);
                    essentia -= cost;
                    ++flux;
                    if (net.minecraft.util.RandomSource.create().nextInt(50) == 0) {
                        makeLatticeDirty();
                    }
                    syncTile(false);
                    setChanged();
                }
            }
        }
    }
    
    private void makeLatticeDirty() {
        if (uncloggedList.size() > 0) {
            int q = this.level.getRandom().nextInt(uncloggedList.size());
            if (q == 0) {
                q = this.level.getRandom().nextInt(uncloggedList.size());
            }
            BlockPos p = BlockPos.of(uncloggedList.get(q));
            if (p != null) {
                BlockState bs = getLevel().getBlockState(p);
                if (bs.getBlock() == BlocksTC.condenserlattice) {
                    getLevel().setBlock(p, BlocksTC.condenserlatticeDirty.defaultBlockState(), 3);
                    ((BlockCondenserLattice)bs.getBlock()).triggerUpdate(level, p);
                }
            }
        }
    }
    
    private void fill() {
        for (Direction face : Direction.Plane.HORIZONTAL) {
            BlockEntity te = ThaumcraftApiHelper.getConnectableTile(level, getBlockPos(), face);
            if (te != null) {
                IEssentiaTransport ic = (IEssentiaTransport)te;
                Aspect ta = null;
                if (!ic.canOutputTo(face.getOpposite())) {
                    return;
                }
                if (ic.getEssentiaAmount(face.getOpposite()) > 0 && ic.getSuctionAmount(face.getOpposite()) < getSuctionAmount(face) && getSuctionAmount(face) >= ic.getMinimumSuction()) {
                    ta = ic.getEssentiaType(face.getOpposite());
                }
                if (ta != null) {
                    if (ta != Aspect.FLUX) {
                        essentia += ic.takeEssentia(ta, 1, face.getOpposite());
                    }
                    else {
                        makeLatticeDirty();
                    }
                    syncTile(false);
                    setChanged();
                    if (essentia >= MAX) {
                        break;
                    }
                }
            }
        }
    }
    
    public void triggerCheck() {
        history.clear();
        blockList.clear();
        uncloggedList.clear();
        latticeCount = 0.0f;
        interval = 0;
        performCheck(getBlockPos(), true, false);
        history.clear();
        if (latticeCount <= 0.0f) {
            latticeCount = 0.0f;
        }
        else {
            if (latticeCount > 40.0f) {
                latticeCount = 40.0f;
            }
            interval = Math.round(600.0f - latticeCount * 15.0f);
            if (interval < 5) {
                interval = 5;
            }
            cost = (int)(4.0 + Math.sqrt(blockList.size()));
        }
    }
    
    private void performCheck(BlockPos pos, boolean skip, boolean clogged) {
        if (latticeCount < 0.0f) {
            return;
        }
        history.add(getBlockPos().asLong());
        boolean found = false;
        int sides = 0;
        for (Direction face : Direction.values()) {
            if (!skip || face == Direction.UP) {
                BlockPos p2 = getBlockPos().relative(face);
                BlockState bs = getLevel().getBlockState(p2);
                boolean lattice = bs.getBlock() == BlocksTC.condenserlattice;
                boolean latticeDirty = bs.getBlock() == BlocksTC.condenserlatticeDirty;
                if (skip && latticeDirty) {
                    clogged = true;
                }
                if (lattice || latticeDirty) {
                    ++sides;
                }
                if (!history.contains(p2.asLong())) {
                    if (face == Direction.DOWN && getLevel().getBlockState(p2).getBlock() == BlocksTC.condenser) {
                        latticeCount = -99.0f;
                        return;
                    }
                    if (getBlockPos().getY() < p2.getY()) {
                        if (getBlockPos().distSqr(p2) <= 74.0) {
                            if (lattice || latticeDirty) {
                                blockList.add(p2.asLong());
                                if (lattice) {
                                    uncloggedList.add(p2.asLong());
                                }
                                found = true;
                                performCheck(p2, false, clogged || latticeDirty);
                                if (skip) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (found && !clogged) {
            float f = 1.0f - 0.15f * sides;
            latticeCount += f;
        }
    }
    
    public boolean isConnectable(Direction face) {
        return face != Direction.UP;
    }
    
    public boolean canInputFrom(Direction face) {
        return face != Direction.UP && face != Direction.DOWN;
    }
    
    public boolean canOutputTo(Direction face) {
        return face == Direction.DOWN;
    }
    
    public void setSuction(Aspect aspect, int amount) {
    }
    
    public Aspect getSuctionType(Direction face) {
        return null;
    }
    
    public int getSuctionAmount(Direction face) {
        return (face == Direction.DOWN || essentia >= MAX) ? 0 : 128;
    }
    
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        int amt = (canOutputTo(face) && (aspect == null || aspect == Aspect.FLUX)) ? Math.min(amount, flux) : 0;
        if (amt > 0) {
            flux -= amt;
            syncTile(false);
            setChanged();
        }
        return amt;
    }
    
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        int amt = canInputFrom(face) ? Math.min(amount, MAX - essentia) : 0;
        if (amt > 0) {
            syncTile(false);
            setChanged();
        }
        return amt;
    }
    
    public Aspect getEssentiaType(Direction face) {
        return Aspect.FLUX;
    }
    
    public int getEssentiaAmount(Direction face) {
        return flux;
    }
    
    public int getMinimumSuction() {
        return 0;
    }
}
