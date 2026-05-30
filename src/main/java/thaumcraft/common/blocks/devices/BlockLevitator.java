package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileLevitator;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft", value = net.neoforged.api.distmarker.Dist.CLIENT)
public class BlockLevitator extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    public BlockLevitator() {
        super(null /*  null   Material removed    */, TileLevitator.class, "levitator");
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

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileLevitator) {
            ((TileLevitator)tile).increaseRange(player);
            world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.key, SoundSource.BLOCKS, 0.5f, 1.0f, false);
            return true;
        }
        return true;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        Direction facing = BlockStateUtils.getFacing(state);
        float f = 0.125f;
        float minx = 0.0f + ((facing.getStepX() > 0) ? f : 0.0f);
        float maxx = 1.0f - ((facing.getStepX() < 0) ? f : 0.0f);
        float miny = 0.0f + ((facing.getStepY() > 0) ? f : 0.0f);
        float maxy = 1.0f - ((facing.getStepY() < 0) ? f : 0.0f);
        float minz = 0.0f + ((facing.getStepZ() > 0) ? f : 0.0f);
        float maxz = 1.0f - ((facing.getStepZ() < 0) ? f : 0.0f);
        return new AABB(minx, miny, minz, maxx, maxy, maxz);
    }
}
