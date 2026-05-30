package thaumcraft.common.lib.research.theorycraft;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class CardReactions extends TheorycraftCard
{
    Aspect aspect1;
    Aspect aspect2;
    
    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("aspect1", aspect1.getTag());
        nbt.putString("aspect2", aspect2.getTag());
        return nbt;
    }
    
    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        aspect1 = Aspect.getAspect(nbt.getStringOr("aspect1", ""));
        aspect2 = Aspect.getAspect(nbt.getStringOr("aspect2", ""));
    }
    
    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(getSeed());
        int num = Mth.floor(r, 0, Aspect.getCompoundAspects().size() - 1);
        aspect1 = Aspect.getCompoundAspects().get(num);
        int num2;
        for (num2 = num; num2 == num; num2 = Mth.floor(r, 0, Aspect.getCompoundAspects().size() - 1)) {}
        aspect2 = Aspect.getCompoundAspects().get(num2);
        return true;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "ALCHEMY";
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.reactions.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.reactions.text", ChatFormatting.BOLD + aspect1.getName() + ChatFormatting.RESET, ChatFormatting.BOLD + aspect2.getName() + ChatFormatting.RESET).getString();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { ThaumcraftApiHelper.makeCrystal(aspect1), ThaumcraftApiHelper.makeCrystal(aspect2) };
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 25);
        if (player.getRandom().nextFloat() < 0.33) {
            data.addInspiration(1);
        }
        return true;
    }
}
