package thaumcraft.common.blocks.basic;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.misc.TileBanner;


public class BlockBannerTCItem extends BlockItem
{
    public BlockBannerTCItem(BlockBannerTC block) {
        super(block, new net.minecraft.world.item.Item.Properties());
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            String tag = stack.get(DataComponents.CUSTOM_DATA).copyTag().getStringOr("aspect", "");
            if (!tag.isEmpty() && Aspect.getAspect(tag) != null) {
                tooltip.accept(net.minecraft.network.chat.Component.literal(Aspect.getAspect(tag).getName()));
            }
        }
    }

    public InteractionResult onItemUse(Player player, Level worldIn, BlockPos pos, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (side == Direction.DOWN) {
            return InteractionResult.FAIL;
        }
        if (!worldIn.getBlockState(pos).isSolid()) {
            return InteractionResult.FAIL;
        }
        pos = pos.relative(side);
        if (!player.mayUseItemAt(pos, side, player.getItemInHand(hand))) {
            return InteractionResult.FAIL;
        }
        if (worldIn.isClientSide()) {
            return InteractionResult.FAIL;
        }
        worldIn.setBlock(pos, getBlock().defaultBlockState(), 3);
        TileBanner tile = (TileBanner)worldIn.getBlockEntity(pos);
        if (tile != null) {
            if (side == Direction.UP) {
                int i = Mth.floor((player.getYRot() + 180.0f) * 16.0f / 360.0f + 0.5) & 0xF;
                tile.setBannerFacing((byte)i);
            } else {
                tile.setWall(true);
                int i = 0;
                if (side == Direction.NORTH) i = 8;
                if (side == Direction.WEST) i = 4;
                if (side == Direction.EAST) i = 12;
                tile.setBannerFacing((byte)i);
            }
            if (player.getItemInHand(hand).has(DataComponents.CUSTOM_DATA)) {
                String aspectTag = player.getItemInHand(hand).get(DataComponents.CUSTOM_DATA).copyTag().getStringOr("aspect", "");
                if (!aspectTag.isEmpty()) {
                    tile.setAspect(Aspect.getAspect(aspectTag));
                }
            }
            tile.setChanged();
            worldIn.sendBlockUpdated(pos, getBlock().defaultBlockState(), getBlock().defaultBlockState(), 3);
        }
        player.getItemInHand(hand).shrink(1);
        return InteractionResult.SUCCESS;
    }
}
