package thaumcraft.common.lib.crafting;
import java.util.ArrayList;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.ItemsTC;


public class RecipeTripleMeatTreat implements Recipe
{
    public boolean matches(CraftingContainer inv, Level worldIn) {
        boolean sugar = false;
        ArrayList<Integer> meats = new ArrayList<Integer>();
        for (int a = 0; a < 3; ++a) {
            for (int b = 0; b < 3; ++b) {
                if (inv.getStackInRowAndColumn(a, b) != null) {
                    if (!inv.getStackInRowAndColumn(a, b).isEmpty()) {
                        ItemStack stack = inv.getStackInRowAndColumn(a, b).copy();
                        if (stack.getItem() == Items.SUGAR && sugar) {
                            return false;
                        }
                        if (stack.getItem() == Items.SUGAR && !sugar) {
                            sugar = true;
                        }
                        else {
                            if (stack.getItem() != ItemsTC.chunks) {
                                return false;
                            }
                            if (meats.contains(stack.getDamageValue()) || meats.size() >= 3) {
                                return false;
                            }
                            meats.add(stack.getDamageValue());
                        }
                    }
                }
            }
        }
        return sugar && meats.size() == 3;
    }
    
    public ItemStack getCraftingResult(CraftingContainer inv) {
        return new ItemStack(ItemsTC.tripleMeatTreat);
    }
    
    public ItemStack getRecipeOutput() {
        return new ItemStack(ItemsTC.tripleMeatTreat);
    }
    
    public boolean canFit(int width, int height) {
        return width * height >= 4;
    }
}
