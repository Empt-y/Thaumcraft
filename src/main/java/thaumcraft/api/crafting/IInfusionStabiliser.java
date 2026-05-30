package thaumcraft.api.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * @author Azanor
 * @deprecated This legacy interface is scheduled to be merged into {@link IInfusionStabiliserExt}.
 * In modern NeoForge 26.x, stabilizer blocks should ideally be evaluated using Block Capabilities.
 * * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * 1. Note the spelling of the method: 'canStabaliseInfusion' (with an 'a'). Do not alter this spelling,
 * as doing so will break legacy references throughout the rest of the ported codebase.
 * 2. Keep this interface as a structural bridge for legacy code blocks.
 */
@Deprecated(since = "1.12.2", forRemoval = false)
public interface IInfusionStabiliser {

    /**
     * Checks if the block at the specified position can stabilize the infusion process.
     * Note: Retains the legacy 1.12.2 method name typo intentionally.
     * * @param world The active world level
     * @param pos The block position being checked
     * @return true if the block acts as a valid stabilizer
     */
    boolean canStabaliseInfusion(Level world, BlockPos pos);
}
