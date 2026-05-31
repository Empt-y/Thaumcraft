package thaumcraft.client.lib;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
        // TODO: rewrite with modern rendering API (tessellator vertex builder removed)
    }

    public void renderFaceYPos(Block b, double x, double y, double z, TextureAtlasSprite sprite, float r, float g, float bl, int bright) {
        // TODO: rewrite with modern rendering API
    }

    public void renderFaceZNeg(Block b, double x, double y, double z, TextureAtlasSprite sprite, float r, float g, float bl, int bright) {
        // TODO: rewrite with modern rendering API
    }

    public void renderFaceZPos(Block b, double x, double y, double z, TextureAtlasSprite sprite, float r, float g, float bl, int bright) {
        // TODO: rewrite with modern rendering API
    }

    public void renderFaceXNeg(Block b, double x, double y, double z, TextureAtlasSprite sprite, float r, float g, float bl, int bright) {
        // TODO: rewrite with modern rendering API
    }

    public void renderFaceXPos(Block b, double x, double y, double z, TextureAtlasSprite sprite, float r, float g, float bl, int bright) {
        // TODO: rewrite with modern rendering API
    }

    public static RenderCubes getInstance() {
        if (RenderCubes.instance == null) {
            RenderCubes.instance = new RenderCubes();
        }
        return RenderCubes.instance;
    }
}
