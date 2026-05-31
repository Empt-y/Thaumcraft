package thaumcraft.common.blocks.essentia;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.blocks.IBlockFacingHorizontal;


public class BlockSmelterAux extends BlockTC implements IBlockFacingHorizontal
{
    public BlockSmelterAux() {
        super(null /*  null   Material removed    */, "smelter_aux");
        setSoundType(SoundType.METAL);
        registerDefaultState(defaultBlockState().setValue((Property)IBlockFacingHorizontal.FACING, Direction.NORTH));
        setHardness(1.0f);
        setResistance(10.0f);
    }

    public boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player) {
        return true;
    }

    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        BlockState bs = defaultBlockState();
        if (!facing.getAxis().isHorizontal()) {
            facing = Direction.NORTH;
        }
        bs = bs.setValue((Property)IBlockFacingHorizontal.FACING, facing.getOpposite());
        return bs;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }
}
