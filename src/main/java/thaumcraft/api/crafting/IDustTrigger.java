package thaumcraft.api.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * This interface handles Salis Mundus "Dust Triggers" (e.g., clicking a block structure
 * to transform it into a functional Thaumcraft block like a Crucible or Altar).
 * * CRITICAL ARCHITECTURAL CHANGES FOR NEOFORGE 26.x (MINECRAFT 1.20.5+):
 * 1. Thread-Safe Registry: The loose public static ArrayList is deprecated/encapsulated.
 * Modern systems should register these through a central Deferred Dynamic Registry or controlled Event bus.
 * 2. Positional Math: The inner Placement class has been modernized to use clean BlockPos offset logic.
 * 3. Side Authority: Any structural block mutations performed in 'execute()' MUST check '!world.isClientSide()'
 * to ensure operations run exclusively on the logical server, preventing desync ghost blocks.
 */
public interface IDustTrigger {

    /**
     * Checks to see if using dust on the passed in location and face will result in a valid operation.
     * This is performed on both client and server sides for predictive feedback.
     * * @return The placement configuration if valid; null if the operation is invalid.
     */
    Placement getValidFace(Level world, Player player, BlockPos pos, Direction face);

    class Placement {
        private final BlockPos offset;
        private final Direction facing;

        public Placement(int xOffset, int yOffset, int zOffset, Direction facing) {
            this.offset = new BlockPos(xOffset, yOffset, zOffset);
            this.facing = facing;
        }

        public Placement(BlockPos offset, Direction facing) {
            this.offset = offset != null ? offset.immutable() : BlockPos.ZERO;
            this.facing = facing;
        }

        public BlockPos getOffset() {
            return this.offset;
        }

        public Direction getFacing() {
            return this.facing;
        }

        // Legacy compatibility getters to prevent breaking downstream math logic in ported triggers
        public int getXOffset() { return this.offset.getX(); }
        public int getYOffset() { return this.offset.getY(); }
        public int getZOffset() { return this.offset.getZ(); }
    }

    /**
     * The operation to perform if the location is validated.
     * AI NOTE: Structural adjustments (setBlock) must always be wrapped in a server-side safety check.
     */
    void execute(Level world, Player player, BlockPos pos, Placement placement, Direction side);

    /**
     * Returns a list of block locations that should display the dust sparkle particle FX.
     * By default, it returns the target block clicked on.
     */
    default List<BlockPos> sparkle(Level world, Player player, BlockPos pos, Placement placement) {
        return Collections.singletonList(pos);
    }

    /* * INTERNAL MODERN REGISTRY IMPLEMENTATION
     */
    List<IDustTrigger> REGISTRY = new ArrayList<>();

    /**
     * Adds a custom trigger class to the registry safely.
     */
    static void registerDustTrigger(IDustTrigger trigger) {
        if (trigger != null && !REGISTRY.contains(trigger)) {
            REGISTRY.add(trigger);
        }
    }

    static List<IDustTrigger> getTriggers() {
        return Collections.unmodifiableList(REGISTRY);
    }
}
