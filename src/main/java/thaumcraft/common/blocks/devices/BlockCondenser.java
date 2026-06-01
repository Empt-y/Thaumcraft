package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.tiles.devices.TileCondenser;


public class BlockCondenser extends BlockTCDevice implements IBlockEnabled
{
    public BlockCondenser() {
        super(null /*  null   Material removed    */, TileCondenser.class, "condenser", true);
        setSoundType(SoundType.METAL);
    }
    
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }
    
    public boolean isFullCube(BlockState state) {
        return false;
    }    
    public int damageDropped(BlockState state) {
        return 0;
    }
}
