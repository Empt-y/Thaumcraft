package thaumcraft.common.blocks.crafting;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;


public class BlockFocalManipulator extends BlockTCDevice
{
    public BlockFocalManipulator() {
        super(null /*  null   Material removed    */, TileFocalManipulator.class, "wand_workbench");
        setSoundType(SoundType.STONE);
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (world.isClientSide()) {
            return true;
        }
        // TODO: open GUI id=7
        return true;
    }
}
