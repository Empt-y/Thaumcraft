package thaumcraft.common.blocks.devices;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.redstone.Orientation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.tiles.devices.TileStabilizer;


public class BlockInlay extends BlockTC implements IInfusionStabiliserExt
{
    public static EnumProperty<EnumAttachPosition> NORTH;
    public static EnumProperty<EnumAttachPosition> EAST;
    public static EnumProperty<EnumAttachPosition> SOUTH;
    public static EnumProperty<EnumAttachPosition> WEST;
    public static IntegerProperty CHARGE;
    protected static AABB[] REDSTONE_WIRE_AABB;

    private static final Direction[] HORIZONTALS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    public BlockInlay() {
        super(null /*  null   Material removed    */, "inlay");
        setSoundType(SoundType.METAL);
        setHardness(0.5f);
        registerDefaultState(defaultBlockState()
            .setValue(NORTH, EnumAttachPosition.NONE)
            .setValue(EAST, EnumAttachPosition.NONE)
            .setValue(SOUTH, EnumAttachPosition.NONE)
            .setValue(WEST, EnumAttachPosition.NONE)
            .setValue(CHARGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, CHARGE);
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return BlockInlay.REDSTONE_WIRE_AABB[getAABBIndex(state)];
    }

    private static int getAABBIndex(BlockState state) {
        int i = 0;
        boolean flag = state.getValue(NORTH) != EnumAttachPosition.NONE;
        boolean flag2 = state.getValue(EAST) != EnumAttachPosition.NONE;
        boolean flag3 = state.getValue(SOUTH) != EnumAttachPosition.NONE;
        boolean flag4 = state.getValue(WEST) != EnumAttachPosition.NONE;
        if (flag || (flag3 && !flag && !flag2 && !flag4)) {
            i |= 1 << Direction.NORTH.get2DDataValue();
        }
        if (flag2 || (flag4 && !flag && !flag2 && !flag3)) {
            i |= 1 << Direction.EAST.get2DDataValue();
        }
        if (flag3 || (flag && !flag2 && !flag3 && !flag4)) {
            i |= 1 << Direction.SOUTH.get2DDataValue();
        }
        if (flag4 || (flag2 && !flag && !flag3 && !flag4)) {
            i |= 1 << Direction.WEST.get2DDataValue();
        }
        return i;
    }

    private EnumAttachPosition getAttachPosition(BlockGetter worldIn, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.relative(direction);
        BlockState iblockstate = worldIn.getBlockState(blockpos);
        if (canConnectTo(iblockstate, direction, worldIn, blockpos)) {
            return EnumAttachPosition.SIDE;
        }
        if (isSourceBlock(worldIn, blockpos)) {
            return EnumAttachPosition.EXT;
        }
        return EnumAttachPosition.NONE;
    }

    protected static boolean canConnectTo(BlockState blockState, @Nullable Direction side, BlockGetter world, BlockPos pos) {
        Block block = blockState.getBlock();
        return block == BlocksTC.inlay || block instanceof BlockPedestal;
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

    public BlockState withRotation(BlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180:
                return state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90:
                return state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
            case CLOCKWISE_90:
                return state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
            default:
                return state;
        }
    }

    public BlockState withMirror(BlockState state, Mirror mirrorIn) {
        switch (mirrorIn) {
            case LEFT_RIGHT:
                return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
            case FRONT_BACK:
                return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
            default:
                return state;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        int charge = (int)stateIn.getValue(CHARGE);
        if (charge > 0 && rand.nextInt(20 - charge) == 0) {
            Direction face = HORIZONTALS[rand.nextInt(HORIZONTALS.length)];
            if (getAttachPosition(worldIn, pos, face) != EnumAttachPosition.NONE) {
                double d0 = pos.getX() + 0.5 + rand.nextGaussian() * 0.08;
                double d2 = pos.getY() + 0.025f;
                double d3 = pos.getZ() + 0.5 + rand.nextGaussian() * 0.08;
                double f0 = face.getStepX() / 70.0 * (1.0 - rand.nextFloat() * 0.1);
                double f2 = face.getStepZ() / 70.0 * (1.0 - rand.nextFloat() * 0.1);
                float r = Mth.nextInt(rand, 150, 200) / 255.0f;
                float g = Mth.nextInt(rand, 0, 200) / 255.0f;
                FXDispatcher.INSTANCE.drawLineSparkle(rand, d0, d2, d3, f0, 0.0, f2, 0.33f, r, g, g / 2.0f, 0, 1.0f, 0.0f, 16);
            }
        }
    }

    public int getLightValue(BlockState state) {
        return 1;
    }

    @OnlyIn(Dist.CLIENT)
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

    @Override
    protected boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int par5, int par6) {
        return super.triggerEvent(state, worldIn, pos, par5, par6);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!worldIn.isClientSide()) {
            updateSurroundingInlay(worldIn, pos, state);
            for (Direction enumfacing1 : HORIZONTALS) {
                notifyInlayNeighborsOfStateChange(worldIn, pos.relative(enumfacing1));
            }
        }
    }

