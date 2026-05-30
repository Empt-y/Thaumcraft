package thaumcraft.common.tiles.essentia;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.devices.TileBellows;


public class TileTubeBuffer extends TileTube implements IAspectContainer
{
    public AspectList aspects;
    public int MAXAMOUNT = 10;
    public byte[] chokedSides;
    int count;
    int bellows;
    
    public TileTubeBuffer(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        aspects = new AspectList();
        chokedSides = new byte[] { 0, 0, 0, 0, 0, 0 };
        count = 0;
        bellows = -1;
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        aspects.loadAdditional(nbttagcompound);
        byte[] sides = nbttagcompound.getByteArray("open").orElse(new byte[0]);
        if (sides != null && sides.length == 6) {
            for (int a = 0; a < 6; ++a) {
                openSides[a] = (sides[a] == 1);
            }
        }
        chokedSides = nbttagcompound.getByteArray("choke").orElse(new byte[0]);
        if (chokedSides == null || chokedSides.length < 6) {
            chokedSides = new byte[] { 0, 0, 0, 0, 0, 0 };
        }
        facing = Direction.values()[nbttagcompound.getIntOr("side", 0)];
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        aspects.saveAdditional(nbttagcompound);
        byte[] sides = new byte[6];
        for (int a = 0; a < 6; ++a) {
            sides[a] = (byte)(openSides[a] ? 1 : 0);
        }
        nbttagcompound.putByteArray("open", sides);
        nbttagcompound.putByteArray("choke", chokedSides);
        nbttagcompound.putInt("side", facing.ordinal());
        return nbttagcompound;
    }
    
    @Override
    public AspectList getAspects() {
        return aspects;
    }
    
    @Override
    public void setAspects(AspectList aspects) {
    }
    
    @Override
    public int addToContainer(Aspect tt, int am) {
        if (am != 1) {
            return am;
        }
        if (aspects.visSize() < 10) {
            aspects.add(tt, am);
            setChanged();
            syncTile(false);
            return 0;
        }
        return am;
    }
    
