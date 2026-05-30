package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.tiles.devices.TileDioptra;


public class BlockDioptra extends BlockTCDevice implements IBlockEnabled
{
    public BlockDioptra() {
        super(null /*  null   Material removed    */, TileDioptra.class, "dioptra");
        setSoundType(SoundType.STONE);
    }

    public int damageDropped(BlockState state) {
        return 0;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public int getComparatorInputOverride(BlockState state, Level world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile != null && tile instanceof TileDioptra) {
            float r = ((TileDioptra)tile).grid_amt[84] / 64.0f;
            return Mth.floor(r * 14.0f) + ((r > 0.0f) ? 1 : 0);
        }
        return 0;
    }

    @Override
    protected void updateState(Level worldIn, BlockPos pos, BlockState state) {
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        boolean b = state.getValue(LiquidBlock.LEVEL) == 1;
        world.setBlock(pos, state.setValue((Property)IBlockEnabled.ENABLED, !b), 3);
        return true;
    }
}
