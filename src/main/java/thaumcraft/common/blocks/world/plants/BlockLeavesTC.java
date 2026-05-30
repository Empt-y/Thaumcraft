package thaumcraft.common.blocks.world.plants;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.world.aura.AuraHandler;

public class BlockLeavesTC extends LeavesBlock
{
    public static final MapCodec<BlockLeavesTC> CODEC = simpleCodec(
            props -> new BlockLeavesTC(0.05f, props));

    public BlockLeavesTC(float leafParticleChance, BlockBehaviour.Properties props) {
        super(leafParticleChance, props);
    }

    /** Convenience factory for registration — caller supplies map color via props. */
    public static BlockBehaviour.Properties defaultProps() {
        return BlockBehaviour.Properties.of()
                .sound(SoundType.GRASS)
                .strength(0.2f)
                .noOcclusion()
                .randomTicks()
                .isSuffocating((s, w, p) -> false)
                .isViewBlocking((s, w, p) -> false);
    }

    @Override
    public MapCodec<? extends LeavesBlock> codec() {
        return CODEC;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 60;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 30;
    }

    /** Silverwood leaves slowly regenerate aura while persistent (player-placed). */
    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        if (this.getBlock() == BlocksTC.leafSilverwood
                && state.getValue(PERSISTENT)
                && AuraHandler.getVis(world, pos) < AuraHandler.getAuraBase(world, pos)) {
            AuraHandler.addVis(world, pos, 0.01f);
        }
        super.randomTick(state, world, pos, rand);
    }

    @Override
    protected void spawnFallingLeavesParticle(Level world, BlockPos pos, RandomSource rand) {
        if (this.getBlock() == BlocksTC.leafSilverwood) {
            float x = pos.getX() + rand.nextFloat();
            float y = pos.getY() - 0.05f;
            float z = pos.getZ() + rand.nextFloat();
            FXDispatcher.INSTANCE.drawWispyMotes(x, y, z, 0.0, -0.02, 0.0, 8, 0.3f, 0.7f, 1.0f, 0.0f);
        }
    }
}
