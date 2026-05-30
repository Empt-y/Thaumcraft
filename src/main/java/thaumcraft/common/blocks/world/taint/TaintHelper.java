package thaumcraft.common.blocks.world.taint;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.aura.AuraHandler;

public class TaintHelper
{
    private static ConcurrentHashMap<Integer, ArrayList<BlockPos>> taintSeeds;

    private static int dimKey(Level world) {
        return world instanceof ServerLevel sl ? sl.dimension().identifier().hashCode() : 0;
    }

    public static void addTaintSeed(Level world, BlockPos pos) {
        int key = dimKey(world);
        ArrayList<BlockPos> locs = taintSeeds.computeIfAbsent(key, k -> new ArrayList<>());
        locs.add(pos);
    }

    public static void removeTaintSeed(Level world, BlockPos pos) {
        ArrayList<BlockPos> locs = taintSeeds.get(dimKey(world));
        if (locs != null) locs.remove(pos);
    }

    public static boolean isNearTaintSeed(Level world, BlockPos pos) {
        double area = ModConfig.CONFIG_WORLD.taintSpreadArea * ModConfig.CONFIG_WORLD.taintSpreadArea;
        ArrayList<BlockPos> locs = taintSeeds.get(dimKey(world));
        if (locs != null && !locs.isEmpty()) {
            for (BlockPos p : locs) {
                if (p.distSqr(pos) <= area) {
                    if (world instanceof ServerLevel sl
                            && sl.getEntitiesOfClass(EntityTaintSeed.class,
                                    new net.minecraft.world.phys.AABB(p).inflate(1.0)).isEmpty()) {
                        removeTaintSeed(world, p);
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAtTaintSeedEdge(Level world, BlockPos pos) {
        double area = ModConfig.CONFIG_WORLD.taintSpreadArea * ModConfig.CONFIG_WORLD.taintSpreadArea;
        double fringe = ModConfig.CONFIG_WORLD.taintSpreadArea * 0.8
                * (ModConfig.CONFIG_WORLD.taintSpreadArea * 0.8);
        ArrayList<BlockPos> locs = taintSeeds.get(dimKey(world));
        if (locs != null && !locs.isEmpty()) {
            for (BlockPos p : locs) {
                double d = p.distSqr(pos);
                if (d < area && d > fringe) return true;
            }
        }
        return false;
    }

    public static void spreadFibres(Level world, BlockPos pos) {
        spreadFibres(world, pos, false);
    }

    public static void spreadFibres(Level world, BlockPos pos, boolean ignore) {
        if (!ignore && ModConfig.CONFIG_MISC.wussMode) return;

        float mod = 0.001f + AuraHandler.getFluxSaturation(world, pos) * 2.0f;
        if (!ignore && world.getRandom().nextFloat()
                > ModConfig.CONFIG_WORLD.taintSpreadRate / 100.0f * mod) {
            return;
        }

        if (!isNearTaintSeed(world, pos)) return;

        int xx = pos.getX() + world.getRandom().nextInt(3) - 1;
        int yy = pos.getY() + world.getRandom().nextInt(3) - 1;
        int zz = pos.getZ() + world.getRandom().nextInt(3) - 1;
        BlockPos t = new BlockPos(xx, yy, zz);
        if (t.equals(pos)) return;

        BlockState bs = world.getBlockState(t);
        float bh = bs.getDestroySpeed(world, t);
        if (bh < 0.0f || bh > 10.0f) return;

        boolean isLeaves = bs.is(BlockTags.LEAVES);
        boolean isLiquid = bs.liquid();
        boolean isEmpty = world.isEmptyBlock(t);
        boolean isReplaceable = bs.canBeReplaced();
        boolean isPlantLike = bs.getBlock() instanceof FlowerBlock
                || bs.is(BlockTags.FLOWERS) || bs.is(BlockTags.SMALL_FLOWERS);

        // Spread taint fibre to empty/replaceable/plant positions
        if (!isLeaves && !isLiquid
                && (isEmpty || isReplaceable || isPlantLike)
                && BlockUtils.isAdjacentToSolidBlock(world, t)
                && !BlockTaintFibre.isOnlyAdjacentToTaint(world, t)) {
            world.setBlock(t, BlocksTC.taintFibre.defaultBlockState(), 2);
            world.blockEvent(t, BlocksTC.taintFibre, 1, 0);
            AuraHelper.drainFlux(world, t, 0.01f, false);
            return;
        }

        // Spread to leaves (replace with taint feature or fibre)
        if (isLeaves) {
            Direction face;
            if (world.getRandom().nextFloat() < 0.6f
                    && (face = BlockUtils.getFaceBlockTouching(world, t, BlocksTC.taintLog)) != null) {
                world.setBlock(t, BlocksTC.taintFeature.defaultBlockState()
                        .setValue(BlockTaintFeature.FACING, face.getOpposite()), 2);
            } else {
                world.setBlock(t, BlocksTC.taintFibre.defaultBlockState(), 2);
                world.blockEvent(t, BlocksTC.taintFibre, 1, 0);
                AuraHelper.drainFlux(world, t, 0.01f, false);
            }
            return;
        }

        // Hemmed position: replace solid blocks with taint variants
        if (BlockTaintFibre.isHemmedByTaint(world, t) && bh < 5.0f) {
            // Wood logs → taint log
            if (Utils.isWoodLog(world, t) && !(bs.getBlock() instanceof ITaintBlock)) {
                world.setBlock(t, BlocksTC.taintLog.defaultBlockState()
                        .setValue(BlockTaintLog.AXIS, BlockUtils.getBlockAxis(world, t)), 2);
                return;
            }
            // Organic blocks → taint crust
            if (bs.getBlock() == Blocks.RED_MUSHROOM_BLOCK || bs.getBlock() == Blocks.BROWN_MUSHROOM_BLOCK
                    || bs.is(BlockTags.LOGS) || bs.is(net.minecraft.tags.BlockTags.CORAL_PLANTS)) {
                world.setBlock(t, BlocksTC.taintCrust.defaultBlockState(), 2);
                world.blockEvent(t, BlocksTC.taintCrust, 1, 0);
                AuraHelper.drainFlux(world, t, 0.01f, false);
                return;
            }
            // Soil blocks → taint soil
            if (bs.is(BlockTags.SAND) || bs.is(BlockTags.DIRT)
                    || bs.is(Blocks.CLAY.builtInRegistryHolder())) {
                world.setBlock(t, BlocksTC.taintSoil.defaultBlockState(), 2);
                world.blockEvent(t, BlocksTC.taintSoil, 1, 0);
                AuraHelper.drainFlux(world, t, 0.01f, false);
                return;
            }
            // Stone blocks → taint rock
            if (bs.is(BlockTags.BASE_STONE_OVERWORLD) || bs.is(BlockTags.STONE_ORE_REPLACEABLES)) {
                world.setBlock(t, BlocksTC.taintRock.defaultBlockState(), 2);
                world.blockEvent(t, BlocksTC.taintRock, 1, 0);
                AuraHelper.drainFlux(world, t, 0.01f, false);
                return;
            }
        }

        // Taint soil/rock + sufficient flux → spawn taint seed
        if ((bs.getBlock() == BlocksTC.taintSoil || bs.getBlock() == BlocksTC.taintRock)
                && world.isEmptyBlock(t.above())
                && AuraHelper.getFlux(world, t) >= 5.0f
                && world.getRandom().nextFloat() < ModConfig.CONFIG_WORLD.taintSpreadRate / 100.0f * 0.33
                && isAtTaintSeedEdge(world, t)
                && world instanceof ServerLevel sl) {
            EntityTaintSeed e = new EntityTaintSeed(sl);
            e.snapTo(t.getX() + 0.5, (double) t.above().getY(), t.getZ() + 0.5,
                    (float) world.getRandom().nextInt(360), 0.0f);
            if (e.checkSpawnRules(sl, EntitySpawnReason.NATURAL)) {
                AuraHelper.drainFlux(world, t, 5.0f, false);
                sl.addFreshEntity(e);
            }
        }
    }

    static {
        taintSeeds = new ConcurrentHashMap<>();
    }
}
