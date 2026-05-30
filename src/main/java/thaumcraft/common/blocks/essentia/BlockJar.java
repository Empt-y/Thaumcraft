package thaumcraft.common.blocks.essentia;
import net.minecraft.world.entity.ExperienceOrb;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTCTile;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.devices.TileJarBrain;
import thaumcraft.common.tiles.essentia.TileJarFillable;


public class BlockJar extends BlockTCTile implements ILabelable
{
    public BlockJar(Class t, String name) {
        super(null /*  null   Material removed    */, t, name);
        setHardness(0.3f);
        setSoundType(SoundsTC.JAR);
    }

    public SoundType getSoundType() {
        return SoundsTC.JAR;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.1875, 0.0, 0.1875, 0.8125, 0.75, 0.8125);
    }

    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null; // Object /* BlockFaceShape removed */ removed
    }

    @OnlyIn(Dist.CLIENT)
    public Object /* BlockRenderLayer removed */ getBlockLayer_removed() {
        return null; // Object /* BlockRenderLayer removed */ removed
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

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        BlockJar.spillEssentia = false;
        super.destroy(worldIn, pos, state);
        BlockJar.spillEssentia = true;
    }

    private void spawnFilledJar(Level world, BlockPos pos, BlockState state, TileJarFillable te) {
        ItemStack drop = new ItemStack(this);
        if (te.amount > 0 && te.aspect != null) {
            ((BlockJarItem)drop.getItem()).setAspects(drop, new AspectList().add(te.aspect, te.amount));
        }
        if (te.blocked) {
            Block.popResource(world, pos, new ItemStack(ItemsTC.jarBrace));
        }
        Block.popResource(world, pos, drop);
    }

    private void spawnBrainJar(Level world, BlockPos pos, BlockState state, TileJarBrain te) {
        ItemStack drop = new ItemStack(this);
        Block.popResource(world, pos, drop);
    }

    public void onBlockPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity ent, ItemStack stack) {
        int l = Mth.floor(ent.getYRot() * 4.0f / 360.0f + 0.5) & 0x3;
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileJarFillable) {
            if (l == 0) {
                ((TileJarFillable)tile).facing = 2;
            }
            if (l == 1) {
                ((TileJarFillable)tile).facing = 5;
            }
            if (l == 2) {
                ((TileJarFillable)tile).facing = 3;
            }
            if (l == 3) {
                ((TileJarFillable)tile).facing = 4;
            }
        }
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null && te instanceof TileJarBrain) {
            ((TileJarBrain)te).eatDelay = 40;
            if (!world.isClientSide()) {
                int var6 = world.getRandom().nextInt(Math.min(((TileJarBrain)te).xp + 1, 64));
                if (var6 > 0) {
                    TileJarBrain tileJarBrain = (TileJarBrain)te;
                    tileJarBrain.xp -= var6;
                    int xp = var6;
                    while (xp > 0) {
                        int var7 = Math.min(xp, 100);
                        xp -= var7;
                        world.addFreshEntity(new ExperienceOrb(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, var7));
                    }
                    world.sendBlockUpdated(pos, state, state, 3);
                    te.setChanged();
                }
            }
            else {
                world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.jar, SoundSource.BLOCKS, 0.2f, 1.0f, false);
            }
        }
        if (te != null && te instanceof TileJarFillable && !((TileJarFillable)te).blocked && player.getItemInHand(hand).getItem() == ItemsTC.jarBrace) {
            ((TileJarFillable)te).blocked = true;
            player.getItemInHand(hand).shrink(1);
            if (world.isClientSide()) {
                world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.key, SoundSource.BLOCKS, 1.0f, 1.0f, false);
            }
            else {
                te.setChanged();
            }
        }
        else if (te != null && te instanceof TileJarFillable && player.isShiftKeyDown() && ((TileJarFillable)te).aspectFilter != null && side.ordinal() == ((TileJarFillable)te).facing) {
            ((TileJarFillable)te).aspectFilter = null;
            if (world.isClientSide()) {
                world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.page, SoundSource.BLOCKS, 1.0f, 1.0f, false);
            }
            else {
                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5f + side.getStepX() / 3.0f, pos.getY() + 0.5f, pos.getZ() + 0.5f + side.getStepZ() / 3.0f, new ItemStack(ItemsTC.label)));
            }
        }
        else if (te != null && te instanceof TileJarFillable && player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
            if (((TileJarFillable)te).aspectFilter == null) {
                ((TileJarFillable)te).aspect = null;
            }
            if (world.isClientSide()) {
                world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.jar, SoundSource.BLOCKS, 0.4f, 1.0f, false);
                world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.5f, 1.0f + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.3f);
            }
            else {
                AuraHelper.polluteAura(world, pos, (float)((TileJarFillable)te).amount, true);
            }
            ((TileJarFillable)te).amount = 0;
            te.setChanged();
        }
        return true;
    }

    @Override
    public boolean applyLabel(Player player, BlockPos pos, Direction side, ItemStack labelstack) {
        BlockEntity te = player.level().getBlockEntity(pos);
        if (te == null || !(te instanceof TileJarFillable) || ((TileJarFillable)te).aspectFilter != null) {
            return false;
        }
        if (((TileJarFillable)te).amount == 0 && ((IEssentiaContainerItem)labelstack.getItem()).getAspects(labelstack) == null) {
            return false;
        }
        if (((TileJarFillable)te).amount == 0 && ((IEssentiaContainerItem)labelstack.getItem()).getAspects(labelstack) != null) {
            ((TileJarFillable)te).aspect = ((IEssentiaContainerItem)labelstack.getItem()).getAspects(labelstack).getAspects()[0];
        }
        onBlockPlacedBy(player.level(), pos, player.level().getBlockState(pos), player, null);
        ((TileJarFillable)te).aspectFilter = ((TileJarFillable)te).aspect;
        player.level().sendBlockUpdated(pos, player.level().getBlockState(pos), player.level().getBlockState(pos), 3);
        te.setChanged();
        player.level().playSound(null, pos, SoundsTC.jar, SoundSource.BLOCKS, 0.4f, 1.0f);
        return true;
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, BlockGetter world, BlockPos pos) {
        BlockEntity te = getLevel().getBlockEntity(pos);
        if (te != null && te instanceof TileJarBrain) {
            return 5.0f;
        }
        return 0.0f;
    }

    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState state, Level world, BlockPos pos, Random rand) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile != null && tile instanceof TileJarBrain && ((TileJarBrain)tile).xp >= ((TileJarBrain)tile).xpMax) {
            FXDispatcher.INSTANCE.spark(pos.getX() + 0.5f, pos.getY() + 0.8f, pos.getZ() + 0.5f, 3.0f, 0.2f + rand.nextFloat() * 0.2f, 1.0f, 0.3f + rand.nextFloat() * 0.2f, 0.5f);
        }
    }

    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public int getComparatorInputOverride(BlockState state, Level world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile != null && tile instanceof TileJarBrain) {
            float r = ((TileJarBrain)tile).xp / (float)((TileJarBrain)tile).xpMax;
            return Mth.floor(r * 14.0f) + ((((TileJarBrain)tile).xp > 0) ? 1 : 0);
        }
        if (tile != null && tile instanceof TileJarFillable) {
            float n = (float)((TileJarFillable)tile).amount;
            float r = n / 250.0f;
            return Mth.floor(r * 14.0f) + ((((TileJarFillable)tile).amount > 0) ? 1 : 0);
        }
        return 0;
    }
}
