package thaumcraft.common.container;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public class TCMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Thaumcraft.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerSmelter>> SMELTER =
            MENUS.register("smelter", () -> IMenuTypeExtension.create(ContainerSmelter::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerArcaneWorkbench>> ARCANE_WORKBENCH =
            MENUS.register("arcane_workbench", () -> IMenuTypeExtension.create(ContainerArcaneWorkbench::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerResearchTable>> RESEARCH_TABLE =
            MENUS.register("research_table", () -> IMenuTypeExtension.create(ContainerResearchTable::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerThaumatorium>> THAUMATORIUM =
            MENUS.register("thaumatorium", () -> IMenuTypeExtension.create(ContainerThaumatorium::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerVoidSiphon>> VOID_SIPHON =
            MENUS.register("void_siphon", () -> IMenuTypeExtension.create(ContainerVoidSiphon::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerSpa>> SPA =
            MENUS.register("spa", () -> IMenuTypeExtension.create(ContainerSpa::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerPotionSprayer>> POTION_SPRAYER =
            MENUS.register("potion_sprayer", () -> IMenuTypeExtension.create(ContainerPotionSprayer::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerFocalManipulator>> FOCAL_MANIPULATOR =
            MENUS.register("focal_manipulator", () -> IMenuTypeExtension.create(ContainerFocalManipulator::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerFocusPouch>> FOCUS_POUCH =
            MENUS.register("focus_pouch", () -> IMenuTypeExtension.create(ContainerFocusPouch::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerGolemBuilder>> GOLEM_BUILDER =
            MENUS.register("golem_builder", () -> IMenuTypeExtension.create(ContainerGolemBuilder::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerArcaneBore>> ARCANE_BORE =
            MENUS.register("arcane_bore", () -> IMenuTypeExtension.create(ContainerArcaneBore::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerLogistics>> LOGISTICS =
            MENUS.register("logistics", () -> IMenuTypeExtension.create(ContainerLogistics::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerPech>> PECH =
            MENUS.register("pech", () -> IMenuTypeExtension.create(ContainerPech::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerTurretBasic>> TURRET_BASIC =
            MENUS.register("turret_basic", () -> IMenuTypeExtension.create(ContainerTurretBasic::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerTurretAdvanced>> TURRET_ADVANCED =
            MENUS.register("turret_advanced", () -> IMenuTypeExtension.create(ContainerTurretAdvanced::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerHandMirror>> HAND_MIRROR =
            MENUS.register("hand_mirror", () -> IMenuTypeExtension.create(ContainerHandMirror::new));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
