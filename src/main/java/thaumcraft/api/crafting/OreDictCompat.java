package thaumcraft.api.crafting;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;

/**
 * Maps 1.12 Forge ore-dict keys to modern Ingredient instances.
 * All lookups are intentionally lazy (called at recipe-match time, never at static init),
 * so ItemsTC and BlocksTC references are always resolved after registration.
 */
public final class OreDictCompat {

    public static Ingredient fromOreDict(String key) {
        switch (key) {
            // Vanilla metals
            case "ingotIron":      return Ingredient.of(Items.IRON_INGOT);
            case "ingotGold":      return Ingredient.of(Items.GOLD_INGOT);
            case "ingotCopper":    return Ingredient.of(Items.COPPER_INGOT);
            case "nuggetIron":     return Ingredient.of(Items.IRON_NUGGET);
            case "nuggetGold":     return Ingredient.of(Items.GOLD_NUGGET);
            // Vanilla gems / misc
            case "gemDiamond":     return Ingredient.of(Items.DIAMOND);
            case "gemQuartz":      return Ingredient.of(Items.QUARTZ);
            case "gemEmerald":     return Ingredient.of(Items.EMERALD);
            case "leather":        return Ingredient.of(Items.LEATHER);
            case "string":         return Ingredient.of(Items.STRING);
            case "stickWood":      return Ingredient.of(Items.STICK);
            case "stone":          return Ingredient.of(Items.STONE);
            case "cobblestone":    return Ingredient.of(Items.COBBLESTONE);
            case "paneGlass":      return Ingredient.of(Items.GLASS_PANE);
            case "blockGlass":     return Ingredient.of(Items.GLASS);
            case "slabWood":       return Ingredient.of(Items.OAK_SLAB);
            case "dyeRed":         return Ingredient.of(Items.RED_DYE);
            case "dyeBlue":        return Ingredient.of(Items.BLUE_DYE);
            case "dyeGreen":       return Ingredient.of(Items.GREEN_DYE);
            case "dyeBlack":       return Ingredient.of(Items.BLACK_DYE);
            case "dyeWhite":       return Ingredient.of(Items.WHITE_DYE);
            case "dyeYellow":      return Ingredient.of(Items.YELLOW_DYE);
            case "paper":          return Ingredient.of(Items.PAPER);
            case "feather":        return Ingredient.of(Items.FEATHER);
            case "obsidian":       return Ingredient.of(Items.OBSIDIAN);
            case "gunpowder":      return Ingredient.of(Items.GUNPOWDER);
            case "oreIron":        return Ingredient.of(Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE);
            case "oreGold":        return Ingredient.of(Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE);
            case "oreCinnabar":    return safe(() -> Ingredient.of(BlocksTC.oreCinnabar.asItem()));
            // TC-specific items — single plate/ingot/nugget item covers all metal variants
            case "ingotBrass":
            case "ingotThaumium":
            case "ingotVoid":      return safe(() -> Ingredient.of(ItemsTC.ingots));
            case "plateBrass":
            case "plateIron":
            case "plateThaumium":
            case "plateVoid":      return safe(() -> Ingredient.of(ItemsTC.plate));
            case "nuggetBrass":    return safe(() -> Ingredient.of(ItemsTC.nuggets));
            case "nuggetQuicksilver": return safe(() -> Ingredient.of(ItemsTC.quicksilver));
            case "nitor":          return safe(() -> Ingredient.of(
                                       BlocksTC.nitor.values().iterator().next().asItem()));
            default:
                thaumcraft.Thaumcraft.log.warn("OreDictCompat: unmapped ore dict key '{}' — treating as empty", key);
                return Ingredient.of(Items.AIR);
        }
    }

    @FunctionalInterface
    private interface IngSupplier { Ingredient get(); }

    private static Ingredient safe(IngSupplier s) {
        try { return s.get(); }
        catch (Exception e) {
            thaumcraft.Thaumcraft.log.warn("OreDictCompat: failed to resolve ingredient — {}", e.getMessage());
            return Ingredient.of(Items.AIR);
        }
    }
}
