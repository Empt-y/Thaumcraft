package thaumcraft.common.world.aura;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.utils.PosXY;
import thaumcraft.common.world.biomes.BiomeHandler;


public class AuraHandler
{
    public static int AURA_CEILING = 500;
    static ConcurrentHashMap<Integer, AuraWorld> auras;
    public static ConcurrentHashMap<Integer, CopyOnWriteArrayList<ChunkPos>> dirtyChunks;
    public static ConcurrentHashMap<Integer, BlockPos> riftTrigger;
    
    public static AuraWorld getAuraWorld(int dim) {
        return AuraHandler.auras.get(dim);
    }
    
    public static AuraChunk getAuraChunk(int dim, int x, int y) {
        if (AuraHandler.auras.containsKey(dim)) {
            return AuraHandler.auras.get(dim).getAuraChunkAt(x, y);
        }
        addAuraWorld(dim);
        if (AuraHandler.auras.containsKey(dim)) {
            return AuraHandler.auras.get(dim).getAuraChunkAt(x, y);
        }
        return null;
    }
    
    public static void addAuraWorld(int dim) {
        if (!AuraHandler.auras.containsKey(dim)) {
            AuraHandler.auras.put(dim, new AuraWorld(dim));
            Thaumcraft.log.info("Creating aura cache for world " + dim);
        }
    }
    
    public static void removeAuraWorld(int dim) {
        AuraHandler.auras.remove(dim);
        Thaumcraft.log.info("Removing aura cache for world " + dim);
    }
    
    public static void addAuraChunk(int dim, LevelChunk chunk, short base, float vis, float flux) {
        AuraWorld aw = AuraHandler.auras.get(dim);
        if (aw == null) {
            aw = new AuraWorld(dim);
        }
        aw.getAuraChunks().put(new PosXY(chunk.getPos().x, chunk.getPos().z), new AuraChunk(chunk, base, vis, flux));
        AuraHandler.auras.put(dim, aw);
    }
    
    public static void removeAuraChunk(int dim, int x, int y) {
        AuraWorld aw = AuraHandler.auras.get(dim);
        if (aw != null) {
            aw.getAuraChunks().remove(new PosXY(x, y));
        }
    }
    
    public static float getTotalAura(Level world, BlockPos pos) {
        AuraChunk ac = getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), pos.getX() >> 4, pos.getZ() >> 4);
        return (ac != null) ? (ac.getVis() + ac.getFlux()) : 0.0f;
    }
    
    public static float getFluxSaturation(Level world, BlockPos pos) {
        AuraChunk ac = getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), pos.getX() >> 4, pos.getZ() >> 4);
        return (ac != null) ? (ac.getFlux() / ac.getBase()) : 0.0f;
    }
    
    public static float getVis(Level world, BlockPos pos) {
        AuraChunk ac = getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), pos.getX() >> 4, pos.getZ() >> 4);
        return (ac != null) ? ac.getVis() : 0.0f;
    }
    
    public static float getFlux(Level world, BlockPos pos) {
        AuraChunk ac = getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), pos.getX() >> 4, pos.getZ() >> 4);
        return (ac != null) ? ac.getFlux() : 0.0f;
    }
    
    public static int getAuraBase(Level world, BlockPos pos) {
        AuraChunk ac = getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), pos.getX() >> 4, pos.getZ() >> 4);
        return (ac != null) ? ac.getBase() : 0;
    }
    
    public static boolean shouldPreserveAura(Level world, Player player, BlockPos pos) {
        return (player == null || ThaumcraftCapabilities.getKnowledge(player).isResearchComplete("AURAPRESERVE")) && getVis(world, pos) / getAuraBase(world, pos) < 0.1;
    }
    
    public static void addVis(Level world, BlockPos pos, float amount) {
        if (amount < 0.0f) {
            return;
        }
        try {
            AuraChunk ac = getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), pos.getX() >> 4, pos.getZ() >> 4);
            modifyVisInChunk(ac, amount, true);
        }
        catch (Exception ex) {}
    }
    
    public static void addFlux(Level world, BlockPos pos, float amount) {
        if (amount < 0.0f) {
            return;
        }
        try {
            AuraChunk ac = getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), pos.getX() >> 4, pos.getZ() >> 4);
            modifyFluxInChunk(ac, amount, true);
        }
        catch (Exception ex) {}
    }
    
    public static float drainVis(Level world, BlockPos pos, float amount, boolean simulate) {
        boolean didit = false;
        try {
            AuraChunk ac = getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), pos.getX() >> 4, pos.getZ() >> 4);
            if (amount > ac.getVis()) {
                amount = ac.getVis();
            }
            didit = modifyVisInChunk(ac, -amount, !simulate);
        }
        catch (Exception ex) {}
        return didit ? amount : 0.0f;
    }
    
    public static float drainFlux(Level world, BlockPos pos, float amount, boolean simulate) {
        boolean didit = false;
        try {
            AuraChunk ac = getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), pos.getX() >> 4, pos.getZ() >> 4);
            if (amount > ac.getFlux()) {
                amount = ac.getFlux();
            }
            didit = modifyFluxInChunk(ac, -amount, !simulate);
        }
        catch (Exception ex) {}
        return didit ? amount : 0.0f;
    }
    
    public static boolean modifyVisInChunk(AuraChunk ac, float amount, boolean doit) {
        if (ac != null) {
            if (doit) {
                ac.setVis(Math.max(0.0f, ac.getVis() + amount));
            }
            return true;
        }
        return false;
    }
    
    private static boolean modifyFluxInChunk(AuraChunk ac, float amount, boolean doit) {
        if (ac != null) {
            if (doit) {
                ac.setFlux(Math.max(0.0f, ac.getFlux() + amount));
            }
            return true;
        }
        return false;
    }
    
    public static void generateAura(LevelChunk chunk, net.minecraft.world.level.Level level, Random rand) {
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;
        Biome bgb = level.getBiome(new BlockPos(cx * 16 + 8, 50, cz * 16 + 8)).value();
        if (BiomeHandler.getBiomeBlacklist(0) != -1) {
            return;
        }
        float life = BiomeHandler.getBiomeAuraModifier(bgb);
        for (int a = 0; a < 4; ++a) {
            Direction dir = Direction.from2DDataValue(a);
            Biome bgb2 = level.getBiome(new BlockPos((cx + dir.getStepX()) * 16 + 8, 50, (cz + dir.getStepZ()) * 16 + 8)).value();
            life += BiomeHandler.getBiomeAuraModifier(bgb2);
        }
        life /= 5.0f;
        float noise = (float)(1.0 + world.getRandom().nextGaussian() * 0.10000000149011612);
        short base = (short)(life * 500.0f * noise);
        base = (short)Mth.clamp(base, 0, 500);
        int dim = (level instanceof net.minecraft.server.level.ServerLevel sl) ? sl.dimension().identifier().hashCode() : 0;
        addAuraChunk(dim, chunk, base, base, 0.0f);
    }
    
    static {
        AuraHandler.auras = new ConcurrentHashMap<Integer, AuraWorld>();
        AuraHandler.dirtyChunks = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<ChunkPos>>();
        AuraHandler.riftTrigger = new ConcurrentHashMap<Integer, BlockPos>();
    }
}
