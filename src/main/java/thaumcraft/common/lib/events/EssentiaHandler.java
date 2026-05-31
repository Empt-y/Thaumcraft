package thaumcraft.common.lib.events;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.internal.WorldCoordinates;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXEssentiaSource;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;


public class EssentiaHandler
{
    static int DELAY = 10000;
    private static HashMap<WorldCoordinates, ArrayList<WorldCoordinates>> sources;
    private static HashMap<WorldCoordinates, Long> sourcesDelay;
    private static BlockEntity lat;
    private static BlockEntity las;
    private static Aspect lasp;
    private static int lext;
    public static ConcurrentHashMap<String, EssentiaSourceFX> sourceFX;
    
    public static boolean drainEssentia(BlockEntity tile, Aspect aspect, Direction direction, int range, int ext) {
        return drainEssentia(tile, aspect, direction, range, false, ext);
    }
    
    public static boolean drainEssentia(BlockEntity tile, Aspect aspect, Direction direction, int range, boolean ignoreMirror, int ext) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.getBlockPos(), tile.getLevel() != null ? tile.getLevel().dimension().identifier().hashCode() : 0);
        if (!EssentiaHandler.sources.containsKey(tileLoc)) {
            getSources(tile.getLevel(), tileLoc, direction, range);
            return EssentiaHandler.sources.containsKey(tileLoc) && drainEssentia(tile, aspect, direction, range, ignoreMirror, ext);
        }
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        for (WorldCoordinates source : es) {
            BlockEntity sourceTile = tile.getLevel().getBlockEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (as.isBlocked()) {
                continue;
            }
            if (ignoreMirror && sourceTile instanceof TileMirrorEssentia) {
                continue;
            }
            if (as.takeFromContainer(aspect, 1)) {
                if (tile.getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
                    BlockPos tp = tile.getBlockPos();
                    PacketHandler.sendToAllAround(
                        new PacketFXEssentiaSource(tp, (byte)(tp.getX() - source.pos.getX()), (byte)(tp.getY() - source.pos.getY()), (byte)(tp.getZ() - source.pos.getZ()), aspect.getColor(), ext),
                        sl, tp.getX(), tp.getY(), tp.getZ(), 32.0);
                }
                return true;
            }
        }
        EssentiaHandler.sources.remove(tileLoc);
        EssentiaHandler.sourcesDelay.put(tileLoc, System.currentTimeMillis() + 10000L);
        return false;
    }
    
    public static boolean drainEssentiaWithConfirmation(BlockEntity tile, Aspect aspect, Direction direction, int range, boolean ignoreMirror, int ext) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.getBlockPos(), tile.getLevel() != null ? tile.getLevel().dimension().identifier().hashCode() : 0);
        if (!EssentiaHandler.sources.containsKey(tileLoc)) {
            getSources(tile.getLevel(), tileLoc, direction, range);
            return EssentiaHandler.sources.containsKey(tileLoc) && drainEssentiaWithConfirmation(tile, aspect, direction, range, ignoreMirror, ext);
        }
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        for (WorldCoordinates source : es) {
            BlockEntity sourceTile = tile.getLevel().getBlockEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (as.isBlocked()) {
                continue;
            }
            if (ignoreMirror && sourceTile instanceof TileMirrorEssentia) {
                continue;
            }
            if (as.doesContainerContainAmount(aspect, 1)) {
                EssentiaHandler.las = sourceTile;
                EssentiaHandler.lasp = aspect;
                EssentiaHandler.lat = tile;
                EssentiaHandler.lext = ext;
                return true;
            }
        }
        EssentiaHandler.sources.remove(tileLoc);
        EssentiaHandler.sourcesDelay.put(tileLoc, System.currentTimeMillis() + 10000L);
        return false;
    }
    
    public static void confirmDrain() {
        if (EssentiaHandler.las != null && EssentiaHandler.lasp != null && EssentiaHandler.lat != null) {
            IAspectSource as = (IAspectSource)EssentiaHandler.las;
            if (as.takeFromContainer(EssentiaHandler.lasp, 1) && EssentiaHandler.lat.getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
                BlockPos tp = EssentiaHandler.lat.getBlockPos();
                BlockPos sp = EssentiaHandler.las.getBlockPos();
                PacketHandler.sendToAllAround(
                    new PacketFXEssentiaSource(tp, (byte)(tp.getX() - sp.getX()), (byte)(tp.getY() - sp.getY()), (byte)(tp.getZ() - sp.getZ()), EssentiaHandler.lasp.getColor(), EssentiaHandler.lext),
                    sl, tp.getX(), tp.getY(), tp.getZ(), 32.0);
            }
        }
        EssentiaHandler.las = null;
        EssentiaHandler.lasp = null;
        EssentiaHandler.lat = null;
    }
    
    public static boolean addEssentia(BlockEntity tile, Aspect aspect, Direction direction, int range, boolean ignoreMirror, int ext) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.getBlockPos(), tile.getLevel() != null ? tile.getLevel().dimension().identifier().hashCode() : 0);
        if (!EssentiaHandler.sources.containsKey(tileLoc)) {
            getSources(tile.getLevel(), tileLoc, direction, range);
            return EssentiaHandler.sources.containsKey(tileLoc) && addEssentia(tile, aspect, direction, range, ignoreMirror, ext);
        }
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        ArrayList<WorldCoordinates> empties = new ArrayList<WorldCoordinates>();
        for (WorldCoordinates source : es) {
            BlockEntity sourceTile = tile.getLevel().getBlockEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (as.isBlocked()) {
                continue;
            }
            if (ignoreMirror && sourceTile instanceof TileMirrorEssentia) {
                continue;
            }
            if (as.doesContainerAccept(aspect) && (as.getAspects() == null || as.getAspects().visSize() == 0)) {
                empties.add(source);
            }
            else {
                if (as.doesContainerAccept(aspect) && as.addToContainer(aspect, 1) <= 0) {
                    if (tile.getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
                        BlockPos sp = source.pos;
                        BlockPos tp = tile.getBlockPos();
                        PacketHandler.sendToAllAround(
                            new PacketFXEssentiaSource(sp, (byte)(sp.getX() - tp.getX()), (byte)(sp.getY() - tp.getY()), (byte)(sp.getZ() - tp.getZ()), aspect.getColor(), ext),
                            sl, tp.getX(), tp.getY(), tp.getZ(), 32.0);
                    }
                    return true;
                }
                continue;
            }
        }
        for (WorldCoordinates source : empties) {
            if (source != null) {
                if (source.pos == null) {
                    continue;
                }
                BlockEntity sourceTile = tile.getLevel().getBlockEntity(source.pos);
                if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                    break;
                }
                IAspectSource as = (IAspectSource)sourceTile;
                if (aspect != null && as.doesContainerAccept(aspect) && as.addToContainer(aspect, 1) <= 0) {
                    if (tile.getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
                        BlockPos sp = source.pos;
                        BlockPos tp = tile.getBlockPos();
                        PacketHandler.sendToAllAround(
                            new PacketFXEssentiaSource(sp, (byte)(sp.getX() - tp.getX()), (byte)(sp.getY() - tp.getY()), (byte)(sp.getZ() - tp.getZ()), aspect.getColor(), ext),
                            sl, tp.getX(), tp.getY(), tp.getZ(), 32.0);
                    }
                    return true;
                }
                continue;
            }
        }
        EssentiaHandler.sources.remove(tileLoc);
        EssentiaHandler.sourcesDelay.put(tileLoc, System.currentTimeMillis() + 10000L);
        return false;
    }
    
    public static boolean findEssentia(BlockEntity tile, Aspect aspect, Direction direction, int range, boolean ignoreMirror) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.getBlockPos(), tile.getLevel() != null ? tile.getLevel().dimension().identifier().hashCode() : 0);
        if (!EssentiaHandler.sources.containsKey(tileLoc)) {
            getSources(tile.getLevel(), tileLoc, direction, range);
            return EssentiaHandler.sources.containsKey(tileLoc) && findEssentia(tile, aspect, direction, range, ignoreMirror);
        }
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        for (WorldCoordinates source : es) {
            BlockEntity sourceTile = tile.getLevel().getBlockEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (as.isBlocked()) {
                continue;
            }
            if (ignoreMirror && sourceTile instanceof TileMirrorEssentia) {
                continue;
            }
            if (as.doesContainerContainAmount(aspect, 1)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean canAcceptEssentia(BlockEntity tile, Aspect aspect, Direction direction, int range, boolean ignoreMirror) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.getBlockPos(), tile.getLevel() != null ? tile.getLevel().dimension().identifier().hashCode() : 0);
        if (!EssentiaHandler.sources.containsKey(tileLoc)) {
            getSources(tile.getLevel(), tileLoc, direction, range);
            return EssentiaHandler.sources.containsKey(tileLoc) && findEssentia(tile, aspect, direction, range, ignoreMirror);
        }
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        for (WorldCoordinates source : es) {
            BlockEntity sourceTile = tile.getLevel().getBlockEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            if (ignoreMirror && sourceTile instanceof TileMirrorEssentia) {
                continue;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (!as.isBlocked() && as.doesContainerAccept(aspect)) {
                return true;
            }
        }
        return false;
    }
    
    private static void getSources(Level world, WorldCoordinates tileLoc, Direction direction, int range) {
        if (EssentiaHandler.sourcesDelay.containsKey(tileLoc)) {
            long d = EssentiaHandler.sourcesDelay.get(tileLoc);
            if (d > System.currentTimeMillis()) {
                return;
            }
            EssentiaHandler.sourcesDelay.remove(tileLoc);
        }
        BlockEntity sourceTile = world.getBlockEntity(tileLoc.pos);
        ArrayList<WorldCoordinates> sourceList = new ArrayList<WorldCoordinates>();
        int start = 0;
        if (direction == null) {
            start = -range;
            direction = Direction.UP;
        }
        int xx = 0;
        int yy = 0;
        int zz = 0;
        for (int aa = -range; aa <= range; ++aa) {
            for (int bb = -range; bb <= range; ++bb) {
                for (int cc = start; cc < range; ++cc) {
                    if (aa != 0 || bb != 0 || cc != 0) {
                        xx = tileLoc.pos.getX();
                        yy = tileLoc.pos.getY();
                        zz = tileLoc.pos.getZ();
                        if (direction.getStepY() != 0) {
                            xx += aa;
                            yy += cc * direction.getStepY();
                            zz += bb;
                        }
                        else if (direction.getStepX() == 0) {
                            xx += aa;
                            yy += bb;
                            zz += cc * direction.getStepZ();
                        }
                        else {
                            xx += cc * direction.getStepX();
                            yy += aa;
                            zz += bb;
                        }
                        BlockEntity te = world.getBlockEntity(new BlockPos(xx, yy, zz));
                        if (te != null && te instanceof IAspectSource) {
                            if (!(sourceTile instanceof TileMirrorEssentia) || !(te instanceof TileMirrorEssentia) || sourceTile.getBlockPos().getX() != ((TileMirrorEssentia)te).linkX || sourceTile.getBlockPos().getY() != ((TileMirrorEssentia)te).linkY || sourceTile.getBlockPos().getZ() != ((TileMirrorEssentia)te).linkZ || (sourceTile.getLevel() != null ? sourceTile.getLevel().dimension().identifier().hashCode() : 0) != ((TileMirrorEssentia)te).linkDim) {
                                sourceList.add(new WorldCoordinates(new BlockPos(xx, yy, zz), (world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0)));
                            }
                        }
                    }
                }
            }
        }
        if (sourceList.size() > 0) {
            ArrayList<WorldCoordinates> sourceList2 = new ArrayList<WorldCoordinates>();
        Label_0467:
            for (WorldCoordinates wc : sourceList) {
                double dist = wc.getDistanceSquaredToWorldCoordinates(tileLoc);
                if (!sourceList2.isEmpty()) {
                    for (int a = 0; a < sourceList2.size(); ++a) {
                        double d2 = sourceList2.get(a).getDistanceSquaredToWorldCoordinates(tileLoc);
                        if (dist < d2) {
                            sourceList2.add(a, wc);
                            continue Label_0467;
                        }
                    }
                }
                sourceList2.add(wc);
            }
            EssentiaHandler.sources.put(tileLoc, sourceList2);
        }
        else {
            EssentiaHandler.sourcesDelay.put(tileLoc, System.currentTimeMillis() + 10000L);
        }
    }
    
    public static void refreshSources(BlockEntity tile) {
        EssentiaHandler.sources.remove(new WorldCoordinates(tile.getBlockPos(), tile.getLevel() != null ? tile.getLevel().dimension().identifier().hashCode() : 0));
    }
    
    static {
        EssentiaHandler.sources = new HashMap<WorldCoordinates, ArrayList<WorldCoordinates>>();
        EssentiaHandler.sourcesDelay = new HashMap<WorldCoordinates, Long>();
        EssentiaHandler.lat = null;
        EssentiaHandler.las = null;
        EssentiaHandler.lasp = null;
        EssentiaHandler.lext = 0;
        EssentiaHandler.sourceFX = new ConcurrentHashMap<String, EssentiaSourceFX>();
    }
    
    public static class EssentiaSourceFX
    {
        public BlockPos start;
        public BlockPos end;
        public int color;
        public int ext;
        
        public EssentiaSourceFX(BlockPos start, BlockPos end, int color, int ext) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.ext = ext;
        }
    }
}
