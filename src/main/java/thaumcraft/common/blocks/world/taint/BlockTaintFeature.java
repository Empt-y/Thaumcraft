package thaumcraft.common.blocks.world.taint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;

import javax.annotation.Nullable;

public class BlockTaintFeature extends BlockTC implements ITaintBlock
{
    public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);

    public BlockTaintFeature() {
        super(BlockBehaviour.Properties.of()
                .strength(0.1f)
                .lightLevel(s -> 10)
                .randomTicks()
                .noLootTable()
                .noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        if (!TaintHelper.isNearTaintSeed(world, pos) && rand.nextInt(10) == 0) {
            die(world, pos, state);
            return;
        }
        TaintHelper.spreadFibres(world, pos);
        BlockState below = world.getBlockState(pos.below());
        if (below.getBlock() == BlocksTC.taintLog
                && below.getValue(BlockTaintLog.AXIS) == Direction.Axis.Y
                && rand.nextInt(100) == 0) {
            world.setBlock(pos, BlocksTC.taintGeyser.defaultBlockState(), 3);
        }
    }

    @Override
    public void die(Level world, BlockPos pos, BlockState state) {
        world.setBlock(pos, BlocksTC.fluxGoo.defaultBlockState(), 3);
    }

    /** Spawn a taint crawler or spread flux on break (server-side only). */
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean movedByPiston) {
        if (world.getRandom().nextFloat() < 0.333f) {
            EntityTaintCrawler crawler = new EntityTaintCrawler(world);
            crawler.snapTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    (float) world.getRandom().nextInt(360), 0.0f);
            world.addFreshEntity(crawler);
        } else {
            AuraHelper.polluteAura(world, pos, 1.0f, true);
        }
        super.affectNeighborsAfterRemoval(state, world, pos, movedByPiston);
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block block,
            @Nullable Orientation orientation, boolean movedByPiston) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        if (!world.getBlockState(supportPos).isFaceSturdy(world, supportPos, facing)) {
            world.removeBlock(pos, false);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
            BlockPos pos, Direction dir, BlockPos neighborPos, BlockState neighborState, RandomSource rand) {
        Direction facing = state.getValue(FACING);
        if (dir == facing.getOpposite()
                && !neighborState.isFaceSturdy(level, neighborPos, facing)) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext ctx) {
        Direction face = ctx.getClickedFace();
        return defaultBlockState().setValue(FACING, face);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACING)) {
            case UP    -> net.minecraft.world.phys.shapes.Shapes.box(0.125, 0.625, 0.125, 0.875, 1.0, 0.875);
            case DOWN  -> net.minecraft.world.phys.shapes.Shapes.box(0.125, 0.0, 0.125, 0.875, 0.375, 0.875);
            case SOUTH -> net.minecraft.world.phys.shapes.Shapes.box(0.125, 0.125, 0.625, 0.875, 0.875, 1.0);
            case NORTH -> net.minecraft.world.phys.shapes.Shapes.box(0.125, 0.125, 0.0, 0.875, 0.875, 0.375);
            case EAST  -> net.minecraft.world.phys.shapes.Shapes.box(0.625, 0.125, 0.125, 1.0, 0.875, 0.875);
            case WEST  -> net.minecraft.world.phys.shapes.Shapes.box(0.0, 0.125, 0.125, 0.375, 0.875, 0.875);
        };
    }
}
