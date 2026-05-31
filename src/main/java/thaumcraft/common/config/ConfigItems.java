package thaumcraft.common.config;
import net.minecraft.world.entity.EquipmentSlot;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.Identifier;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import thaumcraft.api.OreDictionaryEntries;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.construct.ItemTurretPlacer;
import thaumcraft.common.golems.ItemGolemBell;
import thaumcraft.common.golems.ItemGolemPlacer;
import thaumcraft.common.golems.seals.ItemSealPlacer;
import thaumcraft.common.golems.seals.SealBreaker;
import thaumcraft.common.golems.seals.SealBreakerAdvanced;
import thaumcraft.common.golems.seals.SealButcher;
import thaumcraft.common.golems.seals.SealEmpty;
import thaumcraft.common.golems.seals.SealEmptyAdvanced;
import thaumcraft.common.golems.seals.SealFill;
import thaumcraft.common.golems.seals.SealFillAdvanced;
import thaumcraft.common.golems.seals.SealGuard;
import thaumcraft.common.golems.seals.SealGuardAdvanced;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.seals.SealHarvest;
import thaumcraft.common.golems.seals.SealLumber;
import thaumcraft.common.golems.seals.SealPickup;
import thaumcraft.common.golems.seals.SealPickupAdvanced;
import thaumcraft.common.golems.seals.SealProvide;
import thaumcraft.common.golems.seals.SealStock;
import thaumcraft.common.golems.seals.SealUse;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.items.armor.ItemBootsTraveller;
import thaumcraft.common.items.armor.ItemCultistBoots;
import thaumcraft.common.items.armor.ItemCultistLeaderArmor;
import thaumcraft.common.items.armor.ItemCultistPlateArmor;
import thaumcraft.common.items.armor.ItemCultistRobeArmor;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.items.armor.ItemGoggles;
import thaumcraft.common.items.armor.ItemRobeArmor;
import thaumcraft.common.items.armor.ItemThaumiumArmor;
import thaumcraft.common.items.armor.ItemVoidArmor;
import thaumcraft.common.items.armor.ItemVoidRobeArmor;
import thaumcraft.common.items.baubles.ItemAmuletVis;
import thaumcraft.common.items.baubles.ItemBaubles;
import thaumcraft.common.items.baubles.ItemCharmUndying;
import thaumcraft.common.items.baubles.ItemCloudRing;
import thaumcraft.common.items.baubles.ItemCuriosityBand;
import thaumcraft.common.items.baubles.ItemVerdantCharm;
import thaumcraft.common.items.baubles.ItemVoidseerCharm;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.ItemFocusPouch;
import thaumcraft.common.items.casters.foci.FocusEffectAir;
import thaumcraft.common.items.casters.foci.FocusEffectBreak;
import thaumcraft.common.items.casters.foci.FocusEffectCurse;
import thaumcraft.common.items.casters.foci.FocusEffectEarth;
import thaumcraft.common.items.casters.foci.FocusEffectExchange;
import thaumcraft.common.items.casters.foci.FocusEffectFire;
import thaumcraft.common.items.casters.foci.FocusEffectFlux;
import thaumcraft.common.items.casters.foci.FocusEffectFrost;
import thaumcraft.common.items.casters.foci.FocusEffectHeal;
import thaumcraft.common.items.casters.foci.FocusEffectRift;
import thaumcraft.common.items.casters.foci.FocusMediumBolt;
import thaumcraft.common.items.casters.foci.FocusMediumCloud;
import thaumcraft.common.items.casters.foci.FocusMediumMine;
import thaumcraft.common.items.casters.foci.FocusMediumPlan;
import thaumcraft.common.items.casters.foci.FocusMediumProjectile;
import thaumcraft.common.items.casters.foci.FocusMediumSpellBat;
import thaumcraft.common.items.casters.foci.FocusMediumTouch;
import thaumcraft.common.items.casters.foci.FocusModScatter;
import thaumcraft.common.items.casters.foci.FocusModSplitTarget;
import thaumcraft.common.items.casters.foci.FocusModSplitTrajectory;
import thaumcraft.common.items.consumables.ItemAlumentum;
import thaumcraft.common.items.consumables.ItemBathSalts;
import thaumcraft.common.items.consumables.ItemBottleTaint;
import thaumcraft.common.items.consumables.ItemCausalityCollapser;
import thaumcraft.common.items.consumables.ItemChunksEdible;
import thaumcraft.common.items.consumables.ItemLabel;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.items.consumables.ItemSanitySoap;
import thaumcraft.common.items.consumables.ItemTripleMeatTreat;
import thaumcraft.common.items.consumables.ItemZombieBrain;
import thaumcraft.common.items.curios.ItemCelestialNotes;
import thaumcraft.common.items.curios.ItemCurio;
import thaumcraft.common.items.curios.ItemEnchantmentPlaceholder;
import thaumcraft.common.items.curios.ItemLootBag;
import thaumcraft.common.items.curios.ItemPechWand;
import thaumcraft.common.items.curios.ItemPrimordialPearl;
import thaumcraft.common.items.curios.ItemThaumonomicon;
import thaumcraft.common.items.misc.ItemCreativeFluxSponge;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import thaumcraft.common.items.resources.ItemMagicDust;
import thaumcraft.common.items.tools.ItemCrimsonBlade;
import thaumcraft.common.items.tools.ItemElementalAxe;
import thaumcraft.common.items.tools.ItemElementalHoe;
import thaumcraft.common.items.tools.ItemElementalPickaxe;
import thaumcraft.common.items.tools.ItemElementalShovel;
import thaumcraft.common.items.tools.ItemElementalSword;
import thaumcraft.common.items.tools.ItemGrappleGun;
import thaumcraft.common.items.tools.ItemHandMirror;
import thaumcraft.common.items.tools.ItemPrimalCrusher;
import thaumcraft.common.items.tools.ItemResonator;
import thaumcraft.common.items.tools.ItemSanityChecker;
import thaumcraft.common.items.tools.ItemScribingTools;
import thaumcraft.common.items.tools.ItemThaumiumAxe;
import thaumcraft.common.items.tools.ItemThaumiumHoe;
import thaumcraft.common.items.tools.ItemThaumiumPickaxe;
import thaumcraft.common.items.tools.ItemThaumiumShovel;
import thaumcraft.common.items.tools.ItemThaumiumSword;
import thaumcraft.common.items.tools.ItemThaumometer;
import thaumcraft.common.items.tools.ItemVoidAxe;
import thaumcraft.common.items.tools.ItemVoidHoe;
import thaumcraft.common.items.tools.ItemVoidPickaxe;
import thaumcraft.common.items.tools.ItemVoidShovel;
import thaumcraft.common.items.tools.ItemVoidSword;
import thaumcraft.common.lib.CreativeTabThaumcraft;


