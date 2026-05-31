package thaumcraft.common.world.objects;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.api.blocks.BlocksTC;

public class WorldGenSilverwoodTrees extends Feature<NoneFeatureConfiguration> {

    public static final MapCodec<WorldGenSilverwoodTrees> CODEC = MapCodec.unit(new WorldGenSilverwoodTrees());

    public WorldGenSilverwoodTrees() { super(NoneFeatureConfiguration.CODEC); }

    public boolean generate(Level world, RandomSource random, BlockPos pos) {
        return doGenerate(world, random, pos);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        return doGenerate(ctx.level(), ctx.random(), ctx.origin());
    }

    private boolean doGenerate(LevelAccessor world, RandomSource random, BlockPos pos) {
        int height = random.nextInt(10) + 10;
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        if (y < 1 || y + height + 4 > world.getMaxY()) return false;

        // Space check
        for (int i = y; i <= y + 1 + height; i++) {
            int spread = (i == y) ? 0 : (i >= y + 1 + height - 2) ? 3 : 1;
            for (int bx = x - spread; bx <= x + spread; bx++)
                for (int bz = z - spread; bz <= z + spread; bz++)
                    if (!canReplace(world.getBlockState(new BlockPos(bx, i, bz))) && i > y) return false;
        }
        BlockState below = world.getBlockState(pos.below());
        if (!below.is(BlockTags.DIRT) && !below.is(BlockTags.SAND)
                && !(below.getBlock() == net.minecraft.world.level.block.Blocks.GRASS_BLOCK)) return false;

        BlockState log = BlocksTC.logSilverwood.defaultBlockState();
        BlockState leaf = BlocksTC.leafSilverwood.defaultBlockState();

        // Spherical canopy
        int start = y + height - 5, end = y + height + 3 + random.nextInt(3);
        for (int ky = start; ky <= end; ky++) {
            int cty = Mth.clamp(ky, y + height - 3, y + height);
            for (int bx = x - 5; bx <= x + 5; bx++)
                for (int bz = z - 5; bz <= z + 5; bz++) {
                    double dx = bx-x, dy = ky-cty, dz = bz-z;
                    if (dx*dx + dy*dy + dz*dz < 10 + random.nextInt(8))
                        setIfReplace(world, new BlockPos(bx, ky, bz), leaf);
                }
        }

        // Cross trunk
        for (int k = 0; k < height; k++) {
            setb(world, x,   y+k, z,   log); setb(world, x-1, y+k, z,   log);
            setb(world, x+1, y+k, z,   log); setb(world, x,   y+k, z-1, log);
            setb(world, x,   y+k, z+1, log);
        }
        setb(world, x, y+height, z, log);

        // Base star
        setb(world,x-1,y,z-1,log); setb(world,x+1,y,z+1,log);
        setb(world,x-1,y,z+1,log); setb(world,x+1,y,z-1,log);
        if (random.nextInt(3)!=0) setb(world,x-1,y+1,z-1,log);
        if (random.nextInt(3)!=0) setb(world,x+1,y+1,z+1,log);
        if (random.nextInt(3)!=0) setb(world,x-1,y+1,z+1,log);
        if (random.nextInt(3)!=0) setb(world,x+1,y+1,z-1,log);

        // Root arms
        setb(world,x-2,y,  z,log); setb(world,x+2,y,  z,log);
        setb(world,x,  y,z-2,log); setb(world,x,  y,z+2,log);
        setb(world,x-2,y-1,  z,log); setb(world,x+2,y-1,  z,log);
        setb(world,x,  y-1,z-2,log); setb(world,x,  y-1,z+2,log);

        // Upper stubs
        int h4 = height-4;
        setb(world,x-1,y+h4,z-1,log); setb(world,x+1,y+h4,z+1,log);
        setb(world,x-1,y+h4,z+1,log); setb(world,x+1,y+h4,z-1,log);
        if (random.nextInt(3)==0) setb(world,x-1,y+h4-1,z-1,log);
        if (random.nextInt(3)==0) setb(world,x+1,y+h4-1,z+1,log);
        if (random.nextInt(3)==0) setb(world,x-1,y+h4-1,z+1,log);
        if (random.nextInt(3)==0) setb(world,x+1,y+h4-1,z-1,log);
        setb(world,x-2,y+h4,z,log); setb(world,x+2,y+h4,z,log);
        setb(world,x,y+h4,z-2,log); setb(world,x,y+h4,z+2,log);
        return true;
    }

    private static boolean canReplace(BlockState s) {
        return s.isAir() || s.is(BlockTags.LEAVES) || s.canBeReplaced();
    }
    private static void setIfReplace(LevelAccessor w, BlockPos p, BlockState s) {
        if (canReplace(w.getBlockState(p))) w.setBlock(p, s, 3);
    }
    private static void setb(LevelAccessor w, int x, int y, int z, BlockState s) {
        w.setBlock(new BlockPos(x,y,z), s, 3);
    }
}
