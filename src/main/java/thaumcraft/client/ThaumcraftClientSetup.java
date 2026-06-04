package thaumcraft.client;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.entities.EntitiesTC;
import thaumcraft.client.gui.*;
import thaumcraft.client.renderers.tile.*;
import thaumcraft.common.config.TCBlockEntityTypes;
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
        modEventBus.addListener(this::registerRenderers);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Entity renderers — NoopRenderer for all entities (invisible but won't crash)
        event.registerEntityRenderer(EntitiesTC.CULTIST_PORTAL_GREATER.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.CULTIST_PORTAL_LESSER.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.FLUX_RIFT.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.SPECIAL_ITEM.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.FOLLOWING_ITEM.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.FALLING_TAINT.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.ALUMENTUM.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.GOLEM_DART.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.ELDRITCH_ORB.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.BOTTLE_TAINT.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.GOLEM_ORB.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.GRAPPLE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.CAUSALITY_COLLAPSER.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.FOCUS_PROJECTILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.FOCUS_CLOUD.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.FOCUS_MINE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.RIFT_BLAST.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.HOMING_SHARD.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.TURRET_CROSSBOW.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.TURRET_CROSSBOW_ADVANCED.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.ARCANE_BORE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.GOLEM.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.ELDRITCH_WARDEN.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.ELDRITCH_GOLEM.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.CULTIST_LEADER.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.TAINTACLE_GIANT.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.BRAINY_ZOMBIE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.GIANT_BRAINY_ZOMBIE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.WISP.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.FIRE_BAT.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.SPELL_BAT.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.PECH.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.MIND_SPIDER.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.ELDRITCH_GUARDIAN.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.CULTIST_KNIGHT.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.CULTIST_CLERIC.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.ELDRITCH_CRAB.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.INHABITED_ZOMBIE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.TAUMIC_SLIME.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.TAINT_CRAWLER.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.TAINT_SEED.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.TAINT_SEED_PRIME.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.TAINT_SWARM.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.TAINTACLE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntitiesTC.TAINTACLE_SMALL.get(), NoopRenderer::new);

        // Block entity renderers
        event.registerBlockEntityRenderer(TCBlockEntityTypes.JAR_FILLABLE.get(), ctx -> new TileJarRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.CRUCIBLE.get(), ctx -> new TileCrucibleRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.BELLOWS.get(), ctx -> new TileBellowsRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.ALEMBIC.get(), ctx -> new TileAlembicRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.BANNER.get(), ctx -> new TileBannerRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.CENTRIFUGE.get(), ctx -> new TileCentrifugeRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.DIOPTRA.get(), ctx -> new TileDioptraRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.FOCAL_MANIPULATOR.get(), ctx -> new TileFocalManipulatorRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.GOLEM_BUILDER.get(), ctx -> new TileGolemBuilderRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.HOLE.get(), ctx -> new TileHoleRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.HUNGRY_CHEST.get(), ctx -> new TileHungryChestRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.INFUSION_MATRIX.get(), ctx -> new TileInfusionMatrixRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.MIRROR.get(), ctx -> new TileMirrorRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.PATTERN_CRAFTER.get(), ctx -> new TilePatternCrafterRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.PEDESTAL.get(), ctx -> new TilePedestalRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.RECHARGE_PEDESTAL.get(), ctx -> new TileRechargePedestalRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.RESEARCH_TABLE.get(), ctx -> new TileResearchTableRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.THAUMATORIUM.get(), ctx -> new TileThaumatoriumRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.TUBE_BUFFER.get(), ctx -> new TileTubeBufferRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.TUBE_ONEWAY.get(), ctx -> new TileTubeOnewayRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.TUBE_VALVE.get(), ctx -> new TileTubeValveRenderer());
        event.registerBlockEntityRenderer(TCBlockEntityTypes.VOID_SIPHON.get(), ctx -> new TileVoidSiphonRenderer());
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
