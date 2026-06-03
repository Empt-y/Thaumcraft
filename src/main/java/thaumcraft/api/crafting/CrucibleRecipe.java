package thaumcraft.api.crafting;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * This class implements custom Crucible recipes for liquid-aspect transmutation in NeoForge 26.x.
 * * CRITICAL REFACTORING GUIDELINES FOR 1.20.5+:
 * 1. Swap Fabric's 'Identifier' with 'Identifier'.
 * 2. Never call 'ItemStack.toString()' inside 'hashCode()' generation routines, as modern component-maps
 * mutate dynamically. Parse the item's static Registry ID path string instead.
 * 3. Because 'IThaumcraftRecipe' inherits from 'Recipe<CraftingInput>', we must provide stubs for
 * vanilla methods ('assemble', 'getResultItem', 'canCraftInDimensions').
 */
public class CrucibleRecipe implements IThaumcraftRecipe {

    public static final CrucibleRecipe EMPTY = new CrucibleRecipe(
            "", ItemStack.EMPTY, net.minecraft.world.item.Items.STONE, new AspectList());

    private final ItemStack recipeOutput;
    private Ingredient catalyst;
    private AspectList aspects;
    private final String research;
    private final String name;
    public int hash;
    private String group = "";

    public CrucibleRecipe(String researchKey, ItemStack result, Object catalyst, AspectList tags) {
        this.recipeOutput = result != null ? result.copy() : ItemStack.EMPTY;
        this.name = "";
        this.setAspects(tags);
        this.research = researchKey;
        this.setCatalyst(ThaumcraftApiHelper.getIngredient(catalyst));

        if (this.getCatalyst() == null) {
            throw new RuntimeException("Invalid crucible recipe catalyst: " + catalyst);
        }

        generateHash();
    }

    private void generateHash() {
        String hc = research;
        if (!recipeOutput.isEmpty()) {
            // AI NOTE: Use static registry paths instead of dynamic ItemStack components for reliable hash calculation
            hc += BuiltInRegistries.ITEM.getKey(recipeOutput.getItem()).toString();
        }
        hash = hc.hashCode();
    }

    /**
     * Logic-check used by the Crucible TileEntity block when an item is tossed into the water.
     */
    public boolean matches(AspectList itags, ItemStack cat) {
        if (!getCatalyst().test(cat)) return false;
        if (itags == null) return false;
        for (Aspect tag : getAspects().getAspects()) {
            if (itags.getAmount(tag) < getAspects().getAmount(tag)) return false;
        }
        return true;
    }

    public boolean catalystMatches(ItemStack cat) {
        return getCatalyst().test(cat);
    }

    public AspectList removeMatching(AspectList itags) {
        AspectList temptags = new AspectList();
        temptags.aspects.putAll(itags.aspects);
        for (Aspect tag : getAspects().getAspects()) {
            temptags.remove(tag, getAspects().getAmount(tag));
        }
        return temptags;
    }

    public ItemStack getRecipeOutput() {
        return recipeOutput;
    }

    @Override
    public String getResearch() {
        return research;
    }

    public Ingredient getCatalyst() {
        return catalyst;
    }

    public void setCatalyst(Ingredient catalyst) {
        this.catalyst = catalyst;
    }

    public AspectList getAspects() {
        return aspects;
    }

    public void setAspects(AspectList aspects) {
        this.aspects = aspects;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public CrucibleRecipe setGroup(Identifier s) {
        this.group = s != null ? s.toString() : "";
        return this;
    }

    // --- MANDATORY OVERRIDES FOR VANILLA RECIPE COMPATIBILITY ---

    @Override
    public boolean matches(CraftingInput inv, Level world) {
        // Crucible recipes are processed in-world by the BlockEntity, not via inventory grids.
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput inv) {
        return recipeOutput.copy();
    }

    public ItemStack getResultItem() {
        return recipeOutput;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeSerializer<? extends Recipe<CraftingInput>> getSerializer() {
        return thaumcraft.common.config.TCRecipes.CRUCIBLE_SERIALIZER.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeType<? extends Recipe<CraftingInput>> getType() {
        return thaumcraft.common.config.TCRecipes.CRUCIBLE_TYPE.get();
    }
}
