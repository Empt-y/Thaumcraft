package thaumcraft.common.lib;

import net.minecraft.world.item.ItemStack;
import thaumcraft.api.items.ItemsTC;


public class CreativeTabThaumcraft
{
    public CreativeTabThaumcraft(int par1, String par2Str) {
    }

    public ItemStack getTabIconItem() {
        return new ItemStack(ItemsTC.goggles);
    }
}
