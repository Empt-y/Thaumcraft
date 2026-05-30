package thaumcraft.api.research;

import java.util.Collection;
import java.util.LinkedHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import thaumcraft.api.aspects.AspectList;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * Central map registry storing category tabs for the Thaumonomicon in NeoForge 26.x.
 * * CLEANUP AND MODERNIZATION:
 * 1. Replaced Fabric 'Identifier' parameters with 'Identifier'.
 * 2. Migrated loose Collections into type-safe parameterized Generics.
 * 3. Replaced dangerous client-only 'I18n' calls with 'Component.translatable'.
 */
public class ResearchCategories {

    public static LinkedHashMap<String, ResearchCategory> researchCategories = new LinkedHashMap<>();

    public static ResearchCategory getResearchCategory(String key) {
        return researchCategories.get(key);
    }

    /**
     * AI NOTE: Returns a Component layer to ensure server-safety during data-sync tasks.
     */
    public static Component getCategoryName(String key) {
        return Component.translatable("tc.research_category." + key);
    }

    public static ResearchEntry getResearch(String key) {
        Collection<ResearchCategory> rc = researchCategories.values();
        for (ResearchCategory cat : rc) {
            Collection<ResearchEntry> rl = cat.research.values();
            for (ResearchEntry ri : rl) {
                if (ri.key.equals(key)) return ri;
            }
        }
        return null;
    }

    public static ResearchCategory registerCategory(String key, String researchkey, AspectList formula, Identifier icon, Identifier background) {
        if (getResearchCategory(key) == null) {
            ResearchCategory rl = new ResearchCategory(key, researchkey, formula, icon, background);
            researchCategories.put(key, rl);
            return rl;
        }
        return null;
    }

    public static ResearchCategory registerCategory(String key, String researchkey, AspectList formula, Identifier icon, Identifier background, Identifier background2) {
        if (getResearchCategory(key) == null) {
            ResearchCategory rl = new ResearchCategory(key, researchkey, formula, icon, background, background2);
            researchCategories.put(key, rl);
            return rl;
        }
        return null;
    }
}