    public static void notifyInlayNeighborsOfStateChange(Level worldIn, BlockPos pos) {
        BlockState bs = worldIn.getBlockState(pos);
        if (bs.getBlock() == BlocksTC.inlay || bs.getBlock() instanceof BlockPedestal) {
            worldIn.updateNeighborsAt(pos, bs.getBlock());
            for (Direction enumfacing : HORIZONTALS) {
                notifyInlayNeighborsOfStateChange(worldIn, pos.relative(enumfacing));
            }
        }
    }

    public static BlockState updateSurroundingInlay(Level worldIn, BlockPos pos, BlockState state) {
        Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();
        state = calculateChanges(worldIn, pos, pos, state, blocksNeedingUpdate);
        List<BlockPos> list = Lists.newArrayList((Iterable)blocksNeedingUpdate);
        for (BlockPos blockpos : list) {
            worldIn.updateNeighborsAt(blockpos, worldIn.getBlockState(pos).getBlock());
        }
        return state;
    }

    public static int getMaxStrength(Level worldIn, BlockPos pos, int strength) {
        BlockState bs = worldIn.getBlockState(pos);
        if (bs.getBlock() != BlocksTC.inlay && !(bs.getBlock() instanceof BlockPedestal)) {
            return strength;
        }
        int i = (int)bs.getValue(CHARGE);
        return (i > strength) ? i : strength;
    }

    public static int getSourceStrength(BlockGetter world, BlockPos pos) {
        for (Direction enumfacing : HORIZONTALS) {
            BlockPos neighbor = pos.relative(enumfacing);
            if (isSourceBlock(world, neighbor)) {
                BlockEntity te = world.getBlockEntity(neighbor);
                if (te instanceof TileStabilizer) {
                    int e = ((TileStabilizer)te).getEnergy();
                    if (e > 0) {
                        return e;
                    }
                }
            }
        }
        return 0;
    }