    @Override
    public boolean takeFromContainer(Aspect tt, int am) {
        if (aspects.getAmount(tt) >= am) {
            aspects.remove(tt, am);
            setChanged();
            syncTile(false);
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
        return aspects.getAmount(tag) >= amt;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }
    
    @Override
    public int containerContains(Aspect tag) {
        return aspects.getAmount(tag);
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
    
    @Override
    public boolean isConnectable(Direction face) {
        return openSides[face.ordinal()];
    }
    
    @Override
    public boolean canInputFrom(Direction face) {
        return openSides[face.ordinal()];
    }
    
    @Override
    public boolean canOutputTo(Direction face) {
        return openSides[face.ordinal()];
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public Aspect getSuctionType(Direction loc) {
        return null;
    }
    
    @Override
    public int getSuctionAmount(Direction loc) {
        return (chokedSides[loc.ordinal()] == 2) ? 0 : ((bellows <= 0 || chokedSides[loc.ordinal()] == 1) ? 1 : (bellows * 32));
    }
    
    @Override
    public Aspect getEssentiaType(Direction loc) {
        return (aspects.size() > 0) ? aspects.getAspects()[this.level.getRandom().nextInt(aspects.getAspects().length)] : null;
    }
    
    @Override
    public int getEssentiaAmount(Direction loc) {
        return aspects.visSize();
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        if (!canOutputTo(face)) {
            return 0;
        }
        BlockEntity te = null;
        IEssentiaTransport ic = null;
        int suction = 0;
        te = ThaumcraftApiHelper.getConnectableTile(level, getBlockPos(), face);
        if (te != null) {
            ic = (IEssentiaTransport)te;
            suction = ic.getSuctionAmount(face.getOpposite());
        }
        for (Direction dir : Direction.values()) {
            if (canOutputTo(dir)) {
                if (dir != face) {
                    te = ThaumcraftApiHelper.getConnectableTile(level, getBlockPos(), dir);
                    if (te != null) {
                        ic = (IEssentiaTransport)te;
                        int sa = ic.getSuctionAmount(dir.getOpposite());
                        Aspect su = ic.getSuctionType(dir.getOpposite());
                        if ((su == aspect || su == null) && suction < sa && getSuctionAmount(dir) < sa) {
                            return 0;
                        }
                    }
                }
            }
        }
        if (amount > aspects.getAmount(aspect)) {
            amount = aspects.getAmount(aspect);
        }
        return takeFromContainer(aspect, amount) ? amount : 0;
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        return canInputFrom(face) ? (amount - addToContainer(aspect, amount)) : 0;
    }
    
    @Override
    public void update() {
        ++count;
        if (bellows < 0 || count % 20 == 0) {
            getBellows();
        }
        if (!getLevel().isClientSide() && count % 5 == 0) {
            int visSize = aspects.visSize();
            getClass();
            if (visSize < 10) {
                fillBuffer();
            }
        }
    }
    
    void fillBuffer() {
        BlockEntity te = null;
        IEssentiaTransport ic = null;
        for (Direction dir : Direction.values()) {
            if (canInputFrom(dir)) {
                te = ThaumcraftApiHelper.getConnectableTile(level, getBlockPos(), dir);
                if (te != null) {
                    ic = (IEssentiaTransport)te;
                    if (ic.getEssentiaAmount(dir.getOpposite()) > 0 && ic.getSuctionAmount(dir.getOpposite()) < getSuctionAmount(dir) && getSuctionAmount(dir) >= ic.getMinimumSuction()) {
                        Aspect ta = ic.getEssentiaType(dir.getOpposite());
                        addToContainer(ta, ic.takeEssentia(ta, 1, dir.getOpposite()));
                        return;
                    }
                }
            }
        }
    }
    
    public void getBellows() {
        bellows = TileBellows.getBellows(level, getBlockPos(), Direction.values());
    }
    
    @Override
    public boolean onCasterRightClick(Level level, ItemStack wandstack, Player player, BlockPos bp, Direction side, InteractionHand hand) {
        HitResult hit = RayTracer.retraceBlock(level, player, getBlockPos());
        if (hit == null) {
            return false;
        }
        if (hit.getType() != net.minecraft.world.phys.HitResult.Type.MISS && true) {
            player.swing(hand);
            if (player.isCrouching()) {
                player.level().playSound(null, bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, SoundsTC.squeek, SoundSource.BLOCKS, 0.6f, 2.0f + this.level.getRandom().nextFloat() * 0.2f);
                if (!this.level.isClientSide()) {
                    byte[] chokedSides = this.chokedSides;
                    int subHit = 0;
                    ++chokedSides[subHit];
                    if (this.chokedSides[0] > 2) {
                        this.chokedSides[0] = 0;
                    }
                    setChanged();
                    syncTile(true);
                }
            }
            else {
                player.level().playSound(null, bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, SoundsTC.tool, SoundSource.BLOCKS, 0.5f, 0.9f + player.level().getRandom().nextFloat() * 0.2f);
                openSides[0] = !openSides[0];
                Direction dir = Direction.values()[0];
                BlockEntity tile = this.level.getBlockEntity(getBlockPos().relative(dir));
                if (tile != null && tile instanceof TileTube) {
                    ((TileTube)tile).openSides[dir.getOpposite().ordinal()] = openSides[0];
                    ((TileTube)tile).syncTile(true);
                    tile.setChanged();
                }
                setChanged();
                syncTile(true);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean canConnectSide(Direction side) {
        BlockEntity tile = getLevel().getBlockEntity(getBlockPos().relative(side));
        return tile != null && tile instanceof IEssentiaTransport;
    }
    
    @Override
    public void addTraceableCuboids(List<IndexedCuboid6> cuboids) {
        float min = 0.375f;
        float max = 0.625f;
        if (canConnectSide(Direction.DOWN)) {
            cuboids.add(new IndexedCuboid6(0, new Cuboid6(getBlockPos().getX() + min, getBlockPos().getY(), getBlockPos().getZ() + min, getBlockPos().getX() + max, getBlockPos().getY() + 0.5, getBlockPos().getZ() + max)));
        }
        if (canConnectSide(Direction.UP)) {
            cuboids.add(new IndexedCuboid6(1, new Cuboid6(getBlockPos().getX() + min, getBlockPos().getY() + 0.5, getBlockPos().getZ() + min, getBlockPos().getX() + max, getBlockPos().getY() + 1, getBlockPos().getZ() + max)));
        }
        if (canConnectSide(Direction.NORTH)) {
            cuboids.add(new IndexedCuboid6(2, new Cuboid6(getBlockPos().getX() + min, getBlockPos().getY() + min, getBlockPos().getZ(), getBlockPos().getX() + max, getBlockPos().getY() + max, getBlockPos().getZ() + 0.5)));
        }
        if (canConnectSide(Direction.SOUTH)) {
            cuboids.add(new IndexedCuboid6(3, new Cuboid6(getBlockPos().getX() + min, getBlockPos().getY() + min, getBlockPos().getZ() + 0.5, getBlockPos().getX() + max, getBlockPos().getY() + max, getBlockPos().getZ() + 1)));
        }
        if (canConnectSide(Direction.WEST)) {
            cuboids.add(new IndexedCuboid6(4, new Cuboid6(getBlockPos().getX(), getBlockPos().getY() + min, getBlockPos().getZ() + min, getBlockPos().getX() + 0.5, getBlockPos().getY() + max, getBlockPos().getZ() + max)));
        }
        if (canConnectSide(Direction.EAST)) {
            cuboids.add(new IndexedCuboid6(5, new Cuboid6(getBlockPos().getX() + 0.5, getBlockPos().getY() + min, getBlockPos().getZ() + min, getBlockPos().getX() + 1, getBlockPos().getY() + max, getBlockPos().getZ() + max)));
        }
        cuboids.add(new IndexedCuboid6(6, new Cuboid6(getBlockPos().getX() + 0.25f, getBlockPos().getY() + 0.25f, getBlockPos().getZ() + 0.25f, getBlockPos().getX() + 0.75f, getBlockPos().getY() + 0.75f, getBlockPos().getZ() + 0.75f)));
    }
}
