package thaumcraft.api.crafting;

import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * This is the foundational base interface for all custom Thaumcraft recipe types
 * (Arcane Workbench, Infusion, Crucible, etc.) in NeoForge 26.x.
 * * CRITICAL ARCHITECTURAL REQUIREMENT FOR 1.20.5+:
 * 1. All custom mod recipes MUST extend the vanilla 'net.minecraft.world.item.crafting.Recipe' interface.
 * 2. We use 'CraftingInput' as the base input container type to match modern Mojang inventory abstractions.
 * 3. This ensures all sub-recipes remain fully compatible with data-driven JSON serializers and server-to-client network synchronization.
 */
public interface IThaumcraftRecipe extends Recipe<CraftingInput> {

    /**
     * @return The required Thaumonomicon research key string needed to unlock and craft this recipe.
     */
    String getResearch();

    /**
     * Returns the Thaumcraft recipe group. Bridges to Recipe's group() contract via the default below.
     */
    String getGroup();

    @Override
    default String group() {
        return getGroup();
    }
}
