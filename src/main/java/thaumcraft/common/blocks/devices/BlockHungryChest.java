package thaumcraft.common.blocks.devices;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.common.blocks.BlockTCTile;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.devices.TileHungryChest;


public class BlockHungryChest extends BlockTCTile
{
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockHungryChest() {
        super(null /*  null   Material removed    */, TileHungryChest.class, "hungry_chest");
        setHardness(2.5f);
        setSoundType(SoundType.WOOD);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null;
    }

    public boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player) {
        return true;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
    }

    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        return defaultBlockState().setValue(FACING, placer.getDirection());
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (worldIn instanceof Level level) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity instanceof Container) {
                Containers.dropContents(level, pos, (Container)tileentity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.destroy(worldIn, pos, state);
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (world.isClientSide()) {
            return true;
        }
        // TODO: open chest GUI
        return true;
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean fromMovement) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te == null || world.isClientSide()) {
            return;
        }
        if (entity instanceof ItemEntity && entity.isAlive()) {
            ItemStack leftovers = ThaumcraftInvHelper.insertStackAt(world, pos, Direction.UP, ((ItemEntity)entity).getItem(), false);
            if (leftovers == null || leftovers.isEmpty() || leftovers.getCount() != ((ItemEntity)entity).getItem().getCount()) {
                entity.playSound(SoundEvents.GENERIC_EAT.value(), 0.25f, (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2f + 1.0f);
            }
            if (leftovers != null && !leftovers.isEmpty()) {
                ((ItemEntity)entity).setItem(leftovers);
            } else {
                entity.discard();
            }
            ((TileHungryChest)te).setChanged();
        }
    }

    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public int getComparatorInputOverride(BlockState state, Level worldIn, BlockPos pos) {
        return 0; // Container.calcRedstoneFromInventory removed
    }

    public boolean rotateBlock(Level world, BlockPos pos, Direction axis) {
        BlockState state = world.getBlockState(pos);
        for (Property<?> prop : state.getProperties()) {
            if (prop.getName().getString().equals("facing")) {
                world.setBlock(pos, state.cycle((Property)prop), 3);
                return true;
            }
        }
        return false;
    }

    public BlockState withRotation(BlockState state, net.minecraft.world.level.block.Rotation rot) {
        return state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
    }

    public BlockState withMirror(BlockState state, net.minecraft.world.level.block.Mirror mirrorIn) {
        return withRotation(state, mirrorIn.getRotation((Direction)state.getValue(FACING)));
    }
}
