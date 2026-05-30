package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardDragonEgg extends TheorycraftCard
{
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public boolean isAidOnly() {
        return true;
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.dragonegg.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.dragonegg.text").getString();
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
        for (int a = 0; a < 10; ++a) {
            String cat = s[player.getRandom().nextInt(s.length)];
            data.addTotal(cat, Mth.randomBetweenInclusive(player.getRandom(), 2, 5));
        }
        return true;
    }
}
