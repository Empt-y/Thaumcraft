package thaumcraft.common.config;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import java.util.Iterator;
import net.minecraft.world.level.block.Block;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.biome.Biome;
        // FIXME: // FIXME: // removed: import net.minecraftforge.common.BiomeDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ScanBlock;
import thaumcraft.api.research.ScanEntity;
import thaumcraft.api.research.ScanItem;
import thaumcraft.api.research.ScanMaterial;
import thaumcraft.api.research.ScanOreDictionary;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.api.research.theorycraft.AidBookshelf;
import thaumcraft.api.research.theorycraft.CardAnalyze;
import thaumcraft.api.research.theorycraft.CardBalance;
import thaumcraft.api.research.theorycraft.CardExperimentation;
import thaumcraft.api.research.theorycraft.CardInspired;
import thaumcraft.api.research.theorycraft.CardNotation;
import thaumcraft.api.research.theorycraft.CardPonder;
import thaumcraft.api.research.theorycraft.CardReject;
import thaumcraft.api.research.theorycraft.CardRethink;
import thaumcraft.api.research.theorycraft.CardStudy;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftManager;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityEldritchCrab;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityFireBat;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import thaumcraft.common.entities.monster.tainted.EntityTaintSwarm;
import thaumcraft.common.entities.monster.tainted.EntityTaintacle;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ScanEnchantment;
import thaumcraft.common.lib.research.ScanGeneric;
import thaumcraft.common.lib.research.ScanPotion;
import thaumcraft.common.lib.research.ScanSky;
import thaumcraft.common.lib.research.theorycraft.AidBasicAlchemy;
import thaumcraft.common.lib.research.theorycraft.AidBasicArtifice;
import thaumcraft.common.lib.research.theorycraft.AidBasicAuromancy;
import thaumcraft.common.lib.research.theorycraft.AidBasicEldritch;
import thaumcraft.common.lib.research.theorycraft.AidBasicGolemancy;
import thaumcraft.common.lib.research.theorycraft.AidBasicInfusion;
import thaumcraft.common.lib.research.theorycraft.AidBeacon;
import thaumcraft.common.lib.research.theorycraft.AidBrainInAJar;
import thaumcraft.common.lib.research.theorycraft.AidEnchantmentTable;
import thaumcraft.common.lib.research.theorycraft.AidGlyphedStone;
import thaumcraft.common.lib.research.theorycraft.AidPortal;
import thaumcraft.common.lib.research.theorycraft.CardAwareness;
import thaumcraft.common.lib.research.theorycraft.CardBeacon;
import thaumcraft.common.lib.research.theorycraft.CardCalibrate;
import thaumcraft.common.lib.research.theorycraft.CardCelestial;
import thaumcraft.common.lib.research.theorycraft.CardChannel;
import thaumcraft.common.lib.research.theorycraft.CardConcentrate;
import thaumcraft.common.lib.research.theorycraft.CardCurio;
import thaumcraft.common.lib.research.theorycraft.CardDarkWhispers;
import thaumcraft.common.lib.research.theorycraft.CardEnchantment;
import thaumcraft.common.lib.research.theorycraft.CardFocus;
import thaumcraft.common.lib.research.theorycraft.CardGlyphs;
import thaumcraft.common.lib.research.theorycraft.CardInfuse;
import thaumcraft.common.lib.research.theorycraft.CardMeasure;
import thaumcraft.common.lib.research.theorycraft.CardMindOverMatter;
import thaumcraft.common.lib.research.theorycraft.CardPortal;
import thaumcraft.common.lib.research.theorycraft.CardReactions;
import thaumcraft.common.lib.research.theorycraft.CardRealization;
import thaumcraft.common.lib.research.theorycraft.CardRevelation;
import thaumcraft.common.lib.research.theorycraft.CardScripting;
import thaumcraft.common.lib.research.theorycraft.CardSculpting;
import thaumcraft.common.lib.research.theorycraft.CardSpellbinding;
import thaumcraft.common.lib.research.theorycraft.CardSynergy;
import thaumcraft.common.lib.research.theorycraft.CardSynthesis;
import thaumcraft.common.lib.research.theorycraft.CardTinker;


