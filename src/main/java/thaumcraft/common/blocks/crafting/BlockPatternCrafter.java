package thaumcraft.common.blocks.crafting;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import thaumcraft.common.tiles.crafting.TilePatternCrafter;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft", value = net.neoforged.api.distmarker.Dist.CLIENT)
public class BlockPatternCrafter extends BlockTCDevice implements IBlockFacingHorizontal, IBlockEnabled
{
    private RayTracer rayTracer;

    public BlockPatternCrafter() {
        super(null /*  null   Material removed    */, TilePatternCrafter.class, "pattern_crafter");
        rayTracer = new RayTracer();
        setSoundType(SoundType.METAL);
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public Object getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null;
    }

    public boolean isSideSolid(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        BlockState bs = defaultBlockState();
        bs = bs.setValue((Property)IBlockFacingHorizontal.FACING, placer.getDirection());
        return bs;
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        HitResult hit = RayTracer.retraceBlock(world, player, pos);
        if (hit == null) {
            return true;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (hit.getType() != HitResult.Type.MISS && tile instanceof TilePatternCrafter) {
            if (!world.isClientSide()) {
                ((TilePatternCrafter)tile).cycle();
                world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.key, SoundSource.BLOCKS, 0.5f, 1.0f, false);
            }
            return true;
        }
        return true;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0, 0, 0, 1, 1, 1);
    }
}
