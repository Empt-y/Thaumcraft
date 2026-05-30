package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardBeacon extends TheorycraftCard
{
    @Override
    public int getInspirationCost() {
        return -2;
    }
    
    @Override
    public boolean isAidOnly() {
        return true;
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.beacon.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.beacon.text").getString();
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        ++data.bonusDraws;
        ++data.penaltyStart;
        return true;
    }
}
