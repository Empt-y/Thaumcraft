package thaumcraft.client.lib;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;

import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.common.config.ModConfig;


public class UtilsFX
{
    public static Identifier nodeTexture;
    public static Object VERTEXFORMAT_POS_TEX_CO_LM_NO; // VertexFormat API removed
    public static String[] colorNames;
    public static String[] colorCodes;
    public static int[] colors;
    public static float sysPartialTicks;
    static DecimalFormat myFormatter;
    public static boolean hideStackOverlay;

    // World-space rendering context — set by tile renderers / event handlers before calling render helpers
    public static SubmitNodeCollector currentCollector = null;
    public static PoseStack currentPoseStack = null;
    public static Identifier currentTexture = null;

    public static void renderFacingQuad(double px, double py, double pz, int gridX, int gridY, int frame, float scale, int color, float alpha, int blend, float partialTicks) {
        // World-space billboard quad — requires PoseStack/SubmitCustomGeometryEvent in MC 26; not yet ported
    }

    public static void drawTexturedQuad(float par1, float par2, float par3, float par4, float par5, float par6, double zLevel) {
        // 2D GUI quad — use GuiGraphicsExtractor.blit() when a graphics context is available
        net.minecraft.client.gui.GuiGraphicsExtractor gg = currentGuiGraphics;
        if (gg == null) return;
        gg.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
            net.minecraft.resources.Identifier.withDefaultNamespace(""),
            (int)par1, (int)par2, par3 * 0.00390625f, par4 * 0.00390625f,
            (int)par5, (int)par6, 256, 256);
    }

    public static void drawTexturedQuadF(float par1, float par2, float par3, float par4, float par5, float par6, double zLevel) {
        // Identical semantics to drawTexturedQuad with 1/16 UV scale
        net.minecraft.client.gui.GuiGraphicsExtractor gg = currentGuiGraphics;
        if (gg == null) return;
        gg.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
            net.minecraft.resources.Identifier.withDefaultNamespace(""),
            (int)par1, (int)par2, par3 * 0.0625f, par4 * 0.0625f,
            16, 16, 256, 256);
    }

    public static void drawTexturedQuadFull(float par1, float par2, double zLevel) {
        net.minecraft.client.gui.GuiGraphicsExtractor gg = currentGuiGraphics;
        if (gg == null) return;
        gg.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
            net.minecraft.resources.Identifier.withDefaultNamespace(""),
            (int)par1, (int)par2, 0, 0, 16, 16, 16, 16);
    }

    public static void renderItemInGUI(int x, int y, int z, ItemStack stack) {
        net.minecraft.client.gui.GuiGraphicsExtractor gg = currentGuiGraphics;
        if (gg == null || stack.isEmpty()) return;
        gg.item(stack, x, y);
    }

    public static void renderQuadCentered(Identifier texture, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        renderQuadCentered(texture, 1, 1, 0, scale, red, green, blue, brightness, blend, opacity);
    }

    public static void renderQuadCentered(Identifier texture, int gridX, int gridY, int frame, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        currentTexture = texture;
        renderQuadCentered(gridX, gridY, frame, scale, red, green, blue, brightness, blend, opacity);
    }

    public static void renderQuadCentered() {
        renderQuadCentered(1, 1, 0, 1.0f, 1.0f, 1.0f, 1.0f, 200, 771, 1.0f);
    }

    public static void renderQuadCentered(int gridX, int gridY, int frame, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        if (currentCollector == null || currentPoseStack == null || currentTexture == null) return;
        int xm = frame % gridX;
        int ym = frame / gridY;
        final float f1 = xm / (float)gridX;
        final float f2 = f1 + 1f / gridX;
        final float f3 = ym / (float)gridY;
        final float f4 = f3 + 1f / gridY;
        final float s = scale * 0.5f;
        final float r = red, g = green, b = blue, a = opacity;
        final int bright = brightness;
        currentCollector.submitCustomGeometry(currentPoseStack, RenderTypes.entityTranslucent(currentTexture), (pose, buf) -> {
            buf.addVertex(pose, -s,  s, 0).setColor(r, g, b, a).setUv(f2, f4).setOverlay(0).setLight(bright).setNormal(0, 0, 1);
            buf.addVertex(pose,  s,  s, 0).setColor(r, g, b, a).setUv(f2, f3).setOverlay(0).setLight(bright).setNormal(0, 0, 1);
            buf.addVertex(pose,  s, -s, 0).setColor(r, g, b, a).setUv(f1, f3).setOverlay(0).setLight(bright).setNormal(0, 0, 1);
            buf.addVertex(pose, -s, -s, 0).setColor(r, g, b, a).setUv(f1, f4).setOverlay(0).setLight(bright).setNormal(0, 0, 1);
        });
    }

    public static void renderQuadFromIcon(TextureAtlasSprite icon, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        if (currentCollector == null || currentPoseStack == null) return;
        Identifier texAtlas = icon.atlasLocation();
        final float u0 = icon.getU0(), u1 = icon.getU1(), v0 = icon.getV0(), v1 = icon.getV1();
        final float s = scale * 0.5f;
        final float r = red, g = green, b = blue, a = opacity;
        final int bright = brightness;
        currentCollector.submitCustomGeometry(currentPoseStack, RenderTypes.entityTranslucent(texAtlas), (pose, buf) -> {
            buf.addVertex(pose, -s, -s, 0).setColor(r, g, b, a).setUv(u0, v1).setOverlay(0).setLight(bright).setNormal(0, 0, 1);
            buf.addVertex(pose,  s, -s, 0).setColor(r, g, b, a).setUv(u1, v1).setOverlay(0).setLight(bright).setNormal(0, 0, 1);
            buf.addVertex(pose,  s,  s, 0).setColor(r, g, b, a).setUv(u1, v0).setOverlay(0).setLight(bright).setNormal(0, 0, 1);
            buf.addVertex(pose, -s,  s, 0).setColor(r, g, b, a).setUv(u0, v0).setOverlay(0).setLight(bright).setNormal(0, 0, 1);
        });
    }

    public static void drawTag(int x, int y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha) {
        drawTag(x, y, aspect, amount, bonus, z, blend, alpha, false);
    }

    public static void drawTag(int x, int y, Aspect aspect, float amt, int bonus, double z) {
        drawTag(x, y, aspect, amt, bonus, z, 771, 1.0f, false);
    }

    public static void drawTag(int x, int y, Aspect aspect) {
        drawTag(x, y, aspect, 0.0f, 0, 0.0, 771, 1.0f, true);
    }

    public static void drawTag(int x, int y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha, boolean bw) {
        drawTag(x, (double)y, aspect, amount, bonus, z, blend, alpha, bw);
    }

    public static void drawTag(double x, double y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha, boolean bw) {
        // 2D GUI rendering via GuiGraphicsExtractor stored in thread-local during tooltip events
        net.minecraft.client.gui.GuiGraphicsExtractor gg = currentGuiGraphics;
        if (gg == null || aspect == null) return;
        int ix = (int)x, iy = (int)y;
        net.minecraft.resources.Identifier img = aspect.getImage();
        if (img != null) {
            // Draw 16x16 aspect icon
            gg.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, img, ix, iy, 0, 0, 16, 16, 16, 16);
        }
        if (amount > 0) {
            // Draw the amount number in the corner
            net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
            String label = (amount == (int)amount) ? String.valueOf((int)amount) : String.format("%.1f", amount);
            gg.text(font, label, ix + 16 - font.width(label), iy + 8, bw ? 0x888888 : aspect.getColor(), true);
        }
    }

    /** Set by HudHandler during tooltip rendering so drawTag can access the graphics context. */
    public static net.minecraft.client.gui.GuiGraphicsExtractor currentGuiGraphics = null;

    public static void drawCustomTooltip(Screen gui, Font fr, List<String> textList, int x, int y, int subTipColor) {
        drawCustomTooltip(gui, fr, textList, x, y, subTipColor, false);
    }

    public static void drawCustomTooltip(Screen gui, Font fr, List<String> textList, int x, int y, int subTipColor, boolean ignoremouse) {
        net.minecraft.client.gui.GuiGraphicsExtractor gg = currentGuiGraphics;
        if (gg == null || textList.isEmpty()) return;
        int sX = x + 12, sY = y - 12;
        int widestLineWidth = 0;
        for (String line : textList) widestLineWidth = Math.max(widestLineWidth, fr.width(line));
        int totalHeight = -2 + textList.size() * 10 + (textList.size() > 1 ? 2 : 0);
        int bgColor = 0xF0100010;
        drawGradientRect(sX - 3, sY - 4, sX + widestLineWidth + 3, sY - 3, bgColor, bgColor);
        drawGradientRect(sX - 3, sY + totalHeight + 3, sX + widestLineWidth + 3, sY + totalHeight + 4, bgColor, bgColor);
        drawGradientRect(sX - 3, sY - 3, sX + widestLineWidth + 3, sY + totalHeight + 3, bgColor, bgColor);
        drawGradientRect(sX - 4, sY - 3, sX - 3, sY + totalHeight + 3, bgColor, bgColor);
        drawGradientRect(sX + widestLineWidth + 3, sY - 3, sX + widestLineWidth + 4, sY + totalHeight + 3, bgColor, bgColor);
        int border = 0x505000FF;
        int borderFade = ((border & 0xFEFEFE) >> 1) | (border & 0xFF000000);
        drawGradientRect(sX - 3, sY - 3 + 1, sX - 2, sY + totalHeight + 2, border, borderFade);
        drawGradientRect(sX + widestLineWidth + 2, sY - 3 + 1, sX + widestLineWidth + 3, sY + totalHeight + 2, border, borderFade);
        drawGradientRect(sX - 3, sY - 3, sX + widestLineWidth + 3, sY - 2, border, border);
        drawGradientRect(sX - 3, sY + totalHeight + 2, sX + widestLineWidth + 3, sY + totalHeight + 3, borderFade, borderFade);
        for (int i = 0; i < textList.size(); i++) {
            String line = textList.get(i);
            int lineColor = (subTipColor != -99 && i == 0)
                ? (0xFF000000 | (subTipColor < 16 ? net.minecraft.util.ARGB.colorFromFloat(1, 0, 0, 0) : subTipColor))
                : 0xFFAAAAAA;
            if (i == 0 && subTipColor != -99) lineColor = 0xFFFFFF55;
            gg.text(fr, line, sX, sY, lineColor, true);
            sY += 10;
            if (i == 0) sY += 2;
        }
    }

    public static void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6) {
        net.minecraft.client.gui.GuiGraphicsExtractor gg = currentGuiGraphics;
        if (gg != null) {
            gg.fillGradient(par1, par2, par3, par4, par5, par6);
        }
    }

    public static void renderBillboardQuad(double scale) {
        if (currentCollector == null || currentPoseStack == null || currentTexture == null) return;
        renderBillboardQuad(scale, 1, 1, 0, 1f, 1f, 1f, 1f, 0xF000F0);
    }

    public static void renderBillboardQuad(double scale, int gridX, int gridY, int frame) {
        if (currentCollector == null || currentPoseStack == null || currentTexture == null) return;
        renderBillboardQuad(scale, gridX, gridY, frame, 1f, 1f, 1f, 1f, 0xF000F0);
    }

    public static void renderBillboardQuad(double scale, int gridX, int gridY, int frame, float r, float g, float b, float a, int bright) {
        if (currentCollector == null || currentPoseStack == null || currentTexture == null) return;
        int xm = frame % gridX;
        int ym = frame / gridY;
        final float f1 = xm / (float)gridX;
        final float f2 = f1 + 1f / gridX;
        final float f3 = ym / (float)gridY;
        final float f4 = f3 + 1f / gridY;
        final float s = (float)scale * 0.5f;
        final int brt = bright;
        currentCollector.submitCustomGeometry(currentPoseStack, RenderTypes.entityTranslucent(currentTexture), (pose, buf) -> {
            buf.addVertex(pose, -s, -s, 0).setColor(r, g, b, a).setUv(f1, f4).setOverlay(0).setLight(brt).setNormal(0, 0, 1);
            buf.addVertex(pose,  s, -s, 0).setColor(r, g, b, a).setUv(f2, f4).setOverlay(0).setLight(brt).setNormal(0, 0, 1);
            buf.addVertex(pose,  s,  s, 0).setColor(r, g, b, a).setUv(f2, f3).setOverlay(0).setLight(brt).setNormal(0, 0, 1);
            buf.addVertex(pose, -s,  s, 0).setColor(r, g, b, a).setUv(f1, f3).setOverlay(0).setLight(brt).setNormal(0, 0, 1);
        });
    }

    public static void rotateToPlayer() {
        if (currentPoseStack == null) return;
        // Apply camera rotation to billboard-face the player
        org.joml.Quaternionf camRot = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        currentPoseStack.mulPose(camRot);
    }

    public static boolean renderItemStack(Minecraft mc, ItemStack itm, int x, int y, String txt) {
        net.minecraft.client.gui.GuiGraphicsExtractor gg = currentGuiGraphics;
        if (gg == null || itm == null || itm.isEmpty()) return false;
        gg.item(itm, x, y);
        if (!hideStackOverlay) gg.itemDecorations(mc.font, itm, x, y);
        return true;
    }

    public static boolean renderItemStackShaded(Minecraft mc, ItemStack itm, int x, int y, String txt, float shade) {
        return renderItemStack(mc, itm, x, y, txt);
    }

    public static void drawBeam(Vector S, Vector E, Vector P, float width, int bright) {
        drawBeam(S, E, P, width, bright, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawBeam(Vector S, Vector E, Vector P, float width, int bright, float r, float g, float b, float a) {
        if (currentCollector == null || currentPoseStack == null) return;
        // Compute cross product of beam direction with camera-to-mid vector to get perpendicular
        Vector dir = Sub(E, S);
        Vector perp = Cross(dir, Sub(P, S));
        if (perp.norm() < 1e-6f) return;
        perp = Mul(perp.normalize(), width * 0.5f);
        Vector p1 = Add(S, perp);
        Vector p2 = Add(E, perp);
        Vector p3 = Sub(E, perp);
        Vector p4 = Sub(S, perp);
        drawQuad(null, p1, p2, p3, p4, bright, r, g, b, a);
    }

    public static void drawQuad(Tesselator tessellator, Vector p1, Vector p2, Vector p3, Vector p4, int bright, float r, float g, float b, float a) {
        if (currentCollector == null || currentPoseStack == null) return;
        Identifier tex = currentTexture != null ? currentTexture : nodeTexture;
        if (tex == null) return;
        final int brt = bright;
        currentCollector.submitCustomGeometry(currentPoseStack, RenderTypes.entityTranslucent(tex), (pose, buf) -> {
            buf.addVertex(pose, p1.x, p1.y, p1.z).setColor(r, g, b, a).setUv(0, 0).setOverlay(0).setLight(brt).setNormal(0, 1, 0);
            buf.addVertex(pose, p2.x, p2.y, p2.z).setColor(r, g, b, a).setUv(1, 0).setOverlay(0).setLight(brt).setNormal(0, 1, 0);
            buf.addVertex(pose, p3.x, p3.y, p3.z).setColor(r, g, b, a).setUv(1, 1).setOverlay(0).setLight(brt).setNormal(0, 1, 0);
            buf.addVertex(pose, p4.x, p4.y, p4.z).setColor(r, g, b, a).setUv(0, 1).setOverlay(0).setLight(brt).setNormal(0, 1, 0);
        });
    }

    private static Vector Cross(Vector a, Vector b) {
        float x = a.y * b.z - a.z * b.y;
        float y = a.z * b.x - a.x * b.z;
        float z = a.x * b.y - a.y * b.x;
        return new Vector(x, y, z);
    }

    public static Vector Sub(Vector a, Vector b) {
        return new Vector(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    private static Vector Add(Vector a, Vector b) {
        return new Vector(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    private static Vector Mul(Vector a, float f) {
        return new Vector(a.x * f, a.y * f, a.z * f);
    }

    public static void renderItemIn2D(String sprite, float thickness) {
        // 2D item-icon render in world space — sprite lookup not yet ported
    }

    public static void renderItemIn2D(TextureAtlasSprite icon, float thickness) {
        if (currentCollector == null || currentPoseStack == null) return;
        // Render a simplified flat face using the atlas sprite UVs
        renderTextureIn3D(icon.getU1(), icon.getV0(), icon.getU0(), icon.getV1(), 16, 16, thickness);
    }

    public static void renderTextureIn3D(float maxu, float maxv, float minu, float minv, int width, int height, float thickness) {
        if (currentCollector == null || currentPoseStack == null || currentTexture == null) return;
        // Front face (z=0) and back face (z=-thickness); skip edge slivers
        currentCollector.submitCustomGeometry(currentPoseStack, RenderTypes.entityTranslucent(currentTexture), (pose, buf) -> {
            // Front face
            buf.addVertex(pose, 0, 0, 0).setColor(1f,1f,1f,1f).setUv(minu, maxv).setOverlay(0).setLight(0xF000F0).setNormal(0, 0, 1);
            buf.addVertex(pose, 1, 0, 0).setColor(1f,1f,1f,1f).setUv(maxu, maxv).setOverlay(0).setLight(0xF000F0).setNormal(0, 0, 1);
            buf.addVertex(pose, 1, 1, 0).setColor(1f,1f,1f,1f).setUv(maxu, minv).setOverlay(0).setLight(0xF000F0).setNormal(0, 0, 1);
            buf.addVertex(pose, 0, 1, 0).setColor(1f,1f,1f,1f).setUv(minu, minv).setOverlay(0).setLight(0xF000F0).setNormal(0, 0, 1);
            // Back face (reverse winding)
            buf.addVertex(pose, 0, 1, -thickness).setColor(1f,1f,1f,1f).setUv(minu, minv).setOverlay(0).setLight(0xF000F0).setNormal(0, 0, -1);
            buf.addVertex(pose, 1, 1, -thickness).setColor(1f,1f,1f,1f).setUv(maxu, minv).setOverlay(0).setLight(0xF000F0).setNormal(0, 0, -1);
            buf.addVertex(pose, 1, 0, -thickness).setColor(1f,1f,1f,1f).setUv(maxu, maxv).setOverlay(0).setLight(0xF000F0).setNormal(0, 0, -1);
            buf.addVertex(pose, 0, 0, -thickness).setColor(1f,1f,1f,1f).setUv(minu, maxv).setOverlay(0).setLight(0xF000F0).setNormal(0, 0, -1);
        });
    }

    static {
        nodeTexture = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/auranodes.png");
        VERTEXFORMAT_POS_TEX_CO_LM_NO = null; // VertexFormat.addElement removed in modern MC
        colorNames = new String[] { "White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black" };
        colorCodes = new String[] { "§f", "§6", "§d", "§9", "§e", "§a", "§d", "§8", "§7", "§b", "§5", "§9", "§4", "§2", "§c", "§8" };
        colors = new int[] { 15790320, 15435844, 12801229, 6719955, 14602026, 4312372, 14188952, 4408131, 10526880, 2651799, 8073150, 2437522, 5320730, 3887386, 11743532, 1973019 };
        UtilsFX.sysPartialTicks = 0.0f;
        UtilsFX.myFormatter = new DecimalFormat("#######.##");
        UtilsFX.hideStackOverlay = false;
    }

    public static class Vector
    {
        public float x;
        public float y;
        public float z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float getX() { return x; }
        public float getY() { return y; }
        public float getZ() { return z; }

        public float norm() {
            return (float)Math.sqrt(x * x + y * y + z * z);
        }

        public Vector normalize() {
            float n = norm();
            return new Vector(x / n, y / n, z / n);
        }
    }
}
