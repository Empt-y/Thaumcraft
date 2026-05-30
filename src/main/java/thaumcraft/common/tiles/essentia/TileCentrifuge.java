package thaumcraft.common.tiles.essentia;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.sounds.SoundSource;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileCentrifuge extends TileThaumcraft implements IAspectContainer, IEssentiaTransport
{
    public Aspect aspectOut;
    public Aspect aspectIn;
    int count;
    int process;
    float rotationSpeed;
    public float rotation;
    
    public TileCentrifuge(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        aspectOut = null;
        aspectIn = null;
        count = 0;
        process = 0;
        rotationSpeed = 0.0f;
        rotation = 0.0f;
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        aspectIn = Aspect.getAspect(nbttagcompound.getStringOr("aspectIn", ""));
        aspectOut = Aspect.getAspect(nbttagcompound.getStringOr("aspectOut", ""));
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        if (aspectIn != null) {
            nbttagcompound.putString("aspectIn", aspectIn.getTag());
        }
        if (aspectOut != null) {
            nbttagcompound.putString("aspectOut", aspectOut.getTag());
        }
        return nbttagcompound;
    }
    
    @Override
    public AspectList getAspects() {
        AspectList al = new AspectList();
        if (aspectOut != null) {
            al.add(aspectOut, 1);
        }
        return al;
    }
    
    @Override
    public int addToContainer(Aspect tt, int am) {
        if (am > 0 && aspectOut == null) {
            aspectOut = tt;
            setChanged();
            getLevel().markAndNotifyBlock(getBlockPos(), getLevel().getChunkAt(getBlockPos()), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3, 512);
            --am;
        }
        return am;
    }
    
    @Override
    public boolean takeFromContainer(Aspect tt, int am) {
        if (aspectOut != null && tt == aspectOut) {
            aspectOut = null;
            setChanged();
            getLevel().markAndNotifyBlock(getBlockPos(), getLevel().getChunkAt(getBlockPos()), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3, 512);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return amt == 1 && tag == aspectOut;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tt : ot.getAspects()) {
            if (tt == aspectOut) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int containerContains(Aspect tag) {
        return (tag == aspectOut) ? 1 : 0;
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
    
    @Override
    public boolean isConnectable(Direction face) {
        return face == Direction.UP || face == Direction.DOWN;
    }
    
    @Override
    public boolean canInputFrom(Direction face) {
        return face == Direction.DOWN;
    }
    
    @Override
    public boolean canOutputTo(Direction face) {
        return face == Direction.UP;
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public Aspect getSuctionType(Direction face) {
        return null;
    }
    
    @Override
    public int getSuctionAmount(Direction face) {
        return (face == Direction.DOWN) ? (gettingPower() ? 0 : ((aspectIn == null) ? 128 : 64)) : 0;
    }
    
    @Override
    public Aspect getEssentiaType(Direction loc) {
        return aspectOut;
    }
    
    @Override
    public int getEssentiaAmount(Direction loc) {
        return (aspectOut != null) ? 1 : 0;
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        return (canOutputTo(face) && takeFromContainer(aspect, amount)) ? amount : 0;
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        if (aspectIn == null && !aspect.isPrimal()) {
            aspectIn = aspect;
            process = 39;
            setChanged();
            getLevel().markAndNotifyBlock(getBlockPos(), getLevel().getChunkAt(getBlockPos()), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3, 512);
            return 1;
        }
        return 0;
    }
    
    public void update() {
        if (!getLevel().isClientSide()) {
            if (!gettingPower()) {
                if (aspectOut == null && aspectIn == null && ++count % 5 == 0) {
                    drawEssentia();
                }
                if (process > 0) {
                    --process;
                }
                if (aspectOut == null && aspectIn != null && process == 0) {
                    processEssentia();
                }
            }
        }
        else {
            if (aspectIn != null && !gettingPower() && rotationSpeed < 20.0f) {
                rotationSpeed += 2.0f;
            }
            if ((aspectIn == null || gettingPower()) && rotationSpeed > 0.0f) {
                rotationSpeed -= 0.5f;
            }
            int pr = (int) rotation;
            rotation += rotationSpeed;
            if (rotation % 180.0f <= 20.0f && pr % 180 >= 160 && rotationSpeed > 0.0f) {
                getLevel().playSound(null, getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, SoundsTC.pump, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }
    
    void processEssentia() {
        Aspect[] comps = aspectIn.getComponents();
        aspectOut = comps[net.minecraft.util.RandomSource.create().nextInt(2)];
        aspectIn = null;
        setChanged();
        getLevel().markAndNotifyBlock(getBlockPos(), getLevel().getChunkAt(getBlockPos()), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3, 512);
    }
    
    void drawEssentia() {
        BlockEntity te = ThaumcraftApiHelper.getConnectableTile(level, getBlockPos(), Direction.DOWN);
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(Direction.UP)) {
                return;
            }
            Aspect ta = null;
            if (ic.getEssentiaAmount(Direction.UP) > 0 && ic.getSuctionAmount(Direction.UP) < getSuctionAmount(Direction.DOWN) && getSuctionAmount(Direction.DOWN) >= ic.getMinimumSuction()) {
                ta = ic.getEssentiaType(Direction.UP);
            }
            if (ta != null && !ta.isPrimal() && ic.getSuctionAmount(Direction.UP) < getSuctionAmount(Direction.DOWN) && ic.takeEssentia(ta, 1, Direction.UP) == 1) {
                aspectIn = ta;
                process = 39;
                setChanged();
                getLevel().markAndNotifyBlock(getBlockPos(), getLevel().getChunkAt(getBlockPos()), getLevel().getBlockState(getBlockPos()), getLevel().getBlockState(getBlockPos()), 3, 512);
            }
        }
    }
    
    @Override
    public void setAspects(AspectList aspects) {
    }
    
    public boolean canRenderBreaking() {
        return true;
    }
}
