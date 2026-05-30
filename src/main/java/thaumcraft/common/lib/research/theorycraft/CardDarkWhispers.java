package thaumcraft.common.lib.research.theorycraft;
import java.util.Iterator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardDarkWhispers extends TheorycraftCard
{
    @Override
    public boolean isAidOnly() {
        return true;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "ELDRITCH";
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.darkwhisper.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.darkwhisper.text").getString();
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        int l = player.experienceLevel;
        player.giveExperienceLevels(-(10 + l));
        if (l > 0) {
            for (String k : ResearchCategories.researchCategories.keySet()) {
                if (player.getRandom().nextBoolean()) {
                    continue;
                }
                data.addTotal(k, Mth.randomBetweenInclusive(player.getRandom(), 0, Math.max(1, (int)Math.sqrt(l))));
            }
        }
        data.addTotal("ELDRITCH", Mth.randomBetweenInclusive(player.getRandom(), Math.max(1, l / 5), Math.max(5, l / 2)));
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, Math.max(1, (int)Math.sqrt(l)), IPlayerWarp.EnumWarpType.NORMAL);
        if (player.getRandom().nextBoolean()) {
            ++data.bonusDraws;
        }
        return true;
    }
}
