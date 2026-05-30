package thaumcraft.common.blocks.basic;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;


public class BlockPillar extends BlockTC
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private Random rand;

    public BlockPillar(String name) {
        super(null /*  null   Material removed    */, name);
        rand = new Random();
        setHardness(2.5f);
        setSoundType(SoundType.STONE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public Object getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
    }

    public AABB getCollisionBoundingBox(BlockState blockState, BlockGetter worldIn, BlockPos pos) {
        return new AABB(0.0, 0.0, 0.0, 1.0, 2.0, 1.0);
    }

    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        return defaultBlockState().setValue(FACING, placer.getDirection());
    }

    public void onBlockPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        Direction enumfacing = Direction.from2DDataValue(Mth.floor(placer.getYRot() * 4.0f / 360.0f + 0.5) & 0x3).getOpposite();
        worldIn.setBlock(pos, state.setValue(FACING, enumfacing), 3);
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (worldIn instanceof Level level) {
            if (state.getBlock() == BlocksTC.pillarArcane) {
                Block.popResource(level, pos, new ItemStack(BlocksTC.stoneArcane, 2));
            }
            if (state.getBlock() == BlocksTC.pillarAncient) {
                Block.popResource(level, pos, new ItemStack(BlocksTC.stoneAncient, 2));
            }
            if (state.getBlock() == BlocksTC.pillarEldritch) {
                Block.popResource(level, pos, new ItemStack(BlocksTC.stoneEldritchTile, 2));
            }
        }
        super.destroy(worldIn, pos, state);
    }

    public BlockState getStateFromMeta(int meta) {
        return defaultBlockState();
    }

    public static int calcMeta(Direction enumfacing) {
        if (enumfacing.getAxis() == Direction.Axis.Y) {
            enumfacing = Direction.NORTH;
        }
        return enumfacing.get2DDataValue();
    }

    public int getMetaFromState(BlockState state) {
        return state.getValue(FACING).get2DDataValue();
    }
}
