package thaumcraft;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * TODO: Convert block/item/entity/potion/biome registration to DeferredRegister.
 * All RegisterEvent handlers need to become DeferredRegister entries
 * in ConfigBlocks, ConfigItems, ConfigEntities, etc.
 */
public class Registrar {

    public static void register(IEventBus modEventBus) {
        // TODO: wire up DeferredRegisters from ConfigBlocks, ConfigItems, etc.
    }
}
