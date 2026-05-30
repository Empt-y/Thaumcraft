package thaumcraft.api.research.theorycraft;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;



public class CardAnalyze extends TheorycraftCard {
	
	String cat = null;
	
	@Override
	public CompoundTag serialize() {
		CompoundTag nbt = super.serialize();
		nbt.putString("cat", cat);
		return nbt;
	}

	@Override
	public void deserialize(CompoundTag nbt) {
		super.deserialize(nbt);
		cat = nbt.getStringOr("cat", "");
	}
	
	@Override
	public String getResearchCategory() {
		return cat;
	}
	
	@Override
	public boolean initialize(Player player, ResearchTableData data) { 
		Random r = new Random(getSeed());
		ArrayList<String> cats = new ArrayList<>();
		for (ResearchCategory rc:ResearchCategories.researchCategories.values()) {
			if (rc.key=="BASICS") continue;
			if (ThaumcraftCapabilities.getKnowledge(player).getKnowledge(
				EnumKnowledgeType.OBSERVATION, ResearchCategories.researchCategories.get(cat))>0)
				cats.add(rc.key);
		}
		if (cats.size()>0) { 
			cat = cats.get(r.nextInt(cats.size()));
		}		
		return cat!=null;
	}

	@Override
	public int getInspirationCost() {
		return 2;
	}
	
	@Override
	public String getLocalizedName() {
		return Component.translatable("card.analyze.name", ChatFormatting.DARK_BLUE+""+ChatFormatting.BOLD+Component.translatable("tc.research_category."+cat).getString()+ChatFormatting.RESET).getString();
	}
	
	@Override
	public String getLocalizedText() {
		return Component.translatable("card.analyze.text", ChatFormatting.BOLD+Component.translatable("tc.research_category."+cat).getString()+ChatFormatting.RESET,
			ChatFormatting.BOLD+Component.translatable("tc.research_category.BASICS").getString()+ChatFormatting.RESET).getString();
	}
	
	@Override
	public boolean activate(Player player, ResearchTableData data) {
		ResearchCategory rc = ResearchCategories.getResearchCategory(cat);
		int k = ThaumcraftCapabilities.getKnowledge(player).getKnowledge(EnumKnowledgeType.OBSERVATION, rc);		
		if (k>=1) {
			data.addTotal("BASICS", 5);
			ThaumcraftCapabilities.getKnowledge(player).addKnowledge(
					EnumKnowledgeType.OBSERVATION, rc, -EnumKnowledgeType.OBSERVATION.getProgression());
			data.addTotal(cat, Mth.randomBetweenInclusive(player.getRandom(), 25, 50));
			return true;
		}
		return false;
	}
	
	
}
