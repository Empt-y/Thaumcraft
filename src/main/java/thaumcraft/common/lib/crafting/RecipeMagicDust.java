package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import net.minecraft.core.NonNullList;
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
import thaumcraft.common.items.resources.ItemCrystalEssence;

public class RecipeMagicDust implements CraftingRecipe {

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        boolean bowl = false, flint = false, redstone = false;
        ArrayList<String> crystals = new ArrayList<>();
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                stack = stack.copy();
                if (stack.getItem() == Items.BOWL) {
                    if (bowl) return false;
                    bowl = true;
                } else if (stack.getItem() == Items.FLINT) {
                    if (flint) return false;
                    flint = true;
                } else if (stack.getItem() == Items.REDSTONE) {
                    if (redstone) return false;
                    redstone = true;
                } else if (stack.getItem() instanceof ItemCrystalEssence ice) {
                    String aspect = ice.getAspects(stack).getAspects()[0].getTag();
                    if (crystals.contains(aspect) || crystals.size() >= 3) return false;
                    crystals.add(aspect);
                } else {
                    return false;
                }
            }
        }
        return bowl && redstone && flint && crystals.size() == 3;
    }

    @Override
    public ItemStack assemble(CraftingInput inv) {
        return new ItemStack(ItemsTC.salisMundus);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.size(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && (stack.getItem() == Items.FLINT || stack.getItem() == Items.BOWL)) {
                ItemStack copy = stack.copy();
                copy.setCount(1);
                ret.set(i, copy);
            }
        }
        return ret;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 6;
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider provider) {
        return new ItemStack(ItemsTC.salisMundus);
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
    public RecipeSerializer<RecipeMagicDust> getSerializer() { return null; }
}
