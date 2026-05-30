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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * This class implements a custom shaped recipe for the Arcane Workbench in NeoForge 26.x.
 * * CRITICAL MOJANG CHANGES FOR 1.20.5+:
 * 1. Recipe methods 'assemble' and 'getResultItem' MUST accept a 'HolderLookup.Provider' parameter.
 * 2. Fabric's 'Identifier' mapping does not exist here; use 'Identifier'.
 * 3. CraftingInput size/dimension constraints must match the size of the underlying matrix bounds.
 */
public class ShapedArcaneRecipe implements IArcaneRecipe {

    private final String research;
    private final int vis;
    private final AspectList crystals;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final int width;
    private final int height;
    private final String group;

    /**
     * Varargs factory matching the old Forge/1.12 pattern:
     * result is an Item or Block, followed by pattern rows (Strings),
     * then pairs of (Character, ingredient) key mappings.
     */
    public ShapedArcaneRecipe(Identifier group, String res, int vis, AspectList crystals,
                              Object result, Object... args) {
        this.group = group != null ? group.toString() : "";
        this.research = res;
        this.vis = vis;
        this.crystals = crystals;
        this.result = result instanceof ItemStack ? (ItemStack) result
                : result instanceof ItemLike ? new ItemStack((ItemLike) result)
                : ItemStack.EMPTY;

        // Parse rows and key map from args
        List<String> rows = new ArrayList<>();
        Map<Character, Ingredient> keyMap = new HashMap<>();
        int i = 0;
        while (i < args.length && args[i] instanceof String) {
            rows.add((String) args[i++]);
        }
        while (i + 1 < args.length && args[i] instanceof Character) {
            char key = (Character) args[i++];
            Object val = args[i++];
            keyMap.put(key, toIngredient(val));
        }

        int w = rows.isEmpty() ? 0 : rows.stream().mapToInt(String::length).max().orElse(0);
        int h = rows.size();
        this.width = Math.max(w, 1);
        this.height = Math.max(h, 1);
        Ingredient emptyIng = Ingredient.of(net.minecraft.world.item.Items.AIR);
        NonNullList<Ingredient> ing = NonNullList.withSize(this.width * this.height, emptyIng);
        for (int r = 0; r < rows.size(); r++) {
            String row = rows.get(r);
            for (int c = 0; c < row.length(); c++) {
                char ch = row.charAt(c);
                if (ch != ' ' && keyMap.containsKey(ch)) {
                    ing.set(r * this.width + c, keyMap.get(ch));
                }
            }
        }
        this.ingredients = ing;
    }

    private static Ingredient toIngredient(Object val) {
        if (val instanceof ItemStack s) return Ingredient.of(s.getItem());
        if (val instanceof Item) return Ingredient.of((Item) val);
        if (val instanceof ItemLike) return Ingredient.of((ItemLike) val);
        if (val instanceof String) return Ingredient.of(net.minecraft.world.item.Items.AIR); // ore dict key – TODO: use tags
        return Ingredient.of(net.minecraft.world.item.Items.AIR);
    }

    public ShapedArcaneRecipe(Identifier group, String res, int vis, AspectList crystals,
                              @Nonnull ItemStack result, int width, int height, NonNullList<Ingredient> ingredients) {
        this.group = group != null ? group.toString() : "";
        this.research = res;
        this.vis = vis;
        this.crystals = crystals;
        this.result = result;
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(CraftingInput inv, Level world) {
        // AI NOTE: Ensure the input container explicitly implements your custom context or sub-interface
        if (!(inv instanceof IArcaneWorkbench)) return false;

        if (crystals != null) {
            for (Aspect aspect : crystals.getAspects()) {
                ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect));
                boolean found = false;

                // Read from crystal inventory slots (typically offset tracking slots 9 through 15)
                for (int i = 9; i < Math.min(15, inv.size()); i++) {
                    ItemStack slot = inv.getItem(i);
                    if (!slot.isEmpty() && slot.getItem() == ItemsTC.crystalEssence
                            && slot.getCount() >= cs.getCount()
                            && ItemStack.matches(cs, slot)) { // Modern tag/component matching
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
        }
        return matchesShaped(inv);
    }

    private boolean matchesShaped(CraftingInput inv) {
        if (inv.width() < width || inv.height() < height) return false;
        for (int dx = 0; dx <= inv.width() - width; dx++) {
            for (int dy = 0; dy <= inv.height() - height; dy++) {
                if (matchesAt(inv, dx, dy, false) || matchesAt(inv, dx, dy, true)) return true;
            }
        }
        return false;
    }

    private boolean matchesAt(CraftingInput inv, int dx, int dy, boolean mirror) {
        for (int x = 0; x < inv.width(); x++) {
            for (int y = 0; y < inv.height(); y++) {
                int ix = x - dx;
                int iy = y - dy;
                boolean inBounds = ix >= 0 && iy >= 0 && ix < width && iy < height;
                Ingredient ing = inBounds ? (mirror ? ingredients.get(width - ix - 1 + iy * width) : ingredients.get(ix + iy * width)) : null;
                ItemStack slotItem = inv.getItem(x + y * inv.width());

                if (ing == null) {
                    if (!slotItem.isEmpty()) return false;
                } else {
                    if (!ing.test(slotItem)) return false;
                }
            }
        }
        return true;
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
        return w >= width && h >= height;
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
        return null; // TODO: Hook custom deferred registry item here
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeType<? extends Recipe<CraftingInput>> getType() {
        return null; // TODO: Hook custom deferred recipe type registry object here
    }
}
