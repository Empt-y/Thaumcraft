package thaumcraft.common.config;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.neoforged.neoforge.common.ModConfigSpec;
// removed: import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFallingTaint;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.entities.monster.EntityEldritchCrab;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;
import thaumcraft.common.entities.monster.boss.EntityCultistPortalGreater;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;
import thaumcraft.common.lib.utils.CropUtils;
import thaumcraft.common.lib.utils.Utils;

public class ModConfig
{
    private static ItemStack s(net.minecraft.world.level.ItemLike item) {
        return (item != null) ? new ItemStack(item) : ItemStack.EMPTY;
    }
    public static float auraSize = 4.0f;
    public static ArrayList<Aspect> aspectOrder;
    public static boolean foundCopperIngot;
    public static boolean foundTinIngot;
    public static boolean foundSilverIngot;
    public static boolean foundLeadIngot;
    public static boolean foundCopperOre;
    public static boolean foundTinOre;
    public static boolean foundSilverOre;
    public static boolean foundLeadOre;
    public static boolean isHalloween;
    
    public static void postInitLoot() {
        int COMMON = 0;
        int UNCOMMON = 1;
        int RARE = 2;
        Random rand = new Random(System.currentTimeMillis());
        ThaumcraftApi.addLootBagItem(s(Items.GOLD_NUGGET), 2500);
        ThaumcraftApi.addLootBagItem(s(Items.GOLD_NUGGET), 2250);
        ThaumcraftApi.addLootBagItem(s(Items.GOLD_NUGGET), 2000);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.salisMundus), 3);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.salisMundus), 6);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.salisMundus), 9);
        ThaumcraftApi.addLootBagItem(s(Items.CHORUS_FRUIT), 5, 0, 1, 2);
        ThaumcraftApi.addLootBagItem(s(Items.COMPASS), 5, 0, 1, 2);
        ThaumcraftApi.addLootBagItem(s(Items.COOKIE), 5, 0, 1, 2);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.primordialPearl), 1);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.primordialPearl), 3);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.primordialPearl), 1);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.primordialPearl), 9);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.primordialPearl), 3);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.primordialPearl), 1);
        ThaumcraftApi.addLootBagItem(s(Items.NETHER_STAR), 1);
        ThaumcraftApi.addLootBagItem(s(Items.DIAMOND), 10);
        ThaumcraftApi.addLootBagItem(s(Items.DIAMOND), 50, 1, 2);
        ThaumcraftApi.addLootBagItem(s(Items.EMERALD), 15);
        ThaumcraftApi.addLootBagItem(s(Items.EMERALD), 75, 1, 2);
        ThaumcraftApi.addLootBagItem(s(Items.GOLD_INGOT), 100, 0, 1, 2);
        ThaumcraftApi.addLootBagItem(s(Items.ENDER_PEARL), 100, 0, 1, 2);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.amuletVis), 6, 1, 2);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.baubles), 10);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.baubles), 10);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.baubles), 10);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.baubles), 5);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.baubles), 5);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.baubles), 5);
        ThaumcraftApi.addLootBagItem(s(ItemsTC.baubles), 5);
        ThaumcraftApi.addLootBagItem(s(Items.EXPERIENCE_BOTTLE), 5);
        ThaumcraftApi.addLootBagItem(s(Items.EXPERIENCE_BOTTLE), 10);
        ThaumcraftApi.addLootBagItem(s(Items.EXPERIENCE_BOTTLE), 20);
        ThaumcraftApi.addLootBagItem(s(Items.GOLDEN_APPLE), 1);
        ThaumcraftApi.addLootBagItem(s(Items.GOLDEN_APPLE), 2);
        ThaumcraftApi.addLootBagItem(s(Items.GOLDEN_APPLE), 3);
        ThaumcraftApi.addLootBagItem(s(Items.GOLDEN_APPLE), 3);
        ThaumcraftApi.addLootBagItem(s(Items.GOLDEN_APPLE), 6);
        ThaumcraftApi.addLootBagItem(s(Items.GOLDEN_APPLE), 9);
        ThaumcraftApi.addLootBagItem(s(Items.BOOK), 10, 0, 1, 2);
        // FIXME: PotionType.REGISTRY removed; potion loot bag items need porting
        ItemStack[] commonLoot = { s(ItemsTC.lootBag), s(ItemsTC.ingots), s(ItemsTC.amber) };
        ItemStack[] uncommonLoot = { s(ItemsTC.lootBag), s(ItemsTC.baubles), s(ItemsTC.baubles), s(ItemsTC.baubles) };
        ItemStack[] rareLoot = { s(ItemsTC.lootBag), s(ItemsTC.thaumonomicon), s(ItemsTC.thaumiumSword), s(ItemsTC.thaumiumAxe), s(ItemsTC.thaumiumHoe), s(ItemsTC.thaumiumPick), s(ItemsTC.baubles), s(ItemsTC.baubles), s(ItemsTC.baubles), s(ItemsTC.baubles), s(ItemsTC.amuletVis) };
    }
    
    public static void modCompatibility() {        // FIXME: OreDictionary API removed; use item tags instead
        // FIXME-OREDICT: Thaumcraft.log.info("Checking for mod & oredict compatibilities");
        // FIXME-OREDICT: Identifier defaultGroup = Identifier.withDefaultNamespace("thaumcraft");
        // FIXME-OREDICT: try {
        // FIXME-OREDICT: if (OreDictionary.doesOreNameExist("oreIron") && OreDictionary.getOres("oreIron", false).size() > 1) {
        // FIXME-OREDICT: for (ItemStack is : OreDictionary.getOres("oreIron", false)) {
        // FIXME-OREDICT: if (is.getItem() != Item.getItemFromBlock(Blocks.IRON_ORE)) {
        // FIXME-OREDICT: Utils.addSpecialMiningResult(is, s(ItemsTC.clusters), 1.0f);
        // FIXME-OREDICT: }
        // FIXME-OREDICT: }
        // FIXME-OREDICT: }
        // FIXME-OREDICT: if (OreDictionary.doesOreNameExist("oreGold") && OreDictionary.getOres("oreGold", false).size() > 1) {
        // FIXME-OREDICT: for (ItemStack is : OreDictionary.getOres("oreGold", false)) {
        // FIXME-OREDICT: if (is.getItem() != Item.getItemFromBlock(Blocks.GOLD_ORE)) {
        // FIXME-OREDICT: Utils.addSpecialMiningResult(is, s(ItemsTC.clusters), 1.0f);
        // FIXME-OREDICT: }
        // FIXME-OREDICT: }
        // FIXME-OREDICT: }
    }

    
    public static void registerSafariNetBlacklist(Class<?> blacklistedEntity) {
        try {
            Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
            if (registry != null) {
                Method reg = registry.getMethod("registerSafariNetBlacklist", Class.class);
                reg.invoke(registry, blacklistedEntity);
            }
        }
        catch (Exception ex) {}
    }
    
    public static void postInitMisc() {
        // FIXME: for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
        // FIXME: if (item != null && item instanceof IPlantable) {
        // FIXME: try {
        // FIXME: BlockState bs = ((IPlantable)item).getPlant(null, null);
        // FIXME: if (bs == null) {
        // FIXME: continue;
        // FIXME: }
        // FIXME: ThaumcraftApi.registerSeed(bs.getBlock(), s(item));
        // FIXME: }
        // FIXME: catch (Exception ex) {}
        // FIXME: }
        // FIXME: }
        CropUtils.addStandardCrop(Blocks.MELON, 32767);
        CropUtils.addStandardCrop(Blocks.PUMPKIN, 32767);
        CropUtils.addStackedCrop(Blocks.SUGAR_CANE, 32767);
        CropUtils.addStackedCrop(Blocks.CACTUS, 32767);
        CropUtils.addStandardCrop(Blocks.NETHER_WART, 3);
        ThaumcraftApi.registerSeed(Blocks.COCOA, s(Items.COCOA_BEANS));
        Utils.addSpecialMiningResult(s(Blocks.IRON_ORE), s(ItemsTC.clusters), 1.0f);
        Utils.addSpecialMiningResult(s(Blocks.GOLD_ORE), s(ItemsTC.clusters), 1.0f);
        Utils.addSpecialMiningResult(s(BlocksTC.oreCinnabar), s(ItemsTC.clusters), 1.0f);
        Utils.addSpecialMiningResult(s(Items.QUARTZ), s(ItemsTC.clusters), 1.0f);
        Collection<Aspect> pa = Aspect.aspects.values();
        for (Aspect aspect : pa) {
            ModConfig.aspectOrder.add(aspect);
        }
    }
    
    static {
        ModConfig.aspectOrder = new ArrayList<Aspect>();
        ModConfig.foundCopperIngot = false;
        ModConfig.foundTinIngot = false;
        ModConfig.foundSilverIngot = false;
        ModConfig.foundLeadIngot = false;
        ModConfig.foundCopperOre = false;
        ModConfig.foundTinOre = false;
        ModConfig.foundSilverOre = false;
        ModConfig.foundLeadOre = false;
    }
    
    public static class CONFIG_GRAPHICS
    {
        public static boolean largeTagText;
        public static boolean disableShaders;
        public static boolean nostress;
        public static boolean crooked;
        public static boolean dialBottom;
        public static boolean showTags;
        public static boolean blueBiome;
        public static boolean showGolemEmotes;
        
        static {
            CONFIG_GRAPHICS.largeTagText = false;
            CONFIG_GRAPHICS.disableShaders = false;
            CONFIG_GRAPHICS.nostress = false;
            CONFIG_GRAPHICS.crooked = true;
            CONFIG_GRAPHICS.dialBottom = false;
            CONFIG_GRAPHICS.showTags = false;
            CONFIG_GRAPHICS.blueBiome = false;
            CONFIG_GRAPHICS.showGolemEmotes = true;
        }
    }
    
    public static class CONFIG_WORLD
    {
        // @Config.RequiresMcRestart // old Forge config annotation
        public static int overworldDim;
        // @Config.RequiresMcRestart // old Forge config annotation
        public static int dimensionOuterId;
        // @Config.RequiresMcRestart // old Forge config annotation
        public static int oreDensity;
        public static boolean generateMagicForest;
        // @Config.RequiresMcRestart // old Forge config annotation
        public static int biomeMagicalForestWeight;
        public static float taintSpreadRate;
        public static int taintSpreadArea;
        public static boolean generateAura;
        public static boolean generateStructure;
        public static boolean generateCinnabar;
        public static boolean generateAmber;
        public static boolean generateQuartz;
        public static boolean generateCrystals;
        public static boolean generateTrees;
        public static String regenKey;
        public static boolean regenAura;
        public static boolean regenStructure;
        public static boolean regenCinnabar;
        public static boolean regenAmber;
        public static boolean regenQuartz;
        public static boolean regenCrystals;
        public static boolean regenTrees;
        public static boolean allowSpawnAngryZombie;
        public static boolean allowSpawnFireBat;
        public static boolean allowSpawnWisp;
        public static boolean allowSpawnTaintacle;
        public static boolean allowSpawnPech;
        public static boolean allowSpawnElder;
        public static boolean allowChampionMobs;
        
        static {
            CONFIG_WORLD.overworldDim = 0;
            CONFIG_WORLD.dimensionOuterId = -42;
            CONFIG_WORLD.oreDensity = 100;
            CONFIG_WORLD.generateMagicForest = true;
            CONFIG_WORLD.biomeMagicalForestWeight = 5;
            CONFIG_WORLD.taintSpreadRate = 100.0f;
            CONFIG_WORLD.taintSpreadArea = 32;
            CONFIG_WORLD.generateAura = true;
            CONFIG_WORLD.generateStructure = true;
            CONFIG_WORLD.generateCinnabar = true;
            CONFIG_WORLD.generateAmber = true;
            CONFIG_WORLD.generateQuartz = true;
            CONFIG_WORLD.generateCrystals = true;
            CONFIG_WORLD.generateTrees = true;
            CONFIG_WORLD.regenKey = "DEFAULT";
            CONFIG_WORLD.regenAura = false;
            CONFIG_WORLD.regenStructure = false;
            CONFIG_WORLD.regenCinnabar = false;
            CONFIG_WORLD.regenAmber = false;
            CONFIG_WORLD.regenQuartz = false;
            CONFIG_WORLD.regenCrystals = false;
            CONFIG_WORLD.regenTrees = false;
            CONFIG_WORLD.allowSpawnAngryZombie = true;
            CONFIG_WORLD.allowSpawnFireBat = true;
            CONFIG_WORLD.allowSpawnWisp = true;
            CONFIG_WORLD.allowSpawnTaintacle = true;
            CONFIG_WORLD.allowSpawnPech = true;
            CONFIG_WORLD.allowSpawnElder = true;
            CONFIG_WORLD.allowChampionMobs = true;
        }
    }
    
    public static class CONFIG_MISC
    {
        public static boolean noSleep;
        public static boolean wussMode;
        public static boolean allowCheatSheet;
        public static int shieldRecharge;
        public static int shieldWait;
        public static int shieldCost;
        
        static {
            CONFIG_MISC.noSleep = false;
            CONFIG_MISC.wussMode = false;
            CONFIG_MISC.allowCheatSheet = false;
            CONFIG_MISC.shieldRecharge = 2000;
            CONFIG_MISC.shieldWait = 4000;
            CONFIG_MISC.shieldCost = 1;
        }
    }
}
