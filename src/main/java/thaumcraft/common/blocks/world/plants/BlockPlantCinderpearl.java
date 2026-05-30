package thaumcraft.common.blocks.world.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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

public class BlockPlantCinderpearl extends BushBlock
{
    public BlockPlantCinderpearl() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.GRASS)
                .lightLevel(s -> 8)
                .noCollision()
                .instabreak());
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
        return state.is(Blocks.SAND) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.TERRACOTTA) || state.is(Blocks.RED_SAND);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if (rand.nextBoolean()) {
            double xr = pos.getX() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * 0.1;
            double yr = pos.getY() + 0.6 + (rand.nextFloat() - rand.nextFloat()) * 0.1;
            double zr = pos.getZ() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * 0.1;
            world.addParticle(ParticleTypes.FLAME, xr, yr, zr, 0.0, 0.01, 0.0);
            world.addParticle(ParticleTypes.SMOKE, xr, yr, zr, 0.0, 0.01, 0.0);
        }
    }
}
