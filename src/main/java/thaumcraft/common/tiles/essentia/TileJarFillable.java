package thaumcraft.common.tiles.essentia;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;


public class TileJarFillable extends TileJar implements IAspectSource, IEssentiaTransport
{
    public static int CAPACITY = 250;
    public Aspect aspect;
    public Aspect aspectFilter;
    public int amount;
    public int facing;
    public boolean blocked;
    int count;

    public TileJarFillable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        aspect = null;
        aspectFilter = null;
        amount = 0;
        facing = 2;
        blocked = false;
        count = 0;
    }

    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        aspect = Aspect.getAspect(nbttagcompound.getStringOr("Aspect", ""));
        aspectFilter = Aspect.getAspect(nbttagcompound.getStringOr("AspectFilter", ""));
        amount = nbttagcompound.getShortOr("Amount", (short)0);
        facing = nbttagcompound.getByteOr("facing", (byte)0);
        blocked = nbttagcompound.getBooleanOr("blocked", false);
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        if (aspect != null) {
            nbttagcompound.putString("Aspect", aspect.getTag());
        }
        if (aspectFilter != null) {
            nbttagcompound.putString("AspectFilter", aspectFilter.getTag());
        }
        nbttagcompound.putShort("Amount", (short) amount);
        nbttagcompound.putByte("facing", (byte) facing);
        nbttagcompound.putBoolean("blocked", blocked);
        return nbttagcompound;
    }

    public AspectList getAspects() {
        AspectList al = new AspectList();
        if (aspect != null && amount > 0) {
            al.add(aspect, amount);
        }
        return al;
    }

    public void setAspects(AspectList aspects) {
        if (aspects != null && aspects.size() > 0) {
            aspect = aspects.getAspectsSortedByAmount()[0];
            amount = aspects.getAmount(aspects.getAspectsSortedByAmount()[0]);
        }
    }

    public int addToContainer(Aspect tt, int am) {
        if (am == 0) {
            return am;
        }
        if ((amount < 250 && tt == aspect) || amount == 0) {
            aspect = tt;
            int added = Math.min(am, 250 - amount);
            amount += added;
            am -= added;
        }
        syncTile(false);
        setChanged();
        return am;
    }

    public boolean takeFromContainer(Aspect tt, int am) {
        if (amount >= am && tt == aspect) {
            amount -= am;
            if (amount <= 0) {
                aspect = null;
                amount = 0;
            }
            syncTile(false);
            setChanged();
            return true;
        }
        return false;
    }

    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return amount >= amt && tag == aspect;
    }

    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tt : ot.getAspects()) {
            if (amount > 0 && tt == aspect) {
                return true;
            }
        }
        return false;
    }

    public int containerContains(Aspect tag) {
        if (tag == aspect) {
            return amount;
        }
        return 0;
    }

    public boolean doesContainerAccept(Aspect tag) {
        return aspectFilter == null || tag.equals(aspectFilter);
    }

    @Override
    public boolean isConnectable(Direction face) {
        return face == Direction.UP;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return face == Direction.UP;
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
        return (aspectFilter != null) ? 64 : 32;
    }

    @Override
    public Aspect getSuctionType(Direction loc) {
        return (aspectFilter != null) ? aspectFilter : aspect;
    }

    @Override
    public int getSuctionAmount(Direction loc) {
        if (amount >= 250) {
            return 0;
        }
        if (aspectFilter != null) {
            return 64;
        }
        return 32;
    }

    @Override
    public Aspect getEssentiaType(Direction loc) {
        return aspect;
    }

    @Override
    public int getEssentiaAmount(Direction loc) {
        return amount;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        return (canOutputTo(face) && takeFromContainer(aspect, amount)) ? amount : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        return canInputFrom(face) ? (amount - addToContainer(aspect, amount)) : 0;
    }

    @Override
    public void update() {
        if (!getLevel().isClientSide() && ++count % 5 == 0 && amount < 250) {
            fillJar();
        }
    }

    void fillJar() {
        BlockEntity te = ThaumcraftApiHelper.getConnectableTile(this.level, this.worldPosition, Direction.UP);
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(Direction.DOWN)) {
                return;
            }
            Aspect ta = null;
            if (aspectFilter != null) {
                ta = aspectFilter;
            }
            else if (aspect != null && amount > 0) {
                ta = aspect;
            }
            else if (ic.getEssentiaAmount(Direction.DOWN) > 0 && ic.getSuctionAmount(Direction.DOWN) < getSuctionAmount(Direction.UP) && getSuctionAmount(Direction.UP) >= ic.getMinimumSuction()) {
                ta = ic.getEssentiaType(Direction.DOWN);
            }
            if (ta != null && ic.getSuctionAmount(Direction.DOWN) < getSuctionAmount(Direction.UP)) {
                addToContainer(ta, ic.takeEssentia(ta, 1, Direction.DOWN));
            }
        }
    }

    @Override
    public boolean isBlocked() {
        return blocked;
    }
}
