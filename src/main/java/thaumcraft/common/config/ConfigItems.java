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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
    
    public static void initItems(Object /* IForgeRegistry removed */ iForgeRegistry) {
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.thaumonomicon = new ItemThaumonomicon()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.curio = new ItemCurio()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.lootBag = new ItemLootBag()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.primordialPearl = new ItemPrimordialPearl()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.pechWand = new ItemPechWand()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.celestialNotes = new ItemCelestialNotes()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.amber = new ItemTCBase("amber")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.quicksilver = new ItemTCBase("quicksilver")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.ingots = new ItemTCBase("ingot", "thaumium", "void", "brass")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.nuggets = new ItemTCBase("nugget", "iron", "copper", "tin", "silver", "lead", "quicksilver", "thaumium", "void", "brass", "quartz", "rareearth")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.clusters = new ItemTCBase("cluster", "iron", "gold", "copper", "tin", "silver", "lead", "cinnabar", "quartz")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.fabric = new ItemTCBase("fabric")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.visResonator = new ItemTCBase("vis_resonator")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.tallow = new ItemTCBase("tallow")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.mechanismSimple = new ItemTCBase("mechanism_simple")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.mechanismComplex = new ItemTCBase("mechanism_complex")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.plate = new ItemTCBase("plate", "brass", "iron", "thaumium", "void")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.filter = new ItemTCBase("filter")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.morphicResonator = new ItemTCBase("morphic_resonator")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.salisMundus = new ItemMagicDust()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.mirroredGlass = new ItemTCBase("mirrored_glass")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidSeed = new ItemTCBase("void_seed")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.mind = new ItemTCBase("mind", "clockwork", "biothaumic")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.modules = new ItemTCBase("module", "vision", "aggression")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.crystalEssence = new ItemCrystalEssence()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.chunks = new ItemChunksEdible()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.tripleMeatTreat = new ItemTripleMeatTreat()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.brain = new ItemZombieBrain()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.label = new ItemLabel()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.phial = new ItemPhial()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.alumentum = new ItemAlumentum()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.jarBrace = new ItemTCBase("jar_brace")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.bottleTaint = new ItemBottleTaint()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.sanitySoap = new ItemSanitySoap()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.bathSalts = new ItemBathSalts()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.turretPlacer = new ItemTurretPlacer()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.causalityCollapser = new ItemCausalityCollapser()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.scribingTools = new ItemScribingTools()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.thaumometer = new ItemThaumometer()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.resonator = new ItemResonator()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.sanityChecker = new ItemSanityChecker()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.handMirror = new ItemHandMirror()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.thaumiumAxe = new ItemThaumiumAxe(ThaumcraftMaterials.TOOLMAT_THAUMIUM)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.thaumiumSword = new ItemThaumiumSword(ThaumcraftMaterials.TOOLMAT_THAUMIUM)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.thaumiumShovel = new ItemThaumiumShovel(ThaumcraftMaterials.TOOLMAT_THAUMIUM)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.thaumiumPick = new ItemThaumiumPickaxe(ThaumcraftMaterials.TOOLMAT_THAUMIUM)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.thaumiumHoe = new ItemThaumiumHoe(ThaumcraftMaterials.TOOLMAT_THAUMIUM)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidAxe = new ItemVoidAxe(ThaumcraftMaterials.TOOLMAT_VOID)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidSword = new ItemVoidSword(ThaumcraftMaterials.TOOLMAT_VOID)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidShovel = new ItemVoidShovel(ThaumcraftMaterials.TOOLMAT_VOID)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidPick = new ItemVoidPickaxe(ThaumcraftMaterials.TOOLMAT_VOID)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidHoe = new ItemVoidHoe(ThaumcraftMaterials.TOOLMAT_VOID)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.elementalAxe = new ItemElementalAxe(ThaumcraftMaterials.TOOLMAT_ELEMENTAL)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.elementalSword = new ItemElementalSword(ThaumcraftMaterials.TOOLMAT_ELEMENTAL)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.elementalShovel = new ItemElementalShovel(ThaumcraftMaterials.TOOLMAT_ELEMENTAL)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.elementalPick = new ItemElementalPickaxe(ThaumcraftMaterials.TOOLMAT_ELEMENTAL)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.elementalHoe = new ItemElementalHoe(ThaumcraftMaterials.TOOLMAT_ELEMENTAL)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.primalCrusher = new ItemPrimalCrusher()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.crimsonBlade = new ItemCrimsonBlade()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.grappleGun = new ItemGrappleGun()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.grappleGunTip = new ItemTCBase("grapple_gun_tip")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.grappleGunSpool = new ItemTCBase("grapple_gun_spool")));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.goggles = new ItemGoggles()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.thaumiumHelm = new ItemThaumiumArmor("thaumium_helm", ThaumcraftMaterials.ARMORMAT_THAUMIUM, 2, EquipmentSlot.HEAD)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.thaumiumChest = new ItemThaumiumArmor("thaumium_chest", ThaumcraftMaterials.ARMORMAT_THAUMIUM, 2, EquipmentSlot.CHEST)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.thaumiumLegs = new ItemThaumiumArmor("thaumium_legs", ThaumcraftMaterials.ARMORMAT_THAUMIUM, 2, EquipmentSlot.LEGS)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.thaumiumBoots = new ItemThaumiumArmor("thaumium_boots", ThaumcraftMaterials.ARMORMAT_THAUMIUM, 2, EquipmentSlot.FEET)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.clothChest = new ItemRobeArmor("cloth_chest", ThaumcraftMaterials.ARMORMAT_SPECIAL, 1, EquipmentSlot.CHEST)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.clothLegs = new ItemRobeArmor("cloth_legs", ThaumcraftMaterials.ARMORMAT_SPECIAL, 2, EquipmentSlot.LEGS)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.clothBoots = new ItemRobeArmor("cloth_boots", ThaumcraftMaterials.ARMORMAT_SPECIAL, 1, EquipmentSlot.FEET)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.travellerBoots = new ItemBootsTraveller()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.fortressHelm = new ItemFortressArmor("fortress_helm", ThaumcraftMaterials.ARMORMAT_FORTRESS, 4, EquipmentSlot.HEAD)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.fortressChest = new ItemFortressArmor("fortress_chest", ThaumcraftMaterials.ARMORMAT_FORTRESS, 4, EquipmentSlot.CHEST)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.fortressLegs = new ItemFortressArmor("fortress_legs", ThaumcraftMaterials.ARMORMAT_FORTRESS, 4, EquipmentSlot.LEGS)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidHelm = new ItemVoidArmor("void_helm", ThaumcraftMaterials.ARMORMAT_VOID, 2, EquipmentSlot.HEAD)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidChest = new ItemVoidArmor("void_chest", ThaumcraftMaterials.ARMORMAT_VOID, 2, EquipmentSlot.CHEST)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidLegs = new ItemVoidArmor("void_legs", ThaumcraftMaterials.ARMORMAT_VOID, 2, EquipmentSlot.LEGS)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidBoots = new ItemVoidArmor("void_boots", ThaumcraftMaterials.ARMORMAT_VOID, 2, EquipmentSlot.FEET)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidRobeHelm = new ItemVoidRobeArmor("void_robe_helm", ThaumcraftMaterials.ARMORMAT_VOIDROBE, 4, EquipmentSlot.HEAD)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidRobeChest = new ItemVoidRobeArmor("void_robe_chest", ThaumcraftMaterials.ARMORMAT_VOIDROBE, 4, EquipmentSlot.CHEST)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.voidRobeLegs = new ItemVoidRobeArmor("void_robe_legs", ThaumcraftMaterials.ARMORMAT_VOIDROBE, 4, EquipmentSlot.LEGS)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.crimsonPlateHelm = new ItemCultistPlateArmor("crimson_plate_helm", ThaumcraftMaterials.ARMORMAT_CULTIST_PLATE, 4, EquipmentSlot.HEAD)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.crimsonPlateChest = new ItemCultistPlateArmor("crimson_plate_chest", ThaumcraftMaterials.ARMORMAT_CULTIST_PLATE, 4, EquipmentSlot.CHEST)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.crimsonPlateLegs = new ItemCultistPlateArmor("crimson_plate_legs", ThaumcraftMaterials.ARMORMAT_CULTIST_PLATE, 4, EquipmentSlot.LEGS)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.crimsonBoots = new ItemCultistBoots()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.crimsonRobeHelm = new ItemCultistRobeArmor("crimson_robe_helm", ThaumcraftMaterials.ARMORMAT_CULTIST_ROBE, 4, EquipmentSlot.HEAD)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.crimsonRobeChest = new ItemCultistRobeArmor("crimson_robe_chest", ThaumcraftMaterials.ARMORMAT_CULTIST_ROBE, 4, EquipmentSlot.CHEST)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.crimsonRobeLegs = new ItemCultistRobeArmor("crimson_robe_legs", ThaumcraftMaterials.ARMORMAT_CULTIST_ROBE, 4, EquipmentSlot.LEGS)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.crimsonPraetorHelm = new ItemCultistLeaderArmor("crimson_praetor_helm", 4, EquipmentSlot.HEAD)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.crimsonPraetorChest = new ItemCultistLeaderArmor("crimson_praetor_chest", 4, EquipmentSlot.CHEST)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.crimsonPraetorLegs = new ItemCultistLeaderArmor("crimson_praetor_legs", 4, EquipmentSlot.LEGS)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.baubles = new ItemBaubles()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.amuletVis = new ItemAmuletVis()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.charmVerdant = new ItemVerdantCharm()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.bandCuriosity = new ItemCuriosityBand()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.charmVoidseer = new ItemVoidseerCharm()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.ringCloud = new ItemCloudRing()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.charmUndying = new ItemCharmUndying()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.creativeFluxSponge = new ItemCreativeFluxSponge()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.enchantedPlaceholder = new ItemEnchantmentPlaceholder()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.casterBasic = new ItemCaster("caster_basic", 0)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.focus1 = new ItemFocus("focus_1", 15)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.focus2 = new ItemFocus("focus_2", 25)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.focus3 = new ItemFocus("focus_3", 50)));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.focusPouch = new ItemFocusPouch()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.golemBell = new ItemGolemBell()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.golemPlacer = new ItemGolemPlacer()));
        // FIXME-REGISTRATION: iForgeRegistry.register((ItemsTC.seals = new ItemSealPlacer()));
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
    
    @OnlyIn(Dist.CLIENT)
    public static void initModelsAndVariants() {
        for (IThaumcraftItems itemVariantHolder : ConfigItems.ITEM_VARIANT_HOLDERS) {
            initModelAndVariants(itemVariantHolder);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
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

    static {
        ConfigItems.startBook = ItemStack.EMPTY;
        ConfigItems.TABTC = null; // FIXME: CreativeModeTab registration changed in 1.20+; use DeferredRegister
        ITEM_VARIANT_HOLDERS = new ArrayList<IThaumcraftItems>();
    }
}
