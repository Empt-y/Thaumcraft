package thaumcraft.common.config;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
// removed: import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraft.core.Registry;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.basic.BlockBannerTC;
import thaumcraft.common.blocks.basic.BlockBannerTCItem;
import thaumcraft.common.blocks.basic.BlockCandle;
import thaumcraft.common.blocks.basic.BlockMetalTC;
import thaumcraft.common.blocks.basic.BlockPavingStone;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.blocks.basic.BlockPlanksTC;
import thaumcraft.common.blocks.basic.BlockSlabTC;
import thaumcraft.common.blocks.basic.BlockStairsTC;
import thaumcraft.common.blocks.basic.BlockStonePorous;
import thaumcraft.common.blocks.basic.BlockStoneTC;
import thaumcraft.common.blocks.basic.BlockTable;
import thaumcraft.common.blocks.basic.BlockTranslucent;
import thaumcraft.common.blocks.crafting.BlockArcaneWorkbench;
import thaumcraft.common.blocks.crafting.BlockArcaneWorkbenchCharger;
import thaumcraft.common.blocks.crafting.BlockCrucible;
import thaumcraft.common.blocks.crafting.BlockFocalManipulator;
import thaumcraft.common.blocks.crafting.BlockGolemBuilder;
import thaumcraft.common.blocks.crafting.BlockInfusionMatrix;
import thaumcraft.common.blocks.crafting.BlockPatternCrafter;
import thaumcraft.common.blocks.crafting.BlockResearchTable;
import thaumcraft.common.blocks.crafting.BlockThaumatorium;
import thaumcraft.common.blocks.crafting.BlockVoidSiphon;
import thaumcraft.common.blocks.devices.BlockArcaneEar;
import thaumcraft.common.blocks.devices.BlockArcaneEarToggle;
import thaumcraft.common.blocks.devices.BlockBellows;
import thaumcraft.common.blocks.devices.BlockBrainBox;
import thaumcraft.common.blocks.devices.BlockCondenser;
import thaumcraft.common.blocks.devices.BlockCondenserLattice;
import thaumcraft.common.blocks.devices.BlockDioptra;
import thaumcraft.common.blocks.devices.BlockHungryChest;
import thaumcraft.common.blocks.devices.BlockInfernalFurnace;
import thaumcraft.common.blocks.devices.BlockInlay;
import thaumcraft.common.blocks.devices.BlockLamp;
import thaumcraft.common.blocks.devices.BlockLevitator;
import thaumcraft.common.blocks.devices.BlockMirror;
import thaumcraft.common.blocks.devices.BlockMirrorItem;
import thaumcraft.common.blocks.devices.BlockPedestal;
import thaumcraft.common.blocks.devices.BlockPotionSprayer;
import thaumcraft.common.blocks.devices.BlockRechargePedestal;
import thaumcraft.common.blocks.devices.BlockRedstoneRelay;
import thaumcraft.common.blocks.devices.BlockSpa;
import thaumcraft.common.blocks.devices.BlockStabilizer;
import thaumcraft.common.blocks.devices.BlockVisBattery;
import thaumcraft.common.blocks.devices.BlockVisGenerator;
import thaumcraft.common.blocks.devices.BlockWaterJug;
import thaumcraft.common.blocks.essentia.BlockAlembic;
import thaumcraft.common.blocks.essentia.BlockCentrifuge;
import thaumcraft.common.blocks.essentia.BlockEssentiaTransport;
import thaumcraft.common.blocks.essentia.BlockJar;
import thaumcraft.common.blocks.essentia.BlockJarBrainItem;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.blocks.essentia.BlockSmelter;
import thaumcraft.common.blocks.essentia.BlockSmelterAux;
import thaumcraft.common.blocks.essentia.BlockSmelterVent;
import thaumcraft.common.blocks.essentia.BlockTube;
import thaumcraft.common.blocks.misc.BlockBarrier;
import thaumcraft.common.blocks.misc.BlockEffect;
import thaumcraft.common.blocks.misc.BlockFlesh;
import thaumcraft.common.blocks.misc.BlockFluidDeath;
import thaumcraft.common.blocks.misc.BlockFluidPure;
import thaumcraft.common.blocks.misc.BlockHole;
import thaumcraft.common.blocks.misc.BlockNitor;
import thaumcraft.common.blocks.misc.BlockPlaceholder;
import thaumcraft.common.blocks.world.BlockGrassAmbient;
import thaumcraft.common.blocks.world.BlockLoot;
import thaumcraft.common.blocks.world.ore.BlockCrystal;
import thaumcraft.common.blocks.world.ore.BlockOreTC;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.blocks.world.plants.BlockLeavesTC;
import thaumcraft.common.blocks.world.plants.BlockLogsTC;
import thaumcraft.common.blocks.world.plants.BlockPlantCinderpearl;
import thaumcraft.common.blocks.world.plants.BlockPlantShimmerleaf;
import thaumcraft.common.blocks.world.plants.BlockPlantVishroom;
import thaumcraft.common.blocks.world.plants.BlockSaplingTC;
import thaumcraft.common.blocks.world.taint.BlockFluxGoo;
import thaumcraft.common.blocks.world.taint.BlockTaint;
import thaumcraft.common.blocks.world.taint.BlockTaintFeature;
import thaumcraft.common.blocks.world.taint.BlockTaintFibre;
import thaumcraft.common.blocks.world.taint.BlockTaintLog;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import thaumcraft.common.tiles.crafting.TileCrucible;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.common.tiles.crafting.TilePatternCrafter;
import thaumcraft.common.tiles.crafting.TilePedestal;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import thaumcraft.common.tiles.crafting.TileThaumatoriumTop;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import thaumcraft.common.tiles.devices.TileBellows;
import thaumcraft.common.tiles.devices.TileCondenser;
import thaumcraft.common.tiles.devices.TileDioptra;
import thaumcraft.common.tiles.devices.TileHungryChest;
import thaumcraft.common.tiles.devices.TileInfernalFurnace;
import thaumcraft.common.tiles.devices.TileJarBrain;
import thaumcraft.common.tiles.devices.TileLampArcane;
import thaumcraft.common.tiles.devices.TileLampFertility;
import thaumcraft.common.tiles.devices.TileLampGrowth;
import thaumcraft.common.tiles.devices.TileLevitator;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;
import thaumcraft.common.tiles.devices.TilePotionSprayer;
import thaumcraft.common.tiles.devices.TileRechargePedestal;
import thaumcraft.common.tiles.devices.TileRedstoneRelay;
import thaumcraft.common.tiles.devices.TileSpa;
import thaumcraft.common.tiles.devices.TileStabilizer;
import thaumcraft.common.tiles.devices.TileVisGenerator;
import thaumcraft.common.tiles.devices.TileWaterJug;
import thaumcraft.common.tiles.essentia.TileAlembic;
import thaumcraft.common.tiles.essentia.TileCentrifuge;
import thaumcraft.common.tiles.essentia.TileEssentiaInput;
import thaumcraft.common.tiles.essentia.TileEssentiaOutput;
import thaumcraft.common.tiles.essentia.TileJarFillable;
import thaumcraft.common.tiles.essentia.TileJarFillableVoid;
import thaumcraft.common.tiles.essentia.TileSmelter;
import thaumcraft.common.tiles.essentia.TileTube;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;
import thaumcraft.common.tiles.essentia.TileTubeFilter;
import thaumcraft.common.tiles.essentia.TileTubeOneway;
import thaumcraft.common.tiles.essentia.TileTubeRestrict;
import thaumcraft.common.tiles.essentia.TileTubeValve;
import thaumcraft.common.tiles.misc.TileBanner;
import thaumcraft.common.tiles.misc.TileBarrierStone;
import thaumcraft.common.tiles.misc.TileHole;
import thaumcraft.common.tiles.misc.TileNitor;


