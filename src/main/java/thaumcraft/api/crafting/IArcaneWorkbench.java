package thaumcraft.api.crafting;

import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Marker + contract interface for any RecipeInput that comes from the Arcane Workbench.
 * Extends RecipeInput so implementations can fulfill both this interface and the CraftingInput
 * wrapper pattern used by ShapedArcaneRecipe / ShapelessArcaneRecipe.
 * NOTE: CraftingInput is a final concrete class with a private constructor and cannot be
 * extended. Workbench inventory wrappers must implement this interface and provide a
 * CraftingInput via a delegate.
 */
public interface IArcaneWorkbench extends RecipeInput {
}
