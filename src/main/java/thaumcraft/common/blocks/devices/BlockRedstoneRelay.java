package thaumcraft.common.blocks.devices;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.redstone.Orientation;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.codechicken.lib.raytracer.ExtendedMOP;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileRedstoneRelay;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft", value = net.neoforged.api.distmarker.Dist.CLIENT)
public class BlockRedstoneRelay extends BlockTCDevice implements IBlockFacingHorizontal, IBlockEnabled
{
    private RayTracer rayTracer;

    public BlockRedstoneRelay() {
        super(null /*  null   Material removed    */, TileRedstoneRelay.class, "redstone_relay");
        rayTracer = new RayTracer();
        setHardness(0.0f);
        setResistance(0.0f);
        setSoundType(SoundType.WOOD);
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null; // Object /* BlockFaceShape removed */ removed
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public int damageDropped(BlockState state) {
        return 0;
    }

    public boolean canPlaceBlockAt(Level worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.below()).isFaceSturdy(worldIn, pos.below(), Direction.UP);
    }

    public boolean canBlockStay(Level worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.below()).isFaceSturdy(worldIn, pos.below(), Direction.UP);
    }

    public void randomTick(Level worldIn, BlockPos pos, BlockState state, Random random) {
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (!player.getAbilities().mayBuild) {
            return false;
        }
        HitResult hit = RayTracer.retraceBlock(world, player, pos);
        if (hit == null) {
            return false;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile != null && tile instanceof TileRedstoneRelay) {
            int subHit = (hit instanceof ExtendedMOP) ? ((ExtendedMOP)hit).subHit : -1;
            if (subHit == 0) {
                ((TileRedstoneRelay)tile).increaseOut();
                world.playSound(null, pos, SoundsTC.key, SoundSource.BLOCKS, 0.5f, 1.0f);
                updateState(world, pos, state);
                notifyNeighbors(world, pos, state);
            }
            if (subHit == 1) {
                ((TileRedstoneRelay)tile).increaseIn();
                world.playSound(null, pos, SoundsTC.key, SoundSource.BLOCKS, 0.5f, 1.0f);
                updateState(world, pos, state);
                notifyNeighbors(world, pos, state);
            }
            return true;
        }
        return false;
    }

    public void updateTick(Level worldIn, BlockPos pos, BlockState state, Random rand) {
        boolean flag = shouldBePowered(worldIn, pos, state);
        if (isPowered(state) && !flag) {
            worldIn.setBlock(pos, getUnpoweredState(state), 2);
            notifyNeighbors(worldIn, pos, state);
        }
        else if (!isPowered(state)) {
            worldIn.setBlock(pos, getPoweredState(state), 2);
            notifyNeighbors(worldIn, pos, state);
            if (!flag) {
                worldIn.scheduleTick(pos, getPoweredState(state).getBlock(), getTickDelay(state));
            }
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel worldIn, BlockPos pos, boolean isMoving) {
        super.affectNeighborsAfterRemoval(state, worldIn, pos, isMoving);
        notifyNeighbors(worldIn, pos, state);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldSideBeRendered(BlockState state, BlockGetter worldIn, BlockPos pos, Direction side) {
        return side.getAxis() != Direction.Axis.Y;
    }

    protected boolean isPowered(BlockState state) {
        return BlockStateUtils.isEnabled(state);
    }

    public int getStrongPower(BlockState state, BlockGetter worldIn, BlockPos pos, Direction side) {
        return getWeakPower(state, worldIn, pos, side);
    }

    public int getWeakPower(BlockState state, BlockGetter worldIn, BlockPos pos, Direction side) {
        return isPowered(state) ? ((state.getValue(BlockRedstoneRelay.FACING) == side) ? getActiveSignal(worldIn, pos, state) : 0) : 0;
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, Orientation orientation, boolean isMoving) {
        if (canBlockStay(worldIn, pos)) {
            updateState(worldIn, pos, state);
        }
        else {
            Block.popResource(worldIn, pos, new ItemStack(this));
            worldIn.removeBlock(pos, false);
            for (Direction enumfacing : Direction.values()) {
                worldIn.updateNeighborsAt(pos.relative(enumfacing), this);
            }
        }
    }

    @Override
    protected void updateState(Level worldIn, BlockPos pos, BlockState state) {
        boolean flag = shouldBePowered(worldIn, pos, state);
        if ((isPowered(state) && !flag) || (!isPowered(state) && flag)) {
            worldIn.scheduleTick(pos, this, getTickDelay(state));
        }
    }

    protected boolean shouldBePowered(Level worldIn, BlockPos pos, BlockState state) {
        int pr = 1;
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile != null && tile instanceof TileRedstoneRelay) {
            pr = ((TileRedstoneRelay)tile).getIn();
        }
        return calculateInputStrength(worldIn, pos, state) >= pr;
    }

    protected int calculateInputStrength(Level worldIn, BlockPos pos, BlockState state) {
        Direction enumfacing = state.getValue(BlockRedstoneRelay.FACING);
        BlockPos blockpos1 = pos.relative(enumfacing);
        int i = worldIn.getSignal(blockpos1, enumfacing);
        if (i >= 15) {
            return i;
        }
        BlockState iblockstate1 = worldIn.getBlockState(blockpos1);
        return Math.max(i, (iblockstate1.getBlock() == Blocks.REDSTONE_WIRE) ? ((int)iblockstate1.getValue(RedStoneWireBlock.POWER)) : 0);
    }

    protected int getPowerOnSides(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        Direction enumfacing = state.getValue(BlockRedstoneRelay.FACING);
        Direction enumfacing2 = enumfacing.getClockWise();
        Direction enumfacing3 = enumfacing.getCounterClockWise();
        return Math.max(getPowerOnSide(worldIn, pos.relative(enumfacing2), enumfacing2), getPowerOnSide(worldIn, pos.relative(enumfacing3), enumfacing3));
    }

    protected int getPowerOnSide(LevelAccessor worldIn, BlockPos pos, Direction side) {
        BlockState iblockstate = worldIn.getBlockState(pos);
        return canPowerSide(iblockstate) ? ((iblockstate.getBlock() == Blocks.REDSTONE_WIRE) ? iblockstate.getValue(RedStoneWireBlock.POWER) : worldIn.getDirectSignal(pos, side)) : 0;
    }

    public boolean canProvidePower(BlockState state) {
        return true;
    }

    public void onBlockPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (shouldBePowered(worldIn, pos, state)) {
            worldIn.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        BlockState bs = defaultBlockState();
        bs = bs.setValue((Property)BlockRedstoneRelay.FACING, (placer.isShiftKeyDown() ? placer.getDirection() : placer.getDirection().getOpposite()));
        bs = bs.setValue((Property)BlockRedstoneRelay.ENABLED, false);
        return bs;
    }

    @Override
    protected void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        notifyNeighbors(worldIn, pos, state);
    }

    protected void notifyNeighbors(Level worldIn, BlockPos pos, BlockState state) {
        Direction enumfacing = state.getValue(BlockRedstoneRelay.FACING);
        BlockPos blockpos1 = pos.relative(enumfacing.getOpposite());
        ((net.minecraft.world.level.LevelAccessor)worldIn).updateNeighborsAt(blockpos1, this);
        ((net.minecraft.world.level.LevelAccessor)worldIn).updateNeighborsAt(blockpos1, this);
    }

    protected boolean canPowerSide(BlockState iblockstate) {
        return iblockstate.isSignalSource();
    }

    protected int getActiveSignal(BlockGetter worldIn, BlockPos pos, BlockState state) {
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile != null && tile instanceof TileRedstoneRelay) {
            return ((TileRedstoneRelay)tile).getOut();
        }
        return 0;
    }

    public static boolean isRedstoneRepeaterBlockID(Block blockIn) {
        return blockIn instanceof RepeaterBlock || blockIn instanceof ComparatorBlock;
    }

    public boolean isAssociated(Block other) {
        return other instanceof BlockRedstoneRelay;
    }

    public boolean isFacingTowardsRepeater(Level worldIn, BlockPos pos, BlockState state) {
        Direction enumfacing = (state.getValue(BlockRedstoneRelay.FACING)).getOpposite();
        BlockPos blockpos1 = pos.relative(enumfacing);
        return isRedstoneRepeaterBlockID(worldIn.getBlockState(blockpos1).getBlock()) && worldIn.getBlockState(blockpos1).getValue(BlockRedstoneRelay.FACING) != enumfacing;
    }

    protected int getTickDelay(BlockState state) {
        return 2;
    }

    protected BlockState getPoweredState(BlockState unpoweredState) {
        Direction enumfacing = unpoweredState.getValue(BlockRedstoneRelay.FACING);
        return defaultBlockState().setValue((Property)BlockRedstoneRelay.FACING, enumfacing).setValue((Property)BlockRedstoneRelay.ENABLED, true);
    }

    protected BlockState getUnpoweredState(BlockState poweredState) {
        Direction enumfacing = poweredState.getValue(BlockRedstoneRelay.FACING);
        return defaultBlockState().setValue((Property)BlockRedstoneRelay.FACING, enumfacing).setValue((Property)BlockRedstoneRelay.ENABLED, false);
    }

    public boolean isAssociatedBlock(Block other) {
        return isAssociated(other);
    }

    @OnlyIn(Dist.CLIENT)
    public Object /* BlockRenderLayer removed */ getBlockLayer_removed() {
        return null; // Object /* BlockRenderLayer removed */ removed
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(Object event) {
        // highlight event handling stubbed out (requires modern render event migration)
    }
}
