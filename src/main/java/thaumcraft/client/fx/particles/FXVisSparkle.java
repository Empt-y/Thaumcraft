package thaumcraft.client.fx.particles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import thaumcraft.client.fx.ParticleEngine;


public class FXVisSparkle extends SingleQuadParticle
{
    private double targetX;
    private double targetY;
    private double targetZ;
    float sizeMod;

    private static TextureAtlasSprite sparkleSprite() {
        // FXVisSparkle row 8, col 0 (index 512)
        TextureAtlasSprite s = ParticleEngine.getSprite(512);
        return s != null ? s : ParticleEngine.getSprite(56);
    }

    public FXVisSparkle(Level par1World, double par2, double par4, double par6, double tx, double ty, double tz) {
        super((ClientLevel) par1World, par2, par4, par6, 0.0, 0.0, 0.0, sparkleSprite());
        sizeMod = 0.0f;
        quadSize = 0.0f;
        targetX = tx; targetY = ty; targetZ = tz;
        lifetime = 1000;
        float f3 = 0.01f;
        xd = (float) random.nextGaussian() * f3;
        yd = (float) random.nextGaussian() * f3;
        zd = (float) random.nextGaussian() * f3;
        sizeMod = 45 + net.minecraft.util.RandomSource.create().nextInt(15);
        rCol = 0.2f;
        gCol = 0.6f + net.minecraft.util.RandomSource.create().nextFloat() * 0.3f;
        bCol = 0.2f;
        gravity = 0.2f;
        alpha = 0.5f;
        xo = par2; yo = par4; zo = par6;
    }

    @Override
    public void tick() {
        xo = x; yo = y; zo = z;
        // Cycle through sparkle frames (row 8, cols 0-3, indices 512-515)
        int frame = age % 4;
        TextureAtlasSprite s = ParticleEngine.getSprite(512 + frame);
        if (s != null) this.sprite = s;
        move(xd, yd, zd);
        xd *= 0.985; yd *= 0.985; zd *= 0.985;
        double dx = targetX - x;
        double dy = targetY - y;
        double dz = targetZ - z;
        double d14 = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (d14 < 2.0) quadSize *= 0.95f;
        if (d14 < 0.2) lifetime = age;
        if (age < 10) quadSize = age / sizeMod;
        if (d14 > 0) { dx /= d14; dy /= d14; dz /= d14; }
        double d13 = 0.1;
        xd += dx * d13; yd += dy * d13; zd += dz * d13;
        xd = Mth.clamp((float) xd, -0.1f, 0.1f);
        yd = Mth.clamp((float) yd, -0.1f, 0.1f);
        zd = Mth.clamp((float) zd, -0.1f, 0.1f);
        if (age++ >= lifetime) remove();
    }

    public void setRBGColorF(float r, float g, float b) { rCol = r; gCol = g; bCol = b; }
    public void setGravity(float value) { gravity = value; }

    @Override
    protected Layer getLayer() { return Layer.TRANSLUCENT; }

    @Override
    public ParticleRenderType getGroup() {
        return sprite != null ? ParticleRenderType.SINGLE_QUADS : ParticleRenderType.NO_RENDER;
    }
}
