package thaumcraft.api.crafting;

import net.minecraft.world.item.ItemStack;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * This is a placeholder data-holder class used exclusively for rendering fake/informational
 * recipes inside the Thaumonomicon. It is not registered to the vanilla recipe manager.
 * * CRITICAL CHANGES FOR 1.20.5+:
 * 1. Never return null for ItemStacks. Missing inputs or outputs MUST default to ItemStack.EMPTY.
 * 2. This class is safe to keep as a standard POJO data structure since it isn't an actual engine injection.
 * * @author Azanor, updated for NeoForge 26.x
 */
public class RecipeMisc {

    public enum MiscRecipeType {
        SMELTING
    }

    private MiscRecipeType type;
    private ItemStack input;
    private ItemStack output;

    public RecipeMisc(ItemStack input, ItemStack output, MiscRecipeType type) {
        this.input = input != null ? input.copy() : ItemStack.EMPTY;
        this.output = output != null ? output.copy() : ItemStack.EMPTY;
        this.type = type;
    }

    /**
     * @return the type
     */
    public MiscRecipeType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(MiscRecipeType type) {
        this.type = type;
    }

    /**
     * @return the input (Guaranteed non-null)
     */
    public ItemStack getInput() {
        return input == null ? ItemStack.EMPTY : input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(ItemStack input) {
        this.input = input != null ? input.copy() : ItemStack.EMPTY;
    }

    /**
     * @return the output (Guaranteed non-null)
     */
    public ItemStack getOutput() {
        return output == null ? ItemStack.EMPTY : output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(ItemStack output) {
        this.output = output != null ? output.copy() : ItemStack.EMPTY;
    }
}
