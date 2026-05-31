package thaumcraft.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.client.gui.*;
import thaumcraft.common.container.TCMenuTypes;

@Mod(value = Thaumcraft.MODID, dist = Dist.CLIENT)
public class ThaumcraftClientSetup {

    public ThaumcraftClientSetup(IEventBus modEventBus) {
        modEventBus.addListener(this::registerScreens);
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(TCMenuTypes.SMELTER.get(),            GuiSmelter::new);
        event.register(TCMenuTypes.ARCANE_WORKBENCH.get(),   GuiArcaneWorkbench::new);
        event.register(TCMenuTypes.RESEARCH_TABLE.get(),     GuiResearchTable::new);
        event.register(TCMenuTypes.THAUMATORIUM.get(),       GuiThaumatorium::new);
        event.register(TCMenuTypes.VOID_SIPHON.get(),        GuiVoidSiphon::new);
        event.register(TCMenuTypes.SPA.get(),                GuiSpa::new);
        event.register(TCMenuTypes.POTION_SPRAYER.get(),     GuiPotionSprayer::new);
        event.register(TCMenuTypes.FOCAL_MANIPULATOR.get(),  GuiFocalManipulator::new);
        event.register(TCMenuTypes.FOCUS_POUCH.get(),         GuiFocusPouch::new);
    }
}
