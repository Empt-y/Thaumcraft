package thaumcraft.common.golems.seals;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.Thaumcraft;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketSealToClient;
import thaumcraft.common.world.aura.AuraHandler;


public class SealHandler
{
    public static LinkedHashMap<String, ISeal> types;
    private static int lastID;
    public static ConcurrentHashMap<Integer, ConcurrentHashMap<SealPos, SealEntity>> sealEntities;
    static int count;
    
    public static void registerSeal(ISeal seal) {
        if (SealHandler.types.containsKey(seal.getKey())) {
            Thaumcraft.log.error("Attempting to register Seal [" + seal.getKey() + "] twice. Ignoring.");
        }
        else {
            SealHandler.types.put(seal.getKey(), seal);
        }
    }
    
    public static String[] getRegisteredSeals() {
        return SealHandler.types.keySet().toArray(new String[0]);
    }
    
    public static ISeal getSeal(String key) {
        return SealHandler.types.get(key);
    }
    
    public static CopyOnWriteArrayList<SealEntity> getSealsInRange(Level world, BlockPos source, int range) {
        CopyOnWriteArrayList<SealEntity> out = new CopyOnWriteArrayList<SealEntity>();
        ConcurrentHashMap<SealPos, SealEntity> list = SealHandler.sealEntities.get((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
        if (list != null && list.size() > 0) {
            for (SealEntity se : list.values()) {
                if (se.getSeal() != null) {
                    if (se.getSealPos() == null) {
                        continue;
                    }
                    if (se.sealPos.pos.distSqr(source) > range * range) {
                        continue;
                    }
                    out.add(se);
                }
            }
        }
        return out;
    }
    
    public static CopyOnWriteArrayList<SealEntity> getSealsInChunk(Level world, ChunkPos chunk) {
        CopyOnWriteArrayList<SealEntity> out = new CopyOnWriteArrayList<SealEntity>();
        ConcurrentHashMap<SealPos, SealEntity> list = SealHandler.sealEntities.get((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
        if (list != null && list.size() > 0) {
            for (SealEntity se : list.values()) {
                if (se.getSeal() != null) {
                    if (se.getSealPos() == null) {
                        continue;
                    }
                    ChunkPos cc = new ChunkPos((se.sealPos.pos).getX(), (se.sealPos.pos).getZ());
                    if (!cc.equals(chunk)) {
                        continue;
                    }
                    out.add(se);
                }
            }
        }
        return out;
    }
    
    public static void removeSealEntity(Level world, SealPos pos, boolean quiet) {
        if (!SealHandler.sealEntities.containsKey((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0))) {
            SealHandler.sealEntities.put((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), new ConcurrentHashMap<SealPos, SealEntity>());
        }
        ConcurrentHashMap<SealPos, SealEntity> se = SealHandler.sealEntities.get((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
        if (se != null) {
            SealEntity seal = se.remove(pos);
            try {
                if (!world.isClientSide() && seal != null && seal.seal != null) {
                    seal.seal.onRemoval(world, pos.pos, pos.face);
                }
                if (!quiet && seal != null && !world.isClientSide()) {
                    String[] rs = getRegisteredSeals();
                    int indx = 1;
                    for (String s : rs) {
                        if (s.equals(seal.getSeal().getKey())) {
                            world.addFreshEntity(new ItemEntity(world, pos.pos.getX() + 0.5 + pos.face.getStepX() / 1.7f, pos.pos.getY() + 0.5 + pos.face.getStepY() / 1.7f, pos.pos.getZ() + 0.5 + pos.face.getStepZ() / 1.7f, new ItemStack(ItemsTC.seals.asItem(), 1)));
                            break;
                        }
                        ++indx;
                    }
                }
            }
            catch (Exception e) {
                Thaumcraft.log.warn("Removing invalid seal at " + pos.pos);
            }
            ConcurrentHashMap<Integer, Task> ts = TaskHandler.getTasks((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
            for (Task task : ts.values()) {
                if (task.getSealPos() != null && task.getSealPos().equals(pos)) {
                    task.setSuspended(true);
                }
            }
            if (!world.isClientSide()) {
                PacketHandler.INSTANCE.sendToDimension(new PacketSealToClient(new SealEntity(world, pos, null)), (world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
            }
            if (!quiet) {
                markChunkAsDirty((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), pos.pos);
            }
        }
    }
    
    public static ISealEntity getSealEntity(int dim, SealPos pos) {
        if (!SealHandler.sealEntities.containsKey(dim)) {
            SealHandler.sealEntities.put(dim, new ConcurrentHashMap<SealPos, SealEntity>());
        }
        if (pos == null) {
            return null;
        }
        ConcurrentHashMap<SealPos, SealEntity> se = SealHandler.sealEntities.get(dim);
        if (se != null) {
            return se.get(pos);
        }
        return null;
    }
    
    public static boolean addSealEntity(Level world, BlockPos pos, Direction face, ISeal seal, Player player) {
        if (!SealHandler.sealEntities.containsKey((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0))) {
            SealHandler.sealEntities.put((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), new ConcurrentHashMap<SealPos, SealEntity>());
        }
        ConcurrentHashMap<SealPos, SealEntity> se = SealHandler.sealEntities.get((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
        SealPos sp = new SealPos(pos, face);
        if (se.containsKey(sp)) {
            return false;
        }
        SealEntity sealent = new SealEntity(world, sp, seal);
        sealent.setOwner(player.getUUID().toString());
        se.put(sp, sealent);
        if (!world.isClientSide()) {
            sealent.syncToClient(world);
            markChunkAsDirty((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), pos);
        }
        return true;
    }
    
    public static boolean addSealEntity(Level world, SealEntity seal) {
        if (world == null || SealHandler.sealEntities == null) {
            return false;
        }
        if (!SealHandler.sealEntities.containsKey((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0))) {
            SealHandler.sealEntities.put((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), new ConcurrentHashMap<SealPos, SealEntity>());
        }
        ConcurrentHashMap<SealPos, SealEntity> se = SealHandler.sealEntities.get((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
        if (se.containsKey(seal.getSealPos())) {
            return false;
        }
        se.put(seal.getSealPos(), seal);
        if (!world.isClientSide()) {
            seal.syncToClient(world);
            markChunkAsDirty((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), seal.getSealPos().pos);
        }
        return true;
    }
    
    public static void tickSealEntities(Level world) {
        if (!SealHandler.sealEntities.containsKey((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0))) {
            SealHandler.sealEntities.put((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), new ConcurrentHashMap<SealPos, SealEntity>());
        }
        ConcurrentHashMap<SealPos, SealEntity> se = SealHandler.sealEntities.get((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
        ++SealHandler.count;
        for (SealEntity sealEntity : se.values()) {
            if (world.isLoaded(sealEntity.sealPos.pos)) {
                try {
                    boolean tick = true;
                    if (SealHandler.count % 20 == 0 && !sealEntity.seal.canPlaceAt(world, sealEntity.sealPos.pos, sealEntity.sealPos.face)) {
                        removeSealEntity(world, sealEntity.sealPos, false);
                        tick = false;
                    }
                    if (!tick) {
                        continue;
                    }
                    sealEntity.tickSealEntity(world);
                }
                catch (Exception e) {
                    removeSealEntity(world, sealEntity.sealPos, false);
                }
            }
        }
    }
    
    public static void markChunkAsDirty(int dim, BlockPos bp) {
        ChunkPos pos = new ChunkPos((bp).getX(), (bp).getZ());
        if (!AuraHandler.dirtyChunks.containsKey(dim)) {
            AuraHandler.dirtyChunks.put(dim, new CopyOnWriteArrayList<ChunkPos>());
        }
        CopyOnWriteArrayList<ChunkPos> dc = AuraHandler.dirtyChunks.get(dim);
        if (!dc.contains(pos)) {
            dc.add(pos);
        }
    }
    
    static {
        SealHandler.types = new LinkedHashMap<String, ISeal>();
        SealHandler.lastID = 0;
        SealHandler.sealEntities = new ConcurrentHashMap<Integer, ConcurrentHashMap<SealPos, SealEntity>>();
        SealHandler.count = 0;
    }
}
