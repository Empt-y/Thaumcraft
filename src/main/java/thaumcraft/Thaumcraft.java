package thaumcraft;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.entities.EntitiesTC;
import thaumcraft.common.lib.crafting.RecipeMagicDust;
import thaumcraft.common.lib.crafting.RecipesRobeArmorDyes;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.config.TCBlockEntityTypes;
import thaumcraft.common.container.TCMenuTypes;
import thaumcraft.common.lib.CommandThaumcraft;
import thaumcraft.common.lib.InternalMethodHandler;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.capabilities.PlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerWarp;
import thaumcraft.common.lib.network.PacketHandler;

@Mod(Thaumcraft.MODID)
public class Thaumcraft {

    public static final String MODID = "thaumcraft";
    public static Logger log = LogManager.getLogger("THAUMCRAFT");

    public Thaumcraft(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(PacketHandler::register);
        modEventBus.addListener(this::registerBlocks);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(SoundsTC::registerSounds);
        TCMenuTypes.register(modEventBus);
        TCBlockEntityTypes.register(modEventBus);
        EntitiesTC.register(modEventBus);
        thaumcraft.common.world.TCFeatures.register(modEventBus);
        modEventBus.addListener(thaumcraft.common.config.TCEntityAttributes::registerAttributes);
        // Recipe serializers
        net.neoforged.neoforge.registries.DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<?>> recipeSerializers =
            net.neoforged.neoforge.registries.DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER, MODID);
        recipeSerializers.register("magic_dust", () -> RecipeMagicDust.SERIALIZER);
        recipeSerializers.register("robe_armor_dyes", () -> RecipesRobeArmorDyes.SERIALIZER);
        recipeSerializers.register(modEventBus);

        ThaumcraftApi.internalMethods = new InternalMethodHandler();
        PlayerKnowledge.preInit();
        PlayerWarp.preInit();
        thaumcraft.common.lib.capabilities.TCPlayerData.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
    }

    private void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        // Return the attachment-backed instance so the capability and persistent storage are the same object
        event.registerEntity(
            thaumcraft.api.capabilities.ThaumcraftCapabilities.WARP,
            net.minecraft.world.entity.EntityType.PLAYER,
            (player, ctx) -> player.getData(thaumcraft.common.lib.capabilities.TCPlayerData.WARP)
        );
        event.registerEntity(
            thaumcraft.api.capabilities.ThaumcraftCapabilities.KNOWLEDGE,
            net.minecraft.world.entity.EntityType.PLAYER,
            (player, ctx) -> player.getData(thaumcraft.common.lib.capabilities.TCPlayerData.KNOWLEDGE)
        );
    }

    private void registerBlocks(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.BLOCK)) {
            ConfigBlocks.initBlocks(null);
            ConfigBlocks.initTileEntities();
            ConfigBlocks.initMisc();
        }
        if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.ITEM)) {
            ConfigItems.initItems();
        }
        if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB)) {
            ConfigItems.initCreativeTab();
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ConfigItems.init();
            ConfigResearch.init();
        });
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        // All post-init that creates ItemStacks is deferred to onServerStarting
        // because Holder$Reference.components is only bound after DataPackRegistries.apply()
        event.enqueueWork(() -> {
            ConfigEntities.postInitEntitySpawns();
        });
    }

    private static boolean postInitDone = false;

    private void onServerStarting(ServerStartingEvent event) {
        ConfigRecipes.initializeSmelting();
        if (!postInitDone) {
            postInitDone = true;
            ConfigAspects.postInit();
            ConfigRecipes.postAspects();
            ModConfig.postInitLoot();
            ModConfig.postInitMisc();
            ConfigRecipes.compileGroups();
            ConfigResearch.postInit();
        }
        // TODO: register command via Commands.register once command class is ported
    }
}
