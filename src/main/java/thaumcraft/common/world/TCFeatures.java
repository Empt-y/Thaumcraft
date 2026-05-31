package thaumcraft.common.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.world.objects.WorldGenGreatwoodTrees;
import thaumcraft.common.world.objects.WorldGenSilverwoodTrees;

public class TCFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Thaumcraft.MODID);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> GREATWOOD_TREE =
            FEATURES.register("greatwood_tree", WorldGenGreatwoodTrees::new);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SILVERWOOD_TREE =
            FEATURES.register("silverwood_tree", WorldGenSilverwoodTrees::new);

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
