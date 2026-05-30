package thaumcraft.client.lib;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PositionTextureVertex;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
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

    public static void renderFacingQuad(double px, double py, double pz, int gridX, int gridY, int frame, float scale, int color, float alpha, int blend, float partialTicks) {
        // TODO: rewrite — ActiveRenderInfo, lastTickPosX/Y/Z, RenderSystem.pushMatrix removed in modern MC
    }

    public static void drawTexturedQuad(float par1, float par2, float par3, float par4, float par5, float par6, double zLevel) {
        // TODO: rewrite — tessellator.getBuffer().pos/tex/endVertex removed in modern MC
    }

    public static void drawTexturedQuadF(float par1, float par2, float par3, float par4, float par5, float par6, double zLevel) {
        // TODO: rewrite with modern rendering API
    }

    public static void drawTexturedQuadFull(float par1, float par2, double zLevel) {
        // TODO: rewrite with modern rendering API
    }

    public static void renderItemInGUI(int x, int y, int z, ItemStack stack) {
        // TODO: rewrite — RenderHelper, RenderSystem.pushMatrix, getRenderItem removed
    }

    public static void renderQuadCentered(Identifier texture, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        // TODO: rewrite — renderEngine.bindTexture removed
    }

    public static void renderQuadCentered(Identifier texture, int gridX, int gridY, int frame, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        // TODO: rewrite with modern rendering API
    }

    public static void renderQuadCentered() {
        renderQuadCentered(1, 1, 0, 1.0f, 1.0f, 1.0f, 1.0f, 200, 771, 1.0f);
    }

    public static void renderQuadCentered(int gridX, int gridY, int frame, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        // TODO: rewrite with modern rendering API
    }

    public static void renderQuadFromIcon(TextureAtlasSprite icon, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        // TODO: rewrite — renderEngine.bindTexture, tessellator vertex builder removed
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
        // TODO: rewrite — renderEngine.bindTexture, mc.font, tessellator vertex builder removed
    }

    public static void drawCustomTooltip(Screen gui, Font fr, List<String> textList, int x, int y, int subTipColor) {
        drawCustomTooltip(gui, fr, textList, x, y, subTipColor, false);
    }

    public static void drawCustomTooltip(Screen gui, Font fr, List<String> textList, int x, int y, int subTipColor, boolean ignoremouse) {
        // TODO: rewrite — ScaledResolution, Mouse, RenderHelper, getRenderItem removed
    }

    public static void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6) {
        // TODO: rewrite — tessellator vertex builder removed
    }

    public static void renderBillboardQuad(double scale) {
        // TODO: rewrite with modern rendering API
    }

    public static void renderBillboardQuad(double scale, int gridX, int gridY, int frame) {
        // TODO: rewrite with modern rendering API
    }

    public static void renderBillboardQuad(double scale, int gridX, int gridY, int frame, float r, float g, float b, float a, int bright) {
        // TODO: rewrite with modern rendering API
    }

    public static void rotateToPlayer() {
        // TODO: rewrite — getRenderManager().playerViewY/X removed
    }

    public static boolean renderItemStack(Minecraft mc, ItemStack itm, int x, int y, String txt) {
        // TODO: rewrite — RenderItem, RenderHelper, OpenGlHelper removed
        return false;
    }

    public static boolean renderItemStackShaded(Minecraft mc, ItemStack itm, int x, int y, String txt, float shade) {
        // TODO: rewrite with modern rendering API
        return false;
    }

    public static void drawBeam(Vector S, Vector E, Vector P, float width, int bright) {
        drawBeam(S, E, P, width, bright, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawBeam(Vector S, Vector E, Vector P, float width, int bright, float r, float g, float b, float a) {
        // TODO: rewrite — tessellator vertex builder removed
    }

    public static void drawQuad(Tesselator tessellator, Vector p1, Vector p2, Vector p3, Vector p4, int bright, float r, float g, float b, float a) {
        // TODO: rewrite — tessellator.getBuffer().pos/tex/lightmap/endVertex removed
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
        // TODO: rewrite — getTextureMapBlocks removed
    }

    public static void renderItemIn2D(TextureAtlasSprite icon, float thickness) {
        // TODO: rewrite — renderEngine.bindTexture removed
    }

    public static void renderTextureIn3D(float maxu, float maxv, float minu, float minv, int width, int height, float thickness) {
        // TODO: rewrite — tessellator vertex builder removed
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
