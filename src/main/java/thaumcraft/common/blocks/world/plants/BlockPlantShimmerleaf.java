package thaumcraft.common.blocks.world.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;

public class BlockPlantShimmerleaf extends BushBlock
{
    public BlockPlantShimmerleaf() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.GRASS)
                .lightLevel(s -> 6)
                .noCollision()
                .instabreak());
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.PODZOL) || state.is(Blocks.MYCELIUM);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if (rand.nextInt(3) == 0) {
            float xr = (float)(pos.getX() + 0.5f + rand.nextGaussian() * 0.1);
            float yr = (float)(pos.getY() + 0.4f + rand.nextGaussian() * 0.1);
            float zr = (float)(pos.getZ() + 0.5f + rand.nextGaussian() * 0.1);
            FXDispatcher.INSTANCE.drawWispyMotes(xr, yr, zr,
                    rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01,
                    10, 0.3f + world.getRandom().nextFloat() * 0.3f,
                    0.7f + world.getRandom().nextFloat() * 0.3f,
                    0.7f + world.getRandom().nextFloat() * 0.3f, 0.0f);
        }
    }
}
