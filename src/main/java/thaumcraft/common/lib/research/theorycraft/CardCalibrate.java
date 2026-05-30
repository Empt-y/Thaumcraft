package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardCalibrate extends TheorycraftCard
{
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "ARTIFICE";
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.calibrate.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.calibrate.text").getString();
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 15);
        ++data.bonusDraws;
        return true;
    }
}
