package thaumcraft.client.fx.particles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.level.Level;


public class FXFireMote extends Particle
{
    float baseScale;
    float baseAlpha;
    int glowlayer;
    private float particleScale;
    private float particleAlpha;
    private float particleAngle;
    private float prevParticleAngle;
    protected float rCol;
    protected float gCol;
    protected float bCol;

    public FXFireMote(Level worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float scale, int layer) {
        super((ClientLevel) worldIn, x, y, z, 0.0, 0.0, 0.0);
        baseScale = 0.0f;
        baseAlpha = 1.0f;
        float colorR = r > 1.0f ? r / 255.0f : r;
        float colorG = g > 1.0f ? g / 255.0f : g;
        float colorB = b > 1.0f ? b / 255.0f : b;
        glowlayer = layer;
        rCol = colorR;
        gCol = colorG;
        bCol = colorB;
        particleAlpha = 1.0f;
        lifetime = 16;
        particleScale = scale;
        baseScale = scale;
        xd = vx;
        yd = vy;
        zd = vz;
        particleAngle = 6.2831855f;
    }

    public void setAlphaF(float alpha) {
        particleAlpha = alpha;
        baseAlpha = alpha;
    }

    public int getFXLayer() {
        return glowlayer;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (net.minecraft.util.RandomSource.create().nextInt(6) == 0) {
            ++age;
        }
        if (age++ >= lifetime) {
            remove();
            return;
        }
        move(xd, yd, zd);
        xd *= 0.98;
        yd *= 0.98;
        zd *= 0.98;
        float lifespan = age / (float) lifetime;
        particleScale = baseScale - baseScale * lifespan;
        baseAlpha = 1.0f - lifespan;
        prevParticleAngle = particleAngle;
        ++particleAngle;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXFireMote visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
