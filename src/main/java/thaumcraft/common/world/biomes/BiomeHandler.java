package thaumcraft.common.world.biomes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.biome.Biome;
import thaumcraft.api.aspects.Aspect;

/**
 * Biome handler: provides biome-specific aura, aspect, and tree generation data.
 * In modern MC, biome identity is tracked by ResourceKey<Biome>, not an integer ID.
 * This class retains the old int-keyed API surface so existing callers compile,
 * but the values are best-effort defaults (all biomes treated as moderate magic).
 */
public class BiomeHandler {
    public static Biome EERIE;
    public static Biome MAGICAL_FOREST;
    public static Biome ELDRITCH;

    public static HashMap<String, List<?>> biomeInfo = new HashMap<>();
    public static Collection<Aspect> c;
    public static ArrayList<Aspect> basicAspects  = new ArrayList<>();
    public static ArrayList<Aspect> complexAspects = new ArrayList<>();
    public static HashMap<Integer, Integer> dimensionBlacklist = new HashMap<>();
    public static HashMap<Integer, Integer> biomeBlacklist     = new HashMap<>();

    // Biome-info registry: biome type name → {auraLevel, aspect, supportsGreatwood, greatwoodChance}
    private static final HashMap<String, float[]> biomeAura       = new HashMap<>();
    private static final HashMap<String, Aspect>  biomeAspect     = new HashMap<>();
    private static final HashMap<String, Float>   greatwoodSupport = new HashMap<>();

    public static float getBiomeAuraModifier(Biome biome) { return 0.5f; }

    public static Aspect getRandomBiomeTag(int biomeId, Random random) {
        if (basicAspects.isEmpty()) return null;
        return basicAspects.get(random.nextInt(basicAspects.size()));
    }

    /** Probability [0,1] that a greatwood tree can generate in this biome. */
    public static float getBiomeSupportsGreatwood(int biomeId) { return 0.25f; }

    public static float getBiomeGreatWoodChance(int biomeId) { return 0.1f; }

    /** -1 = not blacklisted, 0 = blacklist all, 1 = no ores, 2 = no trees. */
    public static int getBiomeBlacklist(int biomeId) {
        return biomeBlacklist.getOrDefault(biomeId, -1);
    }

    public static int getDimBlacklist(int dimId) {
        return dimensionBlacklist.getOrDefault(dimId, -1);
    }

    public static void addBiomeBlacklist(int biomeId, int val) { biomeBlacklist.put(biomeId, val); }
    public static void addDimBlacklist(int dimId, int val)     { dimensionBlacklist.put(dimId, val); }

    public static void init() {
        basicAspects.clear();
        complexAspects.clear();
        for (Aspect a : Aspect.aspects.values()) {
            if (a.isPrimal()) basicAspects.add(a);
            else              complexAspects.add(a);
        }
    }

    public static void registerBiomeInfo(String type, float auraLevel, Aspect tag,
            boolean greatwood, float greatwoodchance) {
        biomeAura.put(type, new float[]{ auraLevel });
        if (tag != null) biomeAspect.put(type, tag);
        greatwoodSupport.put(type, greatwood ? greatwoodchance : 0.0f);
    }
}
