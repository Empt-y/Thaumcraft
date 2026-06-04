package thaumcraft.client.lib.events;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.items.IArchitect;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.ItemFocusPouch;
import thaumcraft.common.lib.events.KeyHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;

import net.minecraft.resources.Identifier;


public class WandRenderingHandler
{
    static float radialHudScale;
    TreeMap<String, Integer> foci;
    HashMap<String, ItemStack> fociItem;
    HashMap<String, Boolean> fociHover;
    HashMap<String, Float> fociScale;
    long lastTime;
    boolean lastState;
    int lastArcHash;
    ArrayList<BlockPos> architectBlocks;
    HashMap<BlockPos, boolean[]> bmCache;

    static final Identifier R1 = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/radial.png");
    static final Identifier R2 = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/radial2.png");
    static final Identifier CFRAME = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/frame_corner.png");
    static final Identifier SFRAME = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/frame_side.png");
    static final int[][] mos = {{ 4, 5, 6, 7 }, { 0, 1, 2, 3 }, { 0, 1, 4, 5 }, { 2, 3, 6, 7 }, { 0, 2, 4, 6 }, { 1, 3, 5, 7 }};
    static final int[][] rotmat = {{ 0, 90, 270, 180 }, { 270, 180, 0, 90 }, { 180, 90, 270, 0 }, { 0, 270, 90, 180 }, { 270, 180, 0, 90 }, { 180, 270, 90, 0 }};

    public WandRenderingHandler() {
        foci = new TreeMap<String, Integer>();
        fociItem = new HashMap<String, ItemStack>();
        fociHover = new HashMap<String, Boolean>();
        fociScale = new HashMap<String, Float>();
        lastTime = 0L;
        lastState = false;
        lastArcHash = 0;
        architectBlocks = new ArrayList<>();
        bmCache = new HashMap<>();
    }

    public void handleFociRadial(Minecraft mc, long time, net.neoforged.neoforge.client.event.RenderGuiLayerEvent event) {
        if (!KeyHandler.radialActive && radialHudScale <= 0.0f) {
            lastTime = time;
            return;
        }

        if (KeyHandler.radialActive) {
            if (mc.screen != null) {
                // Screen open — cancel radial
                KeyHandler.radialActive = false;
                KeyHandler.radialLock = true;
                return;
            }
            if (radialHudScale == 0.0f) {
                getFociInfo(mc);
                if (foci.size() > 0) {
                    mc.mouseHandler.releaseMouse();
                }
            }
        } else if (mc.screen == null && lastState) {
            mc.mouseHandler.grabMouse();
            lastState = false;
        }

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        renderFocusRadialHUD(mc, event.getGuiGraphics(), sw, sh, time, event.getPartialTick().getRealtimeDeltaTicks());

        if (time > lastTime) {
            for (String key : fociHover.keySet()) {
                if (fociHover.get(key)) {
                    if (!KeyHandler.radialActive && !KeyHandler.radialLock) {
                        PacketHandler.sendToServer(new PacketFocusChangeToServer(key));
                        KeyHandler.radialLock = true;
                    }
                    fociScale.put(key, Math.min(1.3f, fociScale.get(key) + getRadialChange(time, lastTime, 150L)));
                } else {
                    fociScale.put(key, Math.max(1.0f, fociScale.get(key) - getRadialChange(time, lastTime, 250L)));
                }
            }
            if (!KeyHandler.radialActive) {
                radialHudScale -= getRadialChange(time, lastTime, 150L);
            } else if (radialHudScale < 1.0f) {
                radialHudScale += getRadialChange(time, lastTime, 150L);
            }
            radialHudScale = Mth.clamp(radialHudScale, 0.0f, 1.0f);
            if (radialHudScale <= 0.0f) {
                KeyHandler.radialLock = false;
            }
            lastState = KeyHandler.radialActive;
        }
        lastTime = time;
    }

    private float getRadialChange(long time, long lasttime, long total) {
        return (time - lasttime) / (float) total;
    }

    private void getFociInfo(Minecraft mc) {
        foci.clear();
        fociItem.clear();
        fociHover.clear();
        fociScale.clear();
        int pouchcount = 0;
        // Scan main inventory for foci and focus pouches
        for (int a = 0; a < mc.player.getInventory().getContainerSize(); ++a) {
            ItemStack item = mc.player.getInventory().getItem(a);
            if (item.isEmpty()) continue;
            if (item.getItem() instanceof ItemFocus focus) {
                String sh = focus.getSortingHelper(item);
                if (sh != null) {
                    foci.put(sh, a);
                    fociItem.put(sh, item.copy());
                    fociScale.put(sh, 1.0f);
                    fociHover.put(sh, false);
                }
            } else if (item.getItem() instanceof ItemFocusPouch pouch) {
                ++pouchcount;
                var inv = pouch.getInventory(item);
                for (int q = 0; q < inv.size(); ++q) {
                    ItemStack fi = inv.get(q);
                    if (!fi.isEmpty() && fi.getItem() instanceof ItemFocus focus2) {
                        String sh = focus2.getSortingHelper(fi);
                        if (sh != null) {
                            foci.put(sh, q + pouchcount * 1000);
                            fociItem.put(sh, fi.copy());
                            fociScale.put(sh, 1.0f);
                            fociHover.put(sh, false);
                        }
                    }
                }
            }
        }
    }

