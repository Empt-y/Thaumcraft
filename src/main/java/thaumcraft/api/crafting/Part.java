package thaumcraft.api.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * This class defines a structural template cell used by Thaumcraft multiblocks
 * (e.g., Infusion Altar, Infernal Furnace layout parsing).
 * * CRITICAL ARCHITECTURAL CHANGES FOR NEOFORGE 26.x (MINECRAFT 1.20.5+):
 * 1. Vanilla 'Material' has been completely deleted by Mojang. Do not attempt to cast or use it.
 * 2. Type-safety should replace raw loose 'Object' definitions where possible, or use explicit
 * modern checks (Block, BlockState, ItemStack, or Item).
 * 3. Any target that is not a Block, BlockState, or ItemStack should fallback safely to
 * prevent registry state corruption.
 */
public class Part {
    private Object source; // Can be a Block, BlockState, ItemStack, or Item
    private Object target; // Block, BlockState, or ItemStack. Anything else implies air/removal.
    private boolean opp;
    private int priority;
    private boolean applyPlayerFacing;

    public Part(Object source, Object target, boolean opp, int priority) {
        this.setSource(source);
        this.setTarget(target);
        this.setOpp(opp);
        this.setPriority(priority);
    }

    public Part(Object source, Object target, boolean opp) {
        this(source, target, opp, 50);
    }

    public Part(Object source, Object target) {
        this(source, target, false, 50);
    }

    public Object getSource() {
        return source;
    }

    /**
     * AI NOTE: Ensure input sources match modern 1.20.5 data-driven equivalents.
     * ItemStacks should be defensive copies to prevent component mutation leaks.
     */
    public void setSource(Object source) {
        if (source instanceof ItemStack stack) {
            this.source = stack.copy();
        } else {
            this.source = source;
        }
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        if (target instanceof ItemStack stack) {
            this.target = stack.copy();
        } else {
            this.target = target;
        }
    }

    public boolean isOpp() {
        return opp;
    }

    public void setOpp(boolean opp) {
        this.opp = opp;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean getApplyPlayerFacing() {
        return applyPlayerFacing;
    }

    public Part setApplyPlayerFacing(boolean applyFacing) {
        this.applyPlayerFacing = applyFacing;
        return this;
    }
}
