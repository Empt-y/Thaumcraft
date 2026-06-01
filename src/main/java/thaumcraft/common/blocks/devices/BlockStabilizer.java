package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.tiles.devices.TileStabilizer;


public class BlockStabilizer extends BlockTCDevice implements IInfusionStabiliserExt
{
    public BlockStabilizer() {
        super(null /*  null   Material removed    */, TileStabilizer.class, "stabilizer", true);
        setSoundType(SoundType.STONE);
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
    
    public static int colorMultiplier(int meta) {
        float f = meta / 15.0f;
        float f2 = f * 0.5f + 0.5f;
        if (meta == 0) {
            f2 = 0.3f;
        }
        int i = Mth.clamp((int)(f2 * 255.0f), 0, 255);
        int j = Mth.clamp((int)(f2 * 255.0f), 0, 255);
        int k = Mth.clamp((int)(f2 * 255.0f), 0, 255);
        return 0xFF000000 | i << 16 | j << 8 | k;
    }
    
    public Object /* BlockRenderLayer removed */ getBlockLayer_removed() {
        return null; // Object /* BlockRenderLayer removed */ removed
    }    
    @Override
    public int getLightEmission(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        return 4;
    }
    
    @Override
    public boolean canStabaliseInfusion(Level world, BlockPos pos) {
        return true;
    }
    
    @Override
    public float getStabilizationAmount(Level world, BlockPos pos) {
        return 0.25f;
    }
}
