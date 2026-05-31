package thaumcraft.common.lib.events;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.golems.ItemGolemBell;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;
import thaumcraft.common.lib.network.misc.PacketItemKeyToServer;

@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft", value = Dist.CLIENT)
public class KeyHandler {

    public static KeyMapping keyF;
    public static KeyMapping keyG;
    private static boolean keyPressedF;
    private boolean keyPressedH;
    private static boolean keyPressedG;
    public static boolean radialActive;
    public static boolean radialLock;
    public static long lastPressF;
    public static long lastPressH;
    public static long lastPressG;

    public KeyHandler() {
        keyPressedH = false;
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        boolean inGame = mc.screen == null && mc.level != null;
        if (!inGame) return;

        if (KeyHandler.keyF != null && KeyHandler.keyF.isDown()) {
            Player player = event.getEntity();
            if (player != null) {
                if (!KeyHandler.keyPressedF) {
                    KeyHandler.lastPressF = System.currentTimeMillis();
                    KeyHandler.radialLock = false;
                }
                if (!KeyHandler.radialLock && ((player.getMainHandItem() != null && player.getMainHandItem().getItem() instanceof ICaster)
                        || (player.getOffhandItem() != null && player.getOffhandItem().getItem() instanceof ICaster))) {
                    if (player.isCrouching()) {
                        PacketHandler.sendToServer(new PacketFocusChangeToServer("REMOVE"));
                    } else {
                        KeyHandler.radialActive = true;
                    }
                } else if (player.getMainHandItem() != null && player.getMainHandItem().getItem() instanceof ItemGolemBell
                        && !KeyHandler.keyPressedF) {
                    PacketHandler.sendToServer(new PacketItemKeyToServer(0));
                }
                KeyHandler.keyPressedF = true;
            }
        } else {
            KeyHandler.radialActive = false;
            if (KeyHandler.keyPressedF) {
                KeyHandler.lastPressF = System.currentTimeMillis();
            }
            KeyHandler.keyPressedF = false;
        }

        if (KeyHandler.keyG != null && KeyHandler.keyG.isDown()) {
            Player player = event.getEntity();
            if (player != null && !KeyHandler.keyPressedG) {
                KeyHandler.lastPressG = System.currentTimeMillis();
                // LWJGL 3: check ctrl/shift via InputConstants
                int modifier = 0;
                if (mc.options.keyShift.isDown()) modifier = 2;
                else if (mc.options.keySprint.isDown()) modifier = 1;
                PacketHandler.sendToServer(new PacketItemKeyToServer(1, modifier));
            }
            KeyHandler.keyPressedG = true;
        } else {
            if (KeyHandler.keyPressedG) {
                KeyHandler.lastPressG = System.currentTimeMillis();
            }
            KeyHandler.keyPressedG = false;
        }
    }

    static {
        KeyHandler.keyF = new KeyMapping("key.thaumcraft.focus", 33, net.minecraft.client.KeyMapping.Category.MISC);
        KeyHandler.keyG = new KeyMapping("key.thaumcraft.misc", 34, net.minecraft.client.KeyMapping.Category.MISC);
        KeyHandler.keyPressedF = false;
        KeyHandler.keyPressedG = false;
        KeyHandler.radialActive = false;
        KeyHandler.radialLock = false;
        KeyHandler.lastPressF = 0L;
        KeyHandler.lastPressH = 0L;
        KeyHandler.lastPressG = 0L;
    }
}
