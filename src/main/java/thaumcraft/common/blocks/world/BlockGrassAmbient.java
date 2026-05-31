package thaumcraft.common.blocks.world;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;

public class BlockGrassAmbient extends GrassBlock
{
    public BlockGrassAmbient() {
        super(thaumcraft.common.blocks.BlockTC.autoProps(BlockBehaviour.Properties.of()
                .strength(0.6f)
                .sound(SoundType.GRASS)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        int skyLight = world.getBrightness(LightLayer.SKY, pos.above());
        long dayTime = world.getOverworldClockTime() % 24000L;
        // simulate sun angle: daytime 0-12000, night 12000-24000
        float sunAngle = (float)(dayTime / 24000.0 * 2.0 * Mth.PI);
        int effectiveLight = Mth.clamp(Math.round(skyLight * Mth.cos(sunAngle)), 0, 15);
        if (4 + effectiveLight * 2 < 1 + rand.nextInt(13)) {
            int x = rand.nextIntBetweenInclusive(-8, 8);
            int z = rand.nextIntBetweenInclusive(-8, 8);
            BlockPos pp = pos.offset(x, 5, z);
            for (int q = 0; q < 10 && pp.getY() > 50
                    && !world.getBlockState(pp).is(Blocks.GRASS_BLOCK); pp = pp.below(), ++q) {}
            if (world.getBlockState(pp).is(Blocks.GRASS_BLOCK)) {
                FXDispatcher.INSTANCE.drawWispyMotesOnBlock(pp.above(), 400, -0.01f);
            }
        }
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
