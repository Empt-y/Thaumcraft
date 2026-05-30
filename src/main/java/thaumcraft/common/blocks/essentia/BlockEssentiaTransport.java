package thaumcraft.common.blocks.essentia;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.utils.BlockStateUtils;


public class BlockEssentiaTransport extends BlockTCDevice implements IBlockFacing
{
    public BlockEssentiaTransport(Class te, String name) {
        super(null /*  null   Material removed    */, te, name);
        setSoundType(SoundType.METAL);
        setHardness(1.0f);
        setResistance(10.0f);
        registerDefaultState(defaultBlockState().setValue((Property)IBlockFacing.FACING, Direction.UP));
    }
    
    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null; // Object /* BlockFaceShape removed */ removed
    }
    
    @Override
    public boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player) {
        return true;
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
    
    @Override
    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        BlockState bs = defaultBlockState();
        bs = bs.setValue((Property)IBlockFacing.FACING, facing);
        return bs;
    }
    
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        Direction facing = BlockStateUtils.getFacing(state);
        switch (facing.ordinal()) {
            default: {
                return new AABB(0.25, 0.5, 0.25, 0.75, 1.0, 0.75);
            }
            case 1: {
                return new AABB(0.25, 0.0, 0.25, 0.75, 0.5, 0.75);
            }
            case 2: {
                return new AABB(0.25, 0.25, 0.5, 0.75, 0.75, 1.0);
            }
            case 3: {
                return new AABB(0.25, 0.25, 0.0, 0.75, 0.75, 0.5);
            }
            case 4: {
                return new AABB(0.5, 0.25, 0.25, 1.0, 0.75, 0.75);
            }
            case 5: {
                return new AABB(0.0, 0.25, 0.25, 0.5, 0.75, 0.75);
            }
        }
    }
}
