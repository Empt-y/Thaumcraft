package thaumcraft.common.world.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class WorldGenSilverwoodTrees {
    private int minTreeHeight;
    private int randomTreeHeight;
    boolean worldgen;

    public WorldGenSilverwoodTrees(boolean doBlockNotify, int minTreeHeight, int randomTreeHeight) {
        worldgen = !doBlockNotify;
        this.minTreeHeight = minTreeHeight;
        this.randomTreeHeight = randomTreeHeight;
    }

    public boolean generate(Level world, RandomSource random, BlockPos pos) {
        return false; // TODO: port to modern world gen feature system
    }
}
