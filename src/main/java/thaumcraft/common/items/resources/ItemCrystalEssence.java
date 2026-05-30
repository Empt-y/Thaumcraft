package thaumcraft.common.items.resources;
import java.util.Iterator;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCEssentiaContainer;


public class ItemCrystalEssence extends ItemTCEssentiaContainer
{
    public ItemCrystalEssence() {
        super("crystal_essence", 1);
    }
    
    
    public void getSubItems(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == null /* SEARCH tab removed */) {
            for (Aspect tag : Aspect.aspects.values()) {
                ItemStack i = new ItemStack(this);
                setAspects(i, new AspectList().add(tag, base));
                items.add(i);
            }
        }
    }
    
    @Override
    public net.minecraft.network.chat.Component getName(ItemStack stack) {
        if (getAspects(stack) != null && !getAspects(stack).aspects.isEmpty()) {
            return super.getName(stack).copy().append(" (" + getAspects(stack).getAspects()[0].getName() + ")");
        }
        return super.getName(stack);
    }
}
