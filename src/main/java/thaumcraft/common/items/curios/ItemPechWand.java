package thaumcraft.common.items.curios;
import net.minecraft.stats.Stats;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.TooltipFlag;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;


public class ItemPechWand extends ItemTCBase
{
    public ItemPechWand() {
        super("pech_wand");
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        tooltip.accept(net.minecraft.network.chat.Component.literal(I18n.get("item.curio.text")));
    }
    
    public InteractionResult use(Level worldIn, Player player, InteractionHand hand) {
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (!knowledge.isResearchKnown("BASEAUROMANCY")) {
            if (!worldIn.isClientSide()) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.RED + I18n.get("not.pechwand")));
            }
            return super.use(worldIn, player, hand);
        }
        if (!player.getAbilities().instabuild) {
            player.getItemInHand(hand).shrink(1);
        }
        worldIn.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundsTC.learn, SoundSource.NEUTRAL, 0.5f, 0.4f / (net.minecraft.util.RandomSource.create().nextFloat() * 0.4f + 0.8f), false);
        if (!worldIn.isClientSide()) {
            if (!knowledge.isResearchKnown("FOCUSPECH")) {
                ThaumcraftApi.internalMethods.progressResearch(player, "FOCUSPECH");
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("got.pechwand")));
            }
            int oProg = IPlayerKnowledge.EnumKnowledgeType.OBSERVATION.getProgression();
            ResearchCategory[] rc = ResearchCategories.researchCategories.values().toArray(new ResearchCategory[0]);
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, rc[player.getRandom().nextInt(rc.length)], Mth.randomBetweenInclusive(player.getRandom(), oProg / 3, oProg / 2));
            int tProg = IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression();
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, rc[player.getRandom().nextInt(rc.length)], Mth.randomBetweenInclusive(player.getRandom(), tProg / 5, tProg / 4));
        }
        player.addStat(Stats.getObjectUseStats(this));
        return super.use(worldIn, player, hand);
    }
}
