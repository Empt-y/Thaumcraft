package thaumcraft.common.config;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.EnderMan;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
// removed: import net.minecraftforge.common.BiomeDictionary;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraft.world.entity.EntityType;
// removed: import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraft.core.Registry;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFallingTaint;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.entities.EntityFollowingItem;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.entities.construct.EntityArcaneBore;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;
import thaumcraft.common.entities.construct.EntityTurretCrossbowAdvanced;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityEldritchCrab;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityFireBat;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import thaumcraft.common.entities.monster.EntityMindSpider;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntitySpellBat;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;
import thaumcraft.common.entities.monster.boss.EntityCultistPortalGreater;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;
import thaumcraft.common.entities.monster.boss.EntityTaintacleGiant;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeedPrime;
import thaumcraft.common.entities.monster.tainted.EntityTaintSwarm;
import thaumcraft.common.entities.monster.tainted.EntityTaintacle;
import thaumcraft.common.entities.monster.tainted.EntityTaintacleSmall;
import thaumcraft.common.entities.projectile.EntityAlumentum;
import thaumcraft.common.entities.projectile.EntityBottleTaint;
import thaumcraft.common.entities.projectile.EntityCausalityCollapser;
import thaumcraft.common.entities.projectile.EntityEldritchOrb;
import thaumcraft.common.entities.projectile.EntityFocusCloud;
import thaumcraft.common.entities.projectile.EntityFocusMine;
import thaumcraft.common.entities.projectile.EntityFocusProjectile;
import thaumcraft.common.entities.projectile.EntityGolemDart;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thaumcraft.common.entities.projectile.EntityGrapple;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import net.minecraft.world.entity.monster.zombie.Zombie;


public class ConfigEntities
{
    public static HashMap<Class, Integer> championModWhitelist;
    
