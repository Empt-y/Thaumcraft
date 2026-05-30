package thaumcraft.common.blocks.basic;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import thaumcraft.common.blocks.BlockTC;


public class BlockStoneTC extends BlockTC
{
    private boolean spawn;
    
    public BlockStoneTC(String name, boolean spawn) {
        super(null /*  null   Material removed    */, name);
        this.spawn = spawn;
        setHardness(2.0f);
        setResistance(10.0f);
        setSoundType(SoundType.STONE);
    }
    
    public boolean isBeaconBase(BlockGetter world, BlockPos pos, BlockPos beacon) {
        return true;
    }
    
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return defaultDestroyTime() >= 0.0f;
    }
}
