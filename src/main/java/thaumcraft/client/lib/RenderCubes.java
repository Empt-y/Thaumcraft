package thaumcraft.client.lib;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;


public class RenderCubes
{
    public BlockGetter blockAccess;
    public boolean flipTexture;
    public boolean field_152631_f;
    public boolean renderAllFaces;
    public boolean useInventoryTint;
    public boolean renderFromInside;
    public double renderMinX;
    public double renderMaxX;
    public double renderMinY;
    public double renderMaxY;
    public double renderMinZ;
    public double renderMaxZ;
    public boolean lockBlockBounds;
    public boolean partialRenderBounds;
    public Minecraft minecraftRB;
    public int uvRotateEast;
    public int uvRotateWest;
    public int uvRotateSouth;
    public int uvRotateNorth;
    public int uvRotateTop;
    public int uvRotateBottom;
    public float aoLightValueScratchXYZNNN;
    public float aoLightValueScratchXYNN;
    public float aoLightValueScratchXYZNNP;
    public float aoLightValueScratchYZNN;
    public float aoLightValueScratchYZNP;
    public float aoLightValueScratchXYZPNN;
    public float aoLightValueScratchXYPN;
    public float aoLightValueScratchXYZPNP;
    public float aoLightValueScratchXYZNPN;
    public float aoLightValueScratchXYNP;
    public float aoLightValueScratchXYZNPP;
    public float aoLightValueScratchYZPN;
    public float aoLightValueScratchXYZPPN;
    public float aoLightValueScratchXYPP;
    public float aoLightValueScratchYZPP;
    public float aoLightValueScratchXYZPPP;
    public float aoLightValueScratchXZNN;
    public float aoLightValueScratchXZPN;
    public float aoLightValueScratchXZNP;
    public float aoLightValueScratchXZPP;
    public int aoBrightnessXYZNNN;
    public int aoBrightnessXYNN;
    public int aoBrightnessXYZNNP;
    public int aoBrightnessYZNN;
    public int aoBrightnessYZNP;
    public int aoBrightnessXYZPNN;
    public int aoBrightnessXYPN;
    public int aoBrightnessXYZPNP;
    public int aoBrightnessXYZNPN;
    public int aoBrightnessXYNP;
    public int aoBrightnessXYZNPP;
    public int aoBrightnessYZPN;
    public int aoBrightnessXYZPPN;
    public int aoBrightnessXYPP;
    public int aoBrightnessYZPP;
    public int aoBrightnessXYZPPP;
    public int aoBrightnessXZNN;
    public int aoBrightnessXZPN;
    public int aoBrightnessXZNP;
    public int aoBrightnessXZPP;
    public int brightnessTopLeft;
    public int brightnessBottomLeft;
    public int brightnessBottomRight;
    public int brightnessTopRight;
    public float colorRedTopLeft;
    public float colorRedBottomLeft;
    public float colorRedBottomRight;
    public float colorRedTopRight;
    public float colorGreenTopLeft;
    public float colorGreenBottomLeft;
    public float colorGreenBottomRight;
    public float colorGreenTopRight;
    public float colorBlueTopLeft;
    public float colorBlueBottomLeft;
    public float colorBlueBottomRight;
    public float colorBlueTopRight;
    private static RenderCubes instance;

    public RenderCubes(BlockGetter p_i1251_1_) {
        useInventoryTint = true;
        renderFromInside = false;
        blockAccess = p_i1251_1_;
        field_152631_f = false;
        flipTexture = false;
        minecraftRB = Minecraft.getInstance();
    }

    public RenderCubes() {
        useInventoryTint = true;
        renderFromInside = false;
        minecraftRB = Minecraft.getInstance();
    }

    public void setRenderBounds(double x0, double y0, double z0, double x1, double y1, double z1) {
        if (!lockBlockBounds) {
            renderMinX = x0; renderMaxX = x1;
            renderMinY = y0; renderMaxY = y1;
            renderMinZ = z0; renderMaxZ = z1;
            partialRenderBounds = (renderMinX > 0.0 || renderMaxX < 1.0 || renderMinY > 0.0 || renderMaxY < 1.0 || renderMinZ > 0.0 || renderMaxZ < 1.0);
        }
    }

    public void overrideBlockBounds(double x0, double y0, double z0, double x1, double y1, double z1) {
        renderMinX = x0; renderMaxX = x1;
        renderMinY = y0; renderMaxY = y1;
        renderMinZ = z0; renderMaxZ = z1;
        lockBlockBounds = true;
        partialRenderBounds = (renderMinX > 0.0 || renderMaxX < 1.0 || renderMinY > 0.0 || renderMaxY < 1.0 || renderMinZ > 0.0 || renderMaxZ < 1.0);
    }

