package thaumcraft.client.fx.particles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.Level;
import thaumcraft.client.fx.ParticleEngine;


public class FXFireMote extends SingleQuadParticle
{
    float baseScale;
    float baseAlpha;
    int glowlayer;

    private static TextureAtlasSprite fireMoteSprite() {
        TextureAtlasSprite s = ParticleEngine.getSprite(7);
        return s != null ? s : ParticleEngine.getSprite(56);
    }

    public FXFireMote(Level worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float scale, int layer) {
        super((ClientLevel) worldIn, x, y, z, vx, vy, vz, fireMoteSprite());
        baseScale = 0.0f;
        baseAlpha = 1.0f;
        float colorR = r > 1.0f ? r / 255.0f : r;
        float colorG = g > 1.0f ? g / 255.0f : g;
        float colorB = b > 1.0f ? b / 255.0f : b;
        glowlayer = layer;
        rCol = colorR;
        gCol = colorG;
        bCol = colorB;
        alpha = 1.0f;
        lifetime = 16;
        quadSize = scale;
        baseScale = scale;
        xd = vx;
        yd = vy;
        zd = vz;
        roll = (float)(Math.PI * 2.0);
        xo = x; yo = y; zo = z;
    }

    public void setAlphaF(float a) { alpha = a; baseAlpha = a; }
    public int getFXLayer() { return glowlayer; }

    @Override
    public void tick() {
        xo = x; yo = y; zo = z;
        if (net.minecraft.util.RandomSource.create().nextInt(6) == 0) ++age;
        if (age++ >= lifetime) { remove(); return; }
        move(xd, yd, zd);
        xd *= 0.98; yd *= 0.98; zd *= 0.98;
        float lifespan = age / (float) lifetime;
        quadSize = baseScale - baseScale * lifespan;
        alpha = baseAlpha * (1.0f - lifespan);
        oRoll = roll;
        roll += 1.0f;
    }

    @Override
    protected Layer getLayer() { return Layer.TRANSLUCENT; }

    @Override
    public ParticleRenderType getGroup() {
        return sprite != null ? ParticleRenderType.SINGLE_QUADS : ParticleRenderType.NO_RENDER;
    }
}
