package thaumcraft.common.items.curios;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.items.ItemTCBase;
import net.minecraft.world.item.TooltipFlag;


public class ItemCelestialNotes extends ItemTCBase
{
    public ItemCelestialNotes() {
        super("celestial_notes", "sun", "stars_1", "stars_2", "stars_3", "stars_4", "moon_1", "moon_2", "moon_3", "moon_4", "moon_5", "moon_6", "moon_7", "moon_8");
    }
    
    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return "item.celestial_notes";
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        try {
            tooltip.add(ChatFormatting.AQUA + I18n.get("item.celestial_notes." + getVariantNames()[stack.getDamageValue()] + ".text"));
        }
        catch (Exception ex) {}
    }
}
