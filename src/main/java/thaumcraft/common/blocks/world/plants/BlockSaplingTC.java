package thaumcraft.common.blocks.world.plants;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.world.objects.WorldGenGreatwoodTrees;
import thaumcraft.common.world.objects.WorldGenSilverwoodTrees;

public class BlockSaplingTC extends SaplingBlock
{
    public static final MapCodec<BlockSaplingTC> CODEC = simpleCodec(
            props -> new BlockSaplingTC(TreeGrower.OAK, props));

    public BlockSaplingTC(TreeGrower treeGrower, BlockBehaviour.Properties props) {
        super(treeGrower, props);
    }

    @Override
    public MapCodec<BlockSaplingTC> codec() {
        return CODEC;
    }

    @Override
    public void advanceTree(ServerLevel world, BlockPos pos, BlockState state, RandomSource rand) {
        if (state.getBlock() == BlocksTC.saplingGreatwood) {
            // Try 2x2 placement first
            for (int i = 0; i >= -1; --i) {
                for (int j = 0; j >= -1; --j) {
                    if (isTwoByTwoOfType(world, pos, i, j, BlocksTC.saplingGreatwood)) {
                        world.setBlock(pos.offset(i, 0, j    ), world.getFluidState(pos.offset(i,     0, j    )).createLegacyBlock(), 4);
                        world.setBlock(pos.offset(i + 1, 0, j    ), world.getFluidState(pos.offset(i + 1, 0, j    )).createLegacyBlock(), 4);
                        world.setBlock(pos.offset(i, 0, j + 1), world.getFluidState(pos.offset(i,     0, j + 1)).createLegacyBlock(), 4);
                        world.setBlock(pos.offset(i + 1, 0, j + 1), world.getFluidState(pos.offset(i + 1, 0, j + 1)).createLegacyBlock(), 4);
                        new WorldGenGreatwoodTrees(true, false).generate(world, rand, pos.offset(i, 0, j));
                        return;
                    }
                }
            }
        } else {
            world.setBlock(pos, world.getFluidState(pos).createLegacyBlock(), 4);
            new WorldGenSilverwoodTrees(true, 7, 4).generate(world, rand, pos);
        }
    }

    private boolean isTwoByTwoOfType(ServerLevel world, BlockPos pos, int ox, int oz, net.minecraft.world.level.block.Block type) {
        return world.getBlockState(pos.offset(ox,     0, oz    )).is(type)
            && world.getBlockState(pos.offset(ox + 1, 0, oz    )).is(type)
            && world.getBlockState(pos.offset(ox,     0, oz + 1)).is(type)
            && world.getBlockState(pos.offset(ox + 1, 0, oz + 1)).is(type);
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
