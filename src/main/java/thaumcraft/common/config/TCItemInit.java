package thaumcraft.common.config;

/**
 * Thread-local injection for Item.Properties.setId() during item construction.
 * In NeoForge 26.x / MC 1.21.x, Item.<init> calls Properties.itemIdOrThrow() immediately,
 * so the ResourceKey must be set before new Item(...) is called.
 *
 * Usage in ri():  TCItemInit.set(props);  Item item = factory.get();
 * Usage in Item:  super(TCItemInit.take())
 */
public final class TCItemInit {
    private static final ThreadLocal<net.minecraft.world.item.Item.Properties> PENDING = new ThreadLocal<>();

    public static void set(net.minecraft.world.item.Item.Properties props) {
        PENDING.set(props);
    }

    /** Returns the pending properties if one was set, otherwise a plain new Properties(). Clears the slot. */
    public static net.minecraft.world.item.Item.Properties take() {
        net.minecraft.world.item.Item.Properties p = PENDING.get();
        PENDING.remove();
        return p != null ? p : new net.minecraft.world.item.Item.Properties();
    }
}
