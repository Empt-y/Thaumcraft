package thaumcraft.client.lib.events;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.items.IArchitect;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketNote;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import thaumcraft.common.tiles.devices.TileRedstoneRelay;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft", value = Dist.CLIENT)
public class RenderEventHandler
{
    public static RenderEventHandler INSTANCE;
    @OnlyIn(Dist.CLIENT)
    public static HudHandler hudHandler;
    @OnlyIn(Dist.CLIENT)
    public static WandRenderingHandler wandHandler;
    @OnlyIn(Dist.CLIENT)
    static ShaderHandler shaderhandler;
    public static List blockTags;
    public static float tagscale;
    public static int tickCount;
    static boolean checkedDate;
    private java.util.Random random;
    public static boolean resetShaders;
    private static int oldDisplayWidth;
    private static int oldDisplayHeight;
    public static Entity thaumTarget;
    static Identifier CFRAME;
    static Identifier MIDDLE;
    static Direction[][] rotfaces;
    static int[][] rotmat;
    public static HashMap<Integer, Object> shaderGroups;
    public static boolean fogFiddled;
    public static float fogTarget;
    public static int fogDuration;
    public static float prevVignetteBrightness;
    public static float targetBrightness;
    protected static Identifier vignetteTexPath;

    public RenderEventHandler() {
        random = new java.util.Random();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void playerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || event.getEntity().getId() != mc.player.getId()) {
            return;
        }
        try {
            RenderEventHandler.shaderhandler.checkShaders(event, mc);
            if (ShaderHandler.warpVignette > 0) {
                --ShaderHandler.warpVignette;
                RenderEventHandler.targetBrightness = 0.0f;
            } else {
                RenderEventHandler.targetBrightness = 1.0f;
            }
            if (RenderEventHandler.fogFiddled) {
                if (RenderEventHandler.fogDuration < 100) {
                    RenderEventHandler.fogTarget = 0.1f * (RenderEventHandler.fogDuration / 100.0f);
                } else if (RenderEventHandler.fogTarget < 0.1f) {
                    RenderEventHandler.fogTarget += 0.001f;
                }
                --RenderEventHandler.fogDuration;
                if (RenderEventHandler.fogDuration < 0) {
                    RenderEventHandler.fogFiddled = false;
                }
            }
        } catch (Exception ex) {}
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void clientWorldTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Level world = mc.level;
        ++RenderEventHandler.tickCount;
        for (String fxk : EssentiaHandler.sourceFX.keySet().toArray(new String[0])) {
            EssentiaHandler.EssentiaSourceFX fx = EssentiaHandler.sourceFX.get(fxk);
            if (world != null) {
                int mod = 0;
                BlockEntity tile = world.getBlockEntity(fx.start);
                if (tile != null && tile instanceof TileInfusionMatrix) {
                    mod = -1;
                }
                FXDispatcher.INSTANCE.essentiaTrailFx(fx.end, fx.start.offset(0, mod, 0), RenderEventHandler.tickCount, fx.color, 0.1f, fx.ext);
                EssentiaHandler.sourceFX.remove(fxk);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        // TODO: update partial tick state via a render event
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void tooltipEvent(net.neoforged.neoforge.event.entity.player.ItemTooltipEvent event) {
        // TODO: rewrite with modern font/screen API
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void tooltipEvent(net.neoforged.neoforge.client.event.RenderTooltipEvent.Pre event) {
        // TODO: rewrite with modern tooltip API (getLines/getHeight removed)
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderOverlay(net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Post event) {
        // TODO: rewrite with modern GUI layer API (getType/getResolution removed)
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderShaders(net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Pre event) {
        // TODO: rewrite with modern shader API (OpenGlHelper/ShaderGroup removed)
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderLast(net.neoforged.neoforge.client.event.RenderLevelStageEvent.AfterLevel event) {
        if (RenderEventHandler.tagscale > 0.0f) {
            RenderEventHandler.tagscale -= 0.005f;
        }
        // TODO: rewrite remainder with modern rendering API (getRenderViewEntity removed)
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void fogDensityEvent(net.neoforged.neoforge.client.event.ViewportEvent.RenderFog event) {
        // TODO: rewrite — GL11 fog API removed in modern OpenGL
    }

    @SubscribeEvent
    public static void livingTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) {
        // TODO: rewrite — getEntityAttribute removed
    }

    @SubscribeEvent
    public static void renderLivingPre(RenderLivingEvent.Pre event) {
        // TODO: rewrite — getEntityAttribute removed
    }

    public static void drawTagsOnContainer(double x, double y, double z, AspectList tags, int bright, Direction dir, float partialTicks) {
        // TODO: rewrite with modern rendering API (fontRenderer removed)
    }

    public static void drawTextInAir(double x, double y, double z, float partialTicks, String text) {
        // TODO: rewrite with modern rendering API (fontRenderer removed)
    }

    protected static void renderVignette(float brightness, double sw, double sh) {
        // TODO: rewrite with modern rendering API (old tessellator/OpenGlHelper removed)
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void textureStitchEventPre(net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent event) {
        // TODO: rewrite — TextureAtlasStitchedEvent.addSprite API may have changed
    }

    static {
        RenderEventHandler.INSTANCE = new RenderEventHandler();
        RenderEventHandler.hudHandler = new HudHandler();
        RenderEventHandler.wandHandler = new WandRenderingHandler();
        RenderEventHandler.shaderhandler = new ShaderHandler();
        RenderEventHandler.blockTags = new ArrayList();
        RenderEventHandler.tagscale = 0.0f;
        RenderEventHandler.tickCount = 0;
        RenderEventHandler.checkedDate = false;
        RenderEventHandler.resetShaders = false;
        RenderEventHandler.oldDisplayWidth = 0;
        RenderEventHandler.oldDisplayHeight = 0;
        RenderEventHandler.thaumTarget = null;
        CFRAME = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/frame_corner.png");
        MIDDLE = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/seal_area.png");
        RenderEventHandler.rotfaces = new Direction[][] { { Direction.DOWN, Direction.NORTH, Direction.WEST }, { Direction.UP, Direction.NORTH, Direction.WEST }, { Direction.DOWN, Direction.NORTH, Direction.EAST }, { Direction.UP, Direction.NORTH, Direction.EAST }, { Direction.DOWN, Direction.SOUTH, Direction.EAST }, { Direction.UP, Direction.SOUTH, Direction.EAST }, { Direction.DOWN, Direction.SOUTH, Direction.WEST }, { Direction.UP, Direction.SOUTH, Direction.WEST } };
        RenderEventHandler.rotmat = new int[][] { { 0, 270, 0 }, { 270, 180, 270 }, { 90, 0, 90 }, { 180, 90, 180 }, { 180, 180, 0 }, { 90, 270, 270 }, { 270, 90, 90 }, { 0, 0, 180 } };
        RenderEventHandler.shaderGroups = new HashMap<Integer, Object>();
        RenderEventHandler.fogFiddled = false;
        RenderEventHandler.fogTarget = 0.0f;
        RenderEventHandler.fogDuration = 0;
        RenderEventHandler.prevVignetteBrightness = 0.0f;
        RenderEventHandler.targetBrightness = 1.0f;
        vignetteTexPath = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/vignette.png");
    }

    public static class ChargeEntry
    {
        public long time;
        public long tickTime;
        public ItemStack item;
        float charge;
        byte diff;

        public ChargeEntry(long time, ItemStack item, float charge) {
            this.charge = 0.0f;
            diff = 0;
            this.time = time;
            this.item = item;
            this.charge = charge;
        }
    }
}
