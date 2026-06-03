package thaumcraft.common.blocks.world.taint;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.lib.SoundsTC;

public class BlockTaintFibre extends Block implements ITaintBlock
{
    public static final BooleanProperty NORTH  = BooleanProperty.create("north");
    public static final BooleanProperty EAST   = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH  = BooleanProperty.create("south");
    public static final BooleanProperty WEST   = BooleanProperty.create("west");
    public static final BooleanProperty UP     = BooleanProperty.create("up");
    public static final BooleanProperty DOWN   = BooleanProperty.create("down");
    public static final BooleanProperty GROWTH1 = BooleanProperty.create("growth1");
    public static final BooleanProperty GROWTH2 = BooleanProperty.create("growth2");
    public static final BooleanProperty GROWTH3 = BooleanProperty.create("growth3");
    public static final BooleanProperty GROWTH4 = BooleanProperty.create("growth4");

    private static final VoxelShape SHAPE_UP    = Shapes.box(0.0, 0.95, 0.0, 1.0, 1.0, 1.0);
    private static final VoxelShape SHAPE_DOWN  = Shapes.box(0.0, 0.0,  0.0, 1.0, 0.05, 1.0);
    private static final VoxelShape SHAPE_EAST  = Shapes.box(0.95, 0.0, 0.0, 1.0, 1.0, 1.0);
    private static final VoxelShape SHAPE_WEST  = Shapes.box(0.0,  0.0, 0.0, 0.05, 1.0, 1.0);
    private static final VoxelShape SHAPE_SOUTH = Shapes.box(0.0,  0.0, 0.95, 1.0, 1.0, 1.0);
    private static final VoxelShape SHAPE_NORTH = Shapes.box(0.0,  0.0, 0.0, 1.0, 1.0, 0.05);

