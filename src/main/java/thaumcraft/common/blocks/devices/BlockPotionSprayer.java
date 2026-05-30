package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.tiles.devices.TilePotionSprayer;


public class BlockPotionSprayer extends BlockTCDevice implements IBlockFacing, IBlockEnabled
{
    public BlockPotionSprayer() {
        super(null /*  null   Material removed    */, TilePotionSprayer.class, "potion_sprayer");
        setSoundType(SoundType.METAL);
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (world.isClientSide()) {
            return true;
        }
        // TODO: open GUI id=21
        return true;
    }

    public int damageDropped(BlockState state) {
        return 0;
    }
}