public class ConfigBlocks
{
    public static void initMisc() {
        // FIXME: setHarvestLevel removed; use block tag #minecraft:needs_stone_tool instead
        // FIXME: setHarvestLevel removed; use block tag #minecraft:needs_iron_tool instead
        BlockUtils.portableHoleBlackList.add("minecraft:bed");
        BlockUtils.portableHoleBlackList.add("minecraft:piston");
        BlockUtils.portableHoleBlackList.add("minecraft:piston_head");
        BlockUtils.portableHoleBlackList.add("minecraft:sticky_piston");
        BlockUtils.portableHoleBlackList.add("minecraft:piston_extension");
        BlockUtils.portableHoleBlackList.add("minecraft:wooden_door");
        BlockUtils.portableHoleBlackList.add("minecraft:spruce_door");
        BlockUtils.portableHoleBlackList.add("minecraft:birch_door");
        BlockUtils.portableHoleBlackList.add("minecraft:jungle_door");
        BlockUtils.portableHoleBlackList.add("minecraft:acacia_door");
        BlockUtils.portableHoleBlackList.add("minecraft:dark_oak_door");
        BlockUtils.portableHoleBlackList.add("minecraft:iron_door");
        BlockUtils.portableHoleBlackList.add("thaumcraft:infernal_furnace");
    }
    
