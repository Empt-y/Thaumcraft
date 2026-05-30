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


public class CardSynthesis extends TheorycraftCard
{
    Aspect aspect1;
    Aspect aspect2;
    Aspect aspect3;
    
    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("aspect1", aspect1.getTag());
        nbt.putString("aspect2", aspect2.getTag());
        nbt.putString("aspect3", aspect3.getTag());
        return nbt;
    }
    
    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        aspect1 = Aspect.getAspect(nbt.getStringOr("aspect1", ""));
        aspect2 = Aspect.getAspect(nbt.getStringOr("aspect2", ""));
        aspect3 = Aspect.getAspect(nbt.getStringOr("aspect3", ""));
    }
    
    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(getSeed());
        int num = r.nextInt(Aspect.getCompoundAspects().size());
        aspect3 = Aspect.getCompoundAspects().get(num);
        aspect1 = aspect3.getComponents()[0];
        aspect2 = aspect3.getComponents()[1];
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
        return Component.translatable("card.synthesis.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.synthesis.text", ChatFormatting.BOLD + aspect1.getName() + ChatFormatting.RESET, ChatFormatting.BOLD + aspect2.getName() + ChatFormatting.RESET).getString();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { ThaumcraftApiHelper.makeCrystal(aspect1), ThaumcraftApiHelper.makeCrystal(aspect2) };
    }
    
    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true, true };
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        ItemStack res = ThaumcraftApiHelper.makeCrystal(aspect3);
        data.addTotal(getResearchCategory(), 40);
        if (player.getRandom().nextFloat() < 0.33) {
            data.addInspiration(1);
        }
        if (!player.getInventory().add(res)) {
            player.dropItem(res, true);
        }
        return true;
    }
}
