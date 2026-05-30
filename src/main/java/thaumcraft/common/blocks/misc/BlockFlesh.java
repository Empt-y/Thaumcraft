package thaumcraft.common.blocks.misc;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.lib.SoundsTC;


public class BlockFlesh extends BlockTC
{
    public BlockFlesh() {
        super(null, "flesh_block");
        setResistance(2.0f);
        setHardness(0.25f);
    }
    
    public SoundType getSoundType() {
        return SoundsTC.GORE;
    }
}