public class ConfigResearch
{
    public static String[] TCCategories;
    private static Identifier BACK_OVER;
    
    public static void init() {
        initCategories();
        initScannables();
        initTheorycraft();
        initWarp();
        for (String cat : ConfigResearch.TCCategories) {
            ThaumcraftApi.registerResearchLocation(net.minecraft.resources.Identifier.parse("thaumcraft:research/" + cat.toLowerCase()));
        }
        ThaumcraftApi.registerResearchLocation(Identifier.fromNamespaceAndPath("thaumcraft", "research/scans"));
    }
    
    public static void postInit() {
        ResearchManager.parseAllResearch();
    }
    
    private static void initCategories() {
        ResearchCategories.registerCategory("BASICS", null, new AspectList().add(Aspect.PLANT, 5).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 5).add(Aspect.AIR, 5).add(Aspect.FIRE, 5).add(Aspect.EARTH, 3).add(Aspect.WATER, 5), Identifier.fromNamespaceAndPath("thaumcraft", "textures/items/thaumonomicon_cheat.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_1.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("AUROMANCY", "UNLOCKAUROMANCY", new AspectList().add(Aspect.AURA, 20).add(Aspect.MAGIC, 20).add(Aspect.FLUX, 15).add(Aspect.CRYSTAL, 5).add(Aspect.COLD, 5).add(Aspect.AIR, 5), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/cat_auromancy.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_2.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("ALCHEMY", "UNLOCKALCHEMY", new AspectList().add(Aspect.ALCHEMY, 30).add(Aspect.FLUX, 10).add(Aspect.MAGIC, 10).add(Aspect.LIFE, 5).add(Aspect.AVERSION, 5).add(Aspect.DESIRE, 5).add(Aspect.WATER, 5), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/cat_alchemy.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_3.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("ARTIFICE", "UNLOCKARTIFICE", new AspectList().add(Aspect.MECHANISM, 10).add(Aspect.CRAFT, 10).add(Aspect.METAL, 10).add(Aspect.TOOL, 10).add(Aspect.ENERGY, 10).add(Aspect.LIGHT, 5).add(Aspect.FLIGHT, 5).add(Aspect.TRAP, 5).add(Aspect.FIRE, 5), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/cat_artifice.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_4.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("INFUSION", "UNLOCKINFUSION", new AspectList().add(Aspect.MAGIC, 30).add(Aspect.PROTECT, 10).add(Aspect.TOOL, 10).add(Aspect.FLUX, 5).add(Aspect.CRAFT, 5).add(Aspect.SOUL, 5).add(Aspect.EARTH, 3), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/cat_infusion.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_7.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("GOLEMANCY", "UNLOCKGOLEMANCY", new AspectList().add(Aspect.MAN, 20).add(Aspect.MOTION, 10).add(Aspect.MIND, 10).add(Aspect.MECHANISM, 10).add(Aspect.EXCHANGE, 5).add(Aspect.SENSES, 5).add(Aspect.BEAST, 5).add(Aspect.ORDER, 5), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/cat_golemancy.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_5.jpg"), ConfigResearch.BACK_OVER);
        ResearchCategories.registerCategory("ELDRITCH", "UNLOCKELDRITCH", new AspectList().add(Aspect.ELDRITCH, 20).add(Aspect.DARKNESS, 10).add(Aspect.MAGIC, 5).add(Aspect.MIND, 5).add(Aspect.VOID, 5).add(Aspect.DEATH, 5).add(Aspect.UNDEAD, 5).add(Aspect.ENTROPY, 5), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/cat_eldritch.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_6.jpg"), ConfigResearch.BACK_OVER);
    }
    
