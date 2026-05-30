package thaumcraft.common.items.misc;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.TooltipFlag;


public class ItemCreativeFluxSponge extends ItemTCBase
{
    public ItemCreativeFluxSponge() {
        super("creative_flux_sponge");
        /* setMaxDamage removed - use Item.Properties */;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GREEN + "Right-click to drain all");
        tooltip.add(ChatFormatting.GREEN + "flux from 9x9 chunk area");
        tooltip.add(ChatFormatting.DARK_AQUA + "Also removes flux rifts");
        tooltip.add(ChatFormatting.DARK_AQUA + "if used while sneaking.");
        tooltip.add(ChatFormatting.DARK_PURPLE + "Creative only");
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.EPIC;
    }
    
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        if (worldIn.isClientSide()) {
            playerIn.swing(hand);
            playerIn.level().playSound(playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundsTC.craftstart, SoundSource.PLAYERS, 0.15f, 1.0f, false);
        }
        else {
            int q = 0;
            BlockPos p = playerIn.getBlockPos();
            for (int x = -4; x <= 4; ++x) {
                for (int z = -4; z <= 4; ++z) {
                    q += (int)AuraHelper.drainFlux(worldIn, p.offset(16 * x, 0, 16 * z), 500.0f, false);
                }
            }
            playerIn.sendSystemMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.GREEN + "" + q + " flux drained from 81 chunks."));
            if (playerIn.isCrouching()) {
                List<EntityFluxRift> list = EntityUtils.getEntitiesInRange(worldIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), null, EntityFluxRift.class, 32.0);
                for (EntityFluxRift fr : list) {
                    fr.discard();
                }
                playerIn.sendSystemMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_AQUA + "" + list.size() + " flux rifts removed."));
            }
        }
        return super.use(worldIn, playerIn, hand);
    }
}
