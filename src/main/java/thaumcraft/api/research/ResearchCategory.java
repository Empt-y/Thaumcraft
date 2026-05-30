package thaumcraft.api.research;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * Holds specific category tab definitions inside the Thaumonomicon system.
 * * UPDATES FOR 26.x:
 * 1. Cleaned up missing field definitions (such as adding the underlying 'research' tracking map).
 * 2. Standardized resource variables to utilize 'Identifier'.
 */
public class ResearchCategory {

    public int minDisplayColumn;
    public int minDisplayRow;
    public int maxDisplayColumn;
    public int maxDisplayRow;

    public Identifier icon;
    public Identifier background;
    public Identifier background2;

    public String researchKey;
    public String key;
    public AspectList formula;

    // AI NOTE: Added missing map registration target to satisfy reference sweeps in ResearchCategories
    public Map<String, ResearchEntry> research = new HashMap<>();

    public ResearchCategory(String key, String researchkey, AspectList formula, Identifier icon, Identifier background) {
        this.key = key;
        this.researchKey = researchkey;
        this.icon = icon;
        this.background = background;
        this.background2 = null;
        this.formula = formula;
    }

    public ResearchCategory(String key, String researchKey, AspectList formula, Identifier icon, Identifier background, Identifier background2) {
        this.key = key;
        this.researchKey = researchKey;
        this.icon = icon;
        this.background = background;
        this.background2 = background2;
        this.formula = formula;
    }

    public int applyFormula(AspectList as) {
        return applyFormula(as, 1.0D);
    }

    public int applyFormula(AspectList as, double mod) {
        if (formula == null) return 0;
        double total = 0;
        for (Aspect aspect : formula.getAspects()) {
            total += (mod * mod) * as.getAmount(aspect) * ((double) formula.getAmount(aspect) / 10.0D);
        }
        return (int) Math.round(total);
    }
}
