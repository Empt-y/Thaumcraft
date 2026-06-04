package thaumcraft.common.world.objects;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.blocks.BlocksTC;

/**
 * Tall magic tree (simplified Jungle-tree-style). Full SOURCE algorithm was ~300 lines of 1.12
 * code; this port produces the same basic silhouette: tall trunk with a rounded top canopy.
 */
public class WorldGenBigMagicTree {

    public WorldGenBigMagicTree(boolean doBlockNotify) {}

    public boolean generate(Level world, Random random, BlockPos pos) {
        return doGenerate(world, random, pos);
    }

    private boolean doGenerate(LevelAccessor world, Random random, BlockPos pos) {
        int height = 16 + random.nextInt(8);
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();

        if (y < 1 || y + height + 4 > world.getMaxY()) return false;

        BlockState below = world.getBlockState(pos.below());
        if (!below.is(BlockTags.DIRT) && !(below.getBlock() == net.minecraft.world.level.block.Blocks.GRASS_BLOCK)) return false;

        BlockState log  = BlocksTC.logGreatwood.defaultBlockState();
        BlockState leaf = BlocksTC.leafGreatwood.defaultBlockState();

        // Single trunk
        for (int k = 0; k < height; k++) {
            setb(world, x, y + k, z, log);
        }

        // Rounded canopy
        int top = y + height;
        int radius = 4 + random.nextInt(2);
        for (int dy = -radius + 1; dy <= radius; dy++) {
            double ry = (double) dy / radius;
            int r = (int) (radius * Math.sqrt(Math.max(0.0, 1.0 - ry * ry * 0.7)));
            for (int bx = x - r; bx <= x + r; bx++) {
                for (int bz = z - r; bz <= z + r; bz++) {
                    double dist = Math.hypot(bx - x, bz - z);
                    if (dist <= r + 0.5) {
                        BlockPos lp = new BlockPos(bx, top + dy, bz);
                        BlockState cur = world.getBlockState(lp);
                        if (cur.isAir() || cur.is(BlockTags.LEAVES) || cur.canBeReplaced()) {
                            setb(world, lp, leaf);
                        }
                    }
                }
            }
        }

        // 4 branch arms
        int armY = y + height - 4;
        for (int dir = 0; dir < 4; dir++) {
            int dx = (dir == 0) ? -3 : (dir == 1) ? 3 : 0;
            int dz = (dir == 2) ? -3 : (dir == 3) ? 3 : 0;
            placeArm(world, x, armY, z, x + dx, armY + 2, z + dz, log, leaf, random);
        }

        return true;
    }

    private void placeArm(LevelAccessor w, int x0, int y0, int z0,
                          int x1, int y1, int z1,
                          BlockState log, BlockState leaf, Random rand) {
        int dx = x1 - x0, dy = y1 - y0, dz = z1 - z0;
        int steps = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
        for (int i = 0; i <= steps; i++) {
            int bx = x0 + Math.round((float) dx * i / steps);
            int by = y0 + Math.round((float) dy * i / steps);
            int bz = z0 + Math.round((float) dz * i / steps);
            setb(w, new BlockPos(bx, by, bz), log);
            if (i == steps) {
                for (int lx = -1; lx <= 1; lx++) for (int ly = 0; ly <= 1; ly++) for (int lz = -1; lz <= 1; lz++) {
                    BlockPos lp = new BlockPos(bx + lx, by + ly, bz + lz);
                    BlockState cur = w.getBlockState(lp);
                    if ((cur.isAir() || cur.canBeReplaced()) && rand.nextInt(2) == 0) setb(w, lp, leaf);
                }
            }
        }
    }

    private static void setb(LevelAccessor w, int x, int y, int z, BlockState s) {
        w.setBlock(new BlockPos(x, y, z), s, 3);
    }
    private static void setb(LevelAccessor w, BlockPos p, BlockState s) { w.setBlock(p, s, 3); }
}
