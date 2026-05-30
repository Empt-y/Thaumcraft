package thaumcraft.common.lib;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.items.ItemsTC;


public class CreativeTabThaumcraft
{
    public CreativeTabThaumcraft(int par1, String par2Str) {
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack getTabIconItem() {
        return new ItemStack(ItemsTC.goggles);
    }
}
