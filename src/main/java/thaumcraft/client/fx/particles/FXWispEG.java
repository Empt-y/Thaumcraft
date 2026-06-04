package thaumcraft.client.fx.particles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import thaumcraft.client.fx.ParticleEngine;


public class FXWispEG extends SingleQuadParticle
{
    Entity target;
    public int blendmode;

    private static TextureAtlasSprite wispSprite() {
        // Wisp uses row 3, col 0 as first frame (index 192)
        TextureAtlasSprite s = ParticleEngine.getSprite(192);
        if (s != null) return s;
        s = ParticleEngine.getSprite(56);
        return s;
    }

    public FXWispEG(Level world, double x, double y, double z, Entity target2) {
        super((ClientLevel) world, x, y, z, 0.0, 0.0, 0.0, wispSprite());
        target = null;
        blendmode = 1;
        target = target2;
        xd = random.nextGaussian() * 0.03;
        yd = -0.05;
        zd = random.nextGaussian() * 0.03;
        quadSize = 0.4f;
        lifetime = (int)(40.0 / (Math.random() * 0.3 + 0.7));
        xo = x; yo = y; zo = z;
        blendmode = 771;
        rCol = random.nextFloat() * 0.05f;
        gCol = random.nextFloat() * 0.05f;
        bCol = random.nextFloat() * 0.05f;
        alpha = 0.2f;
    }

    public int getFXLayer() { return (blendmode != 1) ? 1 : 0; }

    @Override
    public void tick() {
        xo = x; yo = y; zo = z;
        if (age++ >= lifetime) { remove(); return; }
        // Cycle through 13 wisp frames (row 3, cols 0-12)
        int frame = age % 13;
        TextureAtlasSprite s = ParticleEngine.getSprite(192 + frame);
        if (s != null) this.sprite = s;
        float agescale = 1.0f - age / (float) lifetime;
        alpha = 0.2f * agescale;
        move(xd, yd, zd);
        xd *= 0.98; yd *= 0.98; zd *= 0.98;
        if (target != null && !onGround) {
            x += target.getDeltaMovement().x;
            z += target.getDeltaMovement().z;
        }
    }

    @Override
    protected Layer getLayer() { return Layer.TRANSLUCENT; }

    @Override
    public ParticleRenderType getGroup() {
        return sprite != null ? ParticleRenderType.SINGLE_QUADS : ParticleRenderType.NO_RENDER;
    }
}