    public BlockTaintFibre() {
        super(thaumcraft.common.blocks.BlockTC.autoProps(BlockBehaviour.Properties.of()
                .strength(1.0f)
                .randomTicks()
                .noCollision()
                .noOcclusion()
                .noLootTable()
                .replaceable()));
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false)
                .setValue(WEST, false).setValue(UP, false).setValue(DOWN, false)
                .setValue(GROWTH1, false).setValue(GROWTH2, false)
                .setValue(GROWTH3, false).setValue(GROWTH4, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, GROWTH1, GROWTH2, GROWTH3, GROWTH4);
    }

    @Override
    public MapColor getMapColor(BlockState state, BlockGetter world, BlockPos pos, MapColor defaultColor) {
        return MapColor.COLOR_PURPLE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        VoxelShape shape = Shapes.empty();
        if (state.getValue(UP))    shape = Shapes.or(shape, SHAPE_UP);
        if (state.getValue(DOWN))  shape = Shapes.or(shape, SHAPE_DOWN);
        if (state.getValue(EAST))  shape = Shapes.or(shape, SHAPE_EAST);
        if (state.getValue(WEST))  shape = Shapes.or(shape, SHAPE_WEST);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SHAPE_SOUTH);
        if (state.getValue(NORTH)) shape = Shapes.or(shape, SHAPE_NORTH);
        return shape.isEmpty() ? Shapes.block() : shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        if (state.getValue(GROWTH3)) return 12;
        if (state.getValue(GROWTH2) || state.getValue(GROWTH4)) return 6;
        return super.getLightEmission(state, world, pos);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 3;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 3;
    }

    // ── State updates ─────────────────────────────────────────────────────────

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
            BlockPos pos, Direction dir, BlockPos neighborPos, BlockState neighborState, RandomSource rand) {
        BooleanProperty prop = dirProp(dir);
        boolean connected = drawAt(level, neighborPos, dir);
        state = state.setValue(prop, connected);
        // Recalculate growth from seed
        state = applyGrowthState(state, level, pos);
        return state;
    }

    private BlockState applyGrowthState(BlockState state, BlockGetter world, BlockPos pos) {
        boolean d = drawAt(world, pos.below(), Direction.DOWN);
        boolean u = drawAt(world, pos.above(), Direction.UP);
        Random rand = new Random(pos.asLong());
        int q = rand.nextInt(50);
        int growth = 0;
        if (d) {
            if (q < 4) growth = 1;
            else if (q == 4 || q == 5) growth = 2;
            else if (q == 6) growth = 3;
        }
        if (u && q > 47) growth = 4;
        return state
                .setValue(GROWTH1, growth == 1)
                .setValue(GROWTH2, growth == 2)
                .setValue(GROWTH3, growth == 3)
                .setValue(GROWTH4, growth == 4);
    }

    private boolean drawAt(BlockGetter world, BlockPos neighbor, Direction sidePointingAtNeighbor) {
        BlockState bs = world.getBlockState(neighbor);
        return bs.getBlock() != BlocksTC.taintFibre
                && bs.getBlock() != BlocksTC.taintFeature
                && bs.isFaceSturdy(world, neighbor, sidePointingAtNeighbor.getOpposite());
    }

    private static BooleanProperty dirProp(Direction dir) {
        return switch (dir) {
            case UP    -> UP;
            case DOWN  -> DOWN;
            case EAST  -> EAST;
            case WEST  -> WEST;
            case SOUTH -> SOUTH;
            case NORTH -> NORTH;
        };
    }

    // ── Tick ──────────────────────────────────────────────────────────────────

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        boolean hasGrowth = state.getValue(GROWTH1) || state.getValue(GROWTH2)
                || state.getValue(GROWTH3) || state.getValue(GROWTH4);
        if (!hasGrowth && isOnlyAdjacentToTaint(world, pos)) {
            die(world, pos, state);
            return;
        }
        if (!TaintHelper.isNearTaintSeed(world, pos)) {
            die(world, pos, state);
            return;
        }
        TaintHelper.spreadFibres(world, pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block block,
            @Nullable Orientation orientation, boolean movedByPiston) {
        boolean hasGrowth = state.getValue(GROWTH1) || state.getValue(GROWTH2)
                || state.getValue(GROWTH3) || state.getValue(GROWTH4);
        if (!hasGrowth && isOnlyAdjacentToTaint(world, pos)) {
            world.removeBlock(pos, false);
        }
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        if (eventID == 1) {
            if (world.isClientSide()) {
                world.playSound(null, pos, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS,
                        0.1f, 0.9f + world.getRandom().nextFloat() * 0.2f);
            }
            return true;
        }
        return super.triggerEvent(state, world, pos, eventID, eventParam);
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClientSide() && entity instanceof LivingEntity living
                && !living.is(EntityTypeTags.UNDEAD)
                && world.getRandom().nextInt(750) == 0) {
            Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionFluxTaint.instance);
            living.addEffect(new MobEffectInstance(effect, 200, 0, false, true));
        }
    }

    // ── Drops ─────────────────────────────────────────────────────────────────

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel world, BlockPos pos,
            net.minecraft.world.item.ItemStack tool, boolean dropExperience) {
        if (state.getValue(GROWTH3)) {
            if (world.getRandom().nextInt(5) == 0 && thaumcraft.common.config.ConfigItems.FLUX_CRYSTAL != null) {
                net.minecraft.world.entity.item.ItemEntity ie = new net.minecraft.world.entity.item.ItemEntity(
                    world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    thaumcraft.common.config.ConfigItems.FLUX_CRYSTAL.copy());
                world.addFreshEntity(ie);
            }
            AuraHelper.polluteAura(world, pos, 1.0f, true);
        }
    }

    // ── Taint helper statics ──────────────────────────────────────────────────

    public void die(Level world, BlockPos pos, BlockState state) {
        world.removeBlock(pos, false);
    }

    public static boolean isOnlyAdjacentToTaint(Level world, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            BlockState bs = world.getBlockState(neighbor);
            if (!bs.isAir()
                    && !(bs.getBlock() instanceof ITaintBlock)
                    && bs.isFaceSturdy(world, neighbor, dir.getOpposite())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHemmedByTaint(Level world, BlockPos pos) {
        int c = 0;
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            BlockState bs = world.getBlockState(neighbor);
            if (bs.getBlock() instanceof ITaintBlock) {
                c++;
            } else if (bs.isAir()) {
                c--;
            } else if (!bs.liquid() && !bs.isFaceSturdy(world, neighbor, dir.getOpposite())) {
                c--;
            }
        }
        return c > 0;
    }

    @Override
    public net.minecraft.world.level.block.SoundType getSoundType(
            net.minecraft.world.level.block.state.BlockState state,
            net.minecraft.world.level.LevelReader world, net.minecraft.core.BlockPos pos,
            @javax.annotation.Nullable net.minecraft.world.entity.Entity entity) {
        net.minecraft.world.level.block.SoundType t = super.getSoundType(state, world, pos, entity);
        return t != null ? t : net.minecraft.world.level.block.SoundType.STONE;
    }

}
