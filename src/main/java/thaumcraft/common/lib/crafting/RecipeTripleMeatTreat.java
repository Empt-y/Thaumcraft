package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.ItemsTC;

public class RecipeTripleMeatTreat implements CraftingRecipe {

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        boolean sugar = false;
        ArrayList<Integer> meats = new ArrayList<>();
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                stack = stack.copy();
                if (stack.getItem() == Items.SUGAR && sugar) return false;
                if (stack.getItem() == Items.SUGAR) {
                    sugar = true;
                } else {
                    if (stack.getItem() != ItemsTC.chunks) return false;
                    if (meats.contains(stack.getDamageValue()) || meats.size() >= 3) return false;
                    meats.add(stack.getDamageValue());
                }
            }
        }
        return sugar && meats.size() == 3;
    }

    @Override
    public ItemStack assemble(CraftingInput inv) {
        return new ItemStack(ItemsTC.tripleMeatTreat);
    }

    @Override
    public net.minecraft.world.item.crafting.PlacementInfo placementInfo() {
        return net.minecraft.world.item.crafting.PlacementInfo.NOT_PLACEABLE;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 4;
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider provider) {
        return new ItemStack(ItemsTC.tripleMeatTreat);
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean showNotification() { return true; }

    @Override
    public String group() { return ""; }

    @Override
    public RecipeSerializer<RecipeTripleMeatTreat> getSerializer() { return null; }
}
