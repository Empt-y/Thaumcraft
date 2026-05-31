package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;


public class BlockMirror extends BlockTCDevice implements IBlockFacing
{
    public BlockMirror(Class cls, String name) {
        super(null /*  null   Material removed    */, cls, name);
        setSoundType(SoundsTC.JAR);
        setHardness(0.1f);
        registerDefaultState(defaultBlockState().setValue(IBlockFacing.FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder); // super adds IBlockFacing.FACING
    }

    public SoundType getSoundType() {
        return SoundsTC.JAR;
    }

    @Override
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

    public int damageDropped(BlockState state) {
        return 0;
    }

    @Override
    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        return defaultBlockState().setValue(IBlockFacing.FACING, facing);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, Orientation orientation, boolean isMoving) {
        Direction d = BlockStateUtils.getFacing(state);
        BlockPos support = pos.relative(d.getOpposite());
        if (!worldIn.getBlockState(support).isFaceSturdy(worldIn, support, d)) {
            // Drop the mirror item with its link data, then remove
            BlockEntity te = worldIn.getBlockEntity(pos);
            dropMirror(worldIn, pos, state, te);
            worldIn.removeBlock(pos, false);
        }
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        Direction facing = BlockStateUtils.getFacing(state);
        switch (facing.ordinal()) {
            default: return new AABB(0.0, 0.875, 0.0, 1.0, 1.0, 1.0);
            case 1:  return new AABB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
            case 2:  return new AABB(0.0, 0.0, 0.875, 1.0, 1.0, 1.0);
            case 3:  return new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 0.125);
            case 4:  return new AABB(0.875, 0.0, 0.0, 1.0, 1.0, 1.0);
            case 5:  return new AABB(0.0, 0.0, 0.0, 0.125, 1.0, 1.0);
        }
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return !world.isClientSide() || true;
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (worldIn instanceof Level level) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileMirror || te instanceof TileMirrorEssentia) {
                dropMirror(level, pos, state, te);
            } else {
                super.destroy(worldIn, pos, state);
                return;
            }
        }
        // skip super to avoid duplicate drops
    }

    public void dropMirror(Level world, BlockPos pos, BlockState state, BlockEntity te) {
        if (tileClass == TileMirror.class) {
            TileMirror tm = (TileMirror)te;
            ItemStack drop = new ItemStack(this, 1);
            if (tm != null && tm.linked) {
                drop.setDamageValue(1);
                CompoundTag nbt = new CompoundTag();
                nbt.putInt("linkX", tm.linkX);
                nbt.putInt("linkY", tm.linkY);
                nbt.putInt("linkZ", tm.linkZ);
                nbt.putInt("linkDim", tm.linkDim);
                drop.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                tm.invalidateLink();
            }
            Block.popResource(world, pos, drop);
        } else {
            TileMirrorEssentia tm2 = (TileMirrorEssentia)te;
            ItemStack drop = new ItemStack(this, 1);
            if (tm2 != null && tm2.linked) {
                drop.setDamageValue(1);
                CompoundTag nbt = new CompoundTag();
                nbt.putInt("linkX", tm2.linkX);
                nbt.putInt("linkY", tm2.linkY);
                nbt.putInt("linkZ", tm2.linkZ);
                nbt.putInt("linkDim", tm2.linkDim);
                drop.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                tm2.invalidateLink();
            }
            Block.popResource(world, pos, drop);
        }
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean fromMovement) {
        if (!world.isClientSide() && tileClass == TileMirror.class && entity instanceof ItemEntity && entity.isAlive() && entity.getPortalCooldown() == 0) {
            TileMirror taf = (TileMirror)world.getBlockEntity(pos);
            if (taf != null) {
                taf.transport((ItemEntity)entity);
            }
        }
    }
}
