package thaumcraft.common.world;

import java.util.Random;
import net.minecraft.world.level.Level;

// TODO: port to modern NeoForge world gen (BiomeModifier / PlacedFeature / ConfiguredFeature)
public class ThaumcraftWorldGenerator {
    public static ThaumcraftWorldGenerator INSTANCE = new ThaumcraftWorldGenerator();

    /** Regenerate Thaumcraft world features in the specified chunk. Currently a no-op pending world-gen port. */
    public void worldGeneration(Random rand, int chunkX, int chunkZ, Level world, boolean newChunk) {
        // TODO: implement when world gen is ported
    }
}