    public void renderFaceYNeg(Block b, double x, double y, double z, TextureAtlasSprite sprite, float r, float g, float bl, int bright) {
        if (UtilsFX.currentCollector == null || UtilsFX.currentPoseStack == null) return;
        Identifier atlas = sprite.atlasLocation();
        // Y-negative (bottom): xz plane at y = renderMinY
        float u0 = renderMinX < 0.0 || renderMaxX > 1.0 ? sprite.getU0() : sprite.getU((float)renderMinX);
        float u1 = renderMinX < 0.0 || renderMaxX > 1.0 ? sprite.getU1() : sprite.getU((float)renderMaxX);
        float v0 = renderMinZ < 0.0 || renderMaxZ > 1.0 ? sprite.getV0() : sprite.getV((float)renderMinZ);
        float v1 = renderMinZ < 0.0 || renderMaxZ > 1.0 ? sprite.getV1() : sprite.getV((float)renderMaxZ);
        final float x0 = (float)(x + (renderFromInside ? renderMaxX : renderMinX));
        final float x1 = (float)(x + (renderFromInside ? renderMinX : renderMaxX));
        final float yy = (float)(y + renderMinY);
        final float z0 = (float)(z + renderMinZ), z1 = (float)(z + renderMaxZ);
        final float fu0=u0,fu1=u1,fv0=v0,fv1=v1; final int brt=bright;
        UtilsFX.currentCollector.submitCustomGeometry(UtilsFX.currentPoseStack, RenderTypes.entityTranslucent(atlas), (pose, buf) -> {
            buf.addVertex(pose, x0, yy, z1).setColor(r,g,bl,1f).setUv(fu0,fv1).setOverlay(0).setLight(brt).setNormal(0,-1,0);
            buf.addVertex(pose, x0, yy, z0).setColor(r,g,bl,1f).setUv(fu0,fv0).setOverlay(0).setLight(brt).setNormal(0,-1,0);
            buf.addVertex(pose, x1, yy, z0).setColor(r,g,bl,1f).setUv(fu1,fv0).setOverlay(0).setLight(brt).setNormal(0,-1,0);
            buf.addVertex(pose, x1, yy, z1).setColor(r,g,bl,1f).setUv(fu1,fv1).setOverlay(0).setLight(brt).setNormal(0,-1,0);
        });
    }

    public void renderFaceYPos(Block b, double x, double y, double z, TextureAtlasSprite sprite, float r, float g, float bl, int bright) {
        if (UtilsFX.currentCollector == null || UtilsFX.currentPoseStack == null) return;
        Identifier atlas = sprite.atlasLocation();
        // Y-positive (top): xz plane at y = renderMaxY
        float u0 = renderMinX < 0.0 || renderMaxX > 1.0 ? sprite.getU0() : sprite.getU((float)renderMinX);
        float u1 = renderMinX < 0.0 || renderMaxX > 1.0 ? sprite.getU1() : sprite.getU((float)renderMaxX);
        float v0 = renderMinZ < 0.0 || renderMaxZ > 1.0 ? sprite.getV0() : sprite.getV((float)renderMinZ);
        float v1 = renderMinZ < 0.0 || renderMaxZ > 1.0 ? sprite.getV1() : sprite.getV((float)renderMaxZ);
        final float x0 = (float)(x + (renderFromInside ? renderMaxX : renderMinX));
        final float x1 = (float)(x + (renderFromInside ? renderMinX : renderMaxX));
        final float yy = (float)(y + renderMaxY);
        final float z0 = (float)(z + renderMinZ), z1 = (float)(z + renderMaxZ);
        final float fu0=u0,fu1=u1,fv0=v0,fv1=v1; final int brt=bright;
        UtilsFX.currentCollector.submitCustomGeometry(UtilsFX.currentPoseStack, RenderTypes.entityTranslucent(atlas), (pose, buf) -> {
            buf.addVertex(pose, x1, yy, z1).setColor(r,g,bl,1f).setUv(fu1,fv1).setOverlay(0).setLight(brt).setNormal(0,1,0);
            buf.addVertex(pose, x1, yy, z0).setColor(r,g,bl,1f).setUv(fu1,fv0).setOverlay(0).setLight(brt).setNormal(0,1,0);
            buf.addVertex(pose, x0, yy, z0).setColor(r,g,bl,1f).setUv(fu0,fv0).setOverlay(0).setLight(brt).setNormal(0,1,0);
            buf.addVertex(pose, x0, yy, z1).setColor(r,g,bl,1f).setUv(fu0,fv1).setOverlay(0).setLight(brt).setNormal(0,1,0);
        });
    }

