package thaumcraft.common.tiles.essentia;
import java.util.List;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileTube extends TileThaumcraft implements IEssentiaTransport, IInteractWithCaster
{
    public static int freq = 5;
    public Direction facing;
    public boolean[] openSides;
    Aspect essentiaType;
    int essentiaAmount;
    Aspect suctionType;
    int suction;
    int venting;
    int count;
    int ventColor;
    
    public TileTube(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        facing = Direction.NORTH;
        openSides = new boolean[] { true, true, true, true, true, true };
        essentiaType = null;
        essentiaAmount = 0;
        suctionType = null;
        suction = 0;
        venting = 0;
        count = 0;
        ventColor = 0;
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        essentiaType = Aspect.getAspect(nbttagcompound.getStringOr("type", ""));
        essentiaAmount = nbttagcompound.getIntOr("amount", 0);
        facing = Direction.values()[nbttagcompound.getIntOr("side", 0)];
        byte[] sides = nbttagcompound.getByteArray("open").orElse(new byte[0]);
        if (sides != null && sides.length == 6) {
            for (int a = 0; a < 6; ++a) {
                openSides[a] = (sides[a] == 1);
            }
        }
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        if (essentiaType != null) {
            nbttagcompound.putString("type", essentiaType.get());
        }
        nbttagcompound.putInt("amount", essentiaAmount);
        byte[] sides = new byte[6];
        for (int a = 0; a < 6; ++a) {
            sides[a] = (byte)(openSides[a] ? 1 : 0);
        }
        nbttagcompound.putInt("side", facing.ordinal());
        nbttagcompound.putByteArray("open", sides);
        return nbttagcompound;
    }
    
        public void loadAdditional(CompoundTag nbttagcompound) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
        suctionType = Aspect.getAspect(nbttagcompound.getStringOr("stype", ""));
        suction = nbttagcompound.getIntOr("samount", 0);
    }
    
        public CompoundTag saveAdditional(CompoundTag nbttagcompound) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
        if (suctionType != null) {
            nbttagcompound.putString("stype", suctionType.get());
        }
        nbttagcompound.putInt("samount", suction);
        return nbttagcompound;
    }
    
    public void update() {
        if (venting > 0) {
            --venting;
        }
        if (count == 0) {
            count = this.level.getRandom().nextInt(10);
        }
        if (!getLevel().isClientSide()) {
            if (venting <= 0) {
                if (++count % 2 == 0) {
                    calculateSuction(null, false, false);
                    checkVenting();
                    if (essentiaType != null && essentiaAmount == 0) {
                        essentiaType = null;
                    }
                }
                if (count % 5 == 0 && suction > 0) {
                    equalizeWithNeighbours(false);
                }
            }
        }
        else if (venting > 0) {
            Random r = new Random(hashCode() * 4);
            float rp = r.nextFloat() * 360.0f;
            float ry = r.nextFloat() * 360.0f;
            double fx = -Mth.sin(ry / 180.0f * 3.1415927f) * Mth.cos(rp / 180.0f * 3.1415927f);
            double fz = Mth.cos(ry / 180.0f * 3.1415927f) * Mth.cos(rp / 180.0f * 3.1415927f);
            double fy = -Mth.sin(rp / 180.0f * 3.1415927f);
            FXDispatcher.INSTANCE.drawVentParticles(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, fx / 5.0, fy / 5.0, fz / 5.0, ventColor);
        }
    }
    
    void calculateSuction(Aspect filter, boolean restrict, boolean directional) {
        suction = 0;
        suctionType = null;
        Direction loc = null;
        for (int dir = 0; dir < 6; ++dir) {
            try {
                loc = Direction.values()[dir];
                if (!directional || facing == loc.getOpposite()) {
                    if (isConnectable(loc)) {
                        BlockEntity te = ThaumcraftApiHelper.getConnectableTile(getLevel(), getBlockPos(), loc);
                        if (te != null) {
                            IEssentiaTransport ic = (IEssentiaTransport)te;
                            if (filter == null || ic.getSuctionType(loc.getOpposite()) == null || ic.getSuctionType(loc.getOpposite()) == filter) {
                                if (filter != null || getEssentiaAmount(loc) <= 0 || ic.getSuctionType(loc.getOpposite()) == null || getEssentiaType(loc) == ic.getSuctionType(loc.getOpposite())) {
                                    if (filter == null || getEssentiaAmount(loc) <= 0 || getEssentiaType(loc) == null || ic.getSuctionType(loc.getOpposite()) == null || getEssentiaType(loc) == ic.getSuctionType(loc.getOpposite())) {
                                        int suck = ic.getSuctionAmount(loc.getOpposite());
                                        if (suck > 0 && suck > suction + 1) {
                                            Aspect st = ic.getSuctionType(loc.getOpposite());
                                            if (st == null) {
                                                st = filter;
                                            }
                                            setSuction(st, restrict ? (suck / 2) : (suck - 1));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
    
    void checkVenting() {
        Direction loc = null;
        for (int dir = 0; dir < 6; ++dir) {
            try {
                loc = Direction.values()[dir];
                if (isConnectable(loc)) {
                    BlockEntity te = ThaumcraftApiHelper.getConnectableTile(getLevel(), getBlockPos(), loc);
                    if (te != null) {
                        IEssentiaTransport ic = (IEssentiaTransport)te;
                        int suck = ic.getSuctionAmount(loc.getOpposite());
                        if (suction > 0 && (suck == suction || suck == suction - 1) && suctionType != ic.getSuctionType(loc.getOpposite()) && !(te instanceof TileTubeFilter)) {
                            int c = -1;
                            if (suctionType != null) {
                                c = ModConfig.aspectOrder.indexOf(suctionType);
                            }
                            getLevel().blockEvent(getBlockPos(), BlocksTC.tube, 1, c);
                            venting = 40;
                        }
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
    
    void equalizeWithNeighbours(boolean directional) {
        Direction loc = null;
        if (essentiaAmount > 0) {
            return;
        }
        for (int dir = 0; dir < 6; ++dir) {
            try {
                loc = Direction.values()[dir];
                if (!directional || facing != loc.getOpposite()) {
                    if (isConnectable(loc)) {
                        BlockEntity te = ThaumcraftApiHelper.getConnectableTile(getLevel(), getBlockPos(), loc);
                        if (te != null) {
                            IEssentiaTransport ic = (IEssentiaTransport)te;
                            if (ic.canOutputTo(loc.getOpposite())) {
                                if ((getSuctionType(null) == null || getSuctionType(null) == ic.getEssentiaType(loc.getOpposite()) || ic.getEssentiaType(loc.getOpposite()) == null) && getSuctionAmount(null) > ic.getSuctionAmount(loc.getOpposite()) && getSuctionAmount(null) >= ic.getMinimumSuction()) {
                                    Aspect a = getSuctionType(null);
                                    if (a == null) {
                                        a = ic.getEssentiaType(loc.getOpposite());
                                        if (a == null) {
                                            a = ic.getEssentiaType(null);
                                        }
                                    }
                                    int am = addEssentia(a, ic.takeEssentia(a, 1, loc.getOpposite()), loc);
                                    if (am > 0) {
                                        if (this.level.getRandom().nextInt(100) == 0) {
                                            getLevel().blockEvent(getBlockPos(), BlocksTC.tube, 0, 0);
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
    
    @Override
    public boolean isConnectable(Direction face) {
        return face != null && openSides[face.ordinal()];
    }
    
    @Override
    public boolean canInputFrom(Direction face) {
        return face != null && openSides[face.ordinal()];
    }
    
    @Override
    public boolean canOutputTo(Direction face) {
        return face != null && openSides[face.ordinal()];
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
        suctionType = aspect;
        suction = amount;
    }
    
    @Override
    public Aspect getSuctionType(Direction loc) {
        return suctionType;
    }
    
    @Override
    public int getSuctionAmount(Direction loc) {
        return suction;
    }
    
    @Override
    public Aspect getEssentiaType(Direction loc) {
        return essentiaType;
    }
    
    @Override
    public int getEssentiaAmount(Direction loc) {
        return essentiaAmount;
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        if (canOutputTo(face) && essentiaType == aspect && essentiaAmount > 0 && amount > 0) {
            --essentiaAmount;
            if (essentiaAmount <= 0) {
                essentiaType = null;
            }
            setChanged();
            return 1;
        }
        return 0;
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        if (canInputFrom(face) && essentiaAmount == 0 && amount > 0) {
            essentiaType = aspect;
            ++essentiaAmount;
            setChanged();
            return 1;
        }
        return 0;
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 0) {
            if (getLevel().isClientSide()) {
                getLevel().playLocalSound(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, SoundsTC.creak, SoundSource.AMBIENT, 1.0f, 1.3f + this.level.getRandom().nextFloat() * 0.2f, false);
            }
            return true;
        }
        if (i == 1) {
            if (getLevel().isClientSide()) {
                if (venting <= 0) {
                    getLevel().playLocalSound(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.1f, 1.0f + this.level.getRandom().nextFloat() * 0.1f, false);
                }
                venting = 50;
                if (j == -1 || j >= ModConfig.aspectOrder.size()) {
                    ventColor = 11184810;
                }
                else {
                    ventColor = ModConfig.aspectOrder.get(j).getColor();
                }
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
    
    @Override
    public boolean onCasterRightClick(Level world, ItemStack wandstack, Player player, BlockPos bp, Direction side, InteractionHand hand) {
        HitResult hit = RayTracer.retraceBlock(world, player, getBlockPos());
        if (hit == null) {
            return false;
        }
        if (hit.getType() != net.minecraft.world.phys.HitResult.Type.MISS && true) {
            player.level().playSound(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, SoundsTC.tool, SoundSource.BLOCKS, 0.5f, 0.9f + player.level().getRandom().nextFloat() * 0.2f, false);
            player.swing(hand);
            setChanged();
            syncTile(true);
            openSides[0] = !openSides[0];
            Direction dir = Direction.values()[0];
            BlockEntity tile = this.level.getBlockEntity(getBlockPos().relative(dir));
            if (tile != null && tile instanceof TileTube) {
                ((TileTube)tile).openSides[dir.getOpposite().ordinal()] = openSides[0];
                ((TileTube)tile).syncTile(true);
                tile.setChanged();
            }
            return true;
        }
        if (0 == 6) {
            player.level().playSound(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, SoundsTC.tool, SoundSource.BLOCKS, 0.5f, 0.9f + player.level().getRandom().nextFloat() * 0.2f, false);
            player.swing(hand);
            int a = facing.ordinal();
            setChanged();
            while (++a < 20) {
                if (canConnectSide(Direction.values()[a % 6].getOpposite()) && isConnectable(Direction.values()[a % 6].getOpposite())) {
                    a %= 6;
                    facing = Direction.values()[a];
                    syncTile(true);
                    setChanged();
                    break;
                }
            }
            return true;
        }
        return false;
    }
    
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 1, getBlockPos().getZ() + 1);
    }
    
    public HitResult rayTrace(Level world, Vec3 vec3d, Vec3 vec3d1, HitResult fullblock) {
        return fullblock;
    }
    
    public boolean canConnectSide(Direction side) {
        BlockEntity tile = getLevel().getBlockEntity(getBlockPos().relative(side));
        return tile != null && tile instanceof IEssentiaTransport;
    }
    
    public void addTraceableCuboids(List<IndexedCuboid6> cuboids) {
        float min = 0.375f;
        float max = 0.625f;
        if (canConnectSide(Direction.DOWN)) {
            cuboids.add(new IndexedCuboid6(0, new Cuboid6(getBlockPos().getX() + min, getBlockPos().getY(), getBlockPos().getZ() + min, getBlockPos().getX() + max, getBlockPos().getY() + 0.375, getBlockPos().getZ() + max)));
        }
        if (canConnectSide(Direction.UP)) {
            cuboids.add(new IndexedCuboid6(1, new Cuboid6(getBlockPos().getX() + min, getBlockPos().getY() + 0.625, getBlockPos().getZ() + min, getBlockPos().getX() + max, getBlockPos().getY() + 1, getBlockPos().getZ() + max)));
        }
        if (canConnectSide(Direction.NORTH)) {
            cuboids.add(new IndexedCuboid6(2, new Cuboid6(getBlockPos().getX() + min, getBlockPos().getY() + min, getBlockPos().getZ(), getBlockPos().getX() + max, getBlockPos().getY() + max, getBlockPos().getZ() + 0.375)));
        }
        if (canConnectSide(Direction.SOUTH)) {
            cuboids.add(new IndexedCuboid6(3, new Cuboid6(getBlockPos().getX() + min, getBlockPos().getY() + min, getBlockPos().getZ() + 0.625, getBlockPos().getX() + max, getBlockPos().getY() + max, getBlockPos().getZ() + 1)));
        }
        if (canConnectSide(Direction.WEST)) {
            cuboids.add(new IndexedCuboid6(4, new Cuboid6(getBlockPos().getX(), getBlockPos().getY() + min, getBlockPos().getZ() + min, getBlockPos().getX() + 0.375, getBlockPos().getY() + max, getBlockPos().getZ() + max)));
        }
        if (canConnectSide(Direction.EAST)) {
            cuboids.add(new IndexedCuboid6(5, new Cuboid6(getBlockPos().getX() + 0.625, getBlockPos().getY() + min, getBlockPos().getZ() + min, getBlockPos().getX() + 1, getBlockPos().getY() + max, getBlockPos().getZ() + max)));
        }
        cuboids.add(new IndexedCuboid6(6, new Cuboid6(getBlockPos().getX() + 0.375, getBlockPos().getY() + 0.375, getBlockPos().getZ() + 0.375, getBlockPos().getX() + 0.625, getBlockPos().getY() + 0.625, getBlockPos().getZ() + 0.625)));
    }
}
