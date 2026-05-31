package thaumcraft.api.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * This class implements a custom shapeless recipe for the Arcane Workbench in NeoForge 26.x.
 * * CRITICAL MOJANG CHANGES FOR 1.20.5+:
 * 1. Interface methods 'assemble' and 'getResultItem' MUST accept a 'HolderLookup.Provider' parameter.
 * 2. Fabric's 'Identifier' mapping does not exist here; use 'Identifier'.
 * 3. Bounding math for inventory checks must cleanly separate matrix ingredients (0-8) from aspects (9-14).
 */
public class ShapelessArcaneRecipe implements IArcaneRecipe {

    private final String research;
    private final int vis;
    private final AspectList crystals;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final String group;

    /** Varargs constructor matching the old Forge/1.12 pattern: result, then varargs ingredients. */
    public ShapelessArcaneRecipe(Identifier group, String res, int vis, AspectList crystals,
                                 Object result, Object... args) {
        this.group = group != null ? group.toString() : "";
        this.research = res;
        this.vis = vis;
        this.crystals = crystals;
        this.result = result instanceof ItemStack ? (ItemStack) result
                : result instanceof ItemLike ? new ItemStack((ItemLike) result)
                : ItemStack.EMPTY;
        NonNullList<Ingredient> ing = NonNullList.create();
        for (Object arg : args) {
            if (arg instanceof ItemStack s) ing.add(Ingredient.of(s.getItem()));
            else if (arg instanceof Item) ing.add(Ingredient.of((Item) arg));
            else if (arg instanceof ItemLike) ing.add(Ingredient.of((ItemLike) arg));
            else if (arg instanceof String key) ing.add(OreDictCompat.fromOreDict(key));
            else if (arg instanceof Object[]) {
                for (Object sub : (Object[]) arg) {
                    if (sub instanceof ItemStack s2) ing.add(Ingredient.of(s2.getItem()));
                    else if (sub instanceof Item) ing.add(Ingredient.of((Item) sub));
                    else if (sub instanceof ItemLike) ing.add(Ingredient.of((ItemLike) sub));
                    else if (sub instanceof String key2) ing.add(OreDictCompat.fromOreDict(key2));
                }
            }
        }
        this.ingredients = ing;
    }

    public ShapelessArcaneRecipe(Identifier group, String res, int vis, AspectList crystals,
                                 NonNullList<Ingredient> ingredients, @Nonnull ItemStack result) {
        this.group = group != null ? group.toString() : "";
        this.research = res;
        this.vis = vis;
        this.crystals = crystals;
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(CraftingInput inv, Level world) {
        if (!(inv instanceof IArcaneWorkbench)) return false;

        if (crystals != null) {
            for (Aspect aspect : crystals.getAspects()) {
                ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect));
                boolean found = false;

                // Read from crystal inventory slots (typically offset tracking slots 9 through 14)
                for (int i = 9; i < Math.min(15, inv.size()); i++) {
                    ItemStack slot = inv.getItem(i);
                    if (!slot.isEmpty() && slot.getItem() == ItemsTC.crystalEssence
                            && slot.getCount() >= cs.getCount()
                            && ItemStack.matches(cs, slot)) { // Modern data component/tag matching
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
        }
        return matchesShapeless(inv);
    }

    private boolean matchesShapeless(CraftingInput inv) {
        List<Ingredient> remaining = new ArrayList<>(ingredients);

        // Loop through the main 3x3 grid area (slots 0-8)
        for (int i = 0; i < Math.min(9, inv.size()); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            boolean matched = false;
            for (int j = 0; j < remaining.size(); j++) {
                if (remaining.get(j).test(stack)) {
                    remaining.remove(j);
                    matched = true;
                    break;
                }
            }
            if (!matched) return false; // Found an item that didn't belong in the recipe
        }
        return remaining.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput inv) {
        return result.copy();
    }

    @Override
    public ItemStack getResultItem() {
        return result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public boolean canCraftInDimensions(int w, int h) {
        // Shapeless items just need to physically fit inside the matrix surface size
        return w * h >= ingredients.size();
    }

    @Override
    public String getGroup() { return group; }

    @Override
    public int getVis() { return vis; }

    @Override
    public String getResearch() { return research; }

    @Override
    public AspectList getCrystals() { return crystals; }

    @Override
    public boolean showNotification() { return false; }

    @Override
    public PlacementInfo placementInfo() { return PlacementInfo.NOT_PLACEABLE; }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeSerializer<? extends Recipe<CraftingInput>> getSerializer() {
        return null; // TODO: Hook custom deferred registry object here
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeType<? extends Recipe<CraftingInput>> getType() {
        return null; // TODO: Hook custom deferred recipe type registry object here
    }
}
