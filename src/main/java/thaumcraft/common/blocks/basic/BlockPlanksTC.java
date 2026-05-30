package thaumcraft.common.blocks.basic;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blocks.BlockTC;


public class BlockPlanksTC extends BlockTC
{
    public BlockPlanksTC(String name) {
        super(null /*  null   Material removed    */, name);
        // setHarvestLevel removed in MC 26; use tool requirements in BlockBehaviour.Properties
        setHardness(2.0f);
        setSoundType(SoundType.WOOD);
    }
    
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 20;
    }

    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 5;
    }
}
