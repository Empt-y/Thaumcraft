package thaumcraft.common.blocks.basic;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import thaumcraft.common.blocks.BlockTC;


public class BlockMetalTC extends BlockTC
{
    public BlockMetalTC(String name) {
        super(null /*  null   Material removed    */, name);
        setHardness(4.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
    }
    
    public boolean isBeaconBase(BlockGetter world, BlockPos pos, BlockPos beacon) {
        return true;
    }
}
