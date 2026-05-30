package thaumcraft.common.lib.utils;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.server.level.ServerLevel;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.items.ItemsTC;



public class BlockUtils
{
    static BlockPos lastPos;
    static int lasty;
    static int lastz;
    static double lastdistance;
    public static ArrayList<String> portableHoleBlackList;

    private static boolean removeBlock(Player player, BlockPos pos) {
        return removeBlock(player, pos, false);
    }

    private static boolean removeBlock(Player player, BlockPos pos, boolean canHarvest) {
        BlockState iblockstate = player.level().getBlockState(pos);
        boolean flag = iblockstate.getBlock().removedByPlayer(iblockstate, player.level(), pos, player, canHarvest, player.level().getFluidState(pos));
        if (flag) {
            iblockstate.getBlock().destroy(player.level(), pos, iblockstate);
        }
        return flag;
    }

    public static boolean harvestBlockSkipCheck(Level world, Player player, BlockPos pos) {
        return harvestBlock(world, player, pos, false, false, 0, true);
    }

    public static boolean harvestBlock(Level world, Player player, BlockPos pos) {
        return harvestBlock(world, player, pos, false, false, 0, false);
    }

    public static boolean harvestBlock(Level world, Player p, BlockPos pos, boolean alwaysDrop, boolean silkOverride, int fortuneOverride, boolean skipEvent) {
        if (world.isClientSide() || !(p instanceof ServerPlayer)) {
            return false;
        }
        ServerPlayer player = (ServerPlayer) p;
        BlockState state = world.getBlockState(pos);
        BlockEntity te = world.getBlockEntity(pos);
        Block block = state.getBlock();
        if (!skipEvent) {
            var event = net.neoforged.neoforge.common.CommonHooks.fireBlockBreak(world, player.gameMode.getGameModeForPlayer(), player, pos, state);
            if (event.isCanceled()) return false;
        }
        block.playerWillDestroy(world, pos, state, player);
        world.removeBlock(pos, false);
        ItemStack tool = player.getMainHandItem();
        if (silkOverride || fortuneOverride > 0) {
            ItemStack fakeStack = tool.isEmpty() ? new ItemStack(ItemsTC.enchantedPlaceholder) : tool.copy();
            ItemEnchantments.Mutable enchMutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(fakeStack));
            if (silkOverride) {
                world.registryAccess().lookup(net.minecraft.core.registries.Registries.ENCHANTMENT)
                    .flatMap(reg -> reg.get(Enchantments.SILK_TOUCH))
                    .ifPresent(h -> enchMutable.set(h, 1));
            }
            if (fortuneOverride > 0) {
                final int fort = fortuneOverride;
                world.registryAccess().lookup(net.minecraft.core.registries.Registries.ENCHANTMENT)
                    .flatMap(reg -> reg.get(Enchantments.FORTUNE))
                    .ifPresent(h -> enchMutable.upgrade(h, fort));
            }
            EnchantmentHelper.setEnchantments(fakeStack, enchMutable.toImmutable());
            Block.dropResources(state, world, pos, te, player, fakeStack);
        } else {
            Block.dropResources(state, world, pos, te, player, tool);
        }
        return true;
    }

    public static void destroyBlockPartially(Level world, int par1, BlockPos pos, int par5) {
        // Packet-based block destruction animation — skipped in modern port
    }

    public static void findBlocks(Level world, BlockPos pos, BlockState block, int reach) {
        for (int xx = -reach; xx <= reach; ++xx) {
            for (int yy = reach; yy >= -reach; --yy) {
                for (int zz = -reach; zz <= reach; ++zz) {
                    if (Math.abs(BlockUtils.lastPos.getX() + xx - pos.getX()) > 24) {
                        return;
                    }
                    if (Math.abs(BlockUtils.lastPos.getY() + yy - pos.getY()) > 48) {
                        return;
                    }
                    if (Math.abs(BlockUtils.lastPos.getZ() + zz - pos.getZ()) > 24) {
                        return;
                    }
                    BlockPos candidate = BlockUtils.lastPos.offset(xx, yy, zz);
                    BlockState bs = world.getBlockState(candidate);
                    boolean same = bs == block;
                    if (same && bs.getDestroySpeed(world, candidate) >= 0.0f) {
                        double xd = BlockUtils.lastPos.getX() + xx - pos.getX();
                        double yd = BlockUtils.lastPos.getY() + yy - pos.getY();
                        double zd = BlockUtils.lastPos.getZ() + zz - pos.getZ();
                        double d = xd * xd + yd * yd + zd * zd;
                        if (d > BlockUtils.lastdistance) {
                            BlockUtils.lastdistance = d;
                            BlockUtils.lastPos = candidate;
                            findBlocks(world, pos, block, reach);
                            return;
                        }
                    }
                }
            }
        }
    }

    public static boolean breakFurthestBlock(Level world, BlockPos pos, BlockState block, Player player) {
        BlockUtils.lastPos = new BlockPos(pos);
        BlockUtils.lastdistance = 0.0;
        int reach = Utils.isWoodLog(world, pos) ? 2 : 1;
        findBlocks(world, pos, block, reach);
        boolean worked = harvestBlockSkipCheck(world, player, BlockUtils.lastPos);
        world.sendBlockUpdated(pos, block, block, 3);
        if (worked && Utils.isWoodLog(world, pos)) {
            world.sendBlockUpdated(pos, block, block, 3);
            for (int xx = -3; xx <= 3; ++xx) {
                for (int yy = -3; yy <= 3; ++yy) {
                    for (int zz = -3; zz <= 3; ++zz) {
                        BlockPos tickPos = BlockUtils.lastPos.offset(xx, yy, zz);
                        world.scheduleTick(tickPos, world.getBlockState(tickPos).getBlock(), 50 + net.minecraft.util.RandomSource.create().nextInt(75));
                    }
                }
            }
        }
        return worked;
    }

    public static HitResult getTargetBlock(Level world, Entity entity, boolean par3) {
        return getTargetBlock(world, entity, par3, par3, 10.0);
    }

    public static HitResult getTargetBlock(Level world, Entity entity, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, double range) {
        float var4 = 1.0f;
        float var5 = entity.xRotO + (entity.getXRot() - entity.xRotO) * var4;
        float var6 = entity.yRotO + (entity.getYRot() - entity.yRotO) * var4;
        double var7 = entity.xOld + (entity.getX() - entity.xOld) * var4;
        double var8 = entity.yOld + (entity.getY() - entity.yOld) * var4 + entity.getEyeHeight();
        double var9 = entity.zOld + (entity.getZ() - entity.zOld) * var4;
        Vec3 var10 = new Vec3(var7, var8, var9);
        float var11 = Mth.cos(-var6 * 0.017453292f - 3.1415927f);
        float var12 = Mth.sin(-var6 * 0.017453292f - 3.1415927f);
        float var13 = -Mth.cos(-var5 * 0.017453292f);
        float var14 = Mth.sin(-var5 * 0.017453292f);
        float var15 = var12 * var13;
        float var16 = var11 * var13;
        Vec3 var17 = var10.add(var15 * range, var14 * range, var16 * range);
        ClipContext.Fluid fluidMode = stopOnLiquid ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
        return world.clip(new ClipContext(var10, var17, ClipContext.Block.COLLIDER, fluidMode, entity));
    }

    public static int countExposedSides(Level world, BlockPos pos) {
        int count = 0;
        for (Direction dir : Direction.values()) {
            if (world.isEmptyBlock(pos.relative(dir))) {
                ++count;
            }
        }
        return count;
    }

    public static boolean isBlockExposed(Level world, BlockPos pos) {
        for (Direction face : Direction.values()) {
            if (!world.getBlockState(pos.relative(face)).canOcclude()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAdjacentToSolidBlock(Level world, BlockPos pos) {
        for (Direction face : Direction.values()) {
            BlockPos neighbor = pos.relative(face);
            if (world.getBlockState(neighbor).isFaceSturdy(world, neighbor, face.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlockTouching(BlockGetter world, BlockPos pos, BlockState bs) {
        for (Direction face : Direction.values()) {
            if (world.getBlockState(pos.relative(face)) == bs) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlockTouching(BlockGetter world, BlockPos pos, Block bs) {
        for (Direction face : Direction.values()) {
            if (world.getBlockState(pos.relative(face)).getBlock() == bs) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if any adjacent block is non-air and (if solid=true) has a sturdy face pointing back.
     * The mat parameter is kept for API compatibility but ignored (Material was removed in 1.20).
     */
    public static boolean isBlockTouching(BlockGetter world, BlockPos pos, Object mat /* Material removed */, boolean solid) {
        for (Direction face : Direction.values()) {
            BlockPos neighborPos = pos.relative(face);
            BlockState ns = world.getBlockState(neighborPos);
            if (!ns.isAir() && (!solid || ns.isFaceSturdy(world, neighborPos, face.getOpposite()))) {
                return true;
            }
        }
        return false;
    }

    public static Direction getFaceBlockTouching(BlockGetter world, BlockPos pos, Block bs) {
        for (Direction face : Direction.values()) {
            if (world.getBlockState(pos.relative(face)).getBlock() == bs) {
                return face;
            }
        }
        return null;
    }

    public static boolean isPortableHoleBlackListed(BlockState blockstate) {
        return isBlockListed(blockstate, BlockUtils.portableHoleBlackList);
    }

    public static boolean isBlockListed(BlockState blockstate, List<String> list) {
        String stateString = blockstate.toString();
        for (String key : list) {
            String[] splitString = key.split(";");
            if (splitString[0].contains(":")) {
                ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockstate.getBlock());
                if (!blockId.toString().equals(splitString[0])) {
                    continue;
                }
                if (splitString.length <= 1) {
                    return true;
                }
                int matches = 0;
                for (int a = 1; a < splitString.length; ++a) {
                    if (stateString.contains(splitString[a])) {
                        ++matches;
                    }
                }
                if (matches == splitString.length - 1) {
                    return true;
                }
            }
            // OreDictionary removed — tag-based checks not supported here
        }
        return false;
    }

    public static double distance(BlockPos b1, BlockPos b2) {
        double d3 = b1.getX() - b2.getX();
        double d4 = b1.getY() - b2.getY();
        double d5 = b1.getZ() - b2.getZ();
        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    @SuppressWarnings("unchecked")
    public static Direction.Axis getBlockAxis(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Direction.Axis ax = Direction.Axis.Y;
        for (Property<?> prop : state.getProperties()) {
            if (prop.getName().equals("axis")) {
                Object val = state.getValue(prop);
                if (val instanceof Direction.Axis) {
                    ax = (Direction.Axis) val;
                }
                break;
            }
        }
        if (ax == null) {
            ax = Direction.Axis.Y;
        }
        return ax;
    }

    public static boolean hasLOS(Level world, BlockPos source, BlockPos target) {
        HitResult mop = ThaumcraftApiHelper.rayTraceIgnoringSource(world,
            new Vec3(source.getX() + 0.5, source.getY() + 0.5, source.getZ() + 0.5),
            new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5),
            false, true, false);
        return mop == null || (mop.getType() == HitResult.Type.BLOCK && ((net.minecraft.world.phys.BlockHitResult) mop).getBlockPos().equals(target));
    }

    public static ItemStack getSilkTouchDrop(BlockState bs) {
        // Reflection-based getSilkTouchDrop removed — return the block's item stack
        try {
            Item item = bs.getBlock().asItem();
            if (item != net.minecraft.world.item.Items.AIR) {
                return new ItemStack(item);
            }
        } catch (Exception e) {
            Thaumcraft.log.warn("Could not get silk touch drop for " + bs);
        }
        return ItemStack.EMPTY;
    }

    static {
        BlockUtils.lastPos = BlockPos.ORIGIN;
        BlockUtils.lasty = 0;
        BlockUtils.lastz = 0;
        BlockUtils.lastdistance = 0.0;
        BlockUtils.portableHoleBlackList = new ArrayList<String>();
    }

    public static class BlockPosComparator implements Comparator<BlockPos>
    {
        private BlockPos source;

        public BlockPosComparator(BlockPos source) {
            this.source = source;
        }

        @Override
        public int compare(BlockPos a, BlockPos b) {
            if (a.equals(b)) {
                return 0;
            }
            double da = source.distSqr(a);
            double db = source.distSqr(b);
            return (da < db) ? -1 : ((da > db) ? 1 : 0);
        }
    }
}
