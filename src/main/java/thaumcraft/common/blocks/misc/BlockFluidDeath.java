package thaumcraft.common.blocks.misc;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

// Fluid block stub - old Forge BlockFluidClassic API removed; fluid behaviour needs modern fluid system
public class BlockFluidDeath extends Block
{
    public BlockFluidDeath() {
        super(BlockBehaviour.Properties.of().liquid());
    }
}
