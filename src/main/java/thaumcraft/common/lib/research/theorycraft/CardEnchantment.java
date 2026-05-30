package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardEnchantment extends TheorycraftCard
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
        return Component.translatable("card.enchantment.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.enchantment.text").getString();
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        if (player.experienceLevel >= 5) {
            player.addExperienceLevel(-5);
            data.addTotal("INFUSION", Mth.randomBetweenInclusive(player.getRandom(), 15, 20));
            data.addTotal("AUROMANCY", Mth.randomBetweenInclusive(player.getRandom(), 15, 20));
            return true;
        }
        return false;
    }
}
