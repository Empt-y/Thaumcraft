package thaumcraft.api.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import thaumcraft.api.research.ResearchCategories;


public class CardExperimentation extends TheorycraftCard {

	@Override
	public int getInspirationCost() {
		return 2;
	}
	
	@Override
	public String getLocalizedName() {
		return Component.translatable("card.experimentation.name").getString();
	}
	
	@Override
	public String getLocalizedText() {
		return Component.translatable("card.experimentation.text").getString();
	}
	
	@Override
	public boolean activate(Player player, ResearchTableData data) {		
		try {
			String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[] {});
			String cat = s[ player.getRandom().nextInt(s.length) ];
			data.addTotal(cat, Mth.randomBetweenInclusive(player.getRandom(), 15, 30));
			data.addTotal("BASICS", Mth.randomBetweenInclusive(player.getRandom(), 1, 10));
		} catch (Exception e) {
			return false;
		}		
		return true;
	}
	
	
}
