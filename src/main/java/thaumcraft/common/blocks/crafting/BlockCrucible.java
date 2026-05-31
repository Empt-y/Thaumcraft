package thaumcraft.common.blocks.crafting;
import java.util.Random;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.blocks.BlockTCTile;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.tiles.crafting.TileCrucible;


public class BlockCrucible extends BlockTCTile
{
    private int delay;

    public BlockCrucible() {
        super(null /*  null   Material removed    */, TileCrucible.class, "crucible");
        delay = 0;
        setSoundType(SoundType.METAL);
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean fromMovement) {
        if (!world.isClientSide()) {
            TileCrucible tile = (TileCrucible)world.getBlockEntity(pos);
            if (tile != null && entity instanceof ItemEntity && !(entity instanceof EntitySpecialItem) && tile.heat > 150 && tile.tank.getFluidAmount() > 0) {
                tile.attemptSmelt((ItemEntity)entity);
            } else {
                ++delay;
                if (delay < 10) return;
                delay = 0;
                if (entity instanceof LivingEntity && tile != null && tile.heat > 150 && tile.tank.getFluidAmount() > 0) {
                    entity.hurt(world.damageSources().inFire(), 1.0f);
                    world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.4f, 2.0f + world.getRandom().nextFloat() * 0.4f, false);
                }
            }
        }
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (worldIn instanceof Level level) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileCrucible) {
                ((TileCrucible)te).spillRemnants();
            }
        }
        super.destroy(worldIn, pos, state);
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (!world.isClientSide()) {
            java.util.Optional<FluidStack> fsOpt = FluidUtil.getFluidContained(player.getItemInHand(hand));
            if (fsOpt.isPresent()) {
                FluidStack fs = fsOpt.get();
                if (fs.getFluid().isSame(Fluids.WATER)) {
                    BlockEntity te = world.getBlockEntity(pos);
                    if (te instanceof TileCrucible) {
                        TileCrucible tile = (TileCrucible)te;
                        if (tile.tank.getFluidAmount() < tile.tank.getCapacity()) {
                            if (FluidUtil.interactWithFluidHandler(player, hand, tile.tank)) {
                                te.setChanged();
                                world.sendBlockUpdated(pos, state, state, 3);
                                world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.33f, 1.0f + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.3f, false);
                            }
                            return true;
                        }
                    }
                    return true;
                }
            }
            if (!player.isCrouching() && !(player.getItemInHand(hand).getItem() instanceof ICaster) && side == Direction.UP) {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof TileCrucible) {
                    TileCrucible tile = (TileCrucible)te;
                    ItemStack ti = player.getItemInHand(hand).copy();
                    ti.setCount(1);
                    if (tile.heat > 150 && tile.tank.getFluidAmount() > 0 && tile.attemptSmelt(ti, player.getName().getString()) == null) {
                        player.getItemInHand(hand).shrink(1);
                        return true;
                    }
                }
            } else if (player.getItemInHand(hand).isEmpty() && player.isCrouching()) {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof TileCrucible) {
                    ((TileCrucible)te).spillRemnants();
                    return true;
                }
            }
        }
        return true;
    }

    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public int getComparatorInputOverride(BlockState state, Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof TileCrucible) {
            float n = (float)((TileCrucible)te).aspects.visSize();
            float r = n / 500.0f;
            return Mth.floor(r * 14.0f) + ((((TileCrucible)te).aspects.visSize() > 0) ? 1 : 0);
        }
        return 0;
    }

    public void randomDisplayTick(BlockState state, Level w, BlockPos pos, Random r) {
        if (r.nextInt(10) == 0) {
            BlockEntity te = w.getBlockEntity(pos);
            if (te instanceof TileCrucible && ((TileCrucible)te).tank.getFluidAmount() > 0 && ((TileCrucible)te).heat > 150) {
                w.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.1f + r.nextFloat() * 0.1f, 1.2f + r.nextFloat() * 0.2f, false);
            }
        }
    }
}