    private void renderFocusRadialHUD(Minecraft mc, GuiGraphicsExtractor g, int sw, int sh, long time, float partialTicks) {
        if (fociItem.isEmpty()) return;

        // Get held wand focus
        ItemStack s = mc.player.getMainHandItem();
        if (!(s.getItem() instanceof ICaster)) s = mc.player.getOffhandItem();
        if (!(s.getItem() instanceof ICaster)) return;
        ICaster wand = (ICaster) s.getItem();

        // Mouse position in GUI coords
        double mouseX = mc.mouseHandler.xpos() * sw / mc.getWindow().getWidth();
        double mouseY = mc.mouseHandler.ypos() * sh / mc.getWindow().getHeight();

        int cx = sw / 2, cy = sh / 2;
        float width = 16.0f + fociItem.size() * 2.5f;

        // Scale around center
        float scl = radialHudScale;

        // Rotating background rings
        float rotAngle = partialTicks + mc.player.tickCount % 720 / 2.0f;
        // Draw ring 1 (R1) rotating clockwise
        int ringHalfW = (int)(width * 2.75f * scl);
        g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, R1,
               cx - ringHalfW, cy - ringHalfW, 0, 0, ringHalfW * 2, ringHalfW * 2, ringHalfW * 2, ringHalfW * 2);
        // Draw ring 2 (R2) rotating counter-clockwise
        int ring2HalfW = (int)(width * 2.55f * scl);
        g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, R2,
               cx - ring2HalfW, cy - ring2HalfW, 0, 0, ring2HalfW * 2, ring2HalfW * 2, ring2HalfW * 2, ring2HalfW * 2);

        // Draw center focus (currently active)
        ItemStack tt = null;
        ItemStack activeFocus = wand.getFocusStack(s);
        if (activeFocus != null && !activeFocus.isEmpty()) {
            int ix = cx - 8, iy = cy - 8;
            g.item(activeFocus, ix, iy);
            if (Math.abs(mouseX - cx) <= 10 && Math.abs(mouseY - cy) <= 10) {
                tt = activeFocus;
            }
        }

        // Draw foci in a ring
        float currentRot = -90.0f * scl;
        float pieSlice = 360.0f / fociItem.size();
        String key = foci.firstKey();
        for (int a = 0; a < fociItem.size(); ++a) {
            double xx = Mth.cos(currentRot / 180.0f * (float)Math.PI) * width * scl;
            double yy = Mth.sin(currentRot / 180.0f * (float)Math.PI) * width * scl;
            currentRot += pieSlice;

            float itemScl = fociScale.get(key);
            int ix = (int)(cx + xx - 8 * itemScl);
            int iy = (int)(cy + yy - 8 * itemScl);
            ItemStack focusItem = fociItem.get(key).copy();
            g.item(focusItem, ix, iy);

            if (!KeyHandler.radialLock && KeyHandler.radialActive) {
                double mx2 = mouseX - cx - xx;
                double my2 = mouseY - cy - yy;
                if (Math.abs(mx2) <= 10 && Math.abs(my2) <= 10) {
                    fociHover.put(key, true);
                    tt = focusItem;
                } else {
                    fociHover.put(key, false);
                }
            }

            key = foci.higherKey(key);
            if (key == null) break;
        }

        // Draw tooltip if hovering
        if (tt != null) {
            java.util.List<String> tooltipLines = new java.util.ArrayList<>();
            tooltipLines.add(tt.getHoverName().getString());
            UtilsFX.currentGuiGraphics = g;
            UtilsFX.drawCustomTooltip(mc.screen, mc.font, tooltipLines, (int)mouseX - 4, (int)mouseY + 20, 11);
            UtilsFX.currentGuiGraphics = null;
        }
    }

    // Tracks the last architect target for use in SubmitCustomGeometryEvent
    BlockPos lastArchitectTarget = null;

    public boolean handleArchitectOverlay(ItemStack stack, Player player, float partialTicks, int playerticks, HitResult target) {
        if (target == null || !(target instanceof net.minecraft.world.phys.BlockHitResult bhr)) return false;
        Minecraft mc = Minecraft.getInstance();
        if (!(stack.getItem() instanceof IArchitect af)) return false;
        BlockPos targetPos = bhr.getBlockPos();
        net.minecraft.core.Direction sideHit = bhr.getDirection();
        String h = targetPos.getX() + "" + targetPos.getY() + "" + targetPos.getZ() + "" + sideHit + "" + playerticks / 5;
        int hc = h.hashCode();
        if (hc != lastArcHash) {
            lastArcHash = hc;
            bmCache.clear();
            architectBlocks = af.getArchitectBlocks(stack, mc.level, targetPos, sideHit, player);
        }
        lastArchitectTarget = targetPos;
        if (architectBlocks == null || architectBlocks.isEmpty()) return false;
        return true; // actual rendering done in SubmitCustomGeometryEvent
    }

    /** Called from SubmitCustomGeometryEvent to render architect overlay when blocks are selected. */
    public void submitArchitectOverlay(net.minecraft.client.renderer.SubmitNodeCollector collector,
            com.mojang.blaze3d.vertex.PoseStack ps, net.minecraft.world.phys.Vec3 cam, Player player) {
        if (architectBlocks == null || architectBlocks.isEmpty()) return;
        UtilsFX.currentCollector = collector;
        UtilsFX.currentPoseStack = ps;
        for (BlockPos pos : architectBlocks) {
            drawOverlayBlock(pos, ps, cam, player);
        }
        UtilsFX.currentCollector = null;
        UtilsFX.currentPoseStack = null;
    }

    private boolean isConnectedBlock(BlockPos pos) {
        return architectBlocks != null && architectBlocks.contains(pos);
    }

    private void drawOverlayBlock(BlockPos pos, com.mojang.blaze3d.vertex.PoseStack ps, net.minecraft.world.phys.Vec3 cam, Player player) {
        ps.pushPose();
        ps.translate(pos.getX() + 0.5 - cam.x, pos.getY() + 0.5 - cam.y, pos.getZ() + 0.5 - cam.z);
        for (Direction face : Direction.values()) {
            if (!isConnectedBlock(pos.relative(face))) {
                ps.pushPose();
                // Rotate to face direction
                ps.mulPose(new org.joml.Quaternionf().rotateAxis(
                    (float)Math.toRadians(90),
                    -face.getStepY(), face.getStepX(), -face.getStepZ()));
                float zoff = face.getStepZ() < 0 ? -0.5f : 0.5f;
                ps.translate(0, 0, zoff);
                ps.mulPose(new org.joml.Quaternionf().rotateZ((float)Math.toRadians(90)));
                // Face overlay
                UtilsFX.currentTexture = SFRAME;
                UtilsFX.renderQuadCentered(SFRAME, 1, 1, 0, 1.0f, 1.0f, 1.0f, 1.0f, 200, 1, 0.1f);
                // Corner frames
                boolean[] bits = getConnectedCorners(pos, face);
                for (int a = 0; a < 4; ++a) {
                    if (bits[a]) {
                        ps.pushPose();
                        ps.mulPose(new org.joml.Quaternionf().rotateZ((float)Math.toRadians(rotmat[face.ordinal()][a])));
                        UtilsFX.currentTexture = CFRAME;
                        UtilsFX.renderQuadCentered(CFRAME, 1, 1, 0, 1.0f, 1.0f, 1.0f, 1.0f, 200, 1, 0.66f);
                        ps.popPose();
                    }
                }
                ps.popPose();
            }
        }
        ps.popPose();
    }

    private boolean[] getConnectedCorners(BlockPos pos, Direction face) {
        if (bmCache.containsKey(pos)) return bmCache.get(pos);
        // 4 corners for this face: top-left, top-right, bottom-left, bottom-right
        boolean[] b = new boolean[8];
        b[0] = !isConnectedBlock(pos.offset(-1, 0, 0)) && !isConnectedBlock(pos.offset(0, 0, -1)) && !isConnectedBlock(pos.offset(0, 1, 0));
        b[1] = !isConnectedBlock(pos.offset(1, 0, 0)) && !isConnectedBlock(pos.offset(0, 0, -1)) && !isConnectedBlock(pos.offset(0, 1, 0));
        b[2] = !isConnectedBlock(pos.offset(-1, 0, 0)) && !isConnectedBlock(pos.offset(0, 0, 1)) && !isConnectedBlock(pos.offset(0, 1, 0));
        b[3] = !isConnectedBlock(pos.offset(1, 0, 0)) && !isConnectedBlock(pos.offset(0, 0, 1)) && !isConnectedBlock(pos.offset(0, 1, 0));
        b[4] = !isConnectedBlock(pos.offset(-1, 0, 0)) && !isConnectedBlock(pos.offset(0, 0, -1)) && !isConnectedBlock(pos.offset(0, -1, 0));
        b[5] = !isConnectedBlock(pos.offset(1, 0, 0)) && !isConnectedBlock(pos.offset(0, 0, -1)) && !isConnectedBlock(pos.offset(0, -1, 0));
        b[6] = !isConnectedBlock(pos.offset(-1, 0, 0)) && !isConnectedBlock(pos.offset(0, 0, 1)) && !isConnectedBlock(pos.offset(0, -1, 0));
        b[7] = !isConnectedBlock(pos.offset(1, 0, 0)) && !isConnectedBlock(pos.offset(0, 0, 1)) && !isConnectedBlock(pos.offset(0, -1, 0));
        bmCache.put(pos, b);
        return b;
    }

    public void drawArchitectAxis(BlockPos pos, float partialTicks, boolean dx, boolean dy, boolean dz) {
        // Axis arrows are world-space rendering; submitted via SubmitCustomGeometryEvent — rendered in submitArchitectOverlay
    }

    static {
        WandRenderingHandler.radialHudScale = 0.0f;
    }
}
