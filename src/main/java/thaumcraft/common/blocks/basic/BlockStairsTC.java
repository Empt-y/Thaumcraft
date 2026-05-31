package thaumcraft.common.blocks.basic;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;


public class BlockStairsTC extends net.minecraft.world.level.block.StairBlock
{
    private final String tcName;

    public BlockStairsTC(String name, BlockState modelState) {
        super(modelState, BlockBehaviour.Properties.ofFullCopy(modelState.getBlock()).setId(
            net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.BLOCK,
                net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", name))));
        this.tcName = name;
    }

    public String getTCRegistryName() { return tcName; }

    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return super.getFlammability(state, world, pos, face);
    }

    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return super.getFireSpreadSpeed(state, world, pos, face);
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
