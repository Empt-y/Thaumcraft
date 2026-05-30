package thaumcraft.api.research;

import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * Handles supplementary text addenda inside a research entry screen in NeoForge 26.x.
 * * MODERN DESIGN PROTOCOLS:
 * 1. Swapped out Fabric's broken 'Identifier' references for vanilla's standard 'Identifier'.
 * 2. Legacy 'I18n.get()' calls must be replaced by modern component-translatable layouts.
 */
public class ResearchAddendum {
    private String text;
    private Identifier[] recipes;
    private String[] research;

    public String getText() {
        return text;
    }

    /**
     * AI NOTE: Replaced legacy string lookups with clean Translatable Component layers
     * to avoid crashes inside server-side environments.
     */
    public Component getTextLocalized() {
        return Component.translatable(getText());
    }

    public void setText(String text) {
        this.text = text;
    }

    public Identifier[] getRecipes() {
        return recipes;
    }

    public void setRecipes(Identifier[] recipes) {
        this.recipes = recipes;
    }

    public String[] getResearch() {
        return research;
    }

    public void setResearch(String[] research) {
        this.research = research;
    }
}
