package thaumcraft.common.blocks.devices;
import java.util.ArrayList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.Orientation;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.devices.TileCondenser;


public class BlockCondenserLattice extends BlockTC
{
    public static BooleanProperty NORTH;
    public static BooleanProperty EAST;
    public static BooleanProperty SOUTH;
    public static BooleanProperty WEST;
    public static BooleanProperty UP;
    public static BooleanProperty DOWN;
    private ArrayList<Long> history;

    public BlockCondenserLattice(boolean dirty) {
        super(null /*  null   Material removed    */, dirty ? "condenser_lattice_dirty" : "condenser_lattice");
        history = new ArrayList<Long>();
        setHardness(0.5f);
        setResistance(5.0f);
        setSoundType(SoundType.METAL);
        setLightLevel(dirty ? 0.0f : 0.33f);
        registerDefaultState(defaultBlockState()
            .setValue(NORTH, false)
            .setValue(EAST, false)
            .setValue(SOUTH, false)
            .setValue(WEST, false)
            .setValue(UP, false)
            .setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        triggerUpdate(worldIn, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, Orientation orientation, boolean isMoving) {
        if (blockIn == BlocksTC.condenserlattice || blockIn == BlocksTC.condenserlatticeDirty || blockIn == BlocksTC.condenser) {
            triggerUpdate(worldIn, pos);
        }
    }

    public boolean onBlockActivated(Level worldIn, BlockPos pos, BlockState state, Player playerIn, InteractionHand hand, Direction facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isClientSide()) {
            return true;
        }
        if (state.getBlock() == BlocksTC.condenserlatticeDirty && playerIn.getItemInHand(hand).getItem() == ItemsTC.filter) {
            playerIn.getItemInHand(hand).shrink(1);
            if (worldIn.getRandom().nextBoolean()) {
                worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5f + facing.getStepX() / 3.0f, pos.getY() + 0.5f, pos.getZ() + 0.5f + facing.getStepZ() / 3.0f, ConfigItems.FLUX_CRYSTAL.copy()));
            }
            worldIn.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2f, ((worldIn.getRandom().nextFloat() - worldIn.getRandom().nextFloat()) * 0.7f + 1.0f) * 1.6f);
            worldIn.setBlock(pos, BlocksTC.condenserlattice.defaultBlockState(), 3);
            BlockState state2 = worldIn.getBlockState(pos);
            if (state2.getBlock() instanceof BlockCondenserLattice) {
                ((BlockCondenserLattice)state2.getBlock()).triggerUpdate(worldIn, pos);
            }
        }
        return true;
    }

    public void triggerUpdate(Level world, BlockPos pos) {
        history.clear();
        BlockPos p = processUpdate(world, pos);
        if (p == null || p.distSqr(pos) > 74.0) {
            Block.popResource(world, pos, new ItemStack(this));
            world.removeBlock(pos, false);
        }
        history.clear();
    }

    private BlockPos processUpdate(Level world, BlockPos pos) {
        history.add(pos.asLong());
        for (Direction face : Direction.values()) {
            BlockPos p2 = pos.relative(face);
            if (!history.contains(p2.asLong())) {
                Block b = world.getBlockState(p2).getBlock();
                if (b instanceof BlockCondenserLattice) {
                    BlockPos pp = processUpdate(world, p2);
                    if (pp != null) {
                        return pp;
                    }
                }
                if (face == Direction.DOWN && b == BlocksTC.condenser) {
                    BlockEntity te = world.getBlockEntity(p2);
                    if (te != null && te instanceof TileCondenser) {
                        ((TileCondenser)te).latticeCount = -1.0f;
                    }
                    return p2;
                }
            }
        }
        return null;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        float minx = 0.3125f;
        float maxx = 0.6875f;
        float miny = 0.3125f;
        float maxy = 0.6875f;
        float minz = 0.3125f;
        float maxz = 0.6875f;
        Direction fd = null;
        for (int side = 0; side < 6; ++side) {
            fd = Direction.from3DDataValue(side);
            Block b = source.getBlockState(pos.relative(fd)).getBlock();
            if (b instanceof BlockCondenserLattice || (fd == Direction.DOWN && b == BlocksTC.condenser)) {
                switch (side) {
                    case 0: { miny = 0.0f; break; }
                    case 1: { maxy = 1.0f; break; }
                    case 2: { minz = 0.0f; break; }
                    case 3: { maxz = 1.0f; break; }
                    case 4: { minx = 0.0f; break; }
                    case 5: { maxx = 1.0f; break; }
                }
            }
        }
        return new AABB(minx, miny, minz, maxx, maxy, maxz);
    }

    static {
        NORTH = BooleanProperty.create("north");
        EAST = BooleanProperty.create("east");
        SOUTH = BooleanProperty.create("south");
        WEST = BooleanProperty.create("west");
        UP = BooleanProperty.create("up");
        DOWN = BooleanProperty.create("down");
    }
}
