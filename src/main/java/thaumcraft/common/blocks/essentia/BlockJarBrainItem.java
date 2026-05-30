package thaumcraft.common.blocks.essentia;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.tiles.devices.TileJarBrain;


public class BlockJarBrainItem extends BlockItem
{
    public BlockJarBrainItem(Block block) {
        super(block, new Item.Properties());
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if (result.consumesAction() && !context.level().isClientSide()) {
            BlockPos pos = context.getClickedPos();
            Level world = context.level();
            BlockState newState = world.getBlockState(pos);
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof TileJarBrain) {
                te.setChanged();
                world.sendBlockUpdated(pos, newState, newState, 3, getBlock());
            }
        }
        return result;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> adder, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, adder, flagIn);
    }
}
