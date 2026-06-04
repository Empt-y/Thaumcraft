package thaumcraft.common.world;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.world.objects.WorldGenCustomFlowers;

/**
 * Retrogeneration: places Thaumcraft features in already-generated chunks.
 * New chunks use JSON-driven BiomeModifiers (ores, crystals, trees).
 * This handles shimmerleaf, cinderpearl, and direct ore/crystal scatter for regen.
 */
public class ThaumcraftWorldGenerator {
    public static ThaumcraftWorldGenerator INSTANCE = new ThaumcraftWorldGenerator();

    public void worldGeneration(Random rand, int chunkX, int chunkZ, Level world, boolean newChunk) {
        if (world.isClientSide()) return;
        BlockPos base = new BlockPos(chunkX * 16 + 8, 64, chunkZ * 16 + 8);

        generateVegetation(world, rand, chunkX, chunkZ);

        if (!(world instanceof net.minecraft.server.level.ServerLevel sl)) return;
        generateOres(sl, rand, chunkX, chunkZ);
        generateCrystals(sl, rand, chunkX, chunkZ);
    }

    private void generateVegetation(Level world, Random rand, int chunkX, int chunkZ) {
        if (!ModConfig.CONFIG_WORLD.generateTrees) return;
        int bx = chunkX * 16 + 8, bz = chunkZ * 16 + 8;
        BlockPos surface = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(bx, 0, bz));

        if (rand.nextInt(16) == 0 && BlocksTC.shimmerleaf != null) {
            new WorldGenCustomFlowers(BlocksTC.shimmerleaf, 0).generate(world, rand, surface);
        }
        if (rand.nextInt(24) == 0 && BlocksTC.cinderpearl != null) {
            new WorldGenCustomFlowers(BlocksTC.cinderpearl, 0).generate(world, rand, surface);
        }
    }

    private void generateOres(net.minecraft.server.level.ServerLevel world, Random rand, int chunkX, int chunkZ) {
        float density = ModConfig.CONFIG_WORLD.oreDensity / 100.0f;
        int baseX = chunkX * 16 + 8, baseZ = chunkZ * 16 + 8;

        if (ModConfig.CONFIG_WORLD.generateCinnabar) {
            for (int i = 0; i < Math.round(3 * density); i++) {
                int x = baseX + rand.nextInt(13) - 6;
                int y = rand.nextInt(Math.max(1, world.getMaxY() / 5));
                int z = baseZ + rand.nextInt(13) - 6;
                tryPlaceOre(world, x, y, z, BlocksTC.oreCinnabar.defaultBlockState());
            }
        }
        if (ModConfig.CONFIG_WORLD.generateQuartz) {
            for (int i = 0; i < Math.round(3 * density); i++) {
                int x = baseX + rand.nextInt(13) - 6;
                int y = rand.nextInt(Math.max(1, world.getMaxY() / 4));
                int z = baseZ + rand.nextInt(13) - 6;
                tryPlaceOre(world, x, y, z, BlocksTC.oreQuartz.defaultBlockState());
            }
        }
        if (ModConfig.CONFIG_WORLD.generateAmber) {
            for (int i = 0; i < Math.round(3 * density); i++) {
                int x = baseX + rand.nextInt(13) - 6;
                int z = baseZ + rand.nextInt(13) - 6;
                BlockPos surface = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));
                int y = surface.getY() - rand.nextInt(25);
                tryPlaceOre(world, x, y, z, BlocksTC.oreAmber.defaultBlockState());
            }
        }
    }

    private void generateCrystals(net.minecraft.server.level.ServerLevel world, Random rand, int chunkX, int chunkZ) {
        if (!ModConfig.CONFIG_WORLD.generateCrystals) return;
        float density = ModConfig.CONFIG_WORLD.oreDensity / 100.0f;
        int baseX = chunkX * 16 + 8, baseZ = chunkZ * 16 + 8;
        thaumcraft.common.blocks.world.ore.ShardType[] types = thaumcraft.common.blocks.world.ore.ShardType.values();

        for (int j = 0; j < Math.round(2 * density); j++) {
            int x = baseX + rand.nextInt(13) - 6;
            int z = baseZ + rand.nextInt(13) - 6;
            BlockPos surface = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));
            int y = rand.nextInt(Math.max(5, surface.getY() - 5));
            int md = rand.nextInt(types.length);
            net.minecraft.world.level.block.Block ore = types[md].getOre();
            if (ore == null) continue;
            for (int xx = -1; xx <= 1; xx++) for (int yy = -1; yy <= 1; yy++) for (int zz = -1; zz <= 1; zz++) {
                if (rand.nextInt(3) != 0) {
                    BlockPos bp = new BlockPos(x + xx, y + yy, z + zz);
                    BlockState bs = world.getBlockState(bp);
                    if ((bs.isAir() || bs.canBeReplaced()) && hasAdjacentStone(world, bp)) {
                        world.setBlock(bp, ore.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    private static void tryPlaceOre(net.minecraft.server.level.ServerLevel world, int x, int y, int z, BlockState ore) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState cur = world.getBlockState(pos);
        if (cur.is(BlockTags.STONE_ORE_REPLACEABLES)) {
            world.setBlock(pos, ore, 2);
        }
    }

    private static boolean hasAdjacentStone(Level world, BlockPos pos) {
        for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
            if (world.getBlockState(pos.relative(d)).is(BlockTags.STONE_ORE_REPLACEABLES)) return true;
        }
        return false;
    }

    public static boolean generateFlowers(Level world, Random random, BlockPos pos,
            net.minecraft.world.level.block.Block block, int md) {
        return new WorldGenCustomFlowers(block, md).generate(world, random, pos);
    }
}