public class ConfigItems
{
    public static ItemStack startBook;
    public static CreativeModeTab TABTC;
    public static List<IThaumcraftItems> ITEM_VARIANT_HOLDERS;
    public static ItemStack AIR_CRYSTAL;
    public static ItemStack FIRE_CRYSTAL;
    public static ItemStack WATER_CRYSTAL;
    public static ItemStack EARTH_CRYSTAL;
    public static ItemStack ORDER_CRYSTAL;
    public static ItemStack ENTROPY_CRYSTAL;
    public static ItemStack FLUX_CRYSTAL;
    
    public static void initMisc() {
        // FIXME: OreDictionaryEntries - uses old Forge ore dictionary, port to tags
        ConfigItems.AIR_CRYSTAL = ThaumcraftApiHelper.makeCrystal(Aspect.AIR);
        ConfigItems.FIRE_CRYSTAL = ThaumcraftApiHelper.makeCrystal(Aspect.FIRE);
        ConfigItems.WATER_CRYSTAL = ThaumcraftApiHelper.makeCrystal(Aspect.WATER);
        ConfigItems.EARTH_CRYSTAL = ThaumcraftApiHelper.makeCrystal(Aspect.EARTH);
        ConfigItems.ORDER_CRYSTAL = ThaumcraftApiHelper.makeCrystal(Aspect.ORDER);
        ConfigItems.ENTROPY_CRYSTAL = ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY);
        ConfigItems.FLUX_CRYSTAL = ThaumcraftApiHelper.makeCrystal(Aspect.FLUX);
        CompoundTag contents = new CompoundTag();
        contents.putInt("generation", 3);
        contents.putString("title", I18n.get("book.start.title"));
        ListTag pages = new ListTag();
        pages.add(new StringTag(I18n.get("book.start.1")));
        pages.add(new StringTag(I18n.get("book.start.2")));
        pages.add(new StringTag(I18n.get("book.start.3")));
        contents.put("pages", pages);
        // FIXME: ItemStack.put(CompoundTag) removed; use startBook.set(DataComponents.CUSTOM_DATA, CustomData.of(contents));
    }
    
    private static Item ri(String name, java.util.function.Supplier<Item> factory) {
        return ri(name, null, factory);
    }

    private static Item ri(String name, EquipmentSlot slot, java.util.function.Supplier<Item> factory) {
        return ri(name, slot, null, factory);
    }

    private static Item ri(String name, EquipmentSlot slot, String assetName, java.util.function.Supplier<Item> factory) {
        net.minecraft.resources.Identifier rl = net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", name);
        net.minecraft.resources.ResourceKey<net.minecraft.world.item.Item> key =
            net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, rl);
        net.minecraft.world.item.Item.Properties props = new net.minecraft.world.item.Item.Properties().setId(key);
        if (slot != null) {
            net.minecraft.world.item.equipment.Equippable.Builder eb =
                net.minecraft.world.item.equipment.Equippable.builder(slot);
            if (assetName != null) {
                eb.setAsset(net.minecraft.resources.ResourceKey.create(
                    net.minecraft.world.item.equipment.EquipmentAssets.ROOT_ID,
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", assetName)));
            }
            props = props.component(net.minecraft.core.component.DataComponents.EQUIPPABLE, eb.build());
        }
        TCItemInit.set(props);
        Item item = factory.get();
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.ITEM, rl, item);
        return item;
    }

    public static void initItems() {
        ItemsTC.thaumonomicon    = ri("thaumonomicon",     () -> new thaumcraft.common.items.curios.ItemThaumonomicon());
        ItemsTC.curio            = ri("curio", () -> new thaumcraft.common.items.curios.ItemCurio());
        ItemsTC.lootBag          = ri("loot_bag", () -> new thaumcraft.common.items.curios.ItemLootBag());
        ItemsTC.primordialPearl  = ri("primordial_pearl", () -> new thaumcraft.common.items.curios.ItemPrimordialPearl());
        ItemsTC.pechWand         = ri("pech_wand", () -> new thaumcraft.common.items.curios.ItemPechWand());
        ItemsTC.celestialNotes   = ri("celestial_notes", () -> new thaumcraft.common.items.curios.ItemCelestialNotes());
        ItemsTC.amber            = ri("amber", () -> new ItemTCBase("amber"));
        ItemsTC.quicksilver      = ri("quicksilver", () -> new ItemTCBase("quicksilver"));
        ItemsTC.ingots           = ri("ingot", () -> new ItemTCBase("ingot", "thaumium", "void", "brass"));
        ItemsTC.nuggets          = ri("nugget", () -> new ItemTCBase("nugget", "iron", "copper", "tin", "silver", "lead", "quicksilver", "thaumium", "void", "brass", "quartz", "rareearth"));
        ItemsTC.clusters         = ri("cluster", () -> new ItemTCBase("cluster", "iron", "gold", "copper", "tin", "silver", "lead", "cinnabar", "quartz"));
        ItemsTC.fabric           = ri("fabric", () -> new ItemTCBase("fabric"));
        ItemsTC.visResonator     = ri("vis_resonator", () -> new ItemTCBase("vis_resonator"));
        ItemsTC.tallow           = ri("tallow", () -> new ItemTCBase("tallow"));
        ItemsTC.mechanismSimple  = ri("mechanism_simple", () -> new ItemTCBase("mechanism_simple"));
        ItemsTC.mechanismComplex = ri("mechanism_complex", () -> new ItemTCBase("mechanism_complex"));
        ItemsTC.plate            = ri("plate", () -> new ItemTCBase("plate", "brass", "iron", "thaumium", "void"));
        ItemsTC.filter           = ri("filter", () -> new ItemTCBase("filter"));
        ItemsTC.morphicResonator = ri("morphic_resonator", () -> new ItemTCBase("morphic_resonator"));
        ItemsTC.salisMundus      = ri("salis_mundus", () -> new thaumcraft.common.items.resources.ItemMagicDust());
        ItemsTC.mirroredGlass    = ri("mirrored_glass", () -> new ItemTCBase("mirrored_glass"));
        ItemsTC.voidSeed         = ri("void_seed", () -> new ItemTCBase("void_seed"));
        ItemsTC.mind             = ri("mind", () -> new ItemTCBase("mind", "clockwork", "biothaumic"));
        ItemsTC.modules          = ri("module", () -> new ItemTCBase("module", "vision", "aggression"));
        ItemsTC.crystalEssence   = ri("crystal_essence", () -> new thaumcraft.common.items.resources.ItemCrystalEssence());
        ItemsTC.chunks           = ri("chunk", () -> new thaumcraft.common.items.consumables.ItemChunksEdible());
        ItemsTC.tripleMeatTreat  = ri("triple_meat_treat", () -> new thaumcraft.common.items.consumables.ItemTripleMeatTreat());
        ItemsTC.brain            = ri("brain", () -> new thaumcraft.common.items.consumables.ItemZombieBrain());
        ItemsTC.label            = ri("label", () -> new thaumcraft.common.items.consumables.ItemLabel());
        ItemsTC.phial            = ri("phial", () -> new thaumcraft.common.items.consumables.ItemPhial());
        ItemsTC.alumentum        = ri("alumentum", () -> new thaumcraft.common.items.consumables.ItemAlumentum());
        ItemsTC.jarBrace         = ri("jar_brace", () -> new ItemTCBase("jar_brace"));
        ItemsTC.bottleTaint      = ri("bottle_taint", () -> new thaumcraft.common.items.consumables.ItemBottleTaint());
        ItemsTC.sanitySoap       = ri("sanity_soap", () -> new thaumcraft.common.items.consumables.ItemSanitySoap());
        ItemsTC.bathSalts        = ri("bath_salts", () -> new thaumcraft.common.items.consumables.ItemBathSalts());
        ItemsTC.turretPlacer     = ri("turret", () -> new thaumcraft.common.entities.construct.ItemTurretPlacer());
        ItemsTC.causalityCollapser = ri("causality_collapser", () -> new thaumcraft.common.items.consumables.ItemCausalityCollapser());
        ItemsTC.scribingTools    = ri("scribing_tools", () -> new thaumcraft.common.items.tools.ItemScribingTools());
        ItemsTC.thaumometer      = ri("thaumometer", () -> new thaumcraft.common.items.tools.ItemThaumometer());
        ItemsTC.resonator        = ri("resonator", () -> new thaumcraft.common.items.tools.ItemResonator());
        ItemsTC.sanityChecker    = ri("sanity_checker", () -> new thaumcraft.common.items.tools.ItemSanityChecker());
        ItemsTC.handMirror       = ri("hand_mirror", () -> new thaumcraft.common.items.tools.ItemHandMirror());
        ItemsTC.thaumiumAxe      = ri("thaumium_axe", () -> new thaumcraft.common.items.tools.ItemThaumiumAxe(ThaumcraftMaterials.TOOLMAT_THAUMIUM));
        ItemsTC.thaumiumSword    = ri("thaumium_sword", () -> new thaumcraft.common.items.tools.ItemThaumiumSword(ThaumcraftMaterials.TOOLMAT_THAUMIUM));
        ItemsTC.thaumiumShovel   = ri("thaumium_shovel", () -> new thaumcraft.common.items.tools.ItemThaumiumShovel(ThaumcraftMaterials.TOOLMAT_THAUMIUM));
        ItemsTC.thaumiumPick     = ri("thaumium_pickaxe", () -> new thaumcraft.common.items.tools.ItemThaumiumPickaxe(ThaumcraftMaterials.TOOLMAT_THAUMIUM));
        ItemsTC.thaumiumHoe      = ri("thaumium_hoe", () -> new thaumcraft.common.items.tools.ItemThaumiumHoe(ThaumcraftMaterials.TOOLMAT_THAUMIUM));
        ItemsTC.voidAxe          = ri("void_axe", () -> new thaumcraft.common.items.tools.ItemVoidAxe(ThaumcraftMaterials.TOOLMAT_VOID));
        ItemsTC.voidSword        = ri("void_sword", () -> new thaumcraft.common.items.tools.ItemVoidSword(ThaumcraftMaterials.TOOLMAT_VOID));
        ItemsTC.voidShovel       = ri("void_shovel", () -> new thaumcraft.common.items.tools.ItemVoidShovel(ThaumcraftMaterials.TOOLMAT_VOID));
        ItemsTC.voidPick         = ri("void_pickaxe", () -> new thaumcraft.common.items.tools.ItemVoidPickaxe(ThaumcraftMaterials.TOOLMAT_VOID));
        ItemsTC.voidHoe          = ri("void_hoe", () -> new thaumcraft.common.items.tools.ItemVoidHoe(ThaumcraftMaterials.TOOLMAT_VOID));
        ItemsTC.elementalAxe     = ri("elemental_axe", () -> new thaumcraft.common.items.tools.ItemElementalAxe(ThaumcraftMaterials.TOOLMAT_ELEMENTAL));
        ItemsTC.elementalSword   = ri("elemental_sword", () -> new thaumcraft.common.items.tools.ItemElementalSword(ThaumcraftMaterials.TOOLMAT_ELEMENTAL));
        ItemsTC.elementalShovel  = ri("elemental_shovel", () -> new thaumcraft.common.items.tools.ItemElementalShovel(ThaumcraftMaterials.TOOLMAT_ELEMENTAL));
        ItemsTC.elementalPick    = ri("elemental_pickaxe", () -> new thaumcraft.common.items.tools.ItemElementalPickaxe(ThaumcraftMaterials.TOOLMAT_ELEMENTAL));
        ItemsTC.elementalHoe     = ri("elemental_hoe", () -> new thaumcraft.common.items.tools.ItemElementalHoe(ThaumcraftMaterials.TOOLMAT_ELEMENTAL));
        ItemsTC.primalCrusher    = ri("primal_crusher", () -> new thaumcraft.common.items.tools.ItemPrimalCrusher());
        ItemsTC.crimsonBlade     = ri("crimson_blade", () -> new thaumcraft.common.items.tools.ItemCrimsonBlade());
        ItemsTC.grappleGun       = ri("grapple_gun", () -> new thaumcraft.common.items.tools.ItemGrappleGun());
        ItemsTC.grappleGunTip    = ri("grapple_gun_tip", () -> new ItemTCBase("grapple_gun_tip"));
        ItemsTC.grappleGunSpool  = ri("grapple_gun_spool", () -> new ItemTCBase("grapple_gun_spool"));
        ItemsTC.goggles          = ri("goggles", EquipmentSlot.HEAD, "goggles", () -> new thaumcraft.common.items.armor.ItemGoggles());
        ItemsTC.thaumiumHelm     = ri("thaumium_helm", EquipmentSlot.HEAD, "thaumium", () -> new thaumcraft.common.items.armor.ItemThaumiumArmor("thaumium_helm",  ThaumcraftMaterials.ARMORMAT_THAUMIUM_KEY, 2, EquipmentSlot.HEAD));
        ItemsTC.thaumiumChest    = ri("thaumium_chest", EquipmentSlot.CHEST, "thaumium", () -> new thaumcraft.common.items.armor.ItemThaumiumArmor("thaumium_chest", ThaumcraftMaterials.ARMORMAT_THAUMIUM_KEY, 2, EquipmentSlot.CHEST));
        ItemsTC.thaumiumLegs     = ri("thaumium_legs", EquipmentSlot.LEGS, "thaumium", () -> new thaumcraft.common.items.armor.ItemThaumiumArmor("thaumium_legs",  ThaumcraftMaterials.ARMORMAT_THAUMIUM_KEY, 2, EquipmentSlot.LEGS));
        ItemsTC.thaumiumBoots    = ri("thaumium_boots", EquipmentSlot.FEET, "thaumium", () -> new thaumcraft.common.items.armor.ItemThaumiumArmor("thaumium_boots", ThaumcraftMaterials.ARMORMAT_THAUMIUM_KEY, 2, EquipmentSlot.FEET));
        ItemsTC.clothChest       = ri("cloth_chest", EquipmentSlot.CHEST, "robe", () -> new thaumcraft.common.items.armor.ItemRobeArmor("cloth_chest",  ThaumcraftMaterials.ARMORMAT_SPECIAL_KEY, 1, EquipmentSlot.CHEST));
        ItemsTC.clothLegs        = ri("cloth_legs", EquipmentSlot.LEGS, "robe", () -> new thaumcraft.common.items.armor.ItemRobeArmor("cloth_legs",   ThaumcraftMaterials.ARMORMAT_SPECIAL_KEY, 2, EquipmentSlot.LEGS));
        ItemsTC.clothBoots       = ri("cloth_boots", EquipmentSlot.FEET, "robe", () -> new thaumcraft.common.items.armor.ItemRobeArmor("cloth_boots",  ThaumcraftMaterials.ARMORMAT_SPECIAL_KEY, 1, EquipmentSlot.FEET));
        ItemsTC.travellerBoots   = ri("traveller_boots", EquipmentSlot.FEET, "traveller", () -> new thaumcraft.common.items.armor.ItemBootsTraveller());
        ItemsTC.fortressHelm     = ri("fortress_helm", EquipmentSlot.HEAD, "fortress", () -> new thaumcraft.common.items.armor.ItemFortressArmor("fortress_helm",  ThaumcraftMaterials.ARMORMAT_FORTRESS_KEY, 4, EquipmentSlot.HEAD));
        ItemsTC.fortressChest    = ri("fortress_chest", EquipmentSlot.CHEST, "fortress", () -> new thaumcraft.common.items.armor.ItemFortressArmor("fortress_chest", ThaumcraftMaterials.ARMORMAT_FORTRESS_KEY, 4, EquipmentSlot.CHEST));
        ItemsTC.fortressLegs     = ri("fortress_legs", EquipmentSlot.LEGS, "fortress", () -> new thaumcraft.common.items.armor.ItemFortressArmor("fortress_legs",  ThaumcraftMaterials.ARMORMAT_FORTRESS_KEY, 4, EquipmentSlot.LEGS));
        ItemsTC.voidHelm         = ri("void_helm", EquipmentSlot.HEAD, "void", () -> new thaumcraft.common.items.armor.ItemVoidArmor("void_helm",   ThaumcraftMaterials.ARMORMAT_VOID_KEY, 2, EquipmentSlot.HEAD));
        ItemsTC.voidChest        = ri("void_chest", EquipmentSlot.CHEST, "void", () -> new thaumcraft.common.items.armor.ItemVoidArmor("void_chest",  ThaumcraftMaterials.ARMORMAT_VOID_KEY, 2, EquipmentSlot.CHEST));
        ItemsTC.voidLegs         = ri("void_legs", EquipmentSlot.LEGS, "void", () -> new thaumcraft.common.items.armor.ItemVoidArmor("void_legs",   ThaumcraftMaterials.ARMORMAT_VOID_KEY, 2, EquipmentSlot.LEGS));
        ItemsTC.voidBoots        = ri("void_boots", EquipmentSlot.FEET, "void", () -> new thaumcraft.common.items.armor.ItemVoidArmor("void_boots",  ThaumcraftMaterials.ARMORMAT_VOID_KEY, 2, EquipmentSlot.FEET));
        ItemsTC.voidRobeHelm     = ri("void_robe_helm", EquipmentSlot.HEAD, "void_robe", () -> new thaumcraft.common.items.armor.ItemVoidRobeArmor("void_robe_helm",  ThaumcraftMaterials.ARMORMAT_VOIDROBE_KEY, 4, EquipmentSlot.HEAD));
        ItemsTC.voidRobeChest    = ri("void_robe_chest", EquipmentSlot.CHEST, "void_robe", () -> new thaumcraft.common.items.armor.ItemVoidRobeArmor("void_robe_chest", ThaumcraftMaterials.ARMORMAT_VOIDROBE_KEY, 4, EquipmentSlot.CHEST));
        ItemsTC.voidRobeLegs     = ri("void_robe_legs", EquipmentSlot.LEGS, "void_robe", () -> new thaumcraft.common.items.armor.ItemVoidRobeArmor("void_robe_legs",  ThaumcraftMaterials.ARMORMAT_VOIDROBE_KEY, 4, EquipmentSlot.LEGS));
        ItemsTC.crimsonPlateHelm  = ri("crimson_plate_helm", EquipmentSlot.HEAD, "cultist_plate", () -> new thaumcraft.common.items.armor.ItemCultistPlateArmor("crimson_plate_helm",  ThaumcraftMaterials.ARMORMAT_CULTIST_PLATE_KEY, 4, EquipmentSlot.HEAD));
        ItemsTC.crimsonPlateChest = ri("crimson_plate_chest", EquipmentSlot.CHEST, "cultist_plate", () -> new thaumcraft.common.items.armor.ItemCultistPlateArmor("crimson_plate_chest", ThaumcraftMaterials.ARMORMAT_CULTIST_PLATE_KEY, 4, EquipmentSlot.CHEST));
        ItemsTC.crimsonPlateLegs  = ri("crimson_plate_legs", EquipmentSlot.LEGS, "cultist_plate", () -> new thaumcraft.common.items.armor.ItemCultistPlateArmor("crimson_plate_legs",  ThaumcraftMaterials.ARMORMAT_CULTIST_PLATE_KEY, 4, EquipmentSlot.LEGS));
        ItemsTC.crimsonBoots      = ri("crimson_boots", EquipmentSlot.FEET, "cultist_boots", () -> new thaumcraft.common.items.armor.ItemCultistBoots());
        ItemsTC.crimsonRobeHelm   = ri("crimson_robe_helm", EquipmentSlot.HEAD, "cultist_robe", () -> new thaumcraft.common.items.armor.ItemCultistRobeArmor("crimson_robe_helm",  ThaumcraftMaterials.ARMORMAT_CULTIST_ROBE_KEY, 4, EquipmentSlot.HEAD));
        ItemsTC.crimsonRobeChest  = ri("crimson_robe_chest", EquipmentSlot.CHEST, "cultist_robe", () -> new thaumcraft.common.items.armor.ItemCultistRobeArmor("crimson_robe_chest", ThaumcraftMaterials.ARMORMAT_CULTIST_ROBE_KEY, 4, EquipmentSlot.CHEST));
        ItemsTC.crimsonRobeLegs   = ri("crimson_robe_legs", EquipmentSlot.LEGS, "cultist_robe", () -> new thaumcraft.common.items.armor.ItemCultistRobeArmor("crimson_robe_legs",  ThaumcraftMaterials.ARMORMAT_CULTIST_ROBE_KEY, 4, EquipmentSlot.LEGS));
        ItemsTC.crimsonPraetorHelm  = ri("crimson_praetor_helm", EquipmentSlot.HEAD, "cultist_praetor", () -> new thaumcraft.common.items.armor.ItemCultistLeaderArmor("crimson_praetor_helm",  4, EquipmentSlot.HEAD));
        ItemsTC.crimsonPraetorChest = ri("crimson_praetor_chest", EquipmentSlot.CHEST, "cultist_praetor", () -> new thaumcraft.common.items.armor.ItemCultistLeaderArmor("crimson_praetor_chest", 4, EquipmentSlot.CHEST));
        ItemsTC.crimsonPraetorLegs  = ri("crimson_praetor_legs", EquipmentSlot.LEGS, "cultist_praetor", () -> new thaumcraft.common.items.armor.ItemCultistLeaderArmor("crimson_praetor_legs",  4, EquipmentSlot.LEGS));
        ItemsTC.baubles          = ri("baubles", () -> new thaumcraft.common.items.baubles.ItemBaubles());
        ItemsTC.amuletVis        = ri("amulet_vis", () -> new thaumcraft.common.items.baubles.ItemAmuletVis());
        ItemsTC.charmVerdant     = ri("verdant_charm", () -> new thaumcraft.common.items.baubles.ItemVerdantCharm());
        ItemsTC.bandCuriosity    = ri("curiosity_band", () -> new thaumcraft.common.items.baubles.ItemCuriosityBand());
        ItemsTC.charmVoidseer    = ri("voidseer_charm", () -> new thaumcraft.common.items.baubles.ItemVoidseerCharm());
        ItemsTC.ringCloud        = ri("cloud_ring", () -> new thaumcraft.common.items.baubles.ItemCloudRing());
        ItemsTC.charmUndying     = ri("charm_undying", () -> new thaumcraft.common.items.baubles.ItemCharmUndying());
        ItemsTC.creativeFluxSponge    = ri("creative_flux_sponge", () -> new thaumcraft.common.items.misc.ItemCreativeFluxSponge());
        ItemsTC.enchantedPlaceholder  = ri("enchanted_placeholder", () -> new thaumcraft.common.items.curios.ItemEnchantmentPlaceholder());
        ItemsTC.casterBasic      = ri("caster_basic", () -> new thaumcraft.common.items.casters.ItemCaster("caster_basic", 0));
        ItemsTC.focus1           = ri("focus_1", () -> new thaumcraft.common.items.casters.ItemFocus("focus_1", 15));
        ItemsTC.focus2           = ri("focus_2", () -> new thaumcraft.common.items.casters.ItemFocus("focus_2", 25));
        ItemsTC.focus3           = ri("focus_3", () -> new thaumcraft.common.items.casters.ItemFocus("focus_3", 50));
        ItemsTC.focusPouch       = ri("focus_pouch", () -> new thaumcraft.common.items.casters.ItemFocusPouch());
        ItemsTC.golemBell        = ri("golem_bell", () -> new thaumcraft.common.golems.ItemGolemBell());
        ItemsTC.golemPlacer      = ri("golem", () -> new thaumcraft.common.golems.ItemGolemPlacer());
        ItemsTC.seals            = ri("seal", () -> new thaumcraft.common.golems.seals.ItemSealPlacer());
    }
    
    public static void init() {
        FocusEngine.registerElement(FocusMediumRoot.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/root.png"), 10066329);
        FocusEngine.registerElement(FocusMediumTouch.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/touch.png"), 11371909);
        FocusEngine.registerElement(FocusMediumBolt.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/bolt.png"), 11377029);
        FocusEngine.registerElement(FocusMediumProjectile.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/projectile.png"), 11382149);
        FocusEngine.registerElement(FocusMediumCloud.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/cloud.png"), 10071429);
        FocusEngine.registerElement(FocusMediumMine.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/mine.png"), 8760709);
        FocusEngine.registerElement(FocusMediumPlan.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/plan.png"), 8760728);
        FocusEngine.registerElement(FocusMediumSpellBat.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/spellbat.png"), 8760748);
        FocusEngine.registerElement(FocusEffectFire.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/fire.png"), 16734721);
        FocusEngine.registerElement(FocusEffectFrost.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/frost.png"), 14811135);
        FocusEngine.registerElement(FocusEffectAir.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/air.png"), 16777086);
        FocusEngine.registerElement(FocusEffectEarth.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/earth.png"), 5685248);
        FocusEngine.registerElement(FocusEffectFlux.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/flux.png"), 8388736);
        FocusEngine.registerElement(FocusEffectBreak.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/break.png"), 9063176);
        FocusEngine.registerElement(FocusEffectRift.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/rift.png"), 3084645);
        FocusEngine.registerElement(FocusEffectExchange.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/exchange.png"), 5735255);
        FocusEngine.registerElement(FocusEffectCurse.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/curse.png"), 6946821);
        FocusEngine.registerElement(FocusEffectHeal.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/heal.png"), 14548997);
        FocusEngine.registerElement(FocusModScatter.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/scatter.png"), 10066329);
        FocusEngine.registerElement(FocusModSplitTarget.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/split_target.png"), 10066329);
        FocusEngine.registerElement(FocusModSplitTrajectory.class, Identifier.fromNamespaceAndPath("thaumcraft", "textures/foci/split_trajectory.png"), 10066329);
    }
    
    public static void preInitSeals() {
        SealHandler.registerSeal(new SealPickup());
        SealHandler.registerSeal(new SealPickupAdvanced());
        SealHandler.registerSeal(new SealFill());
        SealHandler.registerSeal(new SealFillAdvanced());
        SealHandler.registerSeal(new SealEmpty());
        SealHandler.registerSeal(new SealEmptyAdvanced());
        SealHandler.registerSeal(new SealHarvest());
        SealHandler.registerSeal(new SealButcher());
        SealHandler.registerSeal(new SealGuard());
        SealHandler.registerSeal(new SealGuardAdvanced());
        SealHandler.registerSeal(new SealLumber());
        SealHandler.registerSeal(new SealBreaker());
        SealHandler.registerSeal(new SealUse());
        SealHandler.registerSeal(new SealProvide());
        SealHandler.registerSeal(new SealStock());
        SealHandler.registerSeal(new SealBreakerAdvanced());
    }
    
    public static void initModelsAndVariants() {
        for (IThaumcraftItems itemVariantHolder : ConfigItems.ITEM_VARIANT_HOLDERS) {
            initModelAndVariants(itemVariantHolder);
        }
    }
    
    private static void initModelAndVariants(IThaumcraftItems item) {
        if (item.getCustomMesh() != null) {
            // FIXME: ModelLoader API removed in 1.21+; use data-driven item models
            for (int i = 0; i < item.getVariantNames().length; ++i) {
                // FIXME: ModelBakery.registerItemVariants removed
            }
        }
        else if (item.getItem() == ItemsTC.seals) {
            for (int i = 0; i < item.getVariantNames().length; ++i) {
            // FIXME: ModelLoader.setCustomModelResourceLocation(item.getItem(), item.getVariantMeta()[i], null /* removed */.getRegistryName() + "_" + item.getVariantNames()[i], null));
            }
        }
        else if (true /* was: !item.getItem().getHasSubtypes() - removed in 1.20+ */) {
            // FIXME: ModelLoader.setCustomModelResourceLocation(item.getItem(), 0, null /* removed */.getRegistryName(), null));
        }
        else {
            for (int i = 0; i < item.getVariantNames().length; ++i) {
            // FIXME: ModelLoader.setCustomModelResourceLocation(item.getItem(), item.getVariantMeta()[i], item.getCustomModelResourceLocation(item.getVariantNames()[i]));
            }
        }
    }
    
    public static ItemStack getStartBook() {
        if (startBook == null || startBook.isEmpty()) {
            startBook = new ItemStack(Items.WRITTEN_BOOK);
        }
        return startBook;
    }

    public static void initCreativeTab() {
        net.minecraft.resources.Identifier rl = net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "thaumcraft");
        TABTC = net.minecraft.world.item.CreativeModeTab.builder()
            .title(net.minecraft.network.chat.Component.translatable("itemGroup.thaumcraft"))
            .icon(() -> new ItemStack(thaumcraft.api.items.ItemsTC.thaumonomicon))
            .displayItems((params, output) -> {
                net.minecraft.core.registries.BuiltInRegistries.ITEM.listElements()
                    .filter(h -> h.key().identifier().getNamespace().equals("thaumcraft"))
                    .forEach(h -> output.accept(new ItemStack(h)));
            })
            .build();
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB, rl, TABTC);
    }

    static {
        ConfigItems.startBook = ItemStack.EMPTY;
        ITEM_VARIANT_HOLDERS = new ArrayList<IThaumcraftItems>();
    }
}
