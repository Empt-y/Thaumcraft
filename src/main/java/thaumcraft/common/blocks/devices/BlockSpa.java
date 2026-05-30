package thaumcraft.common.blocks.devices;
import java.util.Optional;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.tiles.devices.TileSpa;


public class BlockSpa extends BlockTCDevice
{
    public BlockSpa() {
        super(null /*  null   Material removed    */, TileSpa.class, "spa");
        setSoundType(SoundType.STONE);
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (world.isClientSide()) {
            return true;
        }
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TileSpa && !player.isShiftKeyDown()) {
            TileSpa tile = (TileSpa)tileEntity;
            if (FluidUtil.interactWithFluidHandler(player, hand, tile.tank)) {
                tile.setChanged();
                world.sendBlockUpdated(pos, state, state, 3);
                world.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.33f, 1.0f + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.3f);
            }
            // TODO: open Spa GUI when no fluid interaction (requires MenuProvider migration)
        }
        return true;
    }
}