    public void renderFaceZNeg(Block b, double x, double y, double z, TextureAtlasSprite sprite, float r, float g, float bl, int bright) {
        if (UtilsFX.currentCollector == null || UtilsFX.currentPoseStack == null) return;
        Identifier atlas = sprite.atlasLocation();
        // Z-negative (north): xy plane at z = renderMinZ
        float u0 = renderMinX < 0.0 || renderMaxX > 1.0 ? sprite.getU0() : sprite.getU((float)renderMinX);
        float u1 = renderMinX < 0.0 || renderMaxX > 1.0 ? sprite.getU1() : sprite.getU((float)renderMaxX);
        float v0 = renderMinY < 0.0 || renderMaxY > 1.0 ? sprite.getV0() : sprite.getV((float)(1.0 - renderMaxY));
        float v1 = renderMinY < 0.0 || renderMaxY > 1.0 ? sprite.getV1() : sprite.getV((float)(1.0 - renderMinY));
        final float x0 = (float)(x + (renderFromInside ? renderMaxX : renderMinX));
        final float x1 = (float)(x + (renderFromInside ? renderMinX : renderMaxX));
        final float y0 = (float)(y + renderMinY), y1 = (float)(y + renderMaxY);
        final float zz = (float)(z + renderMinZ);
        final float fu0=u0,fu1=u1,fv0=v0,fv1=v1; final int brt=bright;
        UtilsFX.currentCollector.submitCustomGeometry(UtilsFX.currentPoseStack, RenderTypes.entityTranslucent(atlas), (pose, buf) -> {
            buf.addVertex(pose, x0, y1, zz).setColor(r,g,bl,1f).setUv(fu0,fv0).setOverlay(0).setLight(brt).setNormal(0,0,-1);
            buf.addVertex(pose, x1, y1, zz).setColor(r,g,bl,1f).setUv(fu1,fv0).setOverlay(0).setLight(brt).setNormal(0,0,-1);
            buf.addVertex(pose, x1, y0, zz).setColor(r,g,bl,1f).setUv(fu1,fv1).setOverlay(0).setLight(brt).setNormal(0,0,-1);
            buf.addVertex(pose, x0, y0, zz).setColor(r,g,bl,1f).setUv(fu0,fv1).setOverlay(0).setLight(brt).setNormal(0,0,-1);
        });
    }

    public void renderFaceZPos(Block b, double x, double y, double z, TextureAtlasSprite sprite, float r, float g, float bl, int bright) {
        if (UtilsFX.currentCollector == null || UtilsFX.currentPoseStack == null) return;
        Identifier atlas = sprite.atlasLocation();
        // Z-positive (south): xy plane at z = renderMaxZ
        float u0 = renderMinX < 0.0 || renderMaxX > 1.0 ? sprite.getU0() : sprite.getU((float)renderMinX);
        float u1 = renderMinX < 0.0 || renderMaxX > 1.0 ? sprite.getU1() : sprite.getU((float)renderMaxX);
        float v0 = renderMinY < 0.0 || renderMaxY > 1.0 ? sprite.getV0() : sprite.getV((float)(1.0 - renderMaxY));
        float v1 = renderMinY < 0.0 || renderMaxY > 1.0 ? sprite.getV1() : sprite.getV((float)(1.0 - renderMinY));
        final float x0 = (float)(x + (renderFromInside ? renderMaxX : renderMinX));
        final float x1 = (float)(x + (renderFromInside ? renderMinX : renderMaxX));
        final float y0 = (float)(y + renderMinY), y1 = (float)(y + renderMaxY);
        final float zz = (float)(z + renderMaxZ);
        final float fu0=u0,fu1=u1,fv0=v0,fv1=v1; final int brt=bright;
        UtilsFX.currentCollector.submitCustomGeometry(UtilsFX.currentPoseStack, RenderTypes.entityTranslucent(atlas), (pose, buf) -> {
            buf.addVertex(pose, x0, y1, zz).setColor(r,g,bl,1f).setUv(fu0,fv0).setOverlay(0).setLight(brt).setNormal(0,0,1);
            buf.addVertex(pose, x0, y0, zz).setColor(r,g,bl,1f).setUv(fu0,fv1).setOverlay(0).setLight(brt).setNormal(0,0,1);
            buf.addVertex(pose, x1, y0, zz).setColor(r,g,bl,1f).setUv(fu1,fv1).setOverlay(0).setLight(brt).setNormal(0,0,1);
            buf.addVertex(pose, x1, y1, zz).setColor(r,g,bl,1f).setUv(fu1,fv0).setOverlay(0).setLight(brt).setNormal(0,0,1);
        });
    }

