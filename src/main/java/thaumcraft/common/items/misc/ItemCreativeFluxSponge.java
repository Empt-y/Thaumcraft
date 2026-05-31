package thaumcraft.common.items.misc;

import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.TooltipFlag;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class ItemCreativeFluxSponge extends ItemTCBase {

    public ItemCreativeFluxSponge() {
        super("creative_flux_sponge");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
        tooltip.accept(Component.literal(ChatFormatting.GREEN + "Right-click to drain all"));
        tooltip.accept(Component.literal(ChatFormatting.GREEN + "flux from 9x9 chunk area"));
        tooltip.accept(Component.literal(ChatFormatting.DARK_AQUA + "Also removes flux rifts"));
        tooltip.accept(Component.literal(ChatFormatting.DARK_AQUA + "if used while sneaking."));
        tooltip.accept(Component.literal(ChatFormatting.DARK_PURPLE + "Creative only"));
    }

    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.EPIC;
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        if (worldIn.isClientSide()) {
            playerIn.swing(hand);
            worldIn.playSound(playerIn, playerIn.blockPosition(), SoundsTC.craftstart, SoundSource.PLAYERS, 0.15f, 1.0f);
        } else {
            int q = 0;
            BlockPos p = playerIn.blockPosition();
            for (int x = -4; x <= 4; ++x) {
                for (int z = -4; z <= 4; ++z) {
                    q += (int) AuraHelper.drainFlux(worldIn, p.offset(16 * x, 0, 16 * z), 500.0f, false);
                }
            }
            playerIn.sendSystemMessage(Component.literal(ChatFormatting.GREEN + "" + q + " flux drained from 81 chunks."));
            if (playerIn.isCrouching()) {
                List<EntityFluxRift> list = EntityUtils.getEntitiesInRange(worldIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), null, EntityFluxRift.class, 32.0);
                for (EntityFluxRift fr : list) {
                    fr.discard();
                }
                playerIn.sendSystemMessage(Component.literal(ChatFormatting.DARK_AQUA + "" + list.size() + " flux rifts removed."));
            }
        }
        return super.use(worldIn, playerIn, hand);
    }
}
