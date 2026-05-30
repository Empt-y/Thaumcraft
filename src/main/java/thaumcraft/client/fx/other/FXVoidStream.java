package thaumcraft.client.fx.other;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;


public class FXVoidStream extends Particle
{
    private double targetX;
    private double targetY;
    private double targetZ;
    private double startX;
    private double startY;
    private double startZ;
    private int seed;
    public int length;
    private static Identifier starsTexture;
    int layer;
    int growing;
    private float particleScale;
    private float particleGravity;

    public FXVoidStream(Level w, double par2, double par4, double par6, double tx, double ty, double tz, int seed, float scale) {
        super((ClientLevel) w, par2, par4, par6, 0.0, 0.0, 0.0);
        this.seed = seed;
        length = 40;
        layer = 1;
        growing = -1;
        particleScale = (float)(scale * (1.0 + random.nextGaussian() * 0.15));
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        double dx = tx - x;
        double dy = ty - y;
        double dz = tz - z;
        int base = (int)(Math.sqrt(dx * dx + dy * dy + dz * dz) * 21.0);
        if (base < 1) base = 1;
        lifetime = base * 2;
        xd = Mth.sin(seed / 4.0f) * 0.025f;
        yd = Mth.sin(seed / 3.0f) * 0.025f;
        zd = Mth.sin(seed / 2.0f) * 0.025f;
        particleGravity = 0.2f;
        startX = x;
        startY = y;
        startZ = z;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime || length < 1) {
            remove();
            return;
        }
        yd += 0.01 * particleGravity;
        move(xd, yd, zd);
        xd *= 0.985;
        yd *= 0.985;
        zd *= 0.985;
        xd = Mth.clamp((float) xd, -0.04f, 0.04f);
        yd = Mth.clamp((float) yd, -0.04f, 0.04f);
        zd = Mth.clamp((float) zd, -0.04f, 0.04f);
        double dx = targetX - x;
        double dy = targetY - y;
        double dz = targetZ - z;
        double d14 = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double d13 = 0.01;
        dx /= d14;
        dy /= d14;
        dz /= d14;
        xd += dx * (d13 / Math.min(1.0, d14)) + random.nextGaussian() * 0.015;
        yd += dy * (d13 / Math.min(1.0, d14)) + random.nextGaussian() * 0.015;
        zd += dz * (d13 / Math.min(1.0, d14)) + random.nextGaussian() * 0.015;
        float scale = particleScale * (0.75f + Mth.sin((seed + age) / 2.0f) * 0.25f);
        if (d14 < 0.5) {
            float f = Mth.sin((float)(d14 * Math.PI / 2.0));
            particleScale *= f;
        }
        if (particleScale <= 0.001) {
            if (growing < 0) growing = age;
            --length;
        }
    }

    public void setFXLayer(int l) { layer = l; }
    public int getFXLayer() { return layer; }
    public void setGravity(float value) { particleGravity = value; }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXVoidStream visual not ported to MC 26 render API (required CoreGLE/shader)
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }

    static {
        starsTexture = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/end_portal.png");
    }
}
