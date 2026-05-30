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
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.CommandThaumcraft;
import thaumcraft.common.lib.InternalMethodHandler;
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

        ThaumcraftApi.internalMethods = new InternalMethodHandler();
        PlayerKnowledge.preInit();
        PlayerWarp.preInit();

        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ConfigItems.init();
            ConfigResearch.init();
            ConfigRecipes.initializeSmelting();
        });
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            ConfigEntities.postInitEntitySpawns();
            ConfigAspects.postInit();
            ConfigRecipes.postAspects();
            ModConfig.postInitLoot();
            ModConfig.postInitMisc();
            ConfigRecipes.compileGroups();
            ConfigResearch.postInit();
        });
    }

    private void onServerStarting(ServerStartingEvent event) {
        // TODO: register command via Commands.register once command class is ported
    }
}
