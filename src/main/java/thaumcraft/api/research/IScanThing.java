package thaumcraft.api.research;

import net.minecraft.world.entity.player.Player;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * Base interface for the Thaumometer scanning system in NeoForge 26.x.
 * * CRITICAL TYPE SAFETY RULES FOR MODERN REFRACTORS:
 * 1. The 'Object obj' parameter can safely pass checking blocks or entity instances.
 * 2. When evaluating coordinates, cast to 'net.minecraft.core.BlockPos' instead of reading loose integer arrays.
 * 3. Never return raw null variables from your scanning registries; manage empty configurations safely.
 */
public interface IScanThing {

    /**
     * Checks if the scanned target satisfies the conditions of this scanner implementation.
     */
    boolean checkThing(Player player, Object obj);

    /**
     * @return The text string research node key linked to this scannable item.
     */
    String getResearchKey(Player player, Object object);

    /**
     * Triggers side-effects (such as custom player notifications or progression knowledge awards) upon a successful sweep.
     */
    default void onSuccess(Player player, Object object) {
        // Optional tracking hook fallback
    }
}