    public void renderFaceXNeg(Block b, double x, double y, double z, TextureAtlasSprite sprite, float r, float g, float bl, int bright) {
        if (UtilsFX.currentCollector == null || UtilsFX.currentPoseStack == null) return;
        Identifier atlas = sprite.atlasLocation();
        // X-negative (west): yz plane at x = renderMinX
        float u0 = renderMinZ < 0.0 || renderMaxZ > 1.0 ? sprite.getU0() : sprite.getU((float)renderMinZ);
        float u1 = renderMinZ < 0.0 || renderMaxZ > 1.0 ? sprite.getU1() : sprite.getU((float)renderMaxZ);
        float v0 = renderMinY < 0.0 || renderMaxY > 1.0 ? sprite.getV0() : sprite.getV((float)(1.0 - renderMaxY));
        float v1 = renderMinY < 0.0 || renderMaxY > 1.0 ? sprite.getV1() : sprite.getV((float)(1.0 - renderMinY));
        final float xx = (float)(x + renderMinX);
        final float y0 = (float)(y + renderMinY), y1 = (float)(y + renderMaxY);
        final float z0 = (float)(z + (renderFromInside ? renderMaxZ : renderMinZ));
        final float z1 = (float)(z + (renderFromInside ? renderMinZ : renderMaxZ));
        final float fu0=u0,fu1=u1,fv0=v0,fv1=v1; final int brt=bright;
        UtilsFX.currentCollector.submitCustomGeometry(UtilsFX.currentPoseStack, RenderTypes.entityTranslucent(atlas), (pose, buf) -> {
            buf.addVertex(pose, xx, y1, z1).setColor(r,g,bl,1f).setUv(fu1,fv0).setOverlay(0).setLight(brt).setNormal(-1,0,0);
            buf.addVertex(pose, xx, y1, z0).setColor(r,g,bl,1f).setUv(fu0,fv0).setOverlay(0).setLight(brt).setNormal(-1,0,0);
            buf.addVertex(pose, xx, y0, z0).setColor(r,g,bl,1f).setUv(fu0,fv1).setOverlay(0).setLight(brt).setNormal(-1,0,0);
            buf.addVertex(pose, xx, y0, z1).setColor(r,g,bl,1f).setUv(fu1,fv1).setOverlay(0).setLight(brt).setNormal(-1,0,0);
        });
    }

    public void renderFaceXPos(Block b, double x, double y, double z, TextureAtlasSprite sprite, float r, float g, float bl, int bright) {
        if (UtilsFX.currentCollector == null || UtilsFX.currentPoseStack == null) return;
        Identifier atlas = sprite.atlasLocation();
        // X-positive (east): yz plane at x = renderMaxX
        float u0 = renderMinZ < 0.0 || renderMaxZ > 1.0 ? sprite.getU0() : sprite.getU((float)renderMinZ);
        float u1 = renderMinZ < 0.0 || renderMaxZ > 1.0 ? sprite.getU1() : sprite.getU((float)renderMaxZ);
        float v0 = renderMinY < 0.0 || renderMaxY > 1.0 ? sprite.getV0() : sprite.getV((float)(1.0 - renderMaxY));
        float v1 = renderMinY < 0.0 || renderMaxY > 1.0 ? sprite.getV1() : sprite.getV((float)(1.0 - renderMinY));
        final float xx = (float)(x + renderMaxX);
        final float y0 = (float)(y + renderMinY), y1 = (float)(y + renderMaxY);
        final float z0 = (float)(z + (renderFromInside ? renderMaxZ : renderMinZ));
        final float z1 = (float)(z + (renderFromInside ? renderMinZ : renderMaxZ));
        final float fu0=u0,fu1=u1,fv0=v0,fv1=v1; final int brt=bright;
        UtilsFX.currentCollector.submitCustomGeometry(UtilsFX.currentPoseStack, RenderTypes.entityTranslucent(atlas), (pose, buf) -> {
            buf.addVertex(pose, xx, y1, z0).setColor(r,g,bl,1f).setUv(fu0,fv0).setOverlay(0).setLight(brt).setNormal(1,0,0);
            buf.addVertex(pose, xx, y1, z1).setColor(r,g,bl,1f).setUv(fu1,fv0).setOverlay(0).setLight(brt).setNormal(1,0,0);
            buf.addVertex(pose, xx, y0, z1).setColor(r,g,bl,1f).setUv(fu1,fv1).setOverlay(0).setLight(brt).setNormal(1,0,0);
            buf.addVertex(pose, xx, y0, z0).setColor(r,g,bl,1f).setUv(fu0,fv1).setOverlay(0).setLight(brt).setNormal(1,0,0);
        });
    }

    public static RenderCubes getInstance() {
        if (RenderCubes.instance == null) {
            RenderCubes.instance = new RenderCubes();
        }
        return RenderCubes.instance;
    }
}
