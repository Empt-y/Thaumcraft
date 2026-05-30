package thaumcraft.api.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import thaumcraft.api.aspects.AspectList;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * This interface establishes the core contract for all Arcane Workbench recipes in NeoForge 26.x.
 * * CRITICAL ARCHITECTURAL COMPLIANCE FOR 1.20.5+:
 * 1. 'IThaumcraftRecipe' already extends 'Recipe<CraftingInput>', making a secondary explicit
 * extends clause here redundant.
 * 2. Vanilla's 'getResultItem' method MANDATORILY requires a 'HolderLookup.Provider' parameter.
 * Failing to include it will cause compilation to break due to unfulfilled interface overrides.
 */
public interface IArcaneRecipe extends IThaumcraftRecipe {

    /**
     * @return The required vis cost required to craft this item in the Arcane Workbench.
     */
    int getVis();

    /**
     * @return The list of elemental crystals required in the workbench's outer slots.
     */
    AspectList getCrystals();

    /**
     * Returns the output ItemStack for display/logic (not via the Recipe interface which removed getResultItem).
     */
    ItemStack getResultItem();

    @Override
    default String getGroup() {
        return "";
    }
}
