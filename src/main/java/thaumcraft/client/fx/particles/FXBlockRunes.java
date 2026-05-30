package thaumcraft.client.fx.particles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.level.Level;


public class FXBlockRunes extends Particle
{
    double ofx;
    double ofy;
    float rotation;
    int runeIndex;
    private float particleGravity;
    private float particleScale;
    private float particleAlpha;
    protected float rCol;
    protected float gCol;
    protected float bCol;

    public FXBlockRunes(Level world, double d, double d1, double d2, float f1, float f2, float f3, int m) {
        super((ClientLevel) world, d, d1, d2, 0.0, 0.0, 0.0);
        if (f1 == 0.0f) f1 = 1.0f;
        rotation = (float)(net.minecraft.util.RandomSource.create().nextInt(4) * 90);
        rCol = f1;
        gCol = f2;
        bCol = f3;
        xd = 0.0;
        yd = 0.0;
        zd = 0.0;
        lifetime = 3 * m;
        // setSize removed - dimensions in EntityType
        xo = x;
        yo = y;
        zo = z;
        runeIndex = (int)(Math.random() * 16.0 + 224.0);
        ofx = net.minecraft.util.RandomSource.create().nextFloat() * 0.2;
        ofy = -0.3 + net.minecraft.util.RandomSource.create().nextFloat() * 0.6;
        particleScale = (float)(1.0 + random.nextGaussian() * 0.1);
        particleAlpha = 0.0f;
    }

    public void setScale(float s) {
        particleScale = s;
    }

    public void setOffsetX(double f) {
        ofx = f;
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
        yd -= 0.04 * particleGravity;
        x += xd;
        y += yd;
        z += zd;
    }

    public void setGravity(float value) {
        particleGravity = value;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXBlockRunes visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
