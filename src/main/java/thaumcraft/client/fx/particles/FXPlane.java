package thaumcraft.client.fx.particles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;


public class FXPlane extends Particle
{
    float angle;
    float angleYaw;
    float anglePitch;
    protected float rCol;
    protected float gCol;
    protected float bCol;
    private float particleGravity;
    private float particleScale;
    private float particleAlpha;

    public FXPlane(Level world, double d, double d1, double d2, double m, double m1, double m2, int life) {
        super((ClientLevel) world, d, d1, d2, 0.0, 0.0, 0.0);
        rCol = 1.0f;
        gCol = 1.0f;
        bCol = 1.0f;
        particleGravity = 0.0f;
        lifetime = life;
        xo = d;
        yo = d1;
        zo = d2;
        particleScale = 1.0f;
        particleAlpha = 0.0f;
        double dx = m - x;
        double dy = m1 - y;
        double dz = m2 - z;
        xd = dx / lifetime;
        yd = dy / lifetime;
        zd = dz / lifetime;
        double d3 = Math.sqrt(dx * dx + dz * dz);
        angleYaw = 0.0f;
        anglePitch = 0.0f;
        if (d3 >= 1.0E-7) {
            angleYaw = (float)(Mth.atan2(dz, dx) * 180.0 / 3.141592653589793) - 90.0f;
            anglePitch = (float)(-(Mth.atan2(dy, d3) * 180.0 / 3.141592653589793));
        }
        angle = (float)(random.nextGaussian() * 20.0);
    }

    public int getFXLayer() {
        return 0;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        float threshold = lifetime / 5.0f;
        if (age <= threshold) {
            particleAlpha = age / threshold;
        } else {
            particleAlpha = (lifetime - age) / (float) lifetime;
        }
        if (age++ >= lifetime) {
            remove();
            return;
        }
        x += xd;
        y += yd;
        z += zd;
    }

    public void setGravity(float value) {
        particleGravity = value;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXPlane visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
