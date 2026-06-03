package thaumcraft.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.client.gui.*;
import thaumcraft.common.container.ContainerArcaneBore;
import thaumcraft.common.container.ContainerGolemBuilder;
import thaumcraft.common.container.ContainerHandMirror;
import thaumcraft.common.container.ContainerLogistics;
import thaumcraft.common.container.ContainerPech;
import thaumcraft.common.container.ContainerTurretAdvanced;
import thaumcraft.common.container.ContainerTurretBasic;
import thaumcraft.common.container.TCMenuTypes;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;

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
        event.register(TCMenuTypes.FOCUS_POUCH.get(),        GuiFocusPouch::new);
        event.<ContainerGolemBuilder,   GuiGolemBuilder>  register(TCMenuTypes.GOLEM_BUILDER.get(),   GuiGolemBuilder::new);
        event.<ContainerArcaneBore,     GuiArcaneBore>    register(TCMenuTypes.ARCANE_BORE.get(),     GuiArcaneBore::new);
        event.<ContainerLogistics,      GuiLogistics>     register(TCMenuTypes.LOGISTICS.get(),       GuiLogistics::new);
        event.<ContainerPech,           GuiPech>          register(TCMenuTypes.PECH.get(),            GuiPech::new);
        event.<ContainerTurretBasic,    GuiTurretBasic>   register(TCMenuTypes.TURRET_BASIC.get(),    GuiTurretBasic::new);
        event.<ContainerTurretAdvanced, GuiTurretAdvanced>register(TCMenuTypes.TURRET_ADVANCED.get(), GuiTurretAdvanced::new);
        event.<ContainerHandMirror,     GuiHandMirror>    register(TCMenuTypes.HAND_MIRROR.get(),     GuiHandMirror::new);
        event.<SealBaseContainer,       SealBaseGUI>      register(TCMenuTypes.SEAL_BASE.get(),        SealBaseGUI::new);
    }
}
