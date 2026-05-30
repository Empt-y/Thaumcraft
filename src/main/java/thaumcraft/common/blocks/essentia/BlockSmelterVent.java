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
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.utils.BlockStateUtils;


public class BlockSmelterVent extends BlockTC implements IBlockFacingHorizontal
{
    public BlockSmelterVent() {
        super(null /*  null   Material removed    */, "smelter_vent");
        setSoundType(SoundType.METAL);
        registerDefaultState(defaultBlockState().setValue((Property)IBlockFacingHorizontal.FACING, Direction.NORTH));
        setHardness(1.0f);
        setResistance(10.0f);
    }

    public boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player) {
        return true;
    }

    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null;
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

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        Direction facing = BlockStateUtils.getFacing(state);
        switch (facing.ordinal()) {
            default: {
                return new AABB(0.125, 0.125, 0.0, 0.875, 0.875, 0.5);
            }
            case 3: {
                return new AABB(0.125, 0.125, 0.5, 0.875, 0.875, 1.0);
            }
            case 4: {
                return new AABB(0.0, 0.125, 0.125, 0.5, 0.875, 0.875);
            }
            case 5: {
                return new AABB(0.5, 0.125, 0.125, 1.0, 0.875, 0.875);
            }
        }
    }
}
