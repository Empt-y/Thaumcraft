package thaumcraft.common.items.curios;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.CommandThaumcraft;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.research.ResearchManager;
import net.minecraft.world.item.TooltipFlag;


public class ItemThaumonomicon extends ItemTCBase
{
    public ItemThaumonomicon() {
        super("thaumonomicon", "normal", "cheat");
    }
    
    
    public void getSubItems(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == null /* SEARCH tab removed */) {
            items.add(new ItemStack(this, 1));
            if (ModConfig.CONFIG_MISC.allowCheatSheet) {
                items.add(new ItemStack(this, 1));
            }
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (stack.getDamageValue() == 1) {
            tooltip.add(net.minecraft.network.chat.Component.literal("" + ChatFormatting.DARK_PURPLE + "Creative only"));
        }
    }
    
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide()) {
            if (ModConfig.CONFIG_MISC.allowCheatSheet && player.getItemInHand(hand).getDamageValue() == 1) {
                Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
                for (ResearchCategory cat : rc) {
                    Collection<ResearchEntry> rl = cat.research.values();
                    for (ResearchEntry ri : rl) {
                        CommandThaumcraft.giveRecursiveResearch(player, ri.getKey());
                    }
                }
            }
            else {
                Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
                for (ResearchCategory cat : rc) {
                    Collection<ResearchEntry> rl = cat.research.values();
                    for (ResearchEntry ri : rl) {
                        if (ThaumcraftCapabilities.knowsResearch(player, ri.getKey()) && ri.getSiblings() != null) {
                            for (String sib : ri.getSiblings()) {
                                if (!ThaumcraftCapabilities.knowsResearch(player, sib)) {
                                    ResearchManager.completeResearch(player, sib);
                                }
                            }
                        }
                    }
                }
            }
            ThaumcraftCapabilities.getKnowledge(player).sync((net.minecraft.server.level.ServerPlayer)player);
        }
        else {
            world.playSound(player.getX(), player.getY(), player.getZ(), SoundsTC.page, SoundSource.PLAYERS, 1.0f, 1.0f, false);
        }
        /* TODO: port to NetworkHooks.openScreen */ 
        return InteractionResult.SUCCESS;
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return (itemstack.getDamageValue() != 1) ? Rarity.UNCOMMON : Rarity.EPIC;
    }
}
