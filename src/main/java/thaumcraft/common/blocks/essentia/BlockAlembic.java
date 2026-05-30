package thaumcraft.common.blocks.essentia;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.BlockTCTile;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.essentia.TileAlembic;


public class BlockAlembic extends BlockTCTile implements ILabelable
{
    public BlockAlembic() {
        super(null /*  null   Material removed    */, TileAlembic.class, "alembic");
        setSoundType(SoundType.WOOD);
    }

    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null; // Object /* BlockFaceShape removed */ removed
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        return defaultBlockState();
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null && te instanceof TileAlembic && player.isShiftKeyDown() && ((TileAlembic)te).aspectFilter != null && side.ordinal() == ((TileAlembic)te).facing) {
            ((TileAlembic)te).aspectFilter = null;
            ((TileAlembic)te).facing = Direction.DOWN.ordinal();
            te.setChanged();
            world.sendBlockUpdated(pos, state, state, 3);
            if (world.isClientSide()) {
                world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.page, SoundSource.BLOCKS, 1.0f, 1.0f, false);
            }
            else {
                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5f + side.getStepX() / 3.0f, pos.getY() + 0.5f, pos.getZ() + 0.5f + side.getStepZ() / 3.0f, new ItemStack(ItemsTC.label)));
            }
            return true;
        }
        if (te != null && te instanceof TileAlembic && player.isShiftKeyDown() && player.getMainHandItem().isEmpty() && (((TileAlembic)te).aspectFilter == null || side.ordinal() != ((TileAlembic)te).facing)) {
            ((TileAlembic)te).aspect = null;
            if (world.isClientSide()) {
                world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.jar, SoundSource.BLOCKS, 0.4f, 1.0f, false);
                world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.5f, 1.0f + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.3f, false);
            }
            else {
                AuraHelper.polluteAura(world, pos, (float)((TileAlembic)te).amount, true);
            }
            ((TileAlembic)te).amount = 0;
            te.setChanged();
            world.sendBlockUpdated(pos, state, state, 3);
        }
        return true;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.125, 0.0, 0.125, 0.875, 1.0, 0.875);
    }

    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public int getComparatorInputOverride(BlockState state, Level world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile != null && tile instanceof TileAlembic) {
            float r = ((TileAlembic)tile).amount / (float)((TileAlembic)tile).maxAmount;
            return Mth.floor(r * 14.0f) + ((((TileAlembic)tile).amount > 0) ? 1 : 0);
        }
        return 0;
    }

    @Override
    public boolean applyLabel(Player player, BlockPos pos, Direction side, ItemStack labelstack) {
        BlockEntity te = player.level().getBlockEntity(pos);
        if (te == null || !(te instanceof TileAlembic) || side.ordinal() <= 1 || ((TileAlembic)te).aspectFilter != null) {
            return false;
        }
        Aspect la = null;
        if (((IEssentiaContainerItem)labelstack.getItem()).getAspects(labelstack) != null) {
            la = ((IEssentiaContainerItem)labelstack.getItem()).getAspects(labelstack).getAspects()[0];
        }
        if (((TileAlembic)te).amount == 0 && la == null) {
            return false;
        }
        Aspect aspect = null;
        if (((TileAlembic)te).amount == 0 && la != null) {
            aspect = la;
        }
        if (((TileAlembic)te).amount > 0) {
            aspect = ((TileAlembic)te).aspect;
        }
        if (aspect == null) {
            return false;
        }
        BlockState state = player.level().getBlockState(pos);
        ((TileAlembic)te).aspectFilter = aspect;
        ((TileAlembic)te).facing = side.ordinal();
        te.setChanged();
        player.level().sendBlockUpdated(pos, state, state, 3);
        player.level().playSound(null, pos, SoundsTC.page, SoundSource.BLOCKS, 1.0f, 1.0f);
        return true;
    }
}
