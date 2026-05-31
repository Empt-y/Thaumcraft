package thaumcraft.common.blocks.devices;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.Orientation;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileArcaneEar;


public class BlockArcaneEar extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    private static List<SoundEvent> INSTRUMENTS;

    public BlockArcaneEar(String name) {
        super(null /*  null   Material removed    */, TileArcaneEar.class, name);
        setSoundType(SoundType.WOOD);
        setHardness(1.0f);
        registerDefaultState(defaultBlockState()
            .setValue(IBlockFacing.FACING, Direction.UP)
            .setValue(IBlockEnabled.ENABLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IBlockFacing.FACING, IBlockEnabled.ENABLED);
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
        return defaultBlockState()
            .setValue(IBlockFacing.FACING, facing)
            .setValue(IBlockEnabled.ENABLED, false);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        TileArcaneEar tile = (TileArcaneEar)worldIn.getBlockEntity(pos);
        if (tile != null) {
            tile.updateTone();
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, Orientation orientation, boolean isMoving) {
        TileArcaneEar tile = (TileArcaneEar)worldIn.getBlockEntity(pos);
        if (tile != null) {
            tile.updateTone();
        }
        Direction facing = BlockStateUtils.getFacing(state);
        BlockPos support = pos.relative(facing.getOpposite());
        if (!worldIn.getBlockState(support).isFaceSturdy(worldIn, support, facing)) {
            Block.popResource(worldIn, pos, new ItemStack(this));
            worldIn.removeBlock(pos, false);
        }
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (world.isClientSide()) {
            return true;
        }
        TileArcaneEar tile = (TileArcaneEar)world.getBlockEntity(pos);
        if (tile != null) {
            tile.changePitch();
            tile.triggerNote(world, pos, true);
        }
        return true;
    }

    public boolean canProvidePower(BlockState state) {
        return true;
    }

    public int getWeakPower(BlockState state, BlockGetter worldIn, BlockPos pos, Direction side) {
        return state.getValue(IBlockEnabled.ENABLED) ? 15 : 0;
    }

    public int getStrongPower(BlockState state, BlockGetter worldIn, BlockPos pos, Direction side) {
        return state.getValue(IBlockEnabled.ENABLED) ? 15 : 0;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        Direction facing = (Direction)state.getValue(IBlockFacing.FACING);
        switch (facing.ordinal()) {
            case 0: {
                return new AABB(0.125, 0.625, 0.125, 0.875, 1.0, 0.875);
            }
            case 1: {
                return new AABB(0.125, 0.0, 0.125, 0.875, 0.375, 0.875);
            }
            case 2: {
                return new AABB(0.125, 0.125, 0.625, 0.875, 0.875, 1.0);
            }
            case 3: {
                return new AABB(0.125, 0.125, 0.0, 0.875, 0.875, 0.375);
            }
            case 4: {
                return new AABB(0.625, 0.125, 0.125, 1.0, 0.875, 0.875);
            }
            default: {
                return new AABB(0.0, 0.125, 0.125, 0.375, 0.875, 0.875);
            }
        }
    }

    protected SoundEvent getInstrument(int type) {
        if (type < 0 || type >= BlockArcaneEar.INSTRUMENTS.size()) {
            type = 0;
        }
        return BlockArcaneEar.INSTRUMENTS.get(type);
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        super.triggerEvent(state, worldIn, pos, id, param);
        float pitch = (float)Math.pow(2.0, (param - 12) / 12.0);
        worldIn.playSound(null, pos, getInstrument(id), SoundSource.BLOCKS, 3.0f, pitch);
        worldIn.addParticle(ParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, param / 24.0, 0.0, 0.0);
        return true;
    }

    static {
        INSTRUMENTS = Lists.newArrayList(
            SoundEvents.NOTE_BLOCK_HARP.value(),
            SoundEvents.NOTE_BLOCK_BASEDRUM.value(),
            SoundEvents.NOTE_BLOCK_SNARE.value(),
            SoundEvents.NOTE_BLOCK_HAT.value(),
            SoundEvents.NOTE_BLOCK_BASS.value(),
            SoundEvents.NOTE_BLOCK_FLUTE.value(),
            SoundEvents.NOTE_BLOCK_BELL.value(),
            SoundEvents.NOTE_BLOCK_GUITAR.value(),
            SoundEvents.NOTE_BLOCK_CHIME.value(),
            SoundEvents.NOTE_BLOCK_XYLOPHONE.value()
        );
    }
}