    public static void initBlocks(Object /* IForgeRegistry removed */ iForgeRegistry) {
        BlocksTC.oreAmber = registerBlock(new BlockOreTC("ore_amber").setHardness(1.5f));
        BlocksTC.oreCinnabar = registerBlock(new BlockOreTC("ore_cinnabar").setHardness(2.0f));
        BlocksTC.oreQuartz = registerBlock(new BlockOreTC("ore_quartz").setHardness(3.0f));
        BlocksTC.crystalAir    = registerBlock(new BlockCrystal("crystal_aer",     Aspect.AIR));
        BlocksTC.crystalFire   = registerBlock(new BlockCrystal("crystal_ignis",   Aspect.FIRE));
        BlocksTC.crystalWater  = registerBlock(new BlockCrystal("crystal_aqua",    Aspect.WATER));
        BlocksTC.crystalEarth  = registerBlock(new BlockCrystal("crystal_terra",   Aspect.EARTH));
        BlocksTC.crystalOrder  = registerBlock(new BlockCrystal("crystal_ordo",    Aspect.ORDER));
        BlocksTC.crystalEntropy= registerBlock(new BlockCrystal("crystal_perditio",Aspect.ENTROPY));
        BlocksTC.crystalTaint  = registerBlock(new BlockCrystal("crystal_vitium",  Aspect.FLUX));
        ShardType.AIR.setOre(BlocksTC.crystalAir);
        ShardType.FIRE.setOre(BlocksTC.crystalFire);
        ShardType.WATER.setOre(BlocksTC.crystalWater);
        ShardType.EARTH.setOre(BlocksTC.crystalEarth);
        ShardType.ORDER.setOre(BlocksTC.crystalOrder);
        ShardType.ENTROPY.setOre(BlocksTC.crystalEntropy);
        ShardType.FLUX.setOre(BlocksTC.crystalTaint);
        BlocksTC.stoneArcane = registerBlock(new BlockStoneTC("stone_arcane", true));
        BlocksTC.stoneArcaneBrick = registerBlock(new BlockStoneTC("stone_arcane_brick", true));
        BlocksTC.stoneAncient = registerBlock(new BlockStoneTC("stone_ancient", true));
        BlocksTC.stoneAncientTile = registerBlock(new BlockStoneTC("stone_ancient_tile", false));
        BlocksTC.stoneAncientRock = registerBlock(new BlockStoneTC("stone_ancient_rock", false).setHardness(-1.0f));
        BlocksTC.stoneAncientGlyphed = registerBlock(new BlockStoneTC("stone_ancient_glyphed", false));
        BlocksTC.stoneAncientDoorway = registerBlock(new BlockStoneTC("stone_ancient_doorway", false).setHardness(-1.0f));
        BlocksTC.stoneEldritchTile = registerBlock(new BlockStoneTC("stone_eldritch_tile", true).setHardness(15.0f).setResistance(1000.0f));
        BlocksTC.stonePorous = registerBlock(new BlockStonePorous());
        BlocksTC.stairsArcane = registerBlock(new BlockStairsTC("stairs_arcane", BlocksTC.stoneArcane.defaultBlockState()));
        BlocksTC.stairsArcaneBrick = registerBlock(new BlockStairsTC("stairs_arcane_brick", BlocksTC.stoneArcaneBrick.defaultBlockState()));
        BlocksTC.stairsAncient = registerBlock(new BlockStairsTC("stairs_ancient", BlocksTC.stoneAncient.defaultBlockState()));
        BlocksTC.slabArcaneStone = (SlabBlock)new BlockSlabTC.Half("slab_arcane_stone", null, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.doubleSlabArcaneStone = (SlabBlock)new BlockSlabTC.Double("slab_double_arcane_stone", BlocksTC.slabArcaneStone, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.slabArcaneBrick = (SlabBlock)new BlockSlabTC.Half("slab_arcane_brick", null, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.doubleSlabArcaneBrick = (SlabBlock)new BlockSlabTC.Double("slab_double_arcane_brick", BlocksTC.slabArcaneBrick, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.slabAncient = (SlabBlock)new BlockSlabTC.Half("slab_ancient", null, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.doubleSlabAncient = (SlabBlock)new BlockSlabTC.Double("slab_double_ancient", BlocksTC.slabAncient, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.slabEldritch = (SlabBlock)new BlockSlabTC.Half("slab_eldritch", null, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.doubleSlabEldritch = (SlabBlock)new BlockSlabTC.Double("slab_double_eldritch", BlocksTC.slabEldritch, false).setHardness(2.0f).setResistance(10.0f);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.slabArcaneStone);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabArcaneStone);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.slabArcaneBrick);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabArcaneBrick);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.slabAncient);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabAncient);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.slabEldritch);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabEldritch);
        // FIXME-REGISTRATION: ForgeRegistries.ITEMS.register(null /* removed *//* setRegistryName removed */));
        // FIXME-REGISTRATION: ForgeRegistries.ITEMS.register(null /* removed *//* setRegistryName removed */));
        // FIXME-REGISTRATION: ForgeRegistries.ITEMS.register(null /* removed *//* setRegistryName removed */));
        // FIXME-REGISTRATION: ForgeRegistries.ITEMS.register(null /* removed *//* setRegistryName removed */));
        BlocksTC.saplingGreatwood  = registerBlock("sapling_greatwood",  () -> new BlockSaplingTC(net.minecraft.world.level.block.grower.TreeGrower.OAK, thaumcraft.common.blocks.BlockTC.autoProps(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().sound(net.minecraft.world.level.block.SoundType.GRASS).randomTicks().instabreak().noCollision())));
        BlocksTC.saplingSilverwood = registerBlock("sapling_silverwood", () -> new BlockSaplingTC(net.minecraft.world.level.block.grower.TreeGrower.OAK, thaumcraft.common.blocks.BlockTC.autoProps(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().sound(net.minecraft.world.level.block.SoundType.GRASS).randomTicks().instabreak().noCollision())));
        BlocksTC.logGreatwood      = registerBlock("log_greatwood",      () -> new BlockLogsTC(thaumcraft.common.blocks.BlockTC.autoProps(BlockLogsTC.defaultProps())));
        BlocksTC.logSilverwood     = registerBlock("log_silverwood",     () -> new BlockLogsTC(thaumcraft.common.blocks.BlockTC.autoProps(BlockLogsTC.defaultProps())));
        BlocksTC.leafGreatwood     = registerBlock("leaves_greatwood",   () -> new BlockLeavesTC(0.05f, thaumcraft.common.blocks.BlockTC.autoProps(BlockLeavesTC.defaultProps().mapColor(net.minecraft.world.level.material.MapColor.PLANT))));
        BlocksTC.leafSilverwood    = registerBlock("leaves_silverwood",  () -> new BlockLeavesTC(0.02f, thaumcraft.common.blocks.BlockTC.autoProps(BlockLeavesTC.defaultProps().mapColor(net.minecraft.world.level.material.MapColor.ICE))));
        BlocksTC.shimmerleaf       = registerBlock("shimmerleaf",        BlockPlantShimmerleaf::new);
        BlocksTC.cinderpearl       = registerBlock("cinderpearl",        BlockPlantCinderpearl::new);
        BlocksTC.vishroom          = registerBlock("vishroom",           BlockPlantVishroom::new);
        BlocksTC.plankGreatwood = registerBlock(new BlockPlanksTC("plank_greatwood"));
        BlocksTC.plankSilverwood = registerBlock(new BlockPlanksTC("plank_silverwood"));
        BlocksTC.stairsGreatwood = registerBlock(new BlockStairsTC("stairs_greatwood", BlocksTC.plankGreatwood.defaultBlockState()));
        BlocksTC.stairsSilverwood = registerBlock(new BlockStairsTC("stairs_silverwood", BlocksTC.plankSilverwood.defaultBlockState()));
        BlocksTC.slabGreatwood = (SlabBlock)new BlockSlabTC.Half("slab_greatwood", null, true).setHardness(1.2f).setResistance(2.0f);
        BlocksTC.doubleSlabGreatwood = (SlabBlock)new BlockSlabTC.Double("slab_double_greatwood", BlocksTC.slabGreatwood, true).setHardness(1.2f).setResistance(2.0f);
        BlocksTC.slabSilverwood = (SlabBlock)new BlockSlabTC.Half("slab_silverwood", null, true).setHardness(1.0f).setResistance(2.0f);
        BlocksTC.doubleSlabSilverwood = (SlabBlock)new BlockSlabTC.Double("slab_double_silverwood", BlocksTC.slabSilverwood, true).setHardness(1.0f).setResistance(2.0f);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.slabGreatwood);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabGreatwood);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.slabSilverwood);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabSilverwood);
        // FIXME-REGISTRATION: ForgeRegistries.ITEMS.register(null /* removed *//* setRegistryName removed */));
        // FIXME-REGISTRATION: ForgeRegistries.ITEMS.register(null /* removed *//* setRegistryName removed */));
        BlocksTC.amberBlock = registerBlock(new BlockTranslucent("amber_block"));
        BlocksTC.amberBrick = registerBlock(new BlockTranslucent("amber_brick"));
        BlocksTC.fleshBlock = registerBlock(new BlockFlesh());
        BlocksTC.lootCrateCommon = registerBlock(new BlockLoot(null /* Material removed */, "loot_crate_common", BlockLoot.LootType.COMMON));
        BlocksTC.lootCrateUncommon = registerBlock(new BlockLoot(null /* Material removed */, "loot_crate_uncommon", BlockLoot.LootType.UNCOMMON));
        BlocksTC.lootCrateRare = registerBlock(new BlockLoot(null /* Material removed */, "loot_crate_rare", BlockLoot.LootType.RARE));
        BlocksTC.lootUrnCommon = registerBlock(new BlockLoot(null /* Material removed */, "loot_urn_common", BlockLoot.LootType.COMMON));
        BlocksTC.lootUrnUncommon = registerBlock(new BlockLoot(null /* Material removed */, "loot_urn_uncommon", BlockLoot.LootType.UNCOMMON));
        BlocksTC.lootUrnRare = registerBlock(new BlockLoot(null /* Material removed */, "loot_urn_rare", BlockLoot.LootType.RARE));
        BlocksTC.taintFibre = registerBlock("taint_fibre", BlockTaintFibre::new);
        BlocksTC.taintCrust = registerBlock(new BlockTaint("taint_crust"));
        BlocksTC.taintSoil = registerBlock(new BlockTaint("taint_soil"));
        BlocksTC.taintRock = registerBlock(new BlockTaint("taint_rock"));
        BlocksTC.taintGeyser = registerBlock(new BlockTaint("taint_geyser"));
        BlocksTC.taintFeature = registerBlock(new BlockTaintFeature());
        BlocksTC.taintLog = registerBlock("taint_log", BlockTaintLog::new);
        BlocksTC.grassAmbient = registerBlock("grass_ambient", BlockGrassAmbient::new);
        BlocksTC.tableWood = registerBlock(new BlockTable(null /* Material removed */, "table_wood", SoundType.WOOD).setHardness(2.0f));
        BlocksTC.tableStone = registerBlock(new BlockTable(null /* Material removed */, "table_stone", SoundType.STONE).setHardness(2.5f));
        BlocksTC.pedestalArcane = registerBlock(new BlockPedestal("pedestal_arcane"));
        BlocksTC.pedestalAncient = registerBlock(new BlockPedestal("pedestal_ancient"));
        BlocksTC.pedestalEldritch = registerBlock(new BlockPedestal("pedestal_eldritch"));
        BlocksTC.metalBlockBrass = registerBlock(new BlockMetalTC("metal_brass"));
        BlocksTC.metalBlockThaumium = registerBlock(new BlockMetalTC("metal_thaumium"));
        BlocksTC.metalBlockVoid = registerBlock(new BlockMetalTC("metal_void"));
        BlocksTC.metalAlchemical = registerBlock(new BlockMetalTC("metal_alchemical"));
        BlocksTC.metalAlchemicalAdvanced = registerBlock(new BlockMetalTC("metal_alchemical_advanced"));
        BlocksTC.pavingStoneTravel = registerBlock(new BlockPavingStone("paving_stone_travel"));
        BlocksTC.pavingStoneBarrier = registerBlock(new BlockPavingStone("paving_stone_barrier"));
        BlocksTC.pillarArcane = registerBlock(new BlockPillar("pillar_arcane"));
        BlocksTC.pillarAncient = registerBlock(new BlockPillar("pillar_ancient"));
        BlocksTC.pillarEldritch = registerBlock(new BlockPillar("pillar_eldritch"));
        BlocksTC.matrixSpeed = registerBlock(new BlockStoneTC("matrix_speed", false));
        BlocksTC.matrixCost = registerBlock(new BlockStoneTC("matrix_cost", false));
        for (DyeColor dye : DyeColor.values()) {
            BlocksTC.candles.put(dye, registerBlock(new BlockCandle("candle_" + dye.getName().toLowerCase(), dye)));
        }
        for (DyeColor dye : DyeColor.values()) {
            BlockBannerTC block = new BlockBannerTC("banner_" + dye.getName().toLowerCase(), dye);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(block);
        // FIXME-REGISTRATION: ForgeRegistries.ITEMS.register(new BlockBannerTCItem(block)/* setRegistryName removed */));
            BlocksTC.banners.put(dye, block);
        }
        BlocksTC.bannerCrimsonCult = new BlockBannerTC("banner_crimson_cult", null);
        // FIXME-REGISTRATION: ForgeRegistries.BLOCKS.register(BlocksTC.bannerCrimsonCult);
        // FIXME-REGISTRATION: ForgeRegistries.ITEMS.register(new BlockBannerTCItem((BlockBannerTC)BlocksTC.bannerCrimsonCult)/* setRegistryName removed */));
        for (DyeColor dye : DyeColor.values()) {
            BlocksTC.nitor.put(dye, registerBlock(new BlockNitor("nitor_" + dye.getName().toLowerCase(), dye)));
        }
        BlocksTC.visBattery = registerBlock(new BlockVisBattery());
        BlocksTC.inlay = registerBlock(new BlockInlay());
        BlocksTC.arcaneWorkbench = registerBlock(new BlockArcaneWorkbench());
        BlocksTC.arcaneWorkbenchCharger = registerBlock(new BlockArcaneWorkbenchCharger());
        BlocksTC.dioptra = registerBlock(new BlockDioptra());
        BlocksTC.researchTable = registerBlock(new BlockResearchTable());
        BlocksTC.crucible = registerBlock(new BlockCrucible());
        BlocksTC.arcaneEar = registerBlock(new BlockArcaneEar("arcane_ear"));
        BlocksTC.arcaneEarToggle = registerBlock(new BlockArcaneEarToggle());
        BlocksTC.lampArcane = registerBlock(new BlockLamp(TileLampArcane.class, "lamp_arcane"));
        BlocksTC.lampFertility = registerBlock(new BlockLamp(TileLampFertility.class, "lamp_fertility"));
        BlocksTC.lampGrowth = registerBlock(new BlockLamp(TileLampGrowth.class, "lamp_growth"));
        BlocksTC.levitator = registerBlock(new BlockLevitator());
        BlocksTC.centrifuge = registerBlock(new BlockCentrifuge());
        BlocksTC.bellows = registerBlock(new BlockBellows());
        BlocksTC.smelterBasic = registerBlock(new BlockSmelter("smelter_basic"));
        BlocksTC.smelterThaumium = registerBlock(new BlockSmelter("smelter_thaumium"));
        BlocksTC.smelterVoid = registerBlock(new BlockSmelter("smelter_void"));
        BlocksTC.smelterAux = registerBlock(new BlockSmelterAux());
        BlocksTC.smelterVent = registerBlock(new BlockSmelterVent());
        BlocksTC.alembic = registerBlock(new BlockAlembic());
        BlocksTC.rechargePedestal = registerBlock(new BlockRechargePedestal());
        BlocksTC.wandWorkbench = registerBlock(new BlockFocalManipulator());
        BlocksTC.hungryChest = registerBlock(new BlockHungryChest());
        BlocksTC.tube = registerBlock(new BlockTube(TileTube.class, "tube"));
        BlocksTC.tubeValve = registerBlock(new BlockTube(TileTubeValve.class, "tube_valve"));
        BlocksTC.tubeRestrict = registerBlock(new BlockTube(TileTubeRestrict.class, "tube_restrict"));
        BlocksTC.tubeOneway = registerBlock(new BlockTube(TileTubeOneway.class, "tube_oneway"));
        BlocksTC.tubeFilter = registerBlock(new BlockTube(TileTubeFilter.class, "tube_filter"));
        BlocksTC.tubeBuffer = registerBlock(new BlockTube(TileTubeBuffer.class, "tube_buffer"));
        BlocksTC.jarNormal = registerBlock(new BlockJar(TileJarFillable.class, "jar_normal"), BlockJarItem.class);
        BlocksTC.jarVoid = registerBlock(new BlockJar(TileJarFillableVoid.class, "jar_void"), BlockJarItem.class);
        BlocksTC.jarBrain = registerBlock(new BlockJar(TileJarBrain.class, "jar_brain"), BlockJarBrainItem.class);
        BlocksTC.infusionMatrix = registerBlock(new BlockInfusionMatrix());
        BlocksTC.infernalFurnace = registerBlock(new BlockInfernalFurnace());
        BlocksTC.everfullUrn = registerBlock(new BlockWaterJug());
        BlocksTC.thaumatorium = registerBlock(new BlockThaumatorium(false));
        BlocksTC.thaumatoriumTop = registerBlock(new BlockThaumatorium(true));
        BlocksTC.brainBox = registerBlock(new BlockBrainBox());
        BlocksTC.spa = registerBlock(new BlockSpa());
        BlocksTC.golemBuilder = registerBlock(new BlockGolemBuilder());
        BlocksTC.mirror = registerBlock(new BlockMirror(TileMirror.class, "mirror"), BlockMirrorItem.class);
        BlocksTC.mirrorEssentia = registerBlock(new BlockMirror(TileMirrorEssentia.class, "mirror_essentia"), BlockMirrorItem.class);
        BlocksTC.essentiaTransportInput = registerBlock(new BlockEssentiaTransport(TileEssentiaInput.class, "essentia_input"));
        BlocksTC.essentiaTransportOutput = registerBlock(new BlockEssentiaTransport(TileEssentiaOutput.class, "essentia_output"));
        BlocksTC.redstoneRelay = registerBlock(new BlockRedstoneRelay());
        BlocksTC.patternCrafter = registerBlock(new BlockPatternCrafter());
        BlocksTC.potionSprayer = registerBlock(new BlockPotionSprayer());
        BlocksTC.activatorRail = registerBlock(new PoweredRailBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().strength(0.7f)));
        BlocksTC.stabilizer = registerBlock(new BlockStabilizer());
        BlocksTC.visGenerator = registerBlock(new BlockVisGenerator());
        BlocksTC.condenser = registerBlock(new BlockCondenser());
        BlocksTC.condenserlattice = registerBlock(new BlockCondenserLattice(false));
        BlocksTC.condenserlatticeDirty = registerBlock(new BlockCondenserLattice(true));
        BlocksTC.voidSiphon = registerBlock(new BlockVoidSiphon());
        // FIXME-REGISTRATION: FluidRegistry.registerFluid(FluidFluxGoo.instance);
        // FIXME-REGISTRATION: iForgeRegistry.register((BlocksTC.fluxGoo = new BlockFluxGoo()));
        // FIXME-REGISTRATION: FluidRegistry.registerFluid(FluidDeath.instance);
        // FIXME-REGISTRATION: FluidRegistry.addBucketForFluid(FluidDeath.instance);
        // FIXME-REGISTRATION: iForgeRegistry.register((BlocksTC.liquidDeath = new BlockFluidDeath()));
        // FIXME-REGISTRATION: FluidRegistry.registerFluid(FluidPure.instance);
        // FIXME-REGISTRATION: FluidRegistry.addBucketForFluid(FluidPure.instance);
        // FIXME-REGISTRATION: iForgeRegistry.register((BlocksTC.purifyingFluid = new BlockFluidPure()));
        BlocksTC.hole = registerBlock(new BlockHole());
        BlocksTC.effectShock = registerBlock(new BlockEffect("effect_shock"));
        BlocksTC.effectSap = registerBlock(new BlockEffect("effect_sap"));
        BlocksTC.effectGlimmer = registerBlock(new BlockEffect("effect_glimmer"));
        BlocksTC.placeholderNetherbrick = registerBlock(new BlockPlaceholder());
        BlocksTC.placeholderObsidian = registerBlock(new BlockPlaceholder());
        BlocksTC.placeholderBars = registerBlock(new BlockPlaceholder());
        BlocksTC.placeholderAnvil = registerBlock(new BlockPlaceholder());
        BlocksTC.placeholderCauldron = registerBlock(new BlockPlaceholder());
        BlocksTC.placeholderTable = registerBlock(new BlockPlaceholder());
        BlocksTC.empty = registerBlock(new BlockTranslucent("empty"));
        BlocksTC.barrier = registerBlock(new BlockBarrier());
    }
    
    public static void initTileEntities() {
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileArcaneWorkbench.class, "thaumcraft:TileArcaneWorkbench");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileDioptra.class, "thaumcraft:TileDioptra");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileArcaneEar.class, "thaumcraft:TileArcaneEar");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileLevitator.class, "thaumcraft:TileLevitator");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileCrucible.class, "thaumcraft:TileCrucible");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileNitor.class, "thaumcraft:TileNitor");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileFocalManipulator.class, "thaumcraft:TileFocalManipulator");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TilePedestal.class, "thaumcraft:TilePedestal");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileRechargePedestal.class, "thaumcraft:TileRechargePedestal");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileResearchTable.class, "thaumcraft:TileResearchTable");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileTube.class, "thaumcraft:TileTube");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileTubeValve.class, "thaumcraft:TileTubeValve");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileTubeFilter.class, "thaumcraft:TileTubeFilter");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileTubeRestrict.class, "thaumcraft:TileTubeRestrict");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileTubeOneway.class, "thaumcraft:TileTubeOneway");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileTubeBuffer.class, "thaumcraft:TileTubeBuffer");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileHungryChest.class, "thaumcraft:TileChestHungry");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileCentrifuge.class, "thaumcraft:TileCentrifuge");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileJarFillable.class, "thaumcraft:TileJar");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileJarFillableVoid.class, "thaumcraft:TileJarVoid");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileJarBrain.class, "thaumcraft:TileJarBrain");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileBellows.class, "thaumcraft:TileBellows");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileSmelter.class, "thaumcraft:TileSmelter");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileAlembic.class, "thaumcraft:TileAlembic");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileInfusionMatrix.class, "thaumcraft:TileInfusionMatrix");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileWaterJug.class, "thaumcraft:TileWaterJug");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileInfernalFurnace.class, "thaumcraft:TileInfernalFurnace");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileThaumatorium.class, "thaumcraft:TileThaumatorium");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileThaumatoriumTop.class, "thaumcraft:TileThaumatoriumTop");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileSpa.class, "thaumcraft:TileSpa");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileLampGrowth.class, "thaumcraft:TileLampGrowth");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileLampArcane.class, "thaumcraft:TileLampArcane");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileLampFertility.class, "thaumcraft:TileLampFertility");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileMirror.class, "thaumcraft:TileMirror");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileMirrorEssentia.class, "thaumcraft:TileMirrorEssentia");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileRedstoneRelay.class, "thaumcraft:TileRedstoneRelay");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileGolemBuilder.class, "thaumcraft:TileGolemBuilder");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileEssentiaInput.class, "thaumcraft:TileEssentiaInput");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileEssentiaOutput.class, "thaumcraft:TileEssentiaOutput");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TilePatternCrafter.class, "thaumcraft:TilePatternCrafter");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TilePotionSprayer.class, "thaumcraft:TilePotionSprayer");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileVisGenerator.class, "thaumcraft:TileVisGenerator");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileStabilizer.class, "thaumcraft:TileStabilizer");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileCondenser.class, "thaumcraft:TileCondenser");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileVoidSiphon.class, "thaumcraft:TileVoidSiphon");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileBanner.class, "thaumcraft:TileBanner");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileHole.class, "thaumcraft:TileHole");
        // FIXME-REGISTRATION: GameRegistry.registerBlockEntity(TileBarrierStone.class, "thaumcraft:TileBarrierStone");
    }
    
    /** Register a block using a factory so PENDING_NAME is set before construction. */
    private static Block registerBlock(String name, java.util.function.Supplier<Block> factory) {
        thaumcraft.common.blocks.BlockTC.PENDING_NAME.set(name);
        Block block = factory.get();
        thaumcraft.common.blocks.BlockTC.PENDING_NAME.remove();
        // Store name on BlockTC subclasses; for non-BlockTC use name from pending
        if (block instanceof thaumcraft.common.blocks.BlockTC tc && tc.getTCRegistryName() == null) {
            tc.setTCRegistryName(name);
        }
        return registerBlockByName(name, block);
    }

    private static Block registerBlock(Block block) {
        String name = null;
        if (block instanceof thaumcraft.common.blocks.BlockTC tc) name = tc.getTCRegistryName();
        if (name == null) {
            // For non-BlockTC blocks that expose getTCRegistryName via duck-typing
            try { name = (String) block.getClass().getMethod("getTCRegistryName").invoke(block); } catch (Exception ignored) {}
        }
        if (name != null) return registerBlockByName(name, block);
        return block;
    }

    private static Block registerBlockByName(String name, Block block) {
        net.minecraft.resources.Identifier rl = net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", name);
        net.minecraft.resources.ResourceKey<net.minecraft.world.item.Item> itemKey =
            net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, rl);
        BlockItem itemBlock = new BlockItem(block, new net.minecraft.world.item.Item.Properties().setId(itemKey));
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.BLOCK, rl, block);
        net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.ITEM, rl, itemBlock);
        return block;
    }

    private static Block registerBlock(Block block, BlockItem itemBlock) {
        if (block instanceof thaumcraft.common.blocks.BlockTC tc && tc.getTCRegistryName() != null) {
            net.minecraft.resources.Identifier rl = net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", tc.getTCRegistryName());
            net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.BLOCK, rl, block);
            net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.ITEM, rl, itemBlock);
        }
        return block;
    }

    private static Block registerBlock(Block block, Class clazz) {
        try {
            BlockItem itemBlock = (BlockItem) clazz.getConstructors()[0].newInstance(block, new net.minecraft.world.item.Item.Properties());
            registerBlock(block, itemBlock);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return block;
    }
    
    public static class FluidPure extends net.minecraft.world.level.material.Fluid
    {
        public static FluidPure instance;
        public static String name = "purifying_fluid";

        @Override
        public net.minecraft.world.item.Item getBucket() {
            return net.minecraft.world.item.Items.BUCKET;
        }
        @Override
        protected boolean canBeReplacedWith(net.minecraft.world.level.material.FluidState s, net.minecraft.world.level.BlockGetter w, net.minecraft.core.BlockPos p, net.minecraft.world.level.material.Fluid f, net.minecraft.core.Direction d) {
            return false;
        }
        @Override
        protected net.minecraft.world.phys.Vec3 getFlow(net.minecraft.world.level.BlockGetter w, net.minecraft.core.BlockPos p, net.minecraft.world.level.material.FluidState s) {
            return net.minecraft.world.phys.Vec3.ZERO;
        }
        @Override
        public int getTickDelay(net.minecraft.world.level.LevelReader l) { return 5; }
        @Override
        protected float getExplosionResistance() { return 100f; }
        @Override
        public float getHeight(net.minecraft.world.level.material.FluidState s, net.minecraft.world.level.BlockGetter w, net.minecraft.core.BlockPos p) { return 0.875f; }
        @Override
        public float getOwnHeight(net.minecraft.world.level.material.FluidState s) { return 0.875f; }
        @Override
        protected net.minecraft.world.level.block.state.BlockState createLegacyBlock(net.minecraft.world.level.material.FluidState s) {
            return net.minecraft.world.level.block.Blocks.WATER.defaultBlockState();
        }
        @Override
        public boolean isSource(net.minecraft.world.level.material.FluidState s) { return false; }
        @Override
        public int getAmount(net.minecraft.world.level.material.FluidState s) { return 8; }
        @Override
        public net.minecraft.world.phys.shapes.VoxelShape getShape(net.minecraft.world.level.material.FluidState s, net.minecraft.world.level.BlockGetter w, net.minecraft.core.BlockPos p) {
            return net.minecraft.world.phys.shapes.Shapes.empty();
        }

        static { instance = new FluidPure(); }
    }
    
    public static class FluidDeath extends net.minecraft.world.level.material.Fluid
    {
        public static FluidDeath instance;
        public static String name = "liquid_death";

        @Override
        public net.minecraft.world.item.Item getBucket() {
            return net.minecraft.world.item.Items.BUCKET;
        }
        @Override
        protected boolean canBeReplacedWith(net.minecraft.world.level.material.FluidState s, net.minecraft.world.level.BlockGetter w, net.minecraft.core.BlockPos p, net.minecraft.world.level.material.Fluid f, net.minecraft.core.Direction d) {
            return false;
        }
        @Override
        protected net.minecraft.world.phys.Vec3 getFlow(net.minecraft.world.level.BlockGetter w, net.minecraft.core.BlockPos p, net.minecraft.world.level.material.FluidState s) {
            return net.minecraft.world.phys.Vec3.ZERO;
        }
        @Override
        public int getTickDelay(net.minecraft.world.level.LevelReader l) { return 5; }
        @Override
        protected float getExplosionResistance() { return 100f; }
        @Override
        public float getHeight(net.minecraft.world.level.material.FluidState s, net.minecraft.world.level.BlockGetter w, net.minecraft.core.BlockPos p) { return 0.875f; }
        @Override
        public float getOwnHeight(net.minecraft.world.level.material.FluidState s) { return 0.875f; }
        @Override
        protected net.minecraft.world.level.block.state.BlockState createLegacyBlock(net.minecraft.world.level.material.FluidState s) {
            return net.minecraft.world.level.block.Blocks.WATER.defaultBlockState();
        }
        @Override
        public boolean isSource(net.minecraft.world.level.material.FluidState s) { return false; }
        @Override
        public int getAmount(net.minecraft.world.level.material.FluidState s) { return 8; }
        @Override
        public net.minecraft.world.phys.shapes.VoxelShape getShape(net.minecraft.world.level.material.FluidState s, net.minecraft.world.level.BlockGetter w, net.minecraft.core.BlockPos p) {
            return net.minecraft.world.phys.shapes.Shapes.empty();
        }

        static { instance = new FluidDeath(); }
    }
    
    public static class FluidFluxGoo extends net.minecraft.world.level.material.Fluid
    {
        public static FluidFluxGoo instance;
        public static String name = "flux_goo";

        @Override
        public net.minecraft.world.item.Item getBucket() {
            return net.minecraft.world.item.Items.BUCKET;
        }
        @Override
        protected boolean canBeReplacedWith(net.minecraft.world.level.material.FluidState s, net.minecraft.world.level.BlockGetter w, net.minecraft.core.BlockPos p, net.minecraft.world.level.material.Fluid f, net.minecraft.core.Direction d) {
            return false;
        }
        @Override
        protected net.minecraft.world.phys.Vec3 getFlow(net.minecraft.world.level.BlockGetter w, net.minecraft.core.BlockPos p, net.minecraft.world.level.material.FluidState s) {
            return net.minecraft.world.phys.Vec3.ZERO;
        }
        @Override
        public int getTickDelay(net.minecraft.world.level.LevelReader l) { return 5; }
        @Override
        protected float getExplosionResistance() { return 100f; }
        @Override
        public float getHeight(net.minecraft.world.level.material.FluidState s, net.minecraft.world.level.BlockGetter w, net.minecraft.core.BlockPos p) { return 0.875f; }
        @Override
        public float getOwnHeight(net.minecraft.world.level.material.FluidState s) { return 0.875f; }
        @Override
        protected net.minecraft.world.level.block.state.BlockState createLegacyBlock(net.minecraft.world.level.material.FluidState s) {
            return net.minecraft.world.level.block.Blocks.WATER.defaultBlockState();
        }
        @Override
        public boolean isSource(net.minecraft.world.level.material.FluidState s) { return false; }
        @Override
        public int getAmount(net.minecraft.world.level.material.FluidState s) { return 8; }
        @Override
        public net.minecraft.world.phys.shapes.VoxelShape getShape(net.minecraft.world.level.material.FluidState s, net.minecraft.world.level.BlockGetter w, net.minecraft.core.BlockPos p) {
            return net.minecraft.world.phys.shapes.Shapes.empty();
        }

        static { instance = new FluidFluxGoo(); }
    }
}
