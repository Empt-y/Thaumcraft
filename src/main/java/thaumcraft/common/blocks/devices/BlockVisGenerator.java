package thaumcraft.common.blocks.devices;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.tiles.devices.TileVisGenerator;


public class BlockVisGenerator extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    public BlockVisGenerator() {
        super(null /*  null   Material removed    */, TileVisGenerator.class, "vis_generator");
        setSoundType(SoundType.WOOD);
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

    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        for (Direction face : Direction.values()) {
            BlockPos adjPos = pos.relative(face);
            EnergyHandler capability = worldIn.getCapability(Capabilities.Energy.BLOCK, adjPos, face.getOpposite());
            if (capability != null) {
                BlockState bs = defaultBlockState();
                bs = bs.setValue((Property)IBlockFacing.FACING, face);
                bs = bs.setValue((Property)IBlockEnabled.ENABLED, true);
                return bs;
            }
        }
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    public void randomDisplayTick(BlockState state, Level world, BlockPos pos, Random rand) {
        BlockEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof TileVisGenerator tileVis) {
            Direction face = BlockStateUtils.getFacing(state);
            if (tileVis.getEnergyStored() > 0) {
                double x = (face.getStepX() == 0) ? (rand.nextGaussian() * 0.1) : (face.getStepX() * 0.1);
                double y = (face.getStepY() == 0) ? (rand.nextGaussian() * 0.1) : (face.getStepY() * 0.1);
                double z = (face.getStepZ() == 0) ? (rand.nextGaussian() * 0.1) : (face.getStepZ() * 0.1);
                FXDispatcher.INSTANCE.spark(pos.getX() + 0.5 + x, pos.getY() + 0.5 + y, pos.getZ() + 0.5 + z, 0.66f + rand.nextFloat(), 0.65f + rand.nextFloat() * 0.1f, 1.0f, 1.0f, 0.8f);
            }
        }
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return Utils.rotateBlockAABB(new AABB(0.25, 0.0, 0.25, 0.75, 0.875, 0.75), BlockStateUtils.getFacing(getMetaFromState(state)));
    }
}
