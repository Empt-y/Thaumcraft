package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardSpellbinding extends TheorycraftCard
{
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "AUROMANCY";
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.spellbinding.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.spellbinding.text").getString();
    }
    
    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        return player.experienceLevel > 0;
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        if (player.experienceLevel <= 0) {
            return false;
        }
        int l = Math.min(5, player.experienceLevel);
        data.addTotal(getResearchCategory(), l * 5);
        player.giveExperienceLevels(-l);
        return true;
    }
}
