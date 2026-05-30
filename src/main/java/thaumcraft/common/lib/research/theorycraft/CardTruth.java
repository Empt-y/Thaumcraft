package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardTruth extends TheorycraftCard
{
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
        return Component.translatable("card.truth.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.truth.text").getString();
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal("ELDRITCH", Mth.randomBetweenInclusive(player.getRandom(), 10, 25));
        ++data.bonusDraws;
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 3, IPlayerWarp.EnumWarpType.TEMPORARY);
        return true;
    }
}
