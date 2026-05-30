package thaumcraft.common.world.biomes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import net.minecraft.world.level.biome.Biome;
import thaumcraft.api.aspects.Aspect;

// TODO: port to modern NeoForge biome tagging (BiomeTags / holder.is())
public class BiomeHandler {
    public static Biome EERIE;
    public static Biome MAGICAL_FOREST;
    public static Biome ELDRITCH;
    public static HashMap<String, java.util.List> biomeInfo = new HashMap<>();
    public static Collection<Aspect> c;
    public static ArrayList<Aspect> basicAspects = new ArrayList<>();
    public static ArrayList<Aspect> complexAspects = new ArrayList<>();
    public static HashMap<Integer, Integer> dimensionBlacklist = new HashMap<>();
    public static HashMap<Integer, Integer> biomeBlacklist = new HashMap<>();

    public static float getBiomeAuraModifier(Biome biome) { return 0.5f; }
    public static Aspect getRandomBiomeTag(int biomeId, Random random) { return null; }
    public static float getBiomeSupportsGreatwood(int biomeId) { return 0.0f; }
    public static float getBiomeGreatWoodChance(int biomeId) { return 0.0f; }
    public static int getBiomeBlacklist(int biomeId) { return -1; }
    public static int getDimBlacklist(int dimId) { return -1; }
    public static void addBiomeBlacklist(int biomeId, int val) { biomeBlacklist.put(biomeId, val); }
    public static void addDimBlacklist(int dimId, int val) { dimensionBlacklist.put(dimId, val); }

    public static void init() {}
    public static void registerBiomeInfo(String type, float auraLevel, Aspect tag, boolean greatwood, float greatwoodchance) {}
}
