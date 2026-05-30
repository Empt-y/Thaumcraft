package thaumcraft.client.fx.particles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;


public class FXVisSparkle extends Particle
{
    private double targetX;
    private double targetY;
    private double targetZ;
    float sizeMod;
    protected float rCol;
    protected float gCol;
    protected float bCol;
    private float particleScale;
    private float particleGravity;

    public FXVisSparkle(Level par1World, double par2, double par4, double par6, double tx, double ty, double tz) {
        super((ClientLevel) par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        sizeMod = 0.0f;
        particleScale = 0.0f;
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        lifetime = 1000;
        float f3 = 0.01f;
        xd = (float) random.nextGaussian() * f3;
        yd = (float) random.nextGaussian() * f3;
        zd = (float) random.nextGaussian() * f3;
        sizeMod = (float)(45 + net.minecraft.util.RandomSource.create().nextInt(15));
        rCol = 0.2f;
        gCol = 0.6f + net.minecraft.util.RandomSource.create().nextFloat() * 0.3f;
        bCol = 0.2f;
        particleGravity = 0.2f;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        move(xd, yd, zd);
        xd *= 0.985;
        yd *= 0.985;
        zd *= 0.985;
        double dx = targetX - x;
        double dy = targetY - y;
        double dz = targetZ - z;
        double d13 = 0.10000000149011612;
        double d14 = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (d14 < 2.0) {
            particleScale *= 0.95f;
        }
        if (d14 < 0.2) {
            lifetime = age;
        }
        if (age < 10) {
            particleScale = age / sizeMod;
        }
        dx /= d14;
        dy /= d14;
        dz /= d14;
        xd += dx * d13;
        yd += dy * d13;
        zd += dz * d13;
        xd = Mth.clamp((float) xd, -0.1f, 0.1f);
        yd = Mth.clamp((float) yd, -0.1f, 0.1f);
        zd = Mth.clamp((float) zd, -0.1f, 0.1f);
        if (age++ >= lifetime) {
            remove();
        }
    }

    public void setRBGColorF(float r, float g, float b) {
        rCol = r; gCol = g; bCol = b;
    }

    public void setGravity(float value) {
        particleGravity = value;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXVisSparkle visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
