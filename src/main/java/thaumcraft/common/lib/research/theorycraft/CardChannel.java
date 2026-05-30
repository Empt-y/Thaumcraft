package thaumcraft.common.lib.research.theorycraft;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.items.consumables.ItemPhial;


public class CardChannel extends TheorycraftCard
{
    Aspect aspect;
    
    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("aspect", aspect.getTag());
        return nbt;
    }
    
    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        aspect = Aspect.getAspect(nbt.getStringOr("aspect", ""));
    }
    
    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(getSeed());
        int num = r.nextInt(Aspect.getCompoundAspects().size());
        aspect = Aspect.getCompoundAspects().get(num);
        return true;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "INFUSION";
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.channel.name", ChatFormatting.DARK_BLUE + aspect.getName() + ChatFormatting.RESET + "" + ChatFormatting.BOLD).getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.channel.text", ChatFormatting.BOLD + aspect.getName() + ChatFormatting.RESET).getString();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { ItemPhial.makeFilledPhial(aspect) };
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 25);
        return true;
    }
}
