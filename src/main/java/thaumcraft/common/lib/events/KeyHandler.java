package thaumcraft.common.lib.events;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
// import net.minecraftforge.fml.client.registry.Object /* ClientRegistry removed */; // removed
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent; // OK
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
// org.lwjgl.input.Keyboard removed in LWJGL 3
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.golems.ItemGolemBell;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;
import thaumcraft.common.lib.network.misc.PacketItemKeyToServer;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft", value = net.neoforged.api.distmarker.Dist.CLIENT)
public class KeyHandler
{
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
    
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void playerTick(PlayerTickEvent event) {
        if (event.side == Side.SERVER) {
            return;
        }
        if (true /* phase check removed, subscribe to .Pre */) {
            if (KeyHandler.keyF.isKeyDown()) {
                if (FMLClientHandler.instance().getClient().inGameHasFocus) {
                    Player player = event.getEntity();
                    if (player != null) {
                        if (!KeyHandler.keyPressedF) {
                            KeyHandler.lastPressF = System.currentTimeMillis();
                            KeyHandler.radialLock = false;
                        }
                        if (!KeyHandler.radialLock && ((player.getMainHandItem() != null && player.getMainHandItem().getItem() instanceof ICaster) || (player.getOffhandItem() != null && player.getOffhandItem().getItem() instanceof ICaster))) {
                            if (player.isCrouching()) {
                                PacketHandler.sendToServer(new PacketFocusChangeToServer("REMOVE"));
                            }
                            else {
                                KeyHandler.radialActive = true;
                            }
                        }
                        else if (player.getMainHandItem() != null && player.getMainHandItem().getItem() instanceof ItemGolemBell && !KeyHandler.keyPressedF) {
                            PacketHandler.sendToServer(new PacketItemKeyToServer(0));
                        }
                    }
                    KeyHandler.keyPressedF = true;
                }
            }
            else {
                KeyHandler.radialActive = false;
                if (KeyHandler.keyPressedF) {
                    KeyHandler.lastPressF = System.currentTimeMillis();
                }
                KeyHandler.keyPressedF = false;
            }
            if (KeyHandler.keyG.isKeyDown()) {
                if (FMLClientHandler.instance().getClient().inGameHasFocus) {
                    Player player = event.getEntity();
                    if (player != null && !KeyHandler.keyPressedG) {
                        KeyHandler.lastPressG = System.currentTimeMillis();
                        PacketHandler.sendToServer(new PacketItemKeyToServer(1, Keyboard.isKeyDown(29) ? 1 : (Keyboard.isKeyDown(42) ? 2 : 0)));
                    }
                    KeyHandler.keyPressedG = true;
                }
            }
            else {
                if (KeyHandler.keyPressedG) {
                    KeyHandler.lastPressG = System.currentTimeMillis();
                }
                KeyHandler.keyPressedG = false;
            }
        }
    }
    
    static {
        KeyHandler.keyF = new KeyMapping("Change Caster Focus", 33, "key.categories.thaumcraft");
        KeyHandler.keyG = new KeyMapping("Misc Caster Toggle", 34, "key.categories.thaumcraft");
        KeyHandler.keyPressedF = false;
        KeyHandler.keyPressedG = false;
        KeyHandler.radialActive = false;
        KeyHandler.radialLock = false;
        KeyHandler.lastPressF = 0L;
        KeyHandler.lastPressH = 0L;
        KeyHandler.lastPressG = 0L;
    }
}
