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
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.TooltipFlag;


public class ItemCurio extends ItemTCBase
{
    public ItemCurio() {
        super("curio", "arcane", "preserved", "ancient", "eldritch", "knowledge", "twisted", "rites");
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        tooltip.add(I18n.get("item.curio.text"));
    }
    
    public InteractionResult use(Level worldIn, Player player, InteractionHand hand) {
        worldIn.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundsTC.learn, SoundSource.NEUTRAL, 0.5f, 0.4f / (ItemCurio.getRandom().nextFloat() * 0.4f + 0.8f));
        if (!worldIn.isClientSide()) {
            int oProg = IPlayerKnowledge.EnumKnowledgeType.OBSERVATION.getProgression();
            int tProg = IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression();
            switch (player.getItemInHand(hand).getDamageValue()) {
                default: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("AUROMANCY"), Mth.randomBetweenInclusive(player.getRandom(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("AUROMANCY"), Mth.randomBetweenInclusive(player.getRandom(), tProg / 3, tProg / 2));
                    break;
                }
                case 1: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ALCHEMY"), Mth.randomBetweenInclusive(player.getRandom(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ALCHEMY"), Mth.randomBetweenInclusive(player.getRandom(), tProg / 3, tProg / 2));
                    break;
                }
                case 2: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("GOLEMANCY"), Mth.randomBetweenInclusive(player.getRandom(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("GOLEMANCY"), Mth.randomBetweenInclusive(player.getRandom(), tProg / 3, tProg / 2));
                    break;
                }
                case 3: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ELDRITCH"), Mth.randomBetweenInclusive(player.getRandom(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ELDRITCH"), Mth.randomBetweenInclusive(player.getRandom(), tProg / 3, tProg / 2));
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, 5, IPlayerWarp.EnumWarpType.TEMPORARY);
                    break;
                }
                case 4: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("INFUSION"), Mth.randomBetweenInclusive(player.getRandom(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("INFUSION"), Mth.randomBetweenInclusive(player.getRandom(), tProg / 3, tProg / 2));
                    break;
                }
                case 5: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ARTIFICE"), Mth.randomBetweenInclusive(player.getRandom(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ARTIFICE"), Mth.randomBetweenInclusive(player.getRandom(), tProg / 3, tProg / 2));
                    break;
                }
                case 6: {
                    int aw = ThaumcraftApi.internalMethods.getActualWarp(player);
                    if (aw > 20) {
                        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
                        if (!knowledge.isResearchKnown("CrimsonRites")) {
                            ThaumcraftApi.internalMethods.completeResearch(player, "CrimsonRites");
                        }
                        ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ELDRITCH"), Mth.randomBetweenInclusive(player.getRandom(), oProg / 2, oProg));
                        ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ELDRITCH"), Mth.randomBetweenInclusive(player.getRandom(), tProg / 3, tProg / 2));
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 5, IPlayerWarp.EnumWarpType.TEMPORARY);
                        if (player.getRandom().nextBoolean()) {
                            ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.PERMANENT);
                        }
                        break;
                    }
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("fail.crimsonrites")));
                    return super.use(worldIn, player, hand);
                }
            }
            ResearchCategory[] rc = ResearchCategories.researchCategories.values().toArray(new ResearchCategory[0]);
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, rc[player.getRandom().nextInt(rc.length)], Mth.randomBetweenInclusive(player.getRandom(), oProg / 2, oProg));
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, rc[player.getRandom().nextInt(rc.length)], Mth.randomBetweenInclusive(player.getRandom(), tProg / 3, tProg / 2));
            if (!player.getAbilities().instabuild) {
                player.getItemInHand(hand).shrink(1);
            }
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("tc.knowledge.gained")));
        }
        player.addStat(Stats.getObjectUseStats(this));
        return super.use(worldIn, player, hand);
    }
}
