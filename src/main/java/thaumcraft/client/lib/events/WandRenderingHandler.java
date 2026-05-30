package thaumcraft.client.lib.events;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.items.IArchitect;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.ItemFocusPouch;
import thaumcraft.common.lib.events.KeyHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;


@OnlyIn(Dist.CLIENT)
public class WandRenderingHandler
{
    static float radialHudScale;
    TreeMap<String, Integer> foci;
    HashMap<String, ItemStack> fociItem;
    HashMap<String, Boolean> fociHover;
    HashMap<String, Float> fociScale;
    long lastTime;
    boolean lastState;

    public WandRenderingHandler() {
        foci = new TreeMap<String, Integer>();
        fociItem = new HashMap<String, ItemStack>();
        fociHover = new HashMap<String, Boolean>();
        fociScale = new HashMap<String, Float>();
        lastTime = 0L;
        lastState = false;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleFociRadial(Minecraft mc, long time, net.neoforged.neoforge.client.event.RenderGuiLayerEvent event) {
        // TODO: rewrite with modern rendering API (Mouse, Display, currentScreen removed)
    }

    @OnlyIn(Dist.CLIENT)
    public boolean handleArchitectOverlay(ItemStack stack, Player player, float partialTicks, int playerticks, HitResult target) {
        return false; // TODO: rewrite with modern rendering API
    }

    @OnlyIn(Dist.CLIENT)
    public void drawOverlayBlock(BlockPos pos, int ticks, Minecraft mc, float partialTicks) {
        // TODO: rewrite with modern rendering API
    }

    @OnlyIn(Dist.CLIENT)
    public void drawArchitectAxis(BlockPos pos, float partialTicks, boolean dx, boolean dy, boolean dz) {
        // TODO: rewrite with modern rendering API
    }

    static {
        WandRenderingHandler.radialHudScale = 0.0f;
    }
}
