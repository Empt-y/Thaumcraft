package thaumcraft.api;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ToolMaterial;

/**
 * Thaumcraft tool material definitions (MC 1.21.4 ToolMaterial record).
 * ToolMaterial(incorrectBlocksForDrops, durability, speed, attackDmgBonus, enchantability, repairItems)
 */
public class ThaumcraftMaterials {

    public static final ToolMaterial TOOLMAT_THAUMIUM = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL, 500, 7.0f, 2.5f, 22, ItemTags.IRON_TOOL_MATERIALS);

    public static final ToolMaterial TOOLMAT_VOID = new ToolMaterial(
        BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 150, 8.0f, 3.0f, 10, ItemTags.DIAMOND_TOOL_MATERIALS);

    public static final ToolMaterial TOOLMAT_ELEMENTAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL, 1500, 9.0f, 3.0f, 18, ItemTags.IRON_TOOL_MATERIALS);

    // Armor material keys — actual registration deferred to a DeferredRegister setup class
    public static final String ARMORMAT_THAUMIUM_KEY   = "thaumcraft:thaumium";
    public static final String ARMORMAT_SPECIAL_KEY    = "thaumcraft:special";
    public static final String ARMORMAT_VOID_KEY       = "thaumcraft:void";
    public static final String ARMORMAT_VOIDROBE_KEY   = "thaumcraft:voidrobe";
    public static final String ARMORMAT_FORTRESS_KEY   = "thaumcraft:fortress";
    public static final String ARMORMAT_CULTIST_PLATE_KEY  = "thaumcraft:cultist_plate";
    public static final String ARMORMAT_CULTIST_ROBE_KEY   = "thaumcraft:cultist_robe";
    public static final String ARMORMAT_CULTIST_LEADER_KEY = "thaumcraft:cultist_leader";
}
