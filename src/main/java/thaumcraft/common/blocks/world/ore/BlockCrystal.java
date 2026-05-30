package thaumcraft.common.blocks.world.ore;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.world.aura.AuraHandler;


public class BlockCrystal extends Block
{
    public static final IntegerProperty SIZE       = IntegerProperty.create("size", 0, 3);
    public static final IntegerProperty GENERATION = IntegerProperty.create("gen", 1, 4);
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST  = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST  = BooleanProperty.create("west");
    public static final BooleanProperty UP    = BooleanProperty.create("up");
    public static final BooleanProperty DOWN  = BooleanProperty.create("down");

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
    static {
        PROPERTY_BY_DIRECTION = new EnumMap<>(Direction.class);
        PROPERTY_BY_DIRECTION.put(Direction.NORTH, NORTH);
        PROPERTY_BY_DIRECTION.put(Direction.EAST,  EAST);
        PROPERTY_BY_DIRECTION.put(Direction.SOUTH, SOUTH);
        PROPERTY_BY_DIRECTION.put(Direction.WEST,  WEST);
        PROPERTY_BY_DIRECTION.put(Direction.UP,    UP);
        PROPERTY_BY_DIRECTION.put(Direction.DOWN,  DOWN);
    }

    public final Aspect aspect;

