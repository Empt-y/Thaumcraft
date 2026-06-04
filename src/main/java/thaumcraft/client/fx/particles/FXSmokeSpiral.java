package thaumcraft.client.fx.particles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.Level;
import thaumcraft.client.fx.ParticleEngine;


public class FXSmokeSpiral extends SingleQuadParticle
{
    private float radius;
    private int start;
    private int miny;

    private static TextureAtlasSprite smokeSprite() {
        // SmokeSpiral uses row 0, col 1-5 (indices 1-5)
        TextureAtlasSprite s = ParticleEngine.getSprite(1);
        return s != null ? s : ParticleEngine.getSprite(56);
    }

    public FXSmokeSpiral(Level world, double d, double d1, double d2, float radius, int start, int miny) {
        super((ClientLevel) world, d, d1, d2, 0.0, 0.0, 0.0, smokeSprite());
        gravity = -0.01f;
        lifetime = 20 + net.minecraft.util.RandomSource.create().nextInt(10);
        xo = d; yo = d1; zo = d2;
        this.radius = radius;
        this.start = start;
        this.miny = miny;
        quadSize = radius * 0.25f;
        alpha = 0.66f;
        rCol = 0.3f; gCol = 0.3f; bCol = 0.3f;
    }

    public void setRBGColorF(float r, float g, float b) { rCol = r; gCol = g; bCol = b; }
    public int getFXLayer() { return 1; }

    @Override
    public void tick() {
        xo = x; yo = y; zo = z;
        alpha = (lifetime - age) / (float) lifetime * 0.66f;
        // Animate through smoke frames (row 0, cols 1-5, indices 1-5)
        int particleIdx = 1 + (int)(1.0f + age / (float) lifetime * 4.0f);
        if (particleIdx > 5) particleIdx = 5;
        TextureAtlasSprite s = ParticleEngine.getSprite(particleIdx);
        if (s != null) this.sprite = s;
        if (age++ >= lifetime) remove();
    }

    @Override
    protected Layer getLayer() { return Layer.TRANSLUCENT; }

    @Override
    public ParticleRenderType getGroup() {
        return sprite != null ? ParticleRenderType.SINGLE_QUADS : ParticleRenderType.NO_RENDER;
    }
}
