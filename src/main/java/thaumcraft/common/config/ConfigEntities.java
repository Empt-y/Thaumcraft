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
        // Entities are registered via EntitiesTC (DeferredRegister pattern)
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
        // Entity spawns are registered via JSON biome modifiers in
        // data/neoforge/biome_modifier/thaumcraft_spawns_<category>.json
        // using type "neoforge:add_spawns" with biome tags and SpawnerData entries.
    }
    
    static {
        ConfigEntities.championModWhitelist = new HashMap<Class, Integer>();
    }
}
