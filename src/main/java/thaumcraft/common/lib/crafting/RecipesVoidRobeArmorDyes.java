package thaumcraft.common.lib.crafting;
import java.util.ArrayList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.item.crafting.RecipesArmorDyes; // removed
import net.minecraft.world.level.Level;
// minecraftforge.oredict import removed
import thaumcraft.common.items.armor.ItemVoidRobeArmor;


public class RecipesVoidRobeArmorDyes
{
    public boolean matches(CraftingContainer par1InventoryCrafting, Level par2World) {
        ItemStack itemstack = ItemStack.EMPTY;
        ArrayList arraylist = new ArrayList();
        for (int i = 0; i < par1InventoryCrafting.getContainerSize(); ++i) {
            ItemStack itemstack2 = par1InventoryCrafting.getItem(i);
            if (itemstack2 != null && !itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof Item /* ItemArmor removed */) {
                    Item /* ItemArmor removed */ itemarmor = (Item /* ItemArmor removed */)itemstack2.getItem();
                    if (!(itemarmor instanceof ItemVoidRobeArmor) || !itemstack.isEmpty()) {
                        return false;
                    }
                    itemstack = itemstack2;
                }
                else {
                    if (!(itemstack2.getItem() instanceof net.minecraft.world.item.DyeItem)) {
                        return false;
                    }
                    arraylist.add(itemstack2);
                }
            }
        }
        return !itemstack.isEmpty() && !arraylist.isEmpty();
    }
    
    public ItemStack getCraftingResult(CraftingContainer par1InventoryCrafting) {
        ItemStack itemstack = ItemStack.EMPTY;
        int[] aint = new int[3];
        int i = 0;
        int j = 0;
        Item /* ItemArmor removed */ itemarmor = null;
        for (int k = 0; k < par1InventoryCrafting.getContainerSize(); ++k) {
            ItemStack itemstack2 = par1InventoryCrafting.getItem(k);
            if (itemstack2 != null && !itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof Item /* ItemArmor removed */) {
                    itemarmor = (Item /* ItemArmor removed */)itemstack2.getItem();
                    if (!(itemarmor instanceof ItemVoidRobeArmor) || !itemstack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    itemstack = itemstack2.copy();
                    itemstack.setCount(1);
                    if (((ItemVoidRobeArmor)itemarmor).hasColor(itemstack2)) {
                        int l = ((ItemVoidRobeArmor)itemarmor).getColor(itemstack);
                        float f = (l >> 16 & 0xFF) / 255.0f;
                        float f2 = (l >> 8 & 0xFF) / 255.0f;
                        float f3 = (l & 0xFF) / 255.0f;
                        i += (int)(Math.max(f, Math.max(f2, f3)) * 255.0f);
                        aint[0] += (int)(f * 255.0f);
                        aint[1] += (int)(f2 * 255.0f);
                        aint[2] += (int)(f3 * 255.0f);
                        ++j;
                    }
                }
                else {
                    if (!(itemstack2.getItem() instanceof net.minecraft.world.item.DyeItem dyeItem)) {
                        return ItemStack.EMPTY;
                    }
                    net.minecraft.world.item.DyeColor dyeColor2 = itemstack2.get(net.minecraft.core.component.DataComponents.DYE);
                    if (dyeColor2 == null) return ItemStack.EMPTY;
                    int dyeRgb = dyeColor2.getFireworkColor();
                    float[] afloat = new float[]{((dyeRgb >> 16) & 0xFF) / 255.0f, ((dyeRgb >> 8) & 0xFF) / 255.0f, (dyeRgb & 0xFF) / 255.0f};
                    int j2 = (int)(afloat[0] * 255.0f);
                    int k2 = (int)(afloat[1] * 255.0f);
                    int i2 = (int)(afloat[2] * 255.0f);
                    i += Math.max(j2, Math.max(k2, i2));
                    int[] array = aint;
                    int n = 0;
                    array[n] += j2;
                    int[] array2 = aint;
                    int n2 = 1;
                    array2[n2] += k2;
                    int[] array3 = aint;
                    int n3 = 2;
                    array3[n3] += i2;
                    ++j;
                }
            }
        }
        if (itemarmor == null) {
            return ItemStack.EMPTY;
        }
        int k = aint[0] / j;
        int l2 = aint[1] / j;
        int l = aint[2] / j;
        float f = i / (float)j;
        float f2 = (float)Math.max(k, Math.max(l2, l));
        k = (int)(k * f / f2);
        l2 = (int)(l2 * f / f2);
        l = (int)(l * f / f2);
        int i2 = (k << 8) + l2;
        i2 = (i2 << 8) + l;
        ((ItemVoidRobeArmor)itemarmor).setColor(itemstack, i2);
        return itemstack;
    }
}
