package thaumcraft.common.world.objects;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.api.blocks.BlocksTC;

/** Greatwood tree: 2x2 trunk, branching canopy. */
public class WorldGenGreatwoodTrees extends Feature<NoneFeatureConfiguration> {

    public static final MapCodec<WorldGenGreatwoodTrees> CODEC = MapCodec.unit(new WorldGenGreatwoodTrees());

    public WorldGenGreatwoodTrees(boolean doBlockNotify, boolean largeBranches) {
        super(NoneFeatureConfiguration.CODEC);
    }
    public WorldGenGreatwoodTrees() { super(NoneFeatureConfiguration.CODEC); }

    public boolean generate(Level world, RandomSource random, BlockPos pos) {
        return doGenerate(world, random, pos);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        return doGenerate(ctx.level(), ctx.random(), ctx.origin());
    }

    private boolean doGenerate(LevelAccessor world, RandomSource random, BlockPos pos) {
        int height = 8 + random.nextInt(8);
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        if (y < 1 || y + height + 5 > world.getMaxY()) return false;

        // Check ground
        BlockState below = world.getBlockState(pos.below());
        if (!below.is(BlockTags.DIRT) && !(below.getBlock() == net.minecraft.world.level.block.Blocks.GRASS_BLOCK)) return false;

        BlockState log  = BlocksTC.logGreatwood.defaultBlockState();
        BlockState leaf = BlocksTC.leafGreatwood.defaultBlockState();

        // 2x2 trunk
        for (int k = 0; k < height; k++) {
            setb(world,x,  y+k,z,  log); setb(world,x+1,y+k,z,  log);
            setb(world,x,  y+k,z+1,log); setb(world,x+1,y+k,z+1,log);
        }

        // Sphere-ish canopy
        int top = y + height;
        int radius = 4 + random.nextInt(2);
        for (int dy = -radius; dy <= radius+1; dy++) {
            double ry = (double)dy / (radius+1);
            int r = (int)(radius * (1.0 - ry*ry*0.7));
            for (int bx = x-r; bx <= x+r+1; bx++) {
                for (int bz = z-r; bz <= z+r+1; bz++) {
                    double dist = Math.hypot(bx - x - 0.5, bz - z - 0.5);
                    if (dist <= r + 0.5) {
                        BlockPos lp = new BlockPos(bx, top+dy, bz);
                        if (canReplace(world.getBlockState(lp))) setb(world, lp, leaf);
                    }
                }
            }
        }

        // Branch arms near the top
        int armY = y + height - 3;
        placeArm(world, x-1, armY, z,   x-3, armY+2, z,   log, leaf, random);
        placeArm(world, x+2, armY, z,   x+4, armY+2, z,   log, leaf, random);
        placeArm(world, x,   armY, z-1, x,   armY+2, z-3, log, leaf, random);
        placeArm(world, x,   armY, z+2, x,   armY+2, z+4, log, leaf, random);

        return true;
    }

    private void placeArm(LevelAccessor w, int x0, int y0, int z0,
                          int x1, int y1, int z1,
                          BlockState log, BlockState leaf, RandomSource rand) {
        int dx = x1-x0, dy = y1-y0, dz = z1-z0;
        int steps = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
        for (int i = 0; i <= steps; i++) {
            int bx = x0 + Math.round((float)dx*i/steps);
            int by = y0 + Math.round((float)dy*i/steps);
            int bz = z0 + Math.round((float)dz*i/steps);
            setb(w, new BlockPos(bx,by,bz), log);
            // Small leaf tufts at end
            if (i == steps) {
                for (int lx=-1;lx<=1;lx++) for (int ly=0;ly<=1;ly++) for (int lz=-1;lz<=1;lz++) {
                    BlockPos lp = new BlockPos(bx+lx,by+ly,bz+lz);
                    if (canReplace(w.getBlockState(lp)) && rand.nextInt(2)==0) setb(w,lp,leaf);
                }
            }
        }
    }

    private static boolean canReplace(BlockState s) {
        return s.isAir() || s.is(BlockTags.LEAVES) || s.canBeReplaced();
    }
    private static void setb(LevelAccessor w, BlockPos p, BlockState s) { w.setBlock(p, s, 3); }
    private static void setb(LevelAccessor w, int x, int y, int z, BlockState s) {
        w.setBlock(new BlockPos(x,y,z), s, 3);
    }
}
