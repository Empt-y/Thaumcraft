package thaumcraft.common.config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.world.level.block.Block;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.minecraft.core.Registry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.IngredientNBTTC;
import thaumcraft.api.crafting.Part;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.lib.crafting.DustTriggerMultiblock;
import thaumcraft.common.lib.crafting.DustTriggerOre;
import thaumcraft.common.lib.crafting.DustTriggerSimple;
import thaumcraft.common.lib.crafting.InfusionEnchantmentRecipe;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import thaumcraft.common.lib.crafting.RecipeMagicDust;
import thaumcraft.common.lib.crafting.RecipeTripleMeatTreat;
import thaumcraft.common.lib.crafting.RecipesRobeArmorDyes;
import thaumcraft.common.lib.crafting.RecipesVoidRobeArmorDyes;
import thaumcraft.common.lib.crafting.ShapedArcaneVoidJar;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;


public class ConfigRecipes
{
    static Identifier defaultGroup;
    public static HashMap<String, ArrayList<Identifier>> recipeGroups;
    
    public static void initializeCompoundRecipes() {
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("!gotdream", Blocks.BOOKSHELF, new ItemStack(ItemsTC.thaumonomicon)));
        IDustTrigger.registerDustTrigger(new DustTriggerOre("!gotdream", "bookshelf", new ItemStack(ItemsTC.thaumonomicon)));
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("FIRSTSTEPS@1", Blocks.CRAFTING_TABLE, new ItemStack(BlocksTC.arcaneWorkbench)));
        IDustTrigger.registerDustTrigger(new DustTriggerOre("FIRSTSTEPS@1", "workbench", new ItemStack(BlocksTC.arcaneWorkbench)));
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("UNLOCKALCHEMY@1", Blocks.CAULDRON, new ItemStack(BlocksTC.crucible)));
        Part NB = new Part(Blocks.NETHER_BRICKS, new ItemStack(BlocksTC.placeholderNetherbrick));
        Part OB = new Part(Blocks.OBSIDIAN, new ItemStack(BlocksTC.placeholderObsidian));
        Part IB = new Part(Blocks.IRON_BARS, "AIR");
        Part LA = new Part(Blocks.LAVA, BlocksTC.infernalFurnace, true);
        Part[][][] infernalFurnaceBlueprint = { { { NB, OB, NB }, { OB, null, OB }, { NB, OB, NB } }, { { NB, OB, NB }, { OB, LA, OB }, { NB, IB, NB } }, { { NB, OB, NB }, { OB, OB, OB }, { NB, OB, NB } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("INFERNALFURNACE", infernalFurnaceBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(net.minecraft.resources.Identifier.parse("thaumcraft:infernalfurnace"), new ThaumcraftApi.BluePrint("INFERNALFURNACE", infernalFurnaceBlueprint, new ItemStack(Blocks.NETHER_BRICKS, 12), new ItemStack(Blocks.OBSIDIAN, 12), new ItemStack(Blocks.IRON_BARS), new ItemStack(Items.LAVA_BUCKET)));
        Part IM = new Part(BlocksTC.infusionMatrix, null);
        Part SNT = new Part(BlocksTC.stoneArcane, "AIR");
        Part SNB1 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane));
        Part SNB2 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane));
        Part SNB3 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane));
        Part SNB4 = new Part(BlocksTC.stoneArcane, new ItemStack(BlocksTC.pillarArcane));
        Part PN = new Part(BlocksTC.pedestalArcane.defaultBlockState(), null);
        Part[][][] infusionAltarNormalBlueprint = { { { null, null, null }, { null, IM, null }, { null, null, null } }, { { SNT, null, SNT }, { null, null, null }, { SNT, null, SNT } }, { { SNB1, null, SNB2 }, { null, PN, null }, { SNB3, null, SNB4 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("INFUSION", infusionAltarNormalBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(net.minecraft.resources.Identifier.parse("thaumcraft:infusionaltar"), new ThaumcraftApi.BluePrint("INFUSION", infusionAltarNormalBlueprint, new ItemStack(BlocksTC.stoneArcane, 8), new ItemStack(BlocksTC.pedestalArcane), new ItemStack(BlocksTC.infusionMatrix)));
        Part SAT = new Part(BlocksTC.stoneAncient, "AIR");
        Part SAB1 = new Part(BlocksTC.stoneAncient, new ItemStack(BlocksTC.pillarAncient));
        Part SAB2 = new Part(BlocksTC.stoneAncient, new ItemStack(BlocksTC.pillarAncient));
        Part SAB3 = new Part(BlocksTC.stoneAncient, new ItemStack(BlocksTC.pillarAncient));
        Part SAB4 = new Part(BlocksTC.stoneAncient, new ItemStack(BlocksTC.pillarAncient));
        Part PA = new Part(BlocksTC.pedestalAncient.defaultBlockState(), null);
        Part[][][] infusionAltarAncientBlueprint = { { { null, null, null }, { null, IM, null }, { null, null, null } }, { { SAT, null, SAT }, { null, null, null }, { SAT, null, SAT } }, { { SAB1, null, SAB2 }, { null, PA, null }, { SAB3, null, SAB4 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("INFUSIONANCIENT", infusionAltarAncientBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(net.minecraft.resources.Identifier.parse("thaumcraft:infusionaltarancient"), new ThaumcraftApi.BluePrint("INFUSIONANCIENT", infusionAltarAncientBlueprint, new ItemStack(BlocksTC.stoneAncient, 8), new ItemStack(BlocksTC.pedestalAncient), new ItemStack(BlocksTC.infusionMatrix)));
        Part SET = new Part(BlocksTC.stoneEldritchTile, "AIR");
        Part SEB1 = new Part(BlocksTC.stoneEldritchTile, new ItemStack(BlocksTC.pillarEldritch));
        Part SEB2 = new Part(BlocksTC.stoneEldritchTile, new ItemStack(BlocksTC.pillarEldritch));
        Part SEB3 = new Part(BlocksTC.stoneEldritchTile, new ItemStack(BlocksTC.pillarEldritch));
        Part SEB4 = new Part(BlocksTC.stoneEldritchTile, new ItemStack(BlocksTC.pillarEldritch));
        Part PE = new Part(BlocksTC.pedestalEldritch.defaultBlockState(), null);
        Part[][][] infusionAltarEldritchBlueprint = { { { null, null, null }, { null, IM, null }, { null, null, null } }, { { SET, null, SET }, { null, null, null }, { SET, null, SET } }, { { SEB1, null, SEB2 }, { null, PE, null }, { SEB3, null, SEB4 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("INFUSIONELDRITCH", infusionAltarEldritchBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(net.minecraft.resources.Identifier.parse("thaumcraft:infusionaltareldritch"), new ThaumcraftApi.BluePrint("INFUSIONELDRITCH", infusionAltarEldritchBlueprint, new ItemStack(BlocksTC.stoneEldritchTile, 8), new ItemStack(BlocksTC.pedestalEldritch), new ItemStack(BlocksTC.infusionMatrix)));
        Part TH1 = new Part(BlocksTC.metalAlchemical.defaultBlockState(), BlocksTC.thaumatoriumTop).setApplyPlayerFacing(true);
        Part TH2 = new Part(BlocksTC.metalAlchemical.defaultBlockState(), BlocksTC.thaumatorium).setApplyPlayerFacing(true);
        Part TH3 = new Part(BlocksTC.crucible, null);
        Part[][][] thaumotoriumBlueprint = { { { TH1 } }, { { TH2 } }, { { TH3 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("THAUMATORIUM", thaumotoriumBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(net.minecraft.resources.Identifier.parse("thaumcraft:Thaumatorium"), new ThaumcraftApi.BluePrint("THAUMATORIUM", thaumotoriumBlueprint, new ItemStack(BlocksTC.metalAlchemical, 2), new ItemStack(BlocksTC.crucible)));
        Part GP1 = new Part(Blocks.IRON_BARS, new ItemStack(BlocksTC.placeholderBars));
        Part GP2 = new Part(Blocks.CAULDRON, new ItemStack(BlocksTC.placeholderCauldron));
        Part GP3 = new Part(Blocks.PISTON.defaultBlockState().setValue(net.minecraft.world.level.block.piston.PistonBaseBlock.FACING, net.minecraft.core.Direction.UP), BlocksTC.golemBuilder);
        Part GP4 = new Part(Blocks.ANVIL, new ItemStack(BlocksTC.placeholderAnvil));
        Part GP5 = new Part(BlocksTC.tableStone, new ItemStack(BlocksTC.placeholderTable));
        Part[][][] golempressBlueprint = { { { null, null }, { GP1, null } }, { { GP2, GP4 }, { GP3, GP5 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("MINDCLOCKWORK", golempressBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(net.minecraft.resources.Identifier.parse("thaumcraft:GolemPress"), new ThaumcraftApi.BluePrint("MINDCLOCKWORK", new ItemStack(BlocksTC.golemBuilder), golempressBlueprint, new ItemStack(Blocks.IRON_BARS), new ItemStack(Items.CAULDRON), new ItemStack(Blocks.PISTON), new ItemStack(Blocks.ANVIL), new ItemStack(BlocksTC.tableStone)));
    }
    
    public static void initializeAlchemyRecipes() {
        Identifier visCrystalGroup = net.minecraft.resources.Identifier.parse("thaumcraft:viscrystalgroup");
        CrucibleRecipe[] cre = new CrucibleRecipe[Aspect.aspects.size()];
        for (Aspect aspect : Aspect.aspects.values()) {
            ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:vis_crystal_" + aspect.getTag()), new CrucibleRecipe("BASEALCHEMY", ThaumcraftApiHelper.makeCrystal(aspect), "nuggetQuartz", new AspectList().add(aspect, 2)).setGroup(visCrystalGroup));
        }
        Identifier nitorGroup = Identifier.fromNamespaceAndPath("thaumcraft", "nitorgroup");
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:nitor"), new CrucibleRecipe("UNLOCKALCHEMY@3", new ItemStack(BlocksTC.nitor.get(DyeColor.YELLOW)), "dustGlowstone", new AspectList().merge(Aspect.ENERGY, 10).merge(Aspect.FIRE, 10).merge(Aspect.LIGHT, 10)));
        int a = 0;
        for (DyeColor d : DyeColor.values()) {
            shapelessOreDictRecipe("NitorDye" + d.getName().toLowerCase(), nitorGroup, new ItemStack(BlocksTC.nitor.get(d)), new Object[] { ConfigAspects.dyes[15 - a], "nitor" });
            ++a;
        }
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:alumentum"), new CrucibleRecipe("ALUMENTUM", new ItemStack(ItemsTC.alumentum), new ItemStack(Items.COAL), new AspectList().merge(Aspect.ENERGY, 10).merge(Aspect.FIRE, 10).merge(Aspect.ENTROPY, 5)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:brassingot"), new CrucibleRecipe("METALLURGY@1", new ItemStack(ItemsTC.ingots), "ingotIron", new AspectList().merge(Aspect.TOOL, 5)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:thaumiumingot"), new CrucibleRecipe("METALLURGY@2", new ItemStack(ItemsTC.ingots), "ingotIron", new AspectList().merge(Aspect.MAGIC, 5).merge(Aspect.EARTH, 5)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:voidingot"), new CrucibleRecipe("BASEELDRITCH", new ItemStack(ItemsTC.ingots), new ItemStack(ItemsTC.voidSeed), new AspectList().merge(Aspect.METAL, 10).merge(Aspect.FLUX, 5)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:hedge_tallow"), new CrucibleRecipe("HEDGEALCHEMY@1", new ItemStack(ItemsTC.tallow), new ItemStack(Items.ROTTEN_FLESH), new AspectList().merge(Aspect.FIRE, 1)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:hedge_leather"), new CrucibleRecipe("HEDGEALCHEMY@1", new ItemStack(Items.LEATHER), new ItemStack(Items.ROTTEN_FLESH), new AspectList().merge(Aspect.AIR, 3).merge(Aspect.BEAST, 3)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:focus_1"), new CrucibleRecipe("UNLOCKAUROMANCY", new ItemStack(ItemsTC.focus1), ConfigItems.ORDER_CRYSTAL, new AspectList().merge(Aspect.CRYSTAL, 20).merge(Aspect.MAGIC, 10).merge(Aspect.AURA, 5)));
        ArrayList<CrucibleRecipe> rl = new ArrayList<CrucibleRecipe>();
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:metal_purification_iron"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters), "oreIron", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:metal_purification_gold"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters), "oreGold", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:metal_purification_cinnabar"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters), "oreCinnabar", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        if (ModConfig.foundCopperOre) {
            ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:metal_purification_copper"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters), "oreCopper", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        }
        if (ModConfig.foundTinOre) {
            ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:metal_purification_tin"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters), "oreTin", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        }
        if (ModConfig.foundSilverOre) {
            ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:metal_purification_silver"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters), "oreSilver", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        }
        if (ModConfig.foundLeadOre) {
            ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:metal_purification_lead"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters), "oreLead", new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
        }
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:LiquidDeath"), new CrucibleRecipe("LIQUIDDEATH", FluidUtil.getFilledBucket(new FluidStack(ConfigBlocks.FluidDeath.instance, 1000)), new ItemStack(Items.BUCKET), new AspectList().add(Aspect.DEATH, 100).add(Aspect.ALCHEMY, 20).add(Aspect.ENTROPY, 50)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:BottleTaint"), new CrucibleRecipe("BOTTLETAINT", new ItemStack(ItemsTC.bottleTaint), ItemPhial.makeFilledPhial(Aspect.FLUX), new AspectList().add(Aspect.FLUX, 30).add(Aspect.WATER, 30)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:BathSalts"), new CrucibleRecipe("BATHSALTS", new ItemStack(ItemsTC.bathSalts), new ItemStack(ItemsTC.salisMundus), new AspectList().add(Aspect.MIND, 40).add(Aspect.AIR, 40).add(Aspect.ORDER, 40).add(Aspect.LIFE, 40)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SaneSoap"), new CrucibleRecipe("SANESOAP", new ItemStack(ItemsTC.sanitySoap), new ItemStack(BlocksTC.fleshBlock), new AspectList().add(Aspect.MIND, 75).add(Aspect.ELDRITCH, 50).add(Aspect.ORDER, 75).add(Aspect.LIFE, 50)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealCollect"), new CrucibleRecipe("SEALCOLLECT", GolemHelper.getSealStack("thaumcraft:pickup"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealCollectAdv"), new CrucibleRecipe("SEALCOLLECT&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:pickup_advanced"), GolemHelper.getSealStack("thaumcraft:pickup"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealStore"), new CrucibleRecipe("SEALSTORE", GolemHelper.getSealStack("thaumcraft:fill"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 10)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealStoreAdv"), new CrucibleRecipe("SEALSTORE&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:fill_advanced"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealEmpty"), new CrucibleRecipe("SEALEMPTY", GolemHelper.getSealStack("thaumcraft:empty"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.VOID, 10)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealEmptyAdv"), new CrucibleRecipe("SEALEMPTY&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:empty_advanced"), GolemHelper.getSealStack("thaumcraft:empty"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealProvide"), new CrucibleRecipe("SEALPROVIDE", GolemHelper.getSealStack("thaumcraft:provider"), GolemHelper.getSealStack("thaumcraft:empty_advanced"), new AspectList().add(Aspect.EXCHANGE, 10).add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealStock"), new CrucibleRecipe("SEALSTOCK", GolemHelper.getSealStack("thaumcraft:stock"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.MIND, 10).add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealGuard"), new CrucibleRecipe("SEALGUARD", GolemHelper.getSealStack("thaumcraft:guard"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 20).add(Aspect.PROTECT, 20)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealGuardAdv"), new CrucibleRecipe("SEALGUARD&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:guard_advanced"), GolemHelper.getSealStack("thaumcraft:guard"), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MIND, 20)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealLumber"), new CrucibleRecipe("SEALLUMBER", GolemHelper.getSealStack("thaumcraft:lumber"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.PLANT, 40).add(Aspect.SENSES, 20)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealUse"), new CrucibleRecipe("SEALUSE", GolemHelper.getSealStack("thaumcraft:use"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.CRAFT, 20).add(Aspect.SENSES, 10).add(Aspect.MIND, 20)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealBreakAdv"), new CrucibleRecipe("SEALBREAK&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:breaker_advanced"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10).add(Aspect.TOOL, 20)));
        ThaumcraftApi.addCrucibleRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:EverfullUrn"), new CrucibleRecipe("EVERFULLURN", new ItemStack(BlocksTC.everfullUrn), new ItemStack(Items.FLOWER_POT), new AspectList().add(Aspect.WATER, 30).add(Aspect.CRAFT, 10).add(Aspect.EARTH, 10)));
    }
    
    public static void initializeArcaneRecipes(Object /* IForgeRegistry removed */ iForgeRegistry) {
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:mechanism_simple"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEARTIFICE", 10, new AspectList().add(Aspect.FIRE, 1).add(Aspect.WATER, 1), ItemsTC.mechanismSimple, " B ", "ISI", " B ", 'B', "plateBrass", 'I', "plateIron", 'S', "stickWood"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:mechanism_complex"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEARTIFICE", 50, new AspectList().add(Aspect.FIRE, 1).add(Aspect.WATER, 1), ItemsTC.mechanismComplex, " M ", "TQT", " M ", 'T', "plateThaumium", 'Q', Blocks.PISTON, 'M', new ItemStack(ItemsTC.mechanismSimple)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:vis_resonator"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKAUROMANCY@2", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1), ItemsTC.visResonator, "plateIron", "gemQuartz"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:activatorrail"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "FIRSTSTEPS", 10, null, BlocksTC.activatorRail, new ItemStack(Blocks.ACTIVATOR_RAIL)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:thaumometer"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "FIRSTSTEPS@2", 20, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), ItemsTC.thaumometer, " I ", "IGI", " I ", 'I', "ingotGold", 'G', new ItemStack(Blocks.GLASS_PANE)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:sanitychecker"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "WARP", 20, new AspectList().add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), ItemsTC.sanityChecker, "BN ", "M N", "BN ", 'N', "nuggetBrass", 'B', new ItemStack(ItemsTC.brain), 'M', new ItemStack(ItemsTC.mirroredGlass)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:rechargepedestal"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "RECHARGEPEDESTAL", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.ORDER, 1), BlocksTC.rechargePedestal, " R ", "DID", "SSS", 'I', "ingotGold", 'D', "gemDiamond", 'R', new ItemStack(ItemsTC.visResonator), 'S', "stone"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:workbenchcharger"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "WORKBENCHCHARGER", 200, new AspectList().add(Aspect.AIR, 2).add(Aspect.ORDER, 2), new ItemStack(BlocksTC.arcaneWorkbenchCharger), " R ", "W W", "I I", 'I', "ingotIron", 'R', new ItemStack(ItemsTC.visResonator), 'W', new ItemStack(BlocksTC.plankGreatwood)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:wand_workbench"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEAUROMANCY@2", 100, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.wandWorkbench), "ISI", "BRB", "GTG", 'S', new ItemStack(BlocksTC.slabArcaneStone), 'T', new ItemStack(BlocksTC.tableStone), 'R', new ItemStack(ItemsTC.visResonator), 'B', new ItemStack(BlocksTC.stoneArcane), 'G', "ingotGold", 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:caster_basic"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKAUROMANCY@2", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(ItemsTC.casterBasic), "III", "LRL", "LTL", 'T', new ItemStack(ItemsTC.thaumometer), 'R', new ItemStack(ItemsTC.visResonator), 'L', "leather", 'I', "ingotIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:EnchantedFabric"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKINFUSION", 5, null, new ItemStack(ItemsTC.fabric), " S ", "SCS", " S ", 'S', "string", 'C', new ItemStack(Blocks.WHITE_WOOL)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:RobeChest"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKINFUSION", 100, null, new ItemStack(ItemsTC.clothChest, 1), "I I", "III", "III", 'I', new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:RobeLegs"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKINFUSION", 100, null, new ItemStack(ItemsTC.clothLegs, 1), "III", "I I", "I I", 'I', new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:RobeBoots"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKINFUSION", 100, null, new ItemStack(ItemsTC.clothBoots, 1), "I I", "I I", 'I', new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:Goggles"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKARTIFICE", 50, null, new ItemStack(ItemsTC.goggles), "LGL", "L L", "TGT", 'T', new ItemStack(ItemsTC.thaumometer), 'G', "ingotBrass", 'L', "leather"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealBlank"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "CONTROLSEALS", 20, new AspectList().add(Aspect.AIR, 1), new ItemStack(ItemsTC.seals, 3), new Object[] { new ItemStack(Items.CLAY_BALL), new ItemStack(ItemsTC.tallow), "dyeRed", "nitor" }));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:modvision"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "GOLEMVISION", 50, new AspectList().add(Aspect.WATER, 1), new ItemStack(ItemsTC.modules), "B B", "E E", "PGP", 'B', new ItemStack(Items.GLASS_BOTTLE), 'E', new ItemStack(Items.FERMENTED_SPIDER_EYE), 'P', "plateBrass", 'G', new ItemStack(ItemsTC.mechanismSimple)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:modaggression"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "SEALGUARD", 50, new AspectList().add(Aspect.FIRE, 1), new ItemStack(ItemsTC.modules), " R ", "RTR", "PGP", 'R', "paneGlass", 'T', new ItemStack(Items.BLAZE_POWDER), 'P', "plateBrass", 'G', new ItemStack(ItemsTC.mechanismSimple)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:mirrorglass"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "BASEARTIFICE", 50, new AspectList().add(Aspect.WATER, 1).add(Aspect.ORDER, 1), new ItemStack(ItemsTC.mirroredGlass), new Object[] { new ItemStack(ItemsTC.quicksilver), "paneGlass" }));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ArcaneSpa"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ARCANESPA", 50, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.spa), "QIQ", "SJS", "SPS", 'P', new ItemStack(ItemsTC.mechanismSimple), 'J', new ItemStack(BlocksTC.jarNormal), 'S', new ItemStack(BlocksTC.stoneArcane), 'Q', new ItemStack(Blocks.QUARTZ_BLOCK), 'I', new ItemStack(Blocks.IRON_BARS)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:Tube"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, null, new ItemStack(BlocksTC.tube), " Q ", "IGI", " B ", 'I', "plateIron", 'B', "nuggetBrass", 'G', "blockGlass", 'Q', "nuggetQuicksilver"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:Resonator"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 50, null, new ItemStack(ItemsTC.resonator), "I I", "INI", " S ", 'I', "plateIron", 'N', Items.QUARTZ, 'S', "stickWood"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:TubeValve"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, null, new ItemStack(BlocksTC.tubeValve), new Object[] { new ItemStack(BlocksTC.tube), new ItemStack(Blocks.LEVER) }));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:TubeFilter"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, null, new ItemStack(BlocksTC.tubeFilter), new Object[] { new ItemStack(BlocksTC.tube), new ItemStack(ItemsTC.filter) }));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:TubeRestrict"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, new AspectList().add(Aspect.EARTH, 1), new ItemStack(BlocksTC.tubeRestrict), new Object[] { new ItemStack(BlocksTC.tube) }));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:TubeOneway"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.tubeOneway), new Object[] { new ItemStack(BlocksTC.tube) }));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:TubeBuffer"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 25, null, new ItemStack(BlocksTC.tubeBuffer), "PVP", "TWT", "PRP", 'T', new ItemStack(BlocksTC.tube), 'V', new ItemStack(BlocksTC.tubeValve), 'W', "plateIron", 'R', new ItemStack(BlocksTC.tubeRestrict), 'P', new ItemStack(ItemsTC.phial)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:WardedJar"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "WARDEDJARS", 5, null, new ItemStack(BlocksTC.jarNormal), "GWG", "G G", "GGG", 'W', "slabWood", 'G', "paneGlass"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:JarVoid"), new ShapedArcaneVoidJar(ConfigRecipes.defaultGroup, "WARDEDJARS", 50, new AspectList().add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.jarVoid), "J", 'J', new ItemStack(BlocksTC.jarNormal)));
        Identifier bannerGroup = Identifier.fromNamespaceAndPath("thaumcraft", "banners");
        int a = 0;
        for (DyeColor d : DyeColor.values()) {
            ItemStack banner = new ItemStack(BlocksTC.banners.get(d));
            ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:Banner" + d.getName().toLowerCase()), new ShapedArcaneRecipe(bannerGroup, "BASEINFUSION", 10, null, banner, "WS", "WS", "WB", 'W', new ItemStack(Blocks.WHITE_WOOL), 'S', "stickWood", 'B', "slabWood"));
            ++a;
        }
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:PaveBarrier"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "PAVINGSTONES", 50, new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.pavingStoneBarrier, 4), "SS", "SS", 'S', new ItemStack(BlocksTC.stoneArcaneBrick)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:PaveTravel"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "PAVINGSTONES", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1), new ItemStack(BlocksTC.pavingStoneTravel, 4), "SS", "SS", 'S', new ItemStack(BlocksTC.stoneArcaneBrick)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ArcaneLamp"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ARCANELAMP", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1), new ItemStack(BlocksTC.lampArcane), " I ", "IAI", " I ", 'A', new ItemStack(BlocksTC.amberBlock), 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:Levitator"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "LEVITATOR", 35, new AspectList().add(Aspect.AIR, 1), new ItemStack(BlocksTC.levitator), "WIW", "BNB", "WGW", 'I', "plateThaumium", 'N', "nitor", 'W', "plankWood", 'B', "plateIron", 'G', new ItemStack(ItemsTC.mechanismSimple)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:RedstoneRelay"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "REDSTONERELAY", 10, new AspectList().add(Aspect.ORDER, 1), new ItemStack(BlocksTC.redstoneRelay), "   ", "TGT", "SSS", 'T', new ItemStack(Blocks.REDSTONE_TORCH), 'G', new ItemStack(ItemsTC.mechanismSimple), 'S', new ItemStack(Blocks.STONE_SLAB)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ArcaneEar"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ARCANEEAR", 15, new AspectList().add(Aspect.AIR, 1), new ItemStack(BlocksTC.arcaneEar), "P P", " G ", "WRW", 'W', "slabWood", 'R', Items.REDSTONE, 'G', new ItemStack(ItemsTC.mechanismSimple), 'P', "plateBrass"));
        shapelessOreDictRecipe("ArcaneEarToggle", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.arcaneEarToggle), new Object[] { new ItemStack(BlocksTC.arcaneEar), new ItemStack(Blocks.LEVER) });
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:InfusionMatrix"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSION@2", 150, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.infusionMatrix), "S S", " N ", "S S", 'S', new ItemStack(BlocksTC.stoneArcaneBrick), 'N', "nitor"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:MatrixMotion"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONBOOST", 500, new AspectList().add(Aspect.AIR, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.matrixSpeed), "SNS", "NGN", "SNS", 'S', new ItemStack(BlocksTC.stoneArcane), 'N', "nitor", 'G', new ItemStack(Blocks.DIAMOND_BLOCK)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:MatrixCost"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONBOOST", 500, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.matrixCost), "SAS", "AGA", "SAS", 'S', new ItemStack(BlocksTC.stoneArcane), 'A', new ItemStack(ItemsTC.alumentum), 'G', new ItemStack(Blocks.DIAMOND_BLOCK)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ArcanePedestal"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSION", 10, null, new ItemStack(BlocksTC.pedestalArcane), "SSS", " B ", "SSS", 'S', new ItemStack(BlocksTC.slabArcaneStone), 'B', new ItemStack(BlocksTC.stoneArcane)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:AncientPedestal"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONANCIENT", 150, null, new ItemStack(BlocksTC.pedestalAncient), "SSS", " B ", "SSS", 'S', new ItemStack(BlocksTC.slabAncient), 'B', new ItemStack(BlocksTC.stoneAncient)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:EldritchPedestal"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONELDRITCH", 150, null, new ItemStack(BlocksTC.pedestalEldritch), "SSS", " B ", "SSS", 'S', new ItemStack(BlocksTC.slabEldritch), 'B', new ItemStack(BlocksTC.stoneEldritchTile)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:FocusPouch"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "FOCUSPOUCH", 25, null, new ItemStack(ItemsTC.focusPouch), "LGL", "LBL", "LLL", 'B', new ItemStack(ItemsTC.baubles), 'L', "leather", 'G', Items.GOLD_INGOT));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:dioptra"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "DIOPTRA", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.dioptra), "APA", "IGI", "AAA", 'A', new ItemStack(BlocksTC.stoneArcane), 'G', new ItemStack(ItemsTC.thaumometer), 'P', new ItemStack(ItemsTC.visResonator), 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:HungryChest"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "HUNGRYCHEST", 15, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.hungryChest), "WTW", "W W", "WWW", 'W', new ItemStack(BlocksTC.plankGreatwood), 'T', "trapdoorWood"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:Filter"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEALCHEMY", 15, new AspectList().add(Aspect.WATER, 1), new ItemStack(ItemsTC.filter), "GWG", 'G', Items.GOLD_INGOT, 'W', new ItemStack(BlocksTC.plankSilverwood)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:MorphicResonator"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEALCHEMY", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1), new ItemStack(ItemsTC.morphicResonator), " G ", "BSB", " G ", 'G', "paneGlass", 'B', "plateBrass", 'S', new ItemStack(ItemsTC.nuggets)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:Alembic"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTER", 50, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.alembic), "WFW", "SBS", "WFW", 'W', new ItemStack(BlocksTC.plankGreatwood), 'B', Items.BUCKET, 'F', new ItemStack(ItemsTC.filter), 'S', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:EssentiaSmelter"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTER@2", 50, new AspectList().add(Aspect.FIRE, 1), new ItemStack(BlocksTC.smelterBasic), "BCB", "SFS", "SSS", 'C', new ItemStack(BlocksTC.crucible), 'F', new ItemStack(Blocks.FURNACE), 'S', "cobblestone", 'B', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:EssentiaSmelterThaumium"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTERTHAUMIUM", 250, new AspectList().add(Aspect.FIRE, 2), new ItemStack(BlocksTC.smelterThaumium), "BFB", "IGI", "III", 'F', new ItemStack(BlocksTC.smelterBasic), 'G', new ItemStack(BlocksTC.metalAlchemical), 'I', "plateThaumium", 'B', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:EssentiaSmelterVoid"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTERVOID", 750, new AspectList().add(Aspect.FIRE, 3), new ItemStack(BlocksTC.smelterVoid), "BFB", "IGI", "III", 'F', new ItemStack(BlocksTC.smelterBasic), 'G', new ItemStack(BlocksTC.metalAlchemicalAdvanced), 'I', "plateVoid", 'B', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:AlchemicalConstruct"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 75, new AspectList().add(Aspect.WATER, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.metalAlchemical, 2), "IVI", "TWT", "IVI", 'W', new ItemStack(BlocksTC.plankGreatwood), 'V', new ItemStack(BlocksTC.tubeValve), 'T', new ItemStack(BlocksTC.tube), 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:AdvAlchemyConstruct"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTERVOID@1", 200, new AspectList().add(Aspect.EARTH, 1).add(Aspect.FIRE, 1), new ItemStack(BlocksTC.metalAlchemicalAdvanced), " A ", "VPV", " A ", 'A', new ItemStack(BlocksTC.metalAlchemical), 'V', "plateVoid", 'P', Ingredient.of(ItemsTC.primordialPearl)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:PotionSprayer"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "POTIONSPRAYER", 75, new AspectList().add(Aspect.WATER, 1).add(Aspect.FIRE, 1), new ItemStack(BlocksTC.potionSprayer), "BDB", "IAI", "ICI", 'B', "plateBrass", 'I', "plateIron", 'A', new ItemStack(Items.BREWING_STAND), 'D', new ItemStack(Blocks.DISPENSER), 'C', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SmelterAux"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "IMPROVEDSMELTING", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1), new ItemStack(BlocksTC.smelterAux), "WTW", "RGR", "IBI", 'W', new ItemStack(BlocksTC.plankGreatwood), 'B', new ItemStack(BlocksTC.bellows), 'R', "plateBrass", 'T', new ItemStack(BlocksTC.tubeFilter), 'I', "plateIron", 'G', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SmelterVent"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "IMPROVEDSMELTING2", 150, new AspectList().add(Aspect.AIR, 1), new ItemStack(BlocksTC.smelterVent), "IBI", "MGF", "IBI", 'I', "plateIron", 'B', "plateBrass", 'F', new ItemStack(ItemsTC.filter), 'M', new ItemStack(ItemsTC.filter), 'G', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:EssentiaTransportIn"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIATRANSPORT", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.essentiaTransportInput), "   ", "BQB", "IGI", 'I', "plateIron", 'B', "plateBrass", 'Q', new ItemStack(Blocks.DISPENSER), 'G', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:EssentiaTransportOut"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIATRANSPORT", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.essentiaTransportOutput), "   ", "BQB", "IGI", 'I', "plateIron", 'B', "plateBrass", 'Q', new ItemStack(Blocks.HOPPER), 'G', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:Bellows"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BELLOWS", 25, new AspectList().add(Aspect.AIR, 1), new ItemStack(BlocksTC.bellows), "WW ", "LLI", "WW ", 'W', "plankWood", 'I', "ingotIron", 'L', "leather"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:Centrifuge"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "CENTRIFUGE", 100, new AspectList().add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.centrifuge), " T ", "RCP", " T ", 'T', new ItemStack(BlocksTC.tube), 'P', new ItemStack(ItemsTC.mechanismSimple), 'R', new ItemStack(ItemsTC.morphicResonator), 'C', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:MnemonicMatrix"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "THAUMATORIUM", 50, new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.brainBox), "IAI", "ABA", "IAI", 'B', new ItemStack(ItemsTC.mind), 'A', "gemAmber", 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:MindClockwork"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "MINDCLOCKWORK@2", 25, new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1), new ItemStack(ItemsTC.mind), " P ", "PGP", "BCB", 'G', new ItemStack(ItemsTC.mechanismSimple), 'B', "plateBrass", 'P', "paneGlass", 'C', new ItemStack(Items.COMPARATOR)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:AutomatedCrossbow"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASICTURRET", 100, new AspectList().add(Aspect.AIR, 1), new ItemStack(ItemsTC.turretPlacer), "BGI", "WMW", "S S", 'G', new ItemStack(ItemsTC.mechanismSimple), 'I', "plateIron", 'S', "stickWood", 'M', new ItemStack(ItemsTC.mind), 'B', Ingredient.of(Items.BOW), 'W', new ItemStack(BlocksTC.plankGreatwood)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:AdvancedCrossbow"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ADVANCEDTURRET", 150, new AspectList().add(Aspect.AIR, 2), new ItemStack(ItemsTC.turretPlacer), "PMP", "PTP", "   ", 'T', new ItemStack(ItemsTC.turretPlacer), 'P', "plateIron", 'M', new ItemStack(ItemsTC.mind)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:patterncrafter"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ARCANEPATTERNCRAFTER", 50, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.patternCrafter), "VH ", "GCG", " W ", 'H', new ItemStack(Blocks.HOPPER), 'W', new ItemStack(BlocksTC.plankGreatwood), 'G', new ItemStack(ItemsTC.mechanismSimple), 'V', new ItemStack(ItemsTC.visResonator), 'C', "workbench"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:GrappleGunTip"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "GRAPPLEGUN", 25, new AspectList().add(Aspect.EARTH, 1), new ItemStack(ItemsTC.grappleGunTip), "BRB", "RHR", "BRB", 'B', "plateBrass", 'R', new ItemStack(ItemsTC.nuggets), 'H', new ItemStack(Blocks.TRIPWIRE_HOOK)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:GrappleGunSpool"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "GRAPPLEGUN", 25, new AspectList().add(Aspect.WATER, 1), new ItemStack(ItemsTC.grappleGunSpool), "SHS", "SGS", "SSS", 'G', new ItemStack(ItemsTC.mechanismSimple), 'S', "string", 'H', new ItemStack(Blocks.TRIPWIRE_HOOK)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:GrappleGun"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "GRAPPLEGUN", 75, new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1), new ItemStack(ItemsTC.grappleGun), "  S", "TII", " BW", 'B', "plateBrass", 'I', "plateIron", 'T', new ItemStack(ItemsTC.grappleGunTip), 'W', "plankWood", 'S', new ItemStack(ItemsTC.grappleGunSpool)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:VisBattery"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "VISBATTERY", 50, new AspectList().add(Aspect.AIR, 2).add(Aspect.EARTH, 2).add(Aspect.WATER, 2).add(Aspect.FIRE, 2).add(Aspect.ORDER, 2).add(Aspect.ENTROPY, 2), new ItemStack(BlocksTC.visBattery), "SSS", "SRS", "SSS", 'R', new ItemStack(ItemsTC.visResonator), 'S', new ItemStack(BlocksTC.slabArcaneStone)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:VisGenerator"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "VISGENERATOR", 25, new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.visGenerator), "WSW", "EPE", "WRW", 'R', new ItemStack(ItemsTC.visResonator), 'E', new ItemStack(ItemsTC.nuggets), 'S', "dustRedstone", 'P', new ItemStack(Blocks.PISTON), 'W', "plankWood"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:Condenser"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "FLUXCLEANUP", 500, new AspectList().add(Aspect.AIR, 5).add(Aspect.WATER, 5).add(Aspect.ENTROPY, 5), new ItemStack(BlocksTC.condenser), "BCB", "WMW", "BTB", 'T', new ItemStack(BlocksTC.tube), 'C', new ItemStack(ItemsTC.morphicResonator), 'W', "plankWood", 'M', new ItemStack(ItemsTC.mechanismComplex), 'B', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:CondenserLattice"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "FLUXCLEANUP", 100, new AspectList().add(Aspect.EARTH, 3).add(Aspect.AIR, 3), new ItemStack(BlocksTC.condenserlattice), "QTQ", "QFQ", "QTQ", 'T', "plateThaumium", 'F', new ItemStack(ItemsTC.filter), 'Q', "gemQuartz"));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:Stabilizer"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONSTABLE", 250, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.stabilizer), "SRS", "BVB", "IMI", 'R', "blockRedstone", 'S', BlocksTC.slabArcaneStone, 'B', BlocksTC.stoneArcane, 'M', new ItemStack(ItemsTC.mechanismComplex), 'V', new ItemStack(ItemsTC.visResonator), 'I', new ItemStack(BlocksTC.inlay)));
        ThaumcraftApi.addArcaneCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:RedstoneInlay"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONSTABLE", 25, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.inlay, 2), new Object[] { "dustRedstone", "ingotGold" }));
    }
    
    public static void initializeInfusionRecipes() {
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealHarvest"), new InfusionRecipe("SEALHARVEST", GolemHelper.getSealStack("thaumcraft:harvest"), 0, new AspectList().add(Aspect.PLANT, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Items.MELON_SEEDS), new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(Items.SUGAR_CANE), new ItemStack(Blocks.CACTUS)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealButcher"), new InfusionRecipe("SEALBUTCHER", GolemHelper.getSealStack("thaumcraft:butcher"), 0, new AspectList().add(Aspect.BEAST, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), GolemHelper.getSealStack("thaumcraft:guard"), "leather", new ItemStack(Blocks.WHITE_WOOL), new ItemStack(Items.RABBIT_HIDE), new ItemStack(Items.PORKCHOP), new ItemStack(Items.MUTTON), new ItemStack(Items.BEEF)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:SealBreak"), new InfusionRecipe("SEALBREAK", GolemHelper.getSealStack("thaumcraft:breaker"), 1, new AspectList().add(Aspect.TOOL, 10).add(Aspect.ENTROPY, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), Ingredient.of(Items.GOLDEN_AXE), Ingredient.of(Items.GOLDEN_PICKAXE), Ingredient.of(Items.GOLDEN_SHOVEL)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:CrystalClusterAir"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalAir), 0, new AspectList().add(Aspect.AIR, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.AIR), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:CrystalClusterFire"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalFire), 0, new AspectList().add(Aspect.FIRE, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:CrystalClusterWater"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalWater), 0, new AspectList().add(Aspect.WATER, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.WATER), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:CrystalClusterEarth"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalEarth), 0, new AspectList().add(Aspect.EARTH, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.EARTH), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:CrystalClusterOrder"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalOrder), 0, new AspectList().add(Aspect.ORDER, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.ORDER), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:CrystalClusterEntropy"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalEntropy), 0, new AspectList().add(Aspect.ENTROPY, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:CrystalClusterFlux"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalTaint), 4, new AspectList().add(Aspect.FLUX, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:focus_2"), new InfusionRecipe("FOCUSADVANCED@1", new ItemStack(ItemsTC.focus2), 3, new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 50), new ItemStack(ItemsTC.focus1), new ItemStack(ItemsTC.quicksilver), "gemDiamond", new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.ENDER_PEARL)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:focus_3"), new InfusionRecipe("FOCUSGREATER@1", new ItemStack(ItemsTC.focus3), 5, new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 50).add(Aspect.VOID, 100), new ItemStack(ItemsTC.focus2), new ItemStack(ItemsTC.quicksilver), Ingredient.of(ItemsTC.primordialPearl), new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.NETHER_STAR)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:JarBrain"), new InfusionRecipe("JARBRAIN", new ItemStack(BlocksTC.jarBrain), 4, new AspectList().add(Aspect.MIND, 25).add(Aspect.SENSES, 25).add(Aspect.UNDEAD, 25), new ItemStack(BlocksTC.jarNormal), new ItemStack(ItemsTC.brain), new ItemStack(Items.SPIDER_EYE), new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.SPIDER_EYE)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:VisAmulet"), new InfusionRecipe("VISAMULET", new ItemStack(ItemsTC.amuletVis), 6, new AspectList().add(Aspect.AURA, 50).add(Aspect.ENERGY, 100).add(Aspect.VOID, 50), new ItemStack(ItemsTC.baubles), new ItemStack(ItemsTC.visResonator), ThaumcraftApiHelper.makeCrystal(Aspect.AIR), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), ThaumcraftApiHelper.makeCrystal(Aspect.WATER), ThaumcraftApiHelper.makeCrystal(Aspect.EARTH), ThaumcraftApiHelper.makeCrystal(Aspect.ORDER)));
        InfusionRunicAugmentRecipe ra = new InfusionRunicAugmentRecipe();
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:RunicArmor"), ra);
        for (int a = 0; a < 3; ++a) {
            ItemStack in = new ItemStack(ItemsTC.baubles);
            if (a > 0) {
                net.minecraft.nbt.CompoundTag _t = in.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
                _t.putByte("TC.RUNIC", (byte)a);
                in.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(_t));
            }
            ThaumcraftApi.addFakeCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:RunicArmorFake" + a), new InfusionRunicAugmentRecipe(in));
        }
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:Mirror"), new InfusionRecipe("MIRROR", new ItemStack(BlocksTC.mirror), 1, new AspectList().add(Aspect.MOTION, 25).add(Aspect.DARKNESS, 25).add(Aspect.EXCHANGE, 25), new ItemStack(ItemsTC.mirroredGlass), "ingotGold", "ingotGold", "ingotGold", new ItemStack(Items.ENDER_PEARL)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:MirrorHand"), new InfusionRecipe("MIRRORHAND", new ItemStack(ItemsTC.handMirror), 5, new AspectList().add(Aspect.TOOL, 50).add(Aspect.MOTION, 50), new ItemStack(BlocksTC.mirror), "stickWood", new ItemStack(Items.COMPASS), new ItemStack(Items.MAP)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:MirrorEssentia"), new InfusionRecipe("MIRRORESSENTIA", new ItemStack(BlocksTC.mirrorEssentia), 2, new AspectList().add(Aspect.MOTION, 25).add(Aspect.WATER, 25).add(Aspect.EXCHANGE, 25), new ItemStack(ItemsTC.mirroredGlass), "ingotIron", "ingotIron", "ingotIron", new ItemStack(Items.ENDER_PEARL)));
        ItemStack isEA = new ItemStack(ItemsTC.elementalAxe);
        EnumInfusionEnchantment.addInfusionEnchantment(isEA, EnumInfusionEnchantment.COLLECTOR, 1);
        EnumInfusionEnchantment.addInfusionEnchantment(isEA, EnumInfusionEnchantment.BURROWING, 1);
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ElementalAxe"), new InfusionRecipe("ELEMENTALTOOLS", isEA, 1, new AspectList().add(Aspect.WATER, 60).add(Aspect.PLANT, 30), new ItemStack(ItemsTC.thaumiumAxe), ConfigItems.WATER_CRYSTAL, ConfigItems.WATER_CRYSTAL, new ItemStack(ItemsTC.nuggets), new ItemStack(BlocksTC.plankGreatwood)));
        ItemStack isEP = new ItemStack(ItemsTC.elementalPick);
        EnumInfusionEnchantment.addInfusionEnchantment(isEP, EnumInfusionEnchantment.REFINING, 1);
        EnumInfusionEnchantment.addInfusionEnchantment(isEP, EnumInfusionEnchantment.SOUNDING, 2);
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ElementalPick"), new InfusionRecipe("ELEMENTALTOOLS", isEP, 1, new AspectList().add(Aspect.FIRE, 30).add(Aspect.METAL, 30).add(Aspect.SENSES, 30), new ItemStack(ItemsTC.thaumiumPick), ConfigItems.FIRE_CRYSTAL, ConfigItems.FIRE_CRYSTAL, new ItemStack(ItemsTC.nuggets), new ItemStack(BlocksTC.plankGreatwood)));
        ItemStack isESW = new ItemStack(ItemsTC.elementalSword);
        EnumInfusionEnchantment.addInfusionEnchantment(isESW, EnumInfusionEnchantment.ARCING, 2);
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ElementalSword"), new InfusionRecipe("ELEMENTALTOOLS", isESW, 1, new AspectList().add(Aspect.AIR, 30).add(Aspect.MOTION, 30).add(Aspect.AVERSION, 30), new ItemStack(ItemsTC.thaumiumSword), ConfigItems.AIR_CRYSTAL, ConfigItems.AIR_CRYSTAL, new ItemStack(ItemsTC.nuggets), new ItemStack(BlocksTC.plankGreatwood)));
        ItemStack isES = new ItemStack(ItemsTC.elementalShovel);
        EnumInfusionEnchantment.addInfusionEnchantment(isES, EnumInfusionEnchantment.DESTRUCTIVE, 1);
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ElementalShovel"), new InfusionRecipe("ELEMENTALTOOLS", isES, 1, new AspectList().add(Aspect.EARTH, 60).add(Aspect.CRAFT, 30), new ItemStack(ItemsTC.thaumiumShovel), ConfigItems.EARTH_CRYSTAL, ConfigItems.EARTH_CRYSTAL, new ItemStack(ItemsTC.nuggets), new ItemStack(BlocksTC.plankGreatwood)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ElementalHoe"), new InfusionRecipe("ELEMENTALTOOLS", new ItemStack(ItemsTC.elementalHoe), 1, new AspectList().add(Aspect.ORDER, 30).add(Aspect.PLANT, 30).add(Aspect.ENTROPY, 30), new ItemStack(ItemsTC.thaumiumHoe), ConfigItems.ORDER_CRYSTAL, ConfigItems.ENTROPY_CRYSTAL, new ItemStack(ItemsTC.nuggets), new ItemStack(BlocksTC.plankGreatwood)));
        InfusionEnchantmentRecipe IEBURROWING = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.BURROWING, new AspectList().add(Aspect.SENSES, 80).add(Aspect.EARTH, 150), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Items.RABBIT_FOOT));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IEBURROWING"), IEBURROWING);
        ThaumcraftApi.addFakeCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IEBURROWINGFAKE"), new InfusionEnchantmentRecipe(IEBURROWING, new ItemStack(Items.WOODEN_PICKAXE)));
        InfusionEnchantmentRecipe IECOLLECTOR = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.COLLECTOR, new AspectList().add(Aspect.DESIRE, 80).add(Aspect.WATER, 100), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Items.LEAD));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IECOLLECTOR"), IECOLLECTOR);
        ThaumcraftApi.addFakeCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IECOLLECTORFAKE"), new InfusionEnchantmentRecipe(IECOLLECTOR, new ItemStack(Items.STONE_AXE)));
        InfusionEnchantmentRecipe IEDESTRUCTIVE = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.DESTRUCTIVE, new AspectList().add(Aspect.AVERSION, 200).add(Aspect.ENTROPY, 250), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Blocks.TNT));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IEDESTRUCTIVE"), IEDESTRUCTIVE);
        ThaumcraftApi.addFakeCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IEDESTRUCTIVEFAKE"), new InfusionEnchantmentRecipe(IEDESTRUCTIVE, new ItemStack(Items.STONE_PICKAXE)));
        InfusionEnchantmentRecipe IEREFINING = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.REFINING, new AspectList().add(Aspect.ORDER, 80).add(Aspect.EXCHANGE, 60), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(ItemsTC.salisMundus));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IEREFINING"), IEREFINING);
        ThaumcraftApi.addFakeCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IEREFININGFAKE"), new InfusionEnchantmentRecipe(IEREFINING, new ItemStack(Items.IRON_PICKAXE)));
        InfusionEnchantmentRecipe IESOUNDING = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.SOUNDING, new AspectList().add(Aspect.SENSES, 40).add(Aspect.FIRE, 60), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Items.MAP));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IESOUNDING"), IESOUNDING);
        ThaumcraftApi.addFakeCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IESOUNDINGFAKE"), new InfusionEnchantmentRecipe(IESOUNDING, new ItemStack(Items.GOLDEN_PICKAXE)));
        InfusionEnchantmentRecipe IEARCING = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.ARCING, new AspectList().add(Aspect.ENERGY, 40).add(Aspect.AIR, 60), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Blocks.REDSTONE_BLOCK));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IEARCING"), IEARCING);
        ThaumcraftApi.addFakeCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IEARCINGFAKE"), new InfusionEnchantmentRecipe(IEARCING, new ItemStack(Items.WOODEN_SWORD)));
        InfusionEnchantmentRecipe IEESSENCE = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.ESSENCE, new AspectList().add(Aspect.BEAST, 40).add(Aspect.FLUX, 60), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(ItemsTC.crystalEssence));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IEESSENCE"), IEESSENCE);
        ThaumcraftApi.addFakeCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IEESSENCEFAKE"), new InfusionEnchantmentRecipe(IEESSENCE, new ItemStack(Items.STONE_SWORD)));
        InfusionEnchantmentRecipe IELAMPLIGHT = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.LAMPLIGHT, new AspectList().add(Aspect.LIGHT, 80).add(Aspect.AIR, 20), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), "nitor");
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IELAMPLIGHT"), IELAMPLIGHT);
        ThaumcraftApi.addFakeCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:IELAMPLIGHTFAKE"), new InfusionEnchantmentRecipe(IELAMPLIGHT, new ItemStack(Items.GOLDEN_PICKAXE)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:BootsTraveller"), new InfusionRecipe("BOOTSTRAVELLER", new ItemStack(ItemsTC.travellerBoots), 1, new AspectList().add(Aspect.FLIGHT, 100).add(Aspect.MOTION, 100), new ItemStack(Items.LEATHER_BOOTS), ConfigItems.AIR_CRYSTAL, ConfigItems.AIR_CRYSTAL, new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(Items.FEATHER), new ItemStack(Items.COD)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:MindBiothaumic"), new InfusionRecipe("MINDBIOTHAUMIC", new ItemStack(ItemsTC.mind), 4, new AspectList().add(Aspect.MIND, 50).add(Aspect.MECHANISM, 25), new ItemStack(ItemsTC.mind), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.mechanismComplex)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ArcaneBore"), new InfusionRecipe("ARCANEBORE", new ItemStack(ItemsTC.turretPlacer), 4, new AspectList().add(Aspect.ENERGY, 25).add(Aspect.EARTH, 25).add(Aspect.MECHANISM, 100).add(Aspect.VOID, 25).add(Aspect.MOTION, 25), new ItemStack(ItemsTC.turretPlacer), new ItemStack(BlocksTC.plankGreatwood), new ItemStack(BlocksTC.plankGreatwood), new ItemStack(ItemsTC.mechanismComplex), "plateBrass", Ingredient.of(Items.DIAMOND_PICKAXE), Ingredient.of(Items.DIAMOND_SHOVEL), new ItemStack(ItemsTC.morphicResonator), new ItemStack(ItemsTC.nuggets)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:LampGrowth"), new InfusionRecipe("LAMPGROWTH", new ItemStack(BlocksTC.lampGrowth), 4, new AspectList().add(Aspect.PLANT, 20).add(Aspect.LIGHT, 15).add(Aspect.LIFE, 15).add(Aspect.TOOL, 15), new ItemStack(BlocksTC.lampArcane), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.WHITE_DYE), ConfigItems.EARTH_CRYSTAL, new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.WHITE_DYE), ConfigItems.EARTH_CRYSTAL));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:LampFertility"), new InfusionRecipe("LAMPFERTILITY", new ItemStack(BlocksTC.lampFertility), 4, new AspectList().add(Aspect.BEAST, 20).add(Aspect.LIGHT, 15).add(Aspect.LIFE, 15).add(Aspect.DESIRE, 15), new ItemStack(BlocksTC.lampArcane), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.WHEAT), ConfigItems.FIRE_CRYSTAL, new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.CARROT), ConfigItems.FIRE_CRYSTAL));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ThaumiumFortressHelm"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressHelm), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 20).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumHelm), "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.EMERALD)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ThaumiumFortressChest"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressChest), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 30).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumChest), "plateThaumium", "plateThaumium", "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), "leather"));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:ThaumiumFortressLegs"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressLegs), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 25).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumLegs), "plateThaumium", "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), "leather"));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:VoidRobeHelm"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeHelm), 6, new AspectList().add(Aspect.METAL, 25).add(Aspect.SENSES, 25).add(Aspect.PROTECT, 25).add(Aspect.ENERGY, 25).add(Aspect.ELDRITCH, 25).add(Aspect.VOID, 25), new ItemStack(ItemsTC.voidHelm), new ItemStack(ItemsTC.goggles), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:VoidRobeChest"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeChest), 6, new AspectList().add(Aspect.METAL, 35).add(Aspect.PROTECT, 35).add(Aspect.ENERGY, 25).add(Aspect.ELDRITCH, 25).add(Aspect.VOID, 35), new ItemStack(ItemsTC.voidChest), new ItemStack(ItemsTC.clothChest), "plateVoid", "plateVoid", new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric), "leather"));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:VoidRobeLegs"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeLegs), 6, new AspectList().add(Aspect.METAL, 30).add(Aspect.PROTECT, 30).add(Aspect.ENERGY, 25).add(Aspect.ELDRITCH, 25).add(Aspect.VOID, 30), new ItemStack(ItemsTC.voidLegs), new ItemStack(ItemsTC.clothLegs), "plateVoid", "plateVoid", new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric), "leather"));
        // HelmGoggles: produces a fortressHelm with CUSTOM_DATA "goggles"=1b, requires any fortressHelm + slimeball + goggles
        { ItemStack outGoggles = new ItemStack(ItemsTC.fortressHelm); net.minecraft.nbt.CompoundTag tg = new net.minecraft.nbt.CompoundTag(); tg.putByte("goggles", (byte)1); outGoggles.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tg)); ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:HelmGoggles"), new InfusionRecipe("FORTRESSMASK", outGoggles, 5, new AspectList().add(Aspect.SENSES, 40).add(Aspect.AURA, 20).add(Aspect.PROTECT, 20), Ingredient.of(ItemsTC.fortressHelm), new ItemStack(Items.SLIME_BALL), Ingredient.of(ItemsTC.goggles))); }
        // Mask variants: produce a fortressHelm with CUSTOM_DATA "mask"=<int>
        { ItemStack outMask0 = new ItemStack(ItemsTC.fortressHelm); net.minecraft.nbt.CompoundTag tm0 = new net.minecraft.nbt.CompoundTag(); tm0.putInt("mask", 0); outMask0.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tm0)); ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:MaskGrinningDevil"), new InfusionRecipe("FORTRESSMASK", outMask0, 8, new AspectList().add(Aspect.MIND, 80).add(Aspect.LIFE, 80).add(Aspect.PROTECT, 20), Ingredient.of(ItemsTC.fortressHelm), new ItemStack(Items.BLACK_DYE), "plateIron", "leather", new ItemStack(BlocksTC.shimmerleaf), new ItemStack(ItemsTC.brain), "plateIron")); }
        { ItemStack outMask1 = new ItemStack(ItemsTC.fortressHelm); net.minecraft.nbt.CompoundTag tm1 = new net.minecraft.nbt.CompoundTag(); tm1.putInt("mask", 1); outMask1.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tm1)); ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:MaskAngryGhost"), new InfusionRecipe("FORTRESSMASK", outMask1, 8, new AspectList().add(Aspect.ENTROPY, 80).add(Aspect.DEATH, 80).add(Aspect.PROTECT, 20), Ingredient.of(ItemsTC.fortressHelm), new ItemStack(Items.WHITE_DYE), "plateIron", "leather", new ItemStack(Items.POISONOUS_POTATO), new ItemStack(Items.WITHER_SKELETON_SKULL), "plateIron")); }
        { ItemStack outMask2 = new ItemStack(ItemsTC.fortressHelm); net.minecraft.nbt.CompoundTag tm2 = new net.minecraft.nbt.CompoundTag(); tm2.putInt("mask", 2); outMask2.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tm2)); ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:MaskSippingFiend"), new InfusionRecipe("FORTRESSMASK", outMask2, 8, new AspectList().add(Aspect.UNDEAD, 80).add(Aspect.LIFE, 80).add(Aspect.PROTECT, 20), Ingredient.of(ItemsTC.fortressHelm), new ItemStack(Items.RED_DYE), "plateIron", "leather", new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.MILK_BUCKET), "plateIron")); }
        ItemStack isPC = new ItemStack(ItemsTC.primalCrusher);
        EnumInfusionEnchantment.addInfusionEnchantment(isPC, EnumInfusionEnchantment.DESTRUCTIVE, 1);
        EnumInfusionEnchantment.addInfusionEnchantment(isPC, EnumInfusionEnchantment.REFINING, 1);
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:PrimalCrusher"), new InfusionRecipe("PRIMALCRUSHER", isPC, 6, new AspectList().add(Aspect.EARTH, 75).add(Aspect.TOOL, 75).add(Aspect.ENTROPY, 50).add(Aspect.VOID, 50).add(Aspect.AVERSION, 50).add(Aspect.ELDRITCH, 50).add(Aspect.DESIRE, 50), Ingredient.of(ItemsTC.primordialPearl), Ingredient.of(ItemsTC.voidPick), Ingredient.of(ItemsTC.voidShovel), Ingredient.of(ItemsTC.elementalPick), Ingredient.of(ItemsTC.elementalShovel)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:VerdantHeart"), new InfusionRecipe("VERDANTCHARMS", new ItemStack(ItemsTC.charmVerdant), 5, new AspectList().add(Aspect.LIFE, 60).add(Aspect.ORDER, 30).add(Aspect.PLANT, 60), new ItemStack(ItemsTC.baubles), new ItemStack(ItemsTC.nuggets), ThaumcraftApiHelper.makeCrystal(Aspect.LIFE), new ItemStack(Items.MILK_BUCKET), ThaumcraftApiHelper.makeCrystal(Aspect.PLANT)));
        // VerdantHeartLife: output charmVerdant with type=1, input requires strong_healing potion (matched via POTION_CONTENTS DataComponent)
        { ItemStack outVHL = new ItemStack(ItemsTC.charmVerdant); net.minecraft.nbt.CompoundTag tvhl = new net.minecraft.nbt.CompoundTag(); tvhl.putByte("type", (byte)1); outVHL.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tvhl)); ItemStack pis1 = new ItemStack(Items.POTION); pis1.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS, new net.minecraft.world.item.alchemy.PotionContents(net.minecraft.world.item.alchemy.Potions.STRONG_HEALING)); ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:VerdantHeartLife"), new InfusionRecipe("VERDANTCHARMS", outVHL, 5, new AspectList().add(Aspect.LIFE, 80).add(Aspect.MAN, 80), new ItemStack(ItemsTC.charmVerdant), new ItemStack(Items.GOLDEN_APPLE), ThaumcraftApiHelper.makeCrystal(Aspect.LIFE), new IngredientNBTTC(pis1).toVanilla(), ThaumcraftApiHelper.makeCrystal(Aspect.MAN))); }
        // VerdantHeartSustain: output charmVerdant with type=2, input requires strong_regeneration potion
        { ItemStack outVHS = new ItemStack(ItemsTC.charmVerdant); net.minecraft.nbt.CompoundTag tvhs = new net.minecraft.nbt.CompoundTag(); tvhs.putByte("type", (byte)2); outVHS.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tvhs)); ItemStack pis2 = new ItemStack(Items.POTION); pis2.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS, new net.minecraft.world.item.alchemy.PotionContents(net.minecraft.world.item.alchemy.Potions.STRONG_REGENERATION)); ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:VerdantHeartSustain"), new InfusionRecipe("VERDANTCHARMS", outVHS, 5, new AspectList().add(Aspect.DESIRE, 80).add(Aspect.AIR, 80), new ItemStack(ItemsTC.charmVerdant), new ItemStack(ItemsTC.tripleMeatTreat), ThaumcraftApiHelper.makeCrystal(Aspect.DESIRE), new IngredientNBTTC(pis2).toVanilla(), ThaumcraftApiHelper.makeCrystal(Aspect.AIR))); }
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:CLOUDRING"), new InfusionRecipe("CLOUDRING", new ItemStack(ItemsTC.ringCloud), 1, new AspectList().add(Aspect.AIR, 50), new ItemStack(ItemsTC.baubles), ConfigItems.AIR_CRYSTAL, new ItemStack(Items.FEATHER)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:CuriosityBand"), new InfusionRecipe("CURIOSITYBAND", new ItemStack(ItemsTC.bandCuriosity), 5, new AspectList().add(Aspect.MIND, 150).add(Aspect.VOID, 50).add(Aspect.TRAP, 100), new ItemStack(ItemsTC.baubles), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:CHARMUNDYING"), new InfusionRecipe("CHARMUNDYING", new ItemStack(ItemsTC.charmUndying), 2, new AspectList().add(Aspect.LIFE, 25), new ItemStack(Items.TOTEM_OF_UNDYING), "plateBrass"));
        int a2 = 0;
        ItemStack[] nitorStacks = new ItemStack[16];
        for (DyeColor d : DyeColor.values()) {
            nitorStacks[a2] = new ItemStack(BlocksTC.nitor.get(d));
            ++a2;
        }
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:CausalityCollapser"), new InfusionRecipe("RIFTCLOSER", new ItemStack(ItemsTC.causalityCollapser), 8, new AspectList().add(Aspect.ELDRITCH, 50).add(Aspect.FLUX, 50), new ItemStack(Blocks.TNT), new ItemStack(ItemsTC.morphicResonator), new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(ItemsTC.alumentum), Ingredient.of(nitorStacks[0].getItem()), new ItemStack(ItemsTC.visResonator), new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(ItemsTC.alumentum), Ingredient.of(nitorStacks[0].getItem())));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:VoidSiphon"), new InfusionRecipe("VOIDSIPHON", new ItemStack(BlocksTC.voidSiphon), 7, new AspectList().add(Aspect.ELDRITCH, 50).add(Aspect.ENTROPY, 50).add(Aspect.VOID, 100).add(Aspect.CRAFT, 50), new ItemStack(BlocksTC.metalBlockVoid), new ItemStack(BlocksTC.stoneArcane), new ItemStack(BlocksTC.stoneArcane), new ItemStack(ItemsTC.mechanismComplex), "plateBrass", "plateBrass", new ItemStack(Items.NETHER_STAR)));
        ThaumcraftApi.addInfusionCraftingRecipe(net.minecraft.resources.Identifier.parse("thaumcraft:VoidseerPearl"), new InfusionRecipe("VOIDSEERPEARL", new ItemStack(ItemsTC.charmVoidseer), 8, new AspectList().add(Aspect.MIND, 150).add(Aspect.VOID, 150).add(Aspect.MAGIC, 100), new ItemStack(ItemsTC.baubles), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.voidSeed), new ItemStack(ItemsTC.brain), Ingredient.of(ItemsTC.primordialPearl)));
    }
    
    public static void initializeNormalRecipes(Object /* IForgeRegistry removed */ iForgeRegistry) {
        Identifier brassGroup = Identifier.fromNamespaceAndPath("thaumcraft", "brass_stuff");
        Identifier thaumiumGroup = Identifier.fromNamespaceAndPath("thaumcraft", "thaumium_stuff");
        Identifier voidGroup = Identifier.fromNamespaceAndPath("thaumcraft", "void_stuff");
        Identifier baublesGroup = Identifier.fromNamespaceAndPath("thaumcraft", "baubles_stuff");
        // Dye-based robe armor recipes, nugget conversion, wood/stair/slab recipes are in data/thaumcraft/recipe/ JSON files
        oreDictRecipe("nuggetstothaumium", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.ingots), new Object[] { "###", "###", "###", '#', "nuggetThaumium" });
        oreDictRecipe("nuggetstovoid", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.ingots), new Object[] { "###", "###", "###", '#', "nuggetVoid" });
        oreDictRecipe("nuggetstobrass", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.ingots), new Object[] { "###", "###", "###", '#', "nuggetBrass" });
        oreDictRecipe("nuggetstoquicksilver", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.quicksilver), new Object[] { "###", "###", "###", '#', "nuggetQuicksilver" });
        oreDictRecipe("thaumiumingotstoblock", thaumiumGroup, new ItemStack(BlocksTC.metalBlockThaumium), new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.ingots) });
        oreDictRecipe("thaumiumblocktoingots", thaumiumGroup, new ItemStack(ItemsTC.ingots), new Object[] { "#", '#', new ItemStack(BlocksTC.metalBlockThaumium) });
        oreDictRecipe("voidingotstoblock", voidGroup, new ItemStack(BlocksTC.metalBlockVoid), new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.ingots) });
        oreDictRecipe("voidblocktoingots", voidGroup, new ItemStack(ItemsTC.ingots), new Object[] { "#", '#', new ItemStack(BlocksTC.metalBlockVoid) });
        oreDictRecipe("brassingotstoblock", brassGroup, new ItemStack(BlocksTC.metalBlockBrass), new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.ingots) });
        oreDictRecipe("brassblocktoingots", brassGroup, new ItemStack(ItemsTC.ingots), new Object[] { "#", '#', new ItemStack(BlocksTC.metalBlockBrass) });
        oreDictRecipe("fleshtoblock", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.fleshBlock), new Object[] { "###", "###", "###", '#', Items.ROTTEN_FLESH });
        oreDictRecipe("blocktoflesh", ConfigRecipes.defaultGroup, new ItemStack(Items.ROTTEN_FLESH), new Object[] { "#", '#', BlocksTC.fleshBlock });
        oreDictRecipe("ambertoblock", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.amberBlock), new Object[] { "##", "##", '#', "gemAmber" });
        oreDictRecipe("amberblocktobrick", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.amberBrick, 4), new Object[] { "##", "##", '#', new ItemStack(BlocksTC.amberBlock) });
        oreDictRecipe("amberbricktoblock", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.amberBlock, 4), new Object[] { "##", "##", '#', new ItemStack(BlocksTC.amberBrick) });
        oreDictRecipe("amberblocktoamber", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.amber, 4), new Object[] { "#", '#', new ItemStack(BlocksTC.amberBlock) });
        oreDictRecipe("ironplate", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.plate), new Object[] { "BBB", 'B', "ingotIron" });
        oreDictRecipe("brassplate", brassGroup, new ItemStack(ItemsTC.plate), new Object[] { "BBB", 'B', "ingotBrass" });
        oreDictRecipe("thaumiumplate", thaumiumGroup, new ItemStack(ItemsTC.plate), new Object[] { "BBB", 'B', "ingotThaumium" });
        oreDictRecipe("thaumiumhelm", thaumiumGroup, new ItemStack(ItemsTC.thaumiumHelm, 1), new Object[] { "III", "I I", 'I', "ingotThaumium" });
        oreDictRecipe("thaumiumchest", thaumiumGroup, new ItemStack(ItemsTC.thaumiumChest, 1), new Object[] { "I I", "III", "III", 'I', "ingotThaumium" });
        oreDictRecipe("thaumiumlegs", thaumiumGroup, new ItemStack(ItemsTC.thaumiumLegs, 1), new Object[] { "III", "I I", "I I", 'I', "ingotThaumium" });
        oreDictRecipe("thaumiumboots", thaumiumGroup, new ItemStack(ItemsTC.thaumiumBoots, 1), new Object[] { "I I", "I I", 'I', "ingotThaumium" });
        oreDictRecipe("thaumiumshovel", thaumiumGroup, new ItemStack(ItemsTC.thaumiumShovel, 1), new Object[] { "I", "S", "S", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("thaumiumpick", thaumiumGroup, new ItemStack(ItemsTC.thaumiumPick, 1), new Object[] { "III", " S ", " S ", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("thaumiumaxe", thaumiumGroup, new ItemStack(ItemsTC.thaumiumAxe, 1), new Object[] { "II", "SI", "S ", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("thaumiumhoe", thaumiumGroup, new ItemStack(ItemsTC.thaumiumHoe, 1), new Object[] { "II", "S ", "S ", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("thaumiumsword", thaumiumGroup, new ItemStack(ItemsTC.thaumiumSword, 1), new Object[] { "I", "I", "S", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("voidplate", voidGroup, new ItemStack(ItemsTC.plate), new Object[] { "BBB", 'B', "ingotVoid" });
        oreDictRecipe("voidhelm", voidGroup, new ItemStack(ItemsTC.voidHelm, 1), new Object[] { "III", "I I", 'I', "ingotVoid" });
        oreDictRecipe("voidchest", voidGroup, new ItemStack(ItemsTC.voidChest, 1), new Object[] { "I I", "III", "III", 'I', "ingotVoid" });
        oreDictRecipe("voidlegs", voidGroup, new ItemStack(ItemsTC.voidLegs, 1), new Object[] { "III", "I I", "I I", 'I', "ingotVoid" });
        oreDictRecipe("voidboots", voidGroup, new ItemStack(ItemsTC.voidBoots, 1), new Object[] { "I I", "I I", 'I', "ingotVoid" });
        oreDictRecipe("voidshovel", voidGroup, new ItemStack(ItemsTC.voidShovel, 1), new Object[] { "I", "S", "S", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("voidpick", voidGroup, new ItemStack(ItemsTC.voidPick, 1), new Object[] { "III", " S ", " S ", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("voidaxe", voidGroup, new ItemStack(ItemsTC.voidAxe, 1), new Object[] { "II", "SI", "S ", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("voidhoe", voidGroup, new ItemStack(ItemsTC.voidHoe, 1), new Object[] { "II", "S ", "S ", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("voidsword", voidGroup, new ItemStack(ItemsTC.voidSword, 1), new Object[] { "I", "I", "S", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("babuleamulet", baublesGroup, new ItemStack(ItemsTC.baubles), new Object[] { " S ", "S S", " I ", 'S', "string", 'I', "ingotBrass" });
        oreDictRecipe("babulering", baublesGroup, new ItemStack(ItemsTC.baubles), new Object[] { "NNN", "N N", "NNN", 'N', "nuggetBrass" });
        oreDictRecipe("babulegirdle", baublesGroup, new ItemStack(ItemsTC.baubles), new Object[] { " L ", "L L", " I ", 'L', "leather", 'I', "ingotBrass" });
        oreDictRecipe("babuleamuletfancy", baublesGroup, new ItemStack(ItemsTC.baubles), new Object[] { " S ", "SGS", " I ", 'S', "string", 'G', "gemDiamond", 'I', "ingotGold" });
        oreDictRecipe("babuleringfancy", baublesGroup, new ItemStack(ItemsTC.baubles), new Object[] { "NGN", "N N", "NNN", 'G', "gemDiamond", 'N', "nuggetGold" });
        oreDictRecipe("babulegirdlefancy", baublesGroup, new ItemStack(ItemsTC.baubles), new Object[] { " L ", "LGL", " I ", 'L', "leather", 'G', "gemDiamond", 'I', "ingotGold" });
        // triple_meat_treat, salis_mundus, shimmerleaf, cinderpearl recipes are in data/thaumcraft/recipe/ JSON files
        Identifier labelsGroup = Identifier.fromNamespaceAndPath("thaumcraft", "jarlabels");
        shapelessOreDictRecipe("JarLabel", labelsGroup, new ItemStack(ItemsTC.label), new Object[] { "dyeBlack", "slimeball", Items.PAPER, Items.PAPER, Items.PAPER, Items.PAPER });
        int count = 0;
        for (Aspect aspect : Aspect.aspects.values()) {
            ItemStack output = new ItemStack(ItemsTC.label);
            ((IEssentiaContainerItem)output.getItem()).setAspects(output, new AspectList().add(aspect, 1));
            shapelessOreDictRecipe("label_" + aspect.getTag(), labelsGroup, output, new Object[] { new ItemStack(ItemsTC.label), new IngredientNBTTC(ItemPhial.makeFilledPhial(aspect)) });
        }
        shapelessOreDictRecipe("JarLabelNull", labelsGroup, new ItemStack(ItemsTC.label), new Object[] { new ItemStack(ItemsTC.label) });
        oreDictRecipe("StoneArcane", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stoneArcane, 9), new Object[] { "KKK", "KCK", "KKK", 'K', "stone", 'C', new ItemStack(ItemsTC.crystalEssence) });
        oreDictRecipe("phial", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.phial), new Object[] { " C ", "G G", " G ", 'G', "blockGlass", 'C', Items.CLAY_BALL });
        oreDictRecipe("tablewood", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.tableWood), new Object[] { "SSS", "W W", 'S', "slabWood", 'W', "plankWood" });
        oreDictRecipe("tablestone", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.tableStone), new Object[] { "SSS", "W W", 'S', new ItemStack(Blocks.STONE_SLAB), 'W', "stone" });
        Identifier inkwellGroup = Identifier.fromNamespaceAndPath("thaumcraft", "inkwell");
        shapelessOreDictRecipe("scribingtoolscraft1", inkwellGroup, new ItemStack(ItemsTC.scribingTools), new Object[] { new ItemStack(ItemsTC.phial), Items.FEATHER, "dyeBlack" });
        shapelessOreDictRecipe("scribingtoolscraft2", inkwellGroup, new ItemStack(ItemsTC.scribingTools), new Object[] { Items.GLASS_BOTTLE, Items.FEATHER, "dyeBlack" });
        shapelessOreDictRecipe("scribingtoolsrefill", inkwellGroup, new ItemStack(ItemsTC.scribingTools), new Object[] { new ItemStack(ItemsTC.scribingTools), "dyeBlack" });
        oreDictRecipe("GolemBell", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.golemBell), new Object[] { " QQ", " QQ", "S  ", 'S', "stickWood", 'Q', "gemQuartz" });
        Identifier candlesGroup = Identifier.fromNamespaceAndPath("thaumcraft", "tallowcandles");
        oreDictRecipe("TallowCandle", candlesGroup, new ItemStack(BlocksTC.candles.get(DyeColor.WHITE), 3), new Object[] { " S ", " T ", " T ", 'S', "string", 'T', new ItemStack(ItemsTC.tallow) });
        Object[] trs = new Object[16];
        int a = 0;
        for (DyeColor d : DyeColor.values()) {
            trs[a] = shapelessOreDictRecipe("TallowCandle" + d.getName().toLowerCase(), candlesGroup, new ItemStack(BlocksTC.candles.get(d)), new Object[] { ConfigAspects.dyes[15 - a], ingredientsFromBlocks(BlocksTC.candles.values().toArray(new Block[0])) });
            ++a;
        }
        oreDictRecipe("BrassBrace", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.jarBrace, 2), new Object[] { "NSN", "S S", "NSN", 'N', "nuggetBrass", 'S', "stickWood" });
    }
    
    public static Ingredient ingredientsFromBlocks(Block... blocks) {
        ItemStack[] aitemstack = new ItemStack[blocks.length];
        for (int i = 0; i < blocks.length; ++i) {
            aitemstack[i] = new ItemStack(blocks[i]);
        }
        return java.util.Arrays.stream(aitemstack).map(s -> Ingredient.of(s.getItem())).findFirst().orElse(Ingredient.of(net.minecraft.world.item.Items.AIR));
    }
    
    private static boolean smeltingInitialized = false;

    // Must be called after DataPackRegistries.apply() (i.e. ServerStartingEvent), not during mod loading.
    // ItemStack construction requires Holder$Reference.components to be bound, which only happens at world load.
    public static void initializeSmelting() {
        if (smeltingInitialized) return;
        smeltingInitialized = true;
        // Smelting recipes for ores and logs are in data/thaumcraft/recipe/ JSON files
        ThaumcraftApi.addSmeltingBonus("oreGold",    s(Items.GOLD_NUGGET));
        ThaumcraftApi.addSmeltingBonus("oreIron",    s(Items.IRON_NUGGET));
        ThaumcraftApi.addSmeltingBonus("oreCinnabar",s(ItemsTC.nuggets));
        ThaumcraftApi.addSmeltingBonus("oreCopper",  s(ItemsTC.nuggets));
        ThaumcraftApi.addSmeltingBonus("oreTin",     s(ItemsTC.nuggets));
        ThaumcraftApi.addSmeltingBonus("oreSilver",  s(ItemsTC.nuggets));
        ThaumcraftApi.addSmeltingBonus("oreLead",    s(ItemsTC.nuggets));
        ThaumcraftApi.addSmeltingBonus("oreQuartz",  s(ItemsTC.nuggets));
        ThaumcraftApi.addSmeltingBonus(s(ItemsTC.clusters), s(Items.IRON_NUGGET));
        ThaumcraftApi.addSmeltingBonus(s(ItemsTC.clusters), s(Items.GOLD_NUGGET));
        ThaumcraftApi.addSmeltingBonus(s(ItemsTC.clusters), s(ItemsTC.nuggets));
        ThaumcraftApi.addSmeltingBonus(s(Items.BEEF),    s(ItemsTC.chunks));
        ThaumcraftApi.addSmeltingBonus(s(Items.CHICKEN), s(ItemsTC.chunks));
        ThaumcraftApi.addSmeltingBonus(s(Items.PORKCHOP),s(ItemsTC.chunks));
        ThaumcraftApi.addSmeltingBonus(s(Items.COD),     s(ItemsTC.chunks));
        ThaumcraftApi.addSmeltingBonus(s(Items.RABBIT),  s(ItemsTC.chunks));
        ThaumcraftApi.addSmeltingBonus(s(Items.MUTTON),  s(ItemsTC.chunks));
        ThaumcraftApi.addSmeltingBonus("oreDiamond", s(ItemsTC.nuggets), 0.025f);
        ThaumcraftApi.addSmeltingBonus("oreRedstone",s(ItemsTC.nuggets), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreLapis",   s(ItemsTC.nuggets), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreEmerald", s(ItemsTC.nuggets), 0.025f);
        ThaumcraftApi.addSmeltingBonus("oreGold",    s(ItemsTC.nuggets), 0.02f);
        ThaumcraftApi.addSmeltingBonus("oreIron",    s(ItemsTC.nuggets), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreCinnabar",s(ItemsTC.nuggets), 0.025f);
        ThaumcraftApi.addSmeltingBonus("oreCopper",  s(ItemsTC.nuggets), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreTin",     s(ItemsTC.nuggets), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreSilver",  s(ItemsTC.nuggets), 0.02f);
        ThaumcraftApi.addSmeltingBonus("oreLead",    s(ItemsTC.nuggets), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreQuartz",  s(ItemsTC.nuggets), 0.01f);
        ThaumcraftApi.addSmeltingBonus(s(ItemsTC.clusters), s(ItemsTC.nuggets), 0.02f);
    }

    private static net.minecraft.world.item.ItemStack s(net.minecraft.world.level.ItemLike item) {
        return (item != null) ? new net.minecraft.world.item.ItemStack(item) : net.minecraft.world.item.ItemStack.EMPTY;
    }
    
    static Object shapelessOreDictRecipe(String name, Identifier optionalGroup, ItemStack res, Object[] params) {
        // ShapelessOreRecipe removed; these should be migrated to data/thaumcraft/recipe/ JSON files
        return null;
    }

    static Object oreDictRecipe(String name, Identifier optionalGroup, ItemStack res, Object[] params) {
        // ShapedOreRecipe removed; these should be migrated to data/thaumcraft/recipe/ JSON files
        return null;
    }
    
    public static void postAspects() {
        // Crucible hedge alchemy recipes added post-aspect initialization
    }

    public static void compileGroups() {
        // Recipe group compilation - recipes now managed via JSON datapacks
    }

    static {
        ConfigRecipes.defaultGroup = Identifier.withDefaultNamespace("thaumcraft");
        ConfigRecipes.recipeGroups = new HashMap<String, ArrayList<Identifier>>();
    }
}