    public static int getSourceStrengthAt(BlockGetter world, BlockPos pos) {
        if (isSourceBlock(world, pos)) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te != null && te instanceof TileStabilizer) {
                return ((TileStabilizer)te).getEnergy();
            }
        }
        return 0;
    }

    public static boolean isSourceBlock(BlockGetter world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == BlocksTC.stabilizer;
    }

    public static BlockState calculateChanges(Level worldIn, BlockPos pos1, BlockPos pos2, BlockState state, Set<BlockPos> blocksNeedingUpdate) {
        BlockState iblockstate = state;
        int current = (int)state.getValue(CHARGE);
        int max = 0;
        max = getMaxStrength(worldIn, pos2, max);
        int source = getSourceStrength(worldIn, pos1);
        if (source > 0 && source > max - 1) {
            max = source;
        }
        int neighbour = 0;
        for (Direction enumfacing : HORIZONTALS) {
            BlockPos blockpos = pos1.relative(enumfacing);
            boolean flag = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ();
            if (flag) {
                neighbour = getMaxStrength(worldIn, blockpos, neighbour);
            }
        }
        if (neighbour > max) {
            max = neighbour - 1;
        } else if (max > 0) {
            --max;
        } else {
            max = 0;
        }
        if (source > max - 1) {
            max = source;
        }
        if (current != max) {
            state = state.setValue(CHARGE, max);
            if (worldIn.getBlockState(pos1) == iblockstate) {
                worldIn.setBlock(pos1, state, 2);
            }
            blocksNeedingUpdate.add(pos1);
            for (Direction enumfacing2 : Direction.values()) {
                blocksNeedingUpdate.add(pos1.relative(enumfacing2));
            }
        }
        return state;
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (worldIn instanceof Level level && !level.isClientSide()) {
            for (Direction enumfacing : HORIZONTALS) {
                level.updateNeighborsAt(pos.relative(enumfacing), this);
            }
            updateSurroundingInlay(level, pos, state);
            for (Direction enumfacing2 : HORIZONTALS) {
                notifyInlayNeighborsOfStateChange(level, pos.relative(enumfacing2));
            }
        }
        super.destroy(worldIn, pos, state);
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, Orientation orientation, boolean isMoving) {
        if (!worldIn.isClientSide()) {
            if (worldIn.getBlockState(pos.below()).isSolid()) {
                updateSurroundingInlay(worldIn, pos, state);
            } else {
                Block.popResource(worldIn, pos, new ItemStack(this));
                worldIn.removeBlock(pos, false);
            }
        }
    }

    public boolean canStabaliseInfusion(Level world, BlockPos pos) {
        return true;
    }

    @Override
    public float getStabilizationAmount(Level world, BlockPos pos) {
        return 0.025f;
    }

    static {
        NORTH = EnumProperty.create("north", EnumAttachPosition.class);
        EAST = EnumProperty.create("east", EnumAttachPosition.class);
        SOUTH = EnumProperty.create("south", EnumAttachPosition.class);
        WEST = EnumProperty.create("west", EnumAttachPosition.class);
        CHARGE = IntegerProperty.create("charge", 0, 15);
        REDSTONE_WIRE_AABB = new AABB[] {
            new AABB(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 0.8125),
            new AABB(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 1.0),
            new AABB(0.0, 0.0, 0.1875, 0.8125, 0.0625, 0.8125),
            new AABB(0.0, 0.0, 0.1875, 0.8125, 0.0625, 1.0),
            new AABB(0.1875, 0.0, 0.0, 0.8125, 0.0625, 0.8125),
            new AABB(0.1875, 0.0, 0.0, 0.8125, 0.0625, 1.0),
            new AABB(0.0, 0.0, 0.0, 0.8125, 0.0625, 0.8125),
            new AABB(0.0, 0.0, 0.0, 0.8125, 0.0625, 1.0),
            new AABB(0.1875, 0.0, 0.1875, 1.0, 0.0625, 0.8125),
            new AABB(0.1875, 0.0, 0.1875, 1.0, 0.0625, 1.0),
            new AABB(0.0, 0.0, 0.1875, 1.0, 0.0625, 0.8125),
            new AABB(0.0, 0.0, 0.1875, 1.0, 0.0625, 1.0),
            new AABB(0.1875, 0.0, 0.0, 1.0, 0.0625, 0.8125),
            new AABB(0.1875, 0.0, 0.0, 1.0, 0.0625, 1.0),
            new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 0.8125),
            new AABB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0)
        };
    }

    enum EnumAttachPosition implements StringRepresentable
    {
        SIDE("side"),
        NONE("none"),
        EXT("ext");

        private String name;

        private EnumAttachPosition(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
