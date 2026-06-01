package thaumcraft.common.blocks.basic;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.tiles.misc.TileBanner;


public class BlockBannerTC extends BlockTC implements EntityBlock
{
    public DyeColor dye;

    public BlockBannerTC(String name, DyeColor dye) {
        super(null /*  null   Material removed    */, name, true);

        setHardness(1.0f);
        setSoundType(SoundType.WOOD);
        this.dye = dye;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public MapColor getMapColor(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return (dye == null) ? MapColor.COLOR_RED : BlockCandle.dyeToMapColor(dye);
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        BlockEntity tile = source.getBlockEntity(pos);
        if (tile instanceof TileBanner) {
            if (!((TileBanner)tile).getWall()) {
                return new AABB(0.33000001311302185, 0.0, 0.33000001311302185, 0.6600000262260437, 2.0, 0.6600000262260437);
            }
            switch (((TileBanner)tile).getBannerFacing()) {
                case 0: return new AABB(0.0, -1.0, 0.0, 1.0, 1.0, 0.25);
                case 8: return new AABB(0.0, -1.0, 0.75, 1.0, 1.0, 1.0);
                case 12: return new AABB(0.0, -1.0, 0.0, 0.25, 1.0, 1.0);
                case 4: return new AABB(0.75, -1.0, 0.0, 1.0, 1.0, 1.0);
            }
        }
        return new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    public AABB getCollisionBoundingBox(BlockState blockState, BlockGetter worldIn, BlockPos pos) {
        return null;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileBanner(null, pos, state);
    }

    public boolean onBlockActivated(Level w, BlockPos pos, BlockState state, Player p, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (!w.isClientSide() && (p.isCrouching() || p.getItemInHand(hand).getItem() instanceof ItemPhial)) {
            TileBanner te = (TileBanner)w.getBlockEntity(pos);
            if (te != null && dye != null) {
                if (p.isCrouching()) {
                    te.setAspect(null);
                } else if (((IEssentiaContainerItem)p.getItemInHand(hand).getItem()).getAspects(p.getItemInHand(hand)) != null) {
                    te.setAspect(((IEssentiaContainerItem)p.getItemInHand(hand).getItem()).getAspects(p.getItemInHand(hand)).getAspects()[0]);
                    p.getItemInHand(hand).shrink(1);
                }
                w.sendBlockUpdated(pos, state, state, 3);
                te.setChanged();
                te.syncTile(false);
                w.playSound(null, pos, SoundEvents.WOOL_HIT, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
        return true;
    }

    public boolean hasBlockEntity(BlockState state) {
        return true;
    }

    public ItemStack getPickBlock(BlockState state, HitResult target, Level world, BlockPos pos, Player player) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof TileBanner) {
            ItemStack drop = new ItemStack(this);
            if (dye != null || ((TileBanner)te).getAspect() != null) {
                Aspect aspect = ((TileBanner)te).getAspect();
                if (aspect != null) {
                    CompoundTag nbt = new CompoundTag();
                    nbt.putString("aspect", aspect.getTag());
                    drop.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                }
            }
            return drop;
        }
        return new ItemStack(this);
    }

    public void dropBlockAsItemWithChance(Level worldIn, BlockPos pos, BlockState state, float chance, int fortune) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof TileBanner) {
            ItemStack drop = new ItemStack(this);
            if (dye != null || ((TileBanner)te).getAspect() != null) {
                Aspect aspect = ((TileBanner)te).getAspect();
                if (aspect != null) {
                    CompoundTag nbt = new CompoundTag();
                    nbt.putString("aspect", aspect.getTag());
                    drop.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                }
            }
            Block.popResource(worldIn, pos, drop);
        } else {
            Block.popResource(worldIn, pos, new ItemStack(this));
        }
    }

    public void harvestBlock(Level worldIn, Player player, BlockPos pos, BlockState state, BlockEntity te, ItemStack stack) {
        if (te instanceof TileBanner) {
            ItemStack drop = new ItemStack(this);
            if (dye != null || ((TileBanner)te).getAspect() != null) {
                Aspect aspect = ((TileBanner)te).getAspect();
                if (aspect != null) {
                    CompoundTag nbt = new CompoundTag();
                    nbt.putString("aspect", aspect.getTag());
                    drop.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                }
            }
            Block.popResource(worldIn, pos, drop);
        } else {
            Block.popResource(worldIn, pos, new ItemStack(this));
        }
    }
}
