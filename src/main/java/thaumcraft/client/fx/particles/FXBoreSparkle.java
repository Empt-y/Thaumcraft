package thaumcraft.client.fx.particles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import thaumcraft.client.fx.ParticleEngine;


public class FXBoreSparkle extends SingleQuadParticle
{
    private Entity target;
    private double targetX;
    private double targetY;
    private double targetZ;

    private static TextureAtlasSprite sparkleSprite() {
        TextureAtlasSprite s = ParticleEngine.getSprite(256);
        return s != null ? s : ParticleEngine.getSprite(56);
    }

    public FXBoreSparkle(Level par1World, double par2, double par4, double par6, double tx, double ty, double tz) {
        super((ClientLevel) par1World, par2, par4, par6, 0.0, 0.0, 0.0, sparkleSprite());
        quadSize = net.minecraft.util.RandomSource.create().nextFloat() * 0.5f + 0.5f;
        targetX = tx; targetY = ty; targetZ = tz;
        double dx = tx - par2, dy = ty - par4, dz = tz - par6;
        int base = (int)(Math.sqrt(dx * dx + dy * dy + dz * dz) * 10.0);
        if (base < 1) base = 1;
        lifetime = base / 2 + net.minecraft.util.RandomSource.create().nextInt(base);
        xd = random.nextGaussian() * 0.01; yd = random.nextGaussian() * 0.01; zd = random.nextGaussian() * 0.01;
        rCol = 0.2f; gCol = 0.6f + random.nextFloat() * 0.3f; bCol = 0.2f;
        gravity = 0.2f; alpha = 1.0f;
        xo = par2; yo = par4; zo = par6;
        var cam = net.minecraft.client.Minecraft.getInstance().getCameraEntity();
        if (cam != null && cam.distanceToSqr(par2, par4, par6) > 64.0 * 64.0) lifetime = 0;
    }

    public FXBoreSparkle(Level par1World, double par2, double par4, double par6, Entity t) {
        this(par1World, par2, par4, par6, t.getX(), t.getY() + t.getEyeHeight(), t.getZ());
        target = t;
    }

    @Override
    public void tick() {
        xo = x; yo = y; zo = z;
        if (target != null) { targetX = target.getX(); targetY = target.getY() + target.getEyeHeight(); targetZ = target.getZ(); }
        // Animate: row 4, cols 0-3 (indices 256-259)
        TextureAtlasSprite s = ParticleEngine.getSprite(256 + age % 4);
        if (s != null) this.sprite = s;
        if (age++ >= lifetime || (Mth.floor(x) == Mth.floor(targetX) && Mth.floor(y) == Mth.floor(targetY) && Mth.floor(z) == Mth.floor(targetZ))) { remove(); return; }
        move(xd, yd, zd);
        xd *= 0.985; yd *= 0.95; zd *= 0.985;
        double dx = targetX - x, dy = targetY - y, dz = targetZ - z;
        double d11 = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double clamp = Math.min(0.25, d11 / 15.0);
        if (d11 < 2.0) quadSize *= 0.9f;
        if (d11 > 0) { dx /= d11; dy /= d11; dz /= d11; }
        xd += dx * clamp; yd += dy * clamp; zd += dz * clamp;
        xd = Mth.clamp((float) xd, -(float)clamp, (float)clamp);
        yd = Mth.clamp((float) yd, -(float)clamp, (float)clamp);
        zd = Mth.clamp((float) zd, -(float)clamp, (float)clamp);
        xd += random.nextGaussian() * 0.01; yd += random.nextGaussian() * 0.01; zd += random.nextGaussian() * 0.01;
    }

    public FXBoreSparkle setRBGColorF(float r, float g, float b) { rCol = r; gCol = g; bCol = b; return this; }
    public void setGravity(float value) { gravity = value; }

    @Override
    protected Layer getLayer() { return Layer.TRANSLUCENT; }

    @Override
    public ParticleRenderType getGroup() {
        return sprite != null ? ParticleRenderType.SINGLE_QUADS : ParticleRenderType.NO_RENDER;
    }
}
