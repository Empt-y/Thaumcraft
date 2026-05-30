package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardPortal extends TheorycraftCard
{
    @Override
    public boolean isAidOnly() {
        return true;
    }
    
    @Override
    public int getInspirationCost() {
        return -1;
    }
    
    @Override
    public String getResearchCategory() {
        return "ELDRITCH";
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.portal.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.portal.text").getString();
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
        data.addTotal(s[player.getRandom().nextInt(s.length)], Mth.randomBetweenInclusive(player.getRandom(), 5, 10));
        data.addTotal(s[player.getRandom().nextInt(s.length)], Mth.randomBetweenInclusive(player.getRandom(), 5, 10));
        data.addTotal("ELDRITCH", Mth.randomBetweenInclusive(player.getRandom(), 5, 10));
        data.bonusDraws += 2;
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 5, IPlayerWarp.EnumWarpType.TEMPORARY);
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
        return true;
    }
}
