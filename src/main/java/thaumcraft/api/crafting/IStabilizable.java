package thaumcraft.api.crafting;

/**
 * @author Azanor
 * @deprecated This legacy stability interface is scheduled for complete removal.
 * In modern versions, stability mechanics should be handled dynamically via block capability
 * attachments or data-driven block tags rather than strict interface pooling.
 * * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * Retain this interface strictly as a compilation bridge for legacy 1.12.2 Infusion Altar references.
 * Do not implement this interface on modern active TileEntities/BlockEntities.
 */
@Deprecated(since = "1.12.2", forRemoval = true)
public interface IStabilizable {

    @Deprecated
    void addStability();

    @Deprecated
    EnumStability getStability();

    @Deprecated
    enum EnumStability {
        VERY_STABLE,
        STABLE,
        UNSTABLE,
        VERY_UNSTABLE
    }
}
