package thaumcraft.common.blocks.world.taint;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.blocks.BlocksTC;

public class BlockTaintLog extends RotatedPillarBlock implements ITaintBlock
{
    public static final MapCodec<BlockTaintLog> CODEC = simpleCodec(BlockTaintLog::new);

    public BlockTaintLog(BlockBehaviour.Properties props) {
        super(props);
    }

    public BlockTaintLog() {
        this(thaumcraft.common.blocks.BlockTC.autoProps(defaultProps()));
    }

    public static BlockBehaviour.Properties defaultProps() {
        return BlockBehaviour.Properties.of()
                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                .strength(3.0f, 100.0f)
                .randomTicks()
                .noLootTable();
    }

    @Override
    public MapCodec<? extends RotatedPillarBlock> codec() {
        return CODEC;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        if (!TaintHelper.isNearTaintSeed(world, pos)) {
            die(world, pos, state);
        } else {
            TaintHelper.spreadFibres(world, pos);
        }
    }

    @Override
    public void die(Level world, BlockPos pos, BlockState state) {
        world.setBlock(pos, BlocksTC.fluxGoo.defaultBlockState(), 3);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 4;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 4;
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
