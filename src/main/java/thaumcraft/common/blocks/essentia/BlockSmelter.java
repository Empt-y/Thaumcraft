package thaumcraft.common.blocks.essentia;
import net.minecraft.world.Container;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.essentia.TileSmelter;


public class BlockSmelter extends BlockTCDevice implements IBlockEnabled, IBlockFacingHorizontal
{
    public BlockSmelter(String name) {
        super(null /*  null   Material removed    */, TileSmelter.class, name);
        setSoundType(SoundType.METAL);
        registerDefaultState(defaultBlockState()
            .setValue((Property)IBlockFacingHorizontal.FACING, Direction.NORTH)
            .setValue((Property)IBlockEnabled.ENABLED, false));
    }

    @Override
    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        BlockState bs = defaultBlockState();
        bs = bs.setValue((Property)IBlockFacingHorizontal.FACING, placer.getDirection().getOpposite());
        bs = bs.setValue((Property)IBlockEnabled.ENABLED, false);
        return bs;
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, Orientation orientation, boolean isMoving) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te != null && te instanceof TileSmelter) {
            ((TileSmelter)te).checkNeighbours();
        }
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (!world.isClientSide() && !player.isShiftKeyDown()) {
            // TODO: open smelter GUI - requires MenuProvider wiring
        }
        return true;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return BlockStateUtils.isEnabled(state) ? 13 : super.getLightEmission(state, world, pos);
    }

    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public int getComparatorInputOverride(BlockState state, Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null && te instanceof Container) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)te);
        }
        return 0;
    }

    public static void setFurnaceState(Level world, BlockPos pos, boolean state) {
        BlockState current = world.getBlockState(pos);
        if (BlockStateUtils.isEnabled(current) == state) {
            return;
        }
        world.setBlock(pos, current.setValue((Property)IBlockEnabled.ENABLED, state), 3);
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (worldIn instanceof Level level && !level.isClientSide()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof TileSmelter && ((TileSmelter)tileentity).vis > 0) {
                AuraHelper.polluteAura(level, pos, (float)((TileSmelter)tileentity).vis, true);
            }
        }
        super.destroy(worldIn, pos, state);
    }

    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState state, Level w, BlockPos pos, Random r) {
        if (BlockStateUtils.isEnabled(state)) {
            float f = pos.getX() + 0.5f;
            float f2 = pos.getY() + 0.2f + r.nextFloat() * 5.0f / 16.0f;
            float f3 = pos.getZ() + 0.5f;
            float f4 = 0.52f;
            float f5 = r.nextFloat() * 0.5f - 0.25f;
            if (BlockStateUtils.getFacing(state) == Direction.WEST) {
                w.addParticle(ParticleTypes.SMOKE, f - f4, f2, f3 + f5, 0.0, 0.0, 0.0);
                w.addParticle(ParticleTypes.FLAME, f - f4, f2, f3 + f5, 0.0, 0.0, 0.0);
            }
            if (BlockStateUtils.getFacing(state) == Direction.EAST) {
                w.addParticle(ParticleTypes.SMOKE, f + f4, f2, f3 + f5, 0.0, 0.0, 0.0);
                w.addParticle(ParticleTypes.FLAME, f + f4, f2, f3 + f5, 0.0, 0.0, 0.0);
            }
            if (BlockStateUtils.getFacing(state) == Direction.NORTH) {
                w.addParticle(ParticleTypes.SMOKE, f + f5, f2, f3 - f4, 0.0, 0.0, 0.0);
                w.addParticle(ParticleTypes.FLAME, f + f5, f2, f3 - f4, 0.0, 0.0, 0.0);
            }
            if (BlockStateUtils.getFacing(state) == Direction.SOUTH) {
                w.addParticle(ParticleTypes.SMOKE, f + f5, f2, f3 + f4, 0.0, 0.0, 0.0);
                w.addParticle(ParticleTypes.FLAME, f + f5, f2, f3 + f4, 0.0, 0.0, 0.0);
            }
        }
    }
}
