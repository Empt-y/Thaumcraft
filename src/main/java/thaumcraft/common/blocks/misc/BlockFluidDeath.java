package thaumcraft.common.blocks.misc;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

// Fluid block stub - old Forge BlockFluidClassic API removed; fluid behaviour needs modern fluid system
public class BlockFluidDeath extends Block
{
    public BlockFluidDeath() {
        super(BlockBehaviour.Properties.of().sound(net.minecraft.world.level.block.SoundType.STONE).liquid());
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