    public static void initEntities(Object /* IForgeRegistry removed */ iForgeRegistry) {
        int id = 0;
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "CultistPortalGreater"), EntityCultistPortalGreater.class, "CultistPortalGreater", id++, Thaumcraft.instance, 64, 20, false, 6842578, 32896);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "CultistPortalLesser"), EntityCultistPortalLesser.class, "CultistPortalLesser", id++, Thaumcraft.instance, 64, 20, false, 9438728, 6316242);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "FluxRift"), EntityFluxRift.class, "FluxRift", id++, Thaumcraft.instance, 64, 20, false);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "SpecialItem"), EntitySpecialItem.class, "SpecialItem", id++, Thaumcraft.instance, 64, 20, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "FollowItem"), EntityFollowingItem.class, "FollowItem", id++, Thaumcraft.instance, 64, 20, false);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "FallingTaint"), EntityFallingTaint.class, "FallingTaint", id++, Thaumcraft.instance, 64, 3, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "Alumentum"), EntityAlumentum.class, "Alumentum", id++, Thaumcraft.instance, 64, 20, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "GolemDart"), EntityGolemDart.class, "GolemDart", id++, Thaumcraft.instance, 64, 20, false);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "EldritchOrb"), EntityEldritchOrb.class, "EldritchOrb", id++, Thaumcraft.instance, 64, 20, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "BottleTaint"), EntityBottleTaint.class, "BottleTaint", id++, Thaumcraft.instance, 64, 20, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "GolemOrb"), EntityGolemOrb.class, "GolemOrb", id++, Thaumcraft.instance, 64, 3, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "Grapple"), EntityGrapple.class, "Grapple", id++, Thaumcraft.instance, 64, 20, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "CausalityCollapser"), EntityCausalityCollapser.class, "CausalityCollapser", id++, Thaumcraft.instance, 64, 20, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "FocusProjectile"), EntityFocusProjectile.class, "FocusProjectile", id++, Thaumcraft.instance, 64, 20, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "FocusCloud"), EntityFocusCloud.class, "FocusCloud", id++, Thaumcraft.instance, 64, 20, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "Focusmine"), EntityFocusMine.class, "Focusmine", id++, Thaumcraft.instance, 64, 20, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "TurretBasic"), EntityTurretCrossbow.class, "TurretBasic", id++, Thaumcraft.instance, 64, 3, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "TurretAdvanced"), EntityTurretCrossbowAdvanced.class, "TurretAdvanced", id++, Thaumcraft.instance, 64, 3, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "ArcaneBore"), EntityArcaneBore.class, "ArcaneBore", id++, Thaumcraft.instance, 64, 3, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "Golem"), EntityThaumcraftGolem.class, "Golem", id++, Thaumcraft.instance, 64, 3, true);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "EldritchWarden"), EntityEldritchWarden.class, "EldritchWarden", id++, Thaumcraft.instance, 64, 3, true, 6842578, 8421504);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "EldritchGolem"), EntityEldritchGolem.class, "EldritchGolem", id++, Thaumcraft.instance, 64, 3, true, 6842578, 8947848);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "CultistLeader"), EntityCultistLeader.class, "CultistLeader", id++, Thaumcraft.instance, 64, 3, true, 6842578, 9438728);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "TaintacleGiant"), EntityTaintacleGiant.class, "TaintacleGiant", id++, Thaumcraft.instance, 96, 3, false, 6842578, 10618530);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "BrainyZombie"), EntityBrainyZombie.class, "BrainyZombie", id++, Thaumcraft.instance, 64, 3, true, -16129, -16744448);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "GiantBrainyZombie"), EntityGiantBrainyZombie.class, "GiantBrainyZombie", id++, Thaumcraft.instance, 64, 3, true, -16129, -16760832);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "Wisp"), EntityWisp.class, "Wisp", id++, Thaumcraft.instance, 64, 3, false, -16129, -1);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "Firebat"), EntityFireBat.class, "Firebat", id++, Thaumcraft.instance, 64, 3, false, -16129, -806354944);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "Spellbat"), EntitySpellBat.class, "Spellbat", id++, Thaumcraft.instance, 64, 3, false, -16129, -806354944);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "Pech"), EntityPech.class, "Pech", id++, Thaumcraft.instance, 64, 3, true, -16129, -12582848);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "MindSpider"), EntityMindSpider.class, "MindSpider", id++, Thaumcraft.instance, 64, 3, true, 4996656, 4473924);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "EldritchGuardian"), EntityEldritchGuardian.class, "EldritchGuardian", id++, Thaumcraft.instance, 64, 3, true, 8421504, 0);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "CultistKnight"), EntityCultistKnight.class, "CultistKnight", id++, Thaumcraft.instance, 64, 3, true, 9438728, 128);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "CultistCleric"), EntityCultistCleric.class, "CultistCleric", id++, Thaumcraft.instance, 64, 3, true, 9438728, 8388608);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "EldritchCrab"), EntityEldritchCrab.class, "EldritchCrab", id++, Thaumcraft.instance, 64, 3, true, 8421504, 5570560);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "InhabitedZombie"), EntityInhabitedZombie.class, "InhabitedZombie", id++, Thaumcraft.instance, 64, 3, true, 8421504, 5570560);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "ThaumSlime"), EntityThaumicSlime.class, "ThaumSlime", id++, Thaumcraft.instance, 64, 3, true, 10618530, -32513);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "TaintCrawler"), EntityTaintCrawler.class, "TaintCrawler", id++, Thaumcraft.instance, 64, 3, true, 10618530, 3158064);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "Taintacle"), EntityTaintacle.class, "Taintacle", id++, Thaumcraft.instance, 64, 3, false, 10618530, 4469572);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "TaintacleTiny"), EntityTaintacleSmall.class, "TaintacleTiny", id++, Thaumcraft.instance, 64, 3, false);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "TaintSwarm"), EntityTaintSwarm.class, "TaintSwarm", id++, Thaumcraft.instance, 64, 3, false, 10618530, 16744576);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "TaintSeed"), EntityTaintSeed.class, "TaintSeed", id++, Thaumcraft.instance, 64, 20, false, 10618530, 4465237);
        // FIXME-REGISTRATION: EntityRegistry.registerModEntity(Identifier.fromNamespaceAndPath("thaumcraft", "TaintSeedPrime"), EntityTaintSeedPrime.class, "TaintSeedPrime", id++, Thaumcraft.instance, 64, 20, false, 10618530, 5583718);
        EntityPech.valuedItems.put(net.minecraft.core.registries.BuiltInRegistries.ITEM.getId(Items.ENDER_PEARL), 15);
        ArrayList<List> forInv = new ArrayList<List>();
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters)));
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters)));
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters)));
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters)));
        if (ModConfig.foundCopperIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters)));
        }
        if (ModConfig.foundTinIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters)));
        }
        if (ModConfig.foundSilverIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters)));
        }
        if (ModConfig.foundLeadIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters)));
        }
        forInv.add(Arrays.asList(2, new ItemStack(Items.BLAZE_ROD)));
        forInv.add(Arrays.asList(2, new ItemStack(BlocksTC.saplingGreatwood)));
        forInv.add(Arrays.asList(2, new ItemStack(Items.DRAGON_BREATH)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.GOLDEN_APPLE)));
        forInv.add(Arrays.asList(4, new ItemStack(ItemsTC.thaumiumPick)));
        forInv.add(Arrays.asList(4, new ItemStack(ItemsTC.thaumiumAxe)));
        forInv.add(Arrays.asList(4, new ItemStack(ItemsTC.thaumiumHoe)));
        forInv.add(Arrays.asList(5, new ItemStack(Items.GOLDEN_APPLE)));
        forInv.add(Arrays.asList(5, new ItemStack(BlocksTC.saplingSilverwood)));
        forInv.add(Arrays.asList(5, new ItemStack(ItemsTC.curio)));
        EntityPech.tradeInventory.put(0, forInv);
        ArrayList<List> forMag = new ArrayList<List>();
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.AIR)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.EARTH)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.FIRE)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.WATER)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.ORDER)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY)));
        forMag.add(Arrays.asList(2, new ItemStack(Items.POTION)));
        forMag.add(Arrays.asList(2, new ItemStack(Items.POTION)));
        forMag.add(Arrays.asList(2, ThaumcraftApiHelper.makeCrystal(Aspect.FLUX)));
        forMag.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forMag.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forMag.add(Arrays.asList(3, ThaumcraftApiHelper.makeCrystal(Aspect.AURA)));
        forMag.add(Arrays.asList(3, new ItemStack(Items.GOLDEN_APPLE)));
        forMag.add(Arrays.asList(4, new ItemStack(ItemsTC.clothBoots)));
        forMag.add(Arrays.asList(4, new ItemStack(ItemsTC.clothChest)));
        forMag.add(Arrays.asList(4, new ItemStack(ItemsTC.clothLegs)));
        forMag.add(Arrays.asList(5, new ItemStack(Items.GOLDEN_APPLE)));
        forMag.add(Arrays.asList(5, new ItemStack(ItemsTC.pechWand)));
        forMag.add(Arrays.asList(5, new ItemStack(ItemsTC.curio)));
        forMag.add(Arrays.asList(5, new ItemStack(ItemsTC.amuletVis)));
        forInv.add(Arrays.asList(5, new ItemStack(Items.TOTEM_OF_UNDYING)));
        EntityPech.tradeInventory.put(1, forMag);
        ArrayList<List> forArc = new ArrayList<List>();
        for (int a = 0; a < 15; ++a) {
            forArc.add(Arrays.asList(1, new ItemStack(BlocksTC.candles.get(DyeColor.values()[a % DyeColor.values().length]))));
        }
        forArc.add(Arrays.asList(2, new ItemStack(Items.GHAST_TEAR)));
        forInv.add(Arrays.asList(2, new ItemStack(Items.COMPASS)));
        // forArc.add enchanted book entries removed (EnchantedBookItem removed)
        forArc.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forArc.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forArc.add(Arrays.asList(3, new ItemStack(Items.GOLDEN_APPLE)));
        forArc.add(Arrays.asList(4, new ItemStack(ItemsTC.eldritchEye)));
        forArc.add(Arrays.asList(4, new ItemStack(Items.GOLDEN_APPLE)));
        forInv.add(Arrays.asList(4, new ItemStack(Items.SPECTRAL_ARROW)));
        forArc.add(Arrays.asList(5, new ItemStack(ItemsTC.baubles)));
        // forArc.add enchanted book entries removed (EnchantedBookItem removed)
        forArc.add(Arrays.asList(5, new ItemStack(ItemsTC.curio)));
        EntityPech.tradeInventory.put(2, forArc);
    }
    
    public static void postInitEntitySpawns() {
        // FIXME: All spawn registration uses old Forge API (BiomeManager, BiomeDictionary, EntityRegistry.addSpawn, FMLInterModComms)
        // FIXME: Port to NeoForge 26.x spawn mechanics using biome modifiers and SpawnModifier events
    }
    
    static {
        ConfigEntities.championModWhitelist = new HashMap<Class, Integer>();
    }
}
