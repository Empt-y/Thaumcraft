package thaumcraft.common.tiles.essentia;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileAlembic extends TileThaumcraft implements IAspectContainer, IEssentiaTransport
{
    public Aspect aspect;
    public Aspect aspectFilter;
    public int amount;
    public int maxAmount;
    public int facing;
    public boolean aboveFurnace;
    Direction fd;
    
    public TileAlembic(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        aspectFilter = null;
        amount = 0;
        maxAmount = 128;
        facing = Direction.DOWN.ordinal();
        aboveFurnace = false;
        fd = null;
    }
    
    @Override
    public AspectList getAspects() {
        return (aspect != null) ? new AspectList().add(aspect, amount) : new AspectList();
    }
    
    @Override
    public void setAspects(AspectList aspects) {
    }
    
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().getX() - 0.1, getBlockPos().getY() - 0.1, getBlockPos().getZ() - 0.1, getBlockPos().getX() + 1.1, getBlockPos().getY() + 1.1, getBlockPos().getZ() + 1.1);
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        facing = nbttagcompound.getByteOr("facing", (byte)0);
        aspectFilter = Aspect.getAspect(nbttagcompound.getStringOr("AspectFilter", ""));
        String tag = nbttagcompound.getStringOr("aspect", "");
        if (tag != null) {
            aspect = Aspect.getAspect(tag);
        }
        amount = nbttagcompound.getShortOr("amount", (short)0);
        fd = Direction.values()[facing];
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        if (aspect != null) {
            nbttagcompound.putString("aspect", aspect.getTag());
        }
        if (aspectFilter != null) {
            nbttagcompound.putString("AspectFilter", aspectFilter.getTag());
        }
        nbttagcompound.putShort("amount", (short) amount);
        nbttagcompound.putByte("facing", (byte) facing);
        return nbttagcompound;
    }
    
    @Override
    public int addToContainer(Aspect tt, int am) {
        if (aspectFilter != null && tt != aspectFilter) {
            return am;
        }
        if ((amount < maxAmount && tt == aspect) || amount == 0) {
            aspect = tt;
            int added = Math.min(am, maxAmount - amount);
            amount += added;
            am -= added;
        }
        setChanged();
        syncTile(false);
        return am;
    }
    
    @Override
    public boolean takeFromContainer(Aspect tt, int am) {
        if (amount == 0 || aspect == null) {
            aspect = null;
            amount = 0;
        }
        if (aspect != null && amount >= am && tt == aspect) {
            amount -= am;
            if (amount <= 0) {
                aspect = null;
                amount = 0;
            }
            setChanged();
            syncTile(false);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return amount > 0 && aspect != null && ot.getAmount(aspect) > 0;
    }
    
    @Override
    public boolean doesContainerContainAmount(Aspect tt, int am) {
        return amount >= am && tt == aspect;
    }
    
    @Override
    public int containerContains(Aspect tt) {
        return (tt == aspect) ? amount : 0;
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
    
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean isConnectable(Direction face) {
        return face != Direction.values()[facing] && face != Direction.DOWN;
    }
    
    @Override
    public boolean canInputFrom(Direction face) {
        return false;
    }
    
    @Override
    public boolean canOutputTo(Direction face) {
        return face != Direction.values()[facing] && face != Direction.DOWN;
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
    }
    
    @Override
    public Aspect getSuctionType(Direction loc) {
        return null;
    }
    
    @Override
    public int getSuctionAmount(Direction loc) {
        return 0;
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
    public int addEssentia(Aspect aspect, int amount, Direction loc) {
        return 0;
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    protected static boolean processAlembics(Level world, BlockPos pos, Aspect aspect) {
        int deep = 1;
        while (true) {
            BlockEntity te = world.getBlockEntity(pos.above(deep));
            if (te != null && te instanceof TileAlembic) {
                TileAlembic alembic = (TileAlembic)te;
                if (alembic.amount > 0 && alembic.aspect == aspect && alembic.addToContainer(aspect, 1) == 0) {
                    return true;
                }
                ++deep;
            }
            else {
                deep = 1;
                while (true) {
                    te = world.getBlockEntity(pos.above(deep));
                    if (te == null || !(te instanceof TileAlembic)) {
                        return false;
                    }
                    TileAlembic alembic = (TileAlembic)te;
                    if ((alembic.aspectFilter == null || alembic.aspectFilter == aspect) && alembic.addToContainer(aspect, 1) == 0) {
                        return true;
                    }
                    ++deep;
                }
            }
        }
    }
}