    private static void initScannables() {
        ScanningManager.addScannableThing(new ScanGeneric());
        // FIXME: for (Identifier loc : Enchantment.REGISTRY.getKeys()) {
        // FIXME:     Enchantment ench = Enchantment.REGISTRY.getObject(loc);
        // FIXME:     ScanningManager.addScannableThing(new ScanEnchantment(ench));
        // FIXME: }
        // FIXME: for (Identifier loc : MobEffect.REGISTRY.getKeys()) {
        // FIXME:     MobEffect pot = MobEffect.REGISTRY.getObject(loc);
        // FIXME:     ScanningManager.addScannableThing(new ScanPotion(pot));
        // FIXME: }
        ScanningManager.addScannableThing(new ScanEntity("!Wisp", EntityWisp.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!ThaumSlime", EntityThaumicSlime.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!Firebat", EntityFireBat.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!Pech", EntityPech.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!BrainyZombie", EntityBrainyZombie.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!EldritchCrab", EntityEldritchCrab.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!EldritchCrab", EntityInhabitedZombie.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!CrimsonCultist", EntityCultist.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!EldritchGuardian", EntityEldritchGuardian.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!TaintCrawler", EntityTaintCrawler.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!Taintacle", EntityTaintacle.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!TaintSeed", EntityTaintSeed.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!TaintSwarm", EntityTaintSwarm.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_toomuchflux", EntityFluxRift.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!FluxRift", EntityFluxRift.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_golem", AbstractGolem.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_golem", EntityOwnedConstruct.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_SPIDER", Spider.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_BAT", Bat.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_BAT", EntityFireBat.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", Bat.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", Parrot.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", EntityFireBat.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", EntityTaintSwarm.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", EntityWisp.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", Ghast.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", net.minecraft.world.entity.monster.Blaze.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!ORMOB", IEldritchMob.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!ORBOSS", EntityThaumcraftBoss.class, true));
        ScanningManager.addScannableThing(new ScanBlock("!ORBLOCK1", BlocksTC.stoneAncient, BlocksTC.stoneAncientTile));
        ScanningManager.addScannableThing(new ScanBlock("!ORBLOCK2", BlocksTC.stoneEldritchTile));
        ScanningManager.addScannableThing(new ScanBlock("!ORBLOCK3", BlocksTC.stoneAncientGlyphed));
        ScanningManager.addScannableThing(new ScanBlock("ORE", BlocksTC.oreAmber, BlocksTC.oreCinnabar, BlocksTC.crystalAir, BlocksTC.crystalFire, BlocksTC.crystalWater, BlocksTC.crystalEarth, BlocksTC.crystalOrder, BlocksTC.crystalEntropy, BlocksTC.crystalTaint));
        ScanningManager.addScannableThing(new ScanBlock("!OREAMBER", BlocksTC.oreAmber));
        ScanningManager.addScannableThing(new ScanBlock("!ORECINNABAR", BlocksTC.oreCinnabar));
        ScanningManager.addScannableThing(new ScanBlock("!ORECRYSTAL", BlocksTC.crystalAir, BlocksTC.crystalFire, BlocksTC.crystalWater, BlocksTC.crystalEarth, BlocksTC.crystalOrder, BlocksTC.crystalEntropy, BlocksTC.crystalTaint));
        ScanningManager.addScannableThing(new ScanBlock("PLANTS", BlocksTC.logGreatwood, BlocksTC.logSilverwood, BlocksTC.saplingGreatwood, BlocksTC.saplingSilverwood, BlocksTC.cinderpearl, BlocksTC.shimmerleaf, BlocksTC.vishroom));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTWOOD", BlocksTC.logGreatwood));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTWOOD", BlocksTC.logSilverwood));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTWOOD", BlocksTC.saplingGreatwood));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTWOOD", BlocksTC.saplingSilverwood));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTCINDERPEARL", BlocksTC.cinderpearl));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTSHIMMERLEAF", BlocksTC.shimmerleaf));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTVISHROOM", BlocksTC.vishroom));
        ScanningManager.addScannableThing(new ScanItem("PRIMPEARL", new ItemStack(ItemsTC.primordialPearl)));
        ScanningManager.addScannableThing(new ScanItem("!DRAGONBREATH", new ItemStack(Items.DRAGON_BREATH)));
        ScanningManager.addScannableThing(new ScanItem("!TOTEMUNDYING", new ItemStack(Items.TOTEM_OF_UNDYING)));
        ScanningManager.addScannableThing(new ScanBlock("f_TELEPORT", Blocks.NETHER_PORTAL, Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME));
        ScanningManager.addScannableThing(new ScanItem("f_TELEPORT", new ItemStack(Items.ENDER_PEARL)));
        ScanningManager.addScannableThing(new ScanEntity("f_TELEPORT", net.minecraft.world.entity.monster.EnderMan.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_BRAIN", EntityBrainyZombie.class, true));
        ScanningManager.addScannableThing(new ScanItem("f_BRAIN", new ItemStack(ItemsTC.brain)));
        ScanningManager.addScannableThing(new ScanBlock("f_DISPENSER", Blocks.DISPENSER));
        ScanningManager.addScannableThing(new ScanItem("f_DISPENSER", new ItemStack(Blocks.DISPENSER)));
        ScanningManager.addScannableThing(new ScanItem("f_MATCLAY", new ItemStack(Items.CLAY_BALL)));
        ScanningManager.addScannableThing(new ScanBlock("f_MATCLAY", Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA));
        // FIXME: Material API removed: ScanningManager.addScannableThing(new ScanMaterial("f_MATCLAY", null /* Material removed */));
        ScanningManager.addScannableThing(new ScanOreDictionary("f_MATIRON", "oreIron", "ingotIron", "blockIron", "plateIron"));
        ScanningManager.addScannableThing(new ScanOreDictionary("f_MATBRASS", "ingotBrass", "blockBrass", "plateBrass"));
        ScanningManager.addScannableThing(new ScanOreDictionary("f_MATTHAUMIUM", "ingotThaumium", "blockThaumium", "plateThaumium"));
        ScanningManager.addScannableThing(new ScanOreDictionary("f_MATVOID", "ingotVoid", "blockVoid", "plateVoid"));
        ScanningManager.addScannableThing(new ScanEntity("f_arrow", AbstractArrow.class, true));
        ScanningManager.addScannableThing(new ScanItem("f_arrow", new ItemStack(Items.ARROW)));
        ScanningManager.addScannableThing(new ScanEntity("f_fireball", net.minecraft.world.entity.projectile.hurtingprojectile.Fireball.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_spit", LlamaSpit.class, true));
        ScanningManager.addScannableThing(new ScanItem("!Pechwand", new ItemStack(ItemsTC.pechWand)));
        ScanningManager.addScannableThing(new ScanItem("f_VOIDSEED", new ItemStack(ItemsTC.voidSeed)));
        ScanningManager.addScannableThing(new ScanSky());
    }
    
    private static void initTheorycraft() {
        TheorycraftManager.registerAid(new AidBookshelf());
        TheorycraftManager.registerAid(new AidBrainInAJar());
        TheorycraftManager.registerAid(new AidGlyphedStone());
        TheorycraftManager.registerAid(new AidPortal.AidPortalEnd());
        TheorycraftManager.registerAid(new AidPortal.AidPortalNether());
        TheorycraftManager.registerAid(new AidPortal.AidPortalCrimson());
        TheorycraftManager.registerAid(new AidBasicAlchemy());
        TheorycraftManager.registerAid(new AidBasicArtifice());
        TheorycraftManager.registerAid(new AidBasicInfusion());
        TheorycraftManager.registerAid(new AidBasicAuromancy());
        TheorycraftManager.registerAid(new AidBasicGolemancy());
        TheorycraftManager.registerAid(new AidBasicEldritch());
        TheorycraftManager.registerAid(new AidEnchantmentTable());
        TheorycraftManager.registerAid(new AidBeacon());
        TheorycraftManager.registerCard(CardStudy.class);
        TheorycraftManager.registerCard(CardAnalyze.class);
        TheorycraftManager.registerCard(CardBalance.class);
        TheorycraftManager.registerCard(CardNotation.class);
        TheorycraftManager.registerCard(CardPonder.class);
        TheorycraftManager.registerCard(CardRethink.class);
        TheorycraftManager.registerCard(CardReject.class);
        TheorycraftManager.registerCard(CardExperimentation.class);
        TheorycraftManager.registerCard(CardCurio.class);
        TheorycraftManager.registerCard(CardInspired.class);
        TheorycraftManager.registerCard(CardEnchantment.class);
        TheorycraftManager.registerCard(CardBeacon.class);
        TheorycraftManager.registerCard(CardCelestial.class);
        TheorycraftManager.registerCard(CardConcentrate.class);
        TheorycraftManager.registerCard(CardReactions.class);
        TheorycraftManager.registerCard(CardSynthesis.class);
        TheorycraftManager.registerCard(CardCalibrate.class);
        TheorycraftManager.registerCard(CardMindOverMatter.class);
        TheorycraftManager.registerCard(CardTinker.class);
        TheorycraftManager.registerCard(CardMeasure.class);
        TheorycraftManager.registerCard(CardChannel.class);
        TheorycraftManager.registerCard(CardInfuse.class);
        TheorycraftManager.registerCard(CardFocus.class);
        TheorycraftManager.registerCard(CardAwareness.class);
        TheorycraftManager.registerCard(CardSpellbinding.class);
        TheorycraftManager.registerCard(CardSculpting.class);
        TheorycraftManager.registerCard(CardScripting.class);
        TheorycraftManager.registerCard(CardSynergy.class);
        TheorycraftManager.registerCard(CardDarkWhispers.class);
        TheorycraftManager.registerCard(CardGlyphs.class);
        TheorycraftManager.registerCard(CardPortal.class);
        TheorycraftManager.registerCard(CardRevelation.class);
        TheorycraftManager.registerCard(CardRealization.class);
    }
    
    private static void initWarp() {
        ThaumcraftApi.addWarpToItem(new ItemStack(BlocksTC.jarBrain), 1);
    }
    
    private static void initGolemancyResearch() {
    }
    
    private static void initEldritchResearch() {
    }
    
    public static void checkPeriodicStuff(Player player) {
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        // FIXME: // FIXME: Biome biome = player.level().getBiome(player.blockPosition());
        // FIXME: // FIXME: if (!knowledge.isResearchKnown("m_hellandback") && false /* BiomeDictionary removed */) {
            knowledge.addResearch("m_hellandback");
            knowledge.sync((net.minecraft.server.level.ServerPlayer)player);
            player.sendOverlayMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("got.hellandback")));
        // FIXME: } // end of BiomeDictionary block
        // FIXME: // FIXME: if (!knowledge.isResearchKnown("m_endoftheworld") && false /* BiomeDictionary removed */) {
            knowledge.addResearch("m_endoftheworld");
            knowledge.sync((net.minecraft.server.level.ServerPlayer)player);
            player.sendOverlayMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("got.endoftheworld")));
        // FIXME: } // end of BiomeDictionary block
        if (knowledge.isResearchKnown("UNLOCKAUROMANCY@1") && !knowledge.isResearchKnown("UNLOCKAUROMANCY@2")) {
            if (player.getY() < 10.0 && !knowledge.isResearchKnown("m_deepdown")) {
                knowledge.addResearch("m_deepdown");
                knowledge.sync((net.minecraft.server.level.ServerPlayer)player);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("got.deepdown")));
            }
            if (player.getY() > player.level().getMaxY() * 0.4 && !knowledge.isResearchKnown("m_uphigh")) {
                knowledge.addResearch("m_uphigh");
                knowledge.sync((net.minecraft.server.level.ServerPlayer)player);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("got.uphigh")));
            }
        }
        // TODO: player walk/travel stats — getServer()/getPlayerStatsFile removed; port to modern stats API
    }
    
    static {
        ConfigResearch.TCCategories = new String[] { "BASICS", "ALCHEMY", "AUROMANCY", "ARTIFICE", "INFUSION", "GOLEMANCY", "ELDRITCH" };
        BACK_OVER = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_back_over.png");
    }
}
