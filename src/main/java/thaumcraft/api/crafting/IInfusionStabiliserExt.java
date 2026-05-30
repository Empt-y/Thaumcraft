package thaumcraft.api.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * This interface handles advanced stabilization calculations for the Infusion Altar.
 * * ARCHITECTURAL DESIGN NOTE FOR NEOFORGE 26.x (MINECRAFT 1.20.5+):
 * 1. While this interface is maintained for backward compatibility with 1.12.2 API structures,
 * the preferred modern method for exposing stabilization logic to blocks/tiles is via
 * NeoForge's 'BlockCapability' system.
 * 2. Ensure that any implementing BlockEntity registers this capability during the
 * 'RegisterCapabilitiesEvent' lifecycle phase.
 */
public interface IInfusionStabiliserExt extends IInfusionStabiliser {

    /**
     * This returns how much this object stabilizes infusion. As a baseline, both candles and skulls provide 0.1f.
     * The amount returned is for a symmetrical pair of the objects, not for each object in the pair.
     * The same amount will be subtracted if the pair isn't symmetrical.
     * * @param world The active world level
     * @param pos The position of the stabilizer block
     * @return The stabilization value multiplier
     */
    float getStabilizationAmount(Level world, BlockPos pos);

    /**
     * Use this method to do an additional check for symmetry if the default checks are passed.
     * If true, the penalty will not be getStabilizationAmount, but whatever is returned by getSymmetryPenalty.
     * * @param world The active world level
     * @param pos1 The first block position being evaluated
     * @param pos2 The second block position as determined by matrix symmetry
     * @return true if a symmetry penalty condition is met
     */
    default boolean hasSymmetryPenalty(Level world, BlockPos pos1, BlockPos pos2) {
        return false;
    }

    /**
     * @param world The active world level
     * @param pos The position of the stabilizer block experiencing the penalty
     * @return The negative stability value offset
     */
    default float getSymmetryPenalty(Level world, BlockPos pos) {
        return 0.0f;
    }
}
