package thaumcraft.common.lib.events;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.event.level.ChunkDataEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.Thaumcraft;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketSealToClient;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft")
public class ChunkEvents
{
    /** Get the dimension hash from a LevelAccessor if it is a Level, else 0. */
    private static int dimHash(LevelAccessor level) {
        if (level instanceof Level l) {
            return l.dimension().identifier().hashCode();
        }
        return 0;
    }

    /** Fires for ALL chunks including brand-new generated ones. Use this for first-time aura setup. */
    @SubscribeEvent
    public static void chunkLoaded(ChunkEvent.Load event) {
        if (!event.isNewChunk()) return; // only handle newly generated chunks here
        if (!(event.getLevel() instanceof Level lvl)) return;
        if (lvl.isClientSide()) return;
        LevelChunk lc = event.getChunk();
        int dim = dimHash(lvl);
        // Only generate if no aura exists yet for this chunk
        if (AuraHandler.getAuraChunk(dim, lc.getPos().x(), lc.getPos().z()) == null) {
            AuraHandler.generateAura(lc, lvl, new java.util.Random());
        }
    }

    @SubscribeEvent
    public static void chunkSave(net.neoforged.neoforge.event.level.ChunkDataEvent.Save event) {
        int dim = dimHash(event.getLevel());
        ChunkPos loc = event.getChunk().getPos();
        CompoundTag attachments = event.getData().attachmentData();
        if (attachments == null) return; // vanilla-path chunk has no attachment slot
        CompoundTag nbt = new CompoundTag();
        attachments.put("Thaumcraft", nbt);
        nbt.putBoolean(ModConfig.CONFIG_WORLD.regenKey, true);
        AuraChunk ac = AuraHandler.getAuraChunk(dim, loc.x(), loc.z());
        if (ac != null) {
            nbt.putShort("base", ac.getBase());
            nbt.putFloat("flux", ac.getFlux());
            nbt.putFloat("vis", ac.getVis());
            // ChunkAccess does not expose isLoaded(); check via event level instead
            boolean chunkUnloading = !(event.getLevel() instanceof Level lv) || !lv.isLoaded(loc.getBlockAt(8, 64, 8));
            if (chunkUnloading) {
                AuraHandler.removeAuraChunk(dim, loc.x(), loc.z());
            }
        }
        ListTag tagList = new ListTag();
        if (event.getLevel() instanceof Level level) {
            for (ISealEntity seal : SealHandler.getSealsInChunk(level, loc)) {
                CompoundTag sealnbt = seal.writeNBT();
                tagList.add(sealnbt);
                boolean chunkUnloading = !level.isLoaded(loc.getBlockAt(8, 64, 8));
                if (chunkUnloading) {
                    SealHandler.removeSealEntity(level, seal.getSealPos(), true);
                }
            }
        }
        nbt.put("seals", tagList);
    }

    @SubscribeEvent
    public static void chunkLoad(net.neoforged.neoforge.event.level.ChunkDataEvent.Load event) {
        int dim = dimHash(event.getLevel());
        ChunkPos loc = event.getChunk().getPos();
        CompoundTag attachments = event.getData().attachmentData();
        // null = brand-new chunk with no mod attachment data; treat same as missing Thaumcraft key
        if (attachments == null) {
            if (event.getChunk() instanceof LevelChunk lc && event.getLevel() instanceof Level lvl) {
                AuraHandler.generateAura(lc, lvl, new java.util.Random());
            }
            return;
        }
        if (attachments.contains("Thaumcraft")) {
            CompoundTag nbt = attachments.getCompoundOrEmpty("Thaumcraft");
            if (nbt.contains("base")) {
                short base = nbt.getShortOr("base", (short)0);
                float flux = nbt.getFloatOr("flux", 0.0f);
                float vis = nbt.getFloatOr("vis", 0.0f);
                // Need a LevelChunk — cast if available, else skip aura load (will regenerate)
                if (event.getChunk() instanceof LevelChunk lc) {
                    AuraHandler.addAuraChunk(dim, lc, base, vis, flux);
                }
            }
        } else {
            if (event.getChunk() instanceof LevelChunk lc && event.getLevel() != null) {
                if (event.getLevel() instanceof net.minecraft.world.level.Level lvl) AuraHandler.generateAura(lc, lvl, new java.util.Random());
            }
        }
        if (attachments.contains("Thaumcraft")) {
            CompoundTag nbt = attachments.getCompoundOrEmpty("Thaumcraft");
            if (nbt.contains("seals")) {
                ListTag tagList = nbt.getListOrEmpty("seals");
                for (int a = 0; a < tagList.size(); ++a) {
                    CompoundTag tasknbt = tagList.getCompoundOrEmpty(a);
                    SealEntity seal = new SealEntity();
                    seal.readNBT(tasknbt);
                    if (event.getLevel() instanceof Level level) {
                        SealHandler.addSealEntity(level, seal);
                    }
                }
            }
        }
        if (!attachments.contains("Thaumcraft") || !attachments.getCompoundOrEmpty("Thaumcraft").contains(ModConfig.CONFIG_WORLD.regenKey)) {
            if (ModConfig.CONFIG_WORLD.regenAmber || ModConfig.CONFIG_WORLD.regenAura || ModConfig.CONFIG_WORLD.regenCinnabar || ModConfig.CONFIG_WORLD.regenCrystals || ModConfig.CONFIG_WORLD.regenStructure || ModConfig.CONFIG_WORLD.regenTrees) {
                Thaumcraft.log.warn("Level gen was never run for chunk at " + event.getChunk().getPos() + ". Adding to queue for regeneration.");
                ArrayList<ChunkPos> chunks = ServerEvents.chunksToGenerate.get(dim);
                if (chunks == null) {
                    ServerEvents.chunksToGenerate.put(dim, new ArrayList<ChunkPos>());
                    chunks = ServerEvents.chunksToGenerate.get(dim);
                }
                if (chunks != null) {
                    chunks.add(loc);
                    ServerEvents.chunksToGenerate.put(dim, chunks);
                }
            }
        }
    }

    @SubscribeEvent
    public static void chunkWatch(net.neoforged.neoforge.event.level.ChunkWatchEvent.Watch event) {
        for (ISealEntity seal : SealHandler.getSealsInChunk(event.getLevel(), event.getPos())) {
            PacketHandler.sendToPlayer(new PacketSealToClient(seal), event.getPlayer());
        }
    }
}