    public BlockCrystal(Aspect aspect) {
        super(BlockBehaviour.Properties.of()
                .strength(0.25f)
                .sound(SoundsTC.CRYSTAL)
                .noOcclusion()
                .randomTicks()
                .lightLevel(s -> 1)
                .isRedstoneConductor((s, w, p) -> false)
                .isViewBlocking((s, w, p) -> false));
        this.aspect = aspect;
        registerDefaultState(this.stateDefinition.any()
                .setValue(SIZE, 0)
                .setValue(GENERATION, 1)
                .setValue(NORTH, false)
                .setValue(EAST,  false)
                .setValue(SOUTH, false)
                .setValue(WEST,  false)
                .setValue(UP,    false)
                .setValue(DOWN,  false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIZE, GENERATION, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    // ── Connections ──────────────────────────────────────────────────────────

    /** Crystal connects to a face when the adjacent block has a sturdy surface facing inward. */
    private boolean drawAt(BlockGetter world, BlockPos neighborPos, Direction sidePointingAtNeighbor) {
        BlockState fbs = level().getBlockState(neighborPos);
        return !fbs.isAir() && fbs.isFaceSturdy(world, neighborPos, sidePointingAtNeighbor.getOpposite());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
            BlockPos pos, Direction dir, BlockPos neighborPos, BlockState neighborState, RandomSource rand) {
        return state.setValue(PROPERTY_BY_DIRECTION.get(dir), drawAt(level, neighborPos, dir));
    }

    private int countConnections(BlockState state) {
        int c = 0;
        for (BooleanProperty prop : PROPERTY_BY_DIRECTION.values()) {
            if (state.getValue(prop)) c++;
        }
        return c;
    }

    // ── Survival ─────────────────────────────────────────────────────────────

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return BlockUtils.isBlockTouching(world, pos, null /* Material removed */, true);
    }

    @Override
    protected void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn,
            @Nullable net.minecraft.world.level.redstone.Orientation orientation, boolean movedByPiston) {
        if (!worldIn.isClientSide() && !state.canSurvive(worldIn, pos)) {
            Block.dropResources(state, worldIn, pos);
            worldIn.removeBlock(pos, false);
        }
    }

    // ── Shapes ───────────────────────────────────────────────────────────────

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        if (countConnections(state) > 1) return Shapes.block();
        if (state.getValue(UP))    return Shapes.box(0.125, 0.5,   0.125, 0.875, 1.0,   0.875);
        if (state.getValue(DOWN))  return Shapes.box(0.125, 0.0,   0.125, 0.875, 0.5,   0.875);
        if (state.getValue(EAST))  return Shapes.box(0.5,   0.125, 0.125, 1.0,   0.875, 0.875);
        if (state.getValue(WEST))  return Shapes.box(0.0,   0.125, 0.125, 0.5,   0.875, 0.875);
        if (state.getValue(SOUTH)) return Shapes.box(0.125, 0.125, 0.5,   0.875, 0.875, 1.0);
        if (state.getValue(NORTH)) return Shapes.box(0.125, 0.125, 0.0,   0.875, 0.875, 0.5);
        return Shapes.box(0.125, 0.0, 0.125, 0.875, 0.875, 0.875);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    // ── Drops ────────────────────────────────────────────────────────────────

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < getGrowth(state) + 1; i++) {
            drops.add(ThaumcraftApiHelper.makeCrystal(aspect));
        }
        return drops;
    }

    // ── Random tick / growth ─────────────────────────────────────────────────

    @Override
    protected void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        if (rand.nextInt(3 + getGeneration(state)) != 0) return;

        final int threshold = 10;
        int growth     = getGrowth(state);
        int generation = getGeneration(state);

        if (aspect != Aspect.FLUX) {
            if (AuraHelper.getVis(worldIn, pos) <= threshold) {
                if (growth > 0) {
                    worldIn.setBlock(pos, state.setValue(SIZE, growth - 1), 3);
                    AuraHelper.addVis(worldIn, pos, (float) threshold);
                } else if (BlockUtils.isBlockTouching(worldIn, pos, null, true)) {
                    Block.dropResources(state, worldIn, pos);
                    worldIn.removeBlock(pos, false);
                    AuraHandler.addVis(worldIn, pos, (float) threshold);
                }
            } else if (AuraHelper.getVis(worldIn, pos) > AuraHandler.getAuraBase(worldIn, pos) + threshold) {
                if (growth < 3 && growth < 5 - generation + (int) (pos.asLong() % 3L)) {
                    if (AuraHelper.drainVis(worldIn, pos, (float) threshold, false) > 0.0f) {
                        worldIn.setBlock(pos, state.setValue(SIZE, growth + 1), 3);
                    }
                } else if (generation < 4) {
                    BlockPos p2 = spreadCrystal(worldIn, pos, rand);
                    if (p2 != null && AuraHelper.drainVis(worldIn, pos, (float) threshold, false) > 0.0f) {
                        if (rand.nextInt(6) == 0) generation--;
                        worldIn.setBlock(p2, defaultBlockState().setValue(GENERATION, generation + 1), 3);
                    }
                }
            }
        } else {
            if (AuraHelper.getFlux(worldIn, pos) <= threshold) {
                if (growth > 0) {
                    worldIn.setBlock(pos, state.setValue(SIZE, growth - 1), 3);
                    AuraHelper.polluteAura(worldIn, pos, (float) threshold, false);
                } else if (BlockUtils.isBlockTouching(worldIn, pos, null, true)) {
                    Block.dropResources(state, worldIn, pos);
                    worldIn.removeBlock(pos, false);
                    AuraHelper.polluteAura(worldIn, pos, (float) threshold, false);
                }
            } else if (AuraHelper.getFlux(worldIn, pos) > AuraHandler.getAuraBase(worldIn, pos) + threshold) {
                if (growth < 3 && growth < 5 - generation + (int) (pos.asLong() % 3L)) {
                    if (AuraHelper.drainFlux(worldIn, pos, (float) threshold, false) > 0.0f) {
                        worldIn.setBlock(pos, state.setValue(SIZE, growth + 1), 3);
                    }
                } else if (generation < 4) {
                    BlockPos p2 = spreadCrystal(worldIn, pos, rand);
                    if (p2 != null && AuraHelper.drainFlux(worldIn, pos, (float) threshold, false) > 0.0f) {
                        if (rand.nextInt(6) == 0) generation--;
                        worldIn.setBlock(p2, defaultBlockState().setValue(GENERATION, generation + 1), 3);
                    }
                }
            }
        }
    }

    public static BlockPos spreadCrystal(Level world, BlockPos pos, RandomSource rand) {
        int xx = pos.getX() + rand.nextInt(3) - 1;
        int yy = pos.getY() + rand.nextInt(3) - 1;
        int zz = pos.getZ() + rand.nextInt(3) - 1;
        BlockPos t = new BlockPos(xx, yy, zz);
        if (t.equals(pos)) return null;
        BlockState bs = world.getBlockState(t);
        if (!bs.isAir() && !bs.canBeReplaced()) return null;
        if (rand.nextInt(16) != 0) return null;
        return BlockUtils.isBlockTouching(world, t, null, true) ? t : null;
    }

    // ── State helpers ────────────────────────────────────────────────────────

    public int getGrowth(BlockState state) {
        return state.getValue(SIZE);
    }

    public int getGeneration(BlockState state) {
        return state.getValue(GENERATION);
    }

    public boolean canSilkHarvest(Level world, BlockPos pos, BlockState state, Player player) {
        return false;
    }
}
