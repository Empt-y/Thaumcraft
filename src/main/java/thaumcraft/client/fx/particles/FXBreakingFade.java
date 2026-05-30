package thaumcraft.client.fx.particles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class FXBreakingFade extends Particle
{
    private float particleScale;
    private float particleGravity;
    protected float rCol;
    protected float gCol;
    protected float bCol;
    private float particleAlpha = 1.0f;

    public FXBreakingFade(Level worldIn, double x, double y, double z, double vx, double vy, double vz, Item item, int meta) {
        super((ClientLevel) worldIn, x, y, z, vx, vy, vz);
        particleScale = net.minecraft.util.RandomSource.create().nextFloat() * 0.5f + 0.5f;
        lifetime = 4;
    }

    public FXBreakingFade(Level worldIn, double x, double y, double z, Item item, int meta) {
        super((ClientLevel) worldIn, x, y, z, 0.0, 0.0, 0.0);
        particleScale = net.minecraft.util.RandomSource.create().nextFloat() * 0.5f + 0.5f;
        lifetime = 4;
    }

    public FXBreakingFade(Level worldIn, double x, double y, double z, Item item) {
        super((ClientLevel) worldIn, x, y, z, 0.0, 0.0, 0.0);
        particleScale = net.minecraft.util.RandomSource.create().nextFloat() * 0.5f + 0.5f;
        lifetime = 4;
    }

    public void setRBGColorF(float r, float g, float b) {
        rCol = r; gCol = g; bCol = b;
    }

    public void setAlphaF(float alpha) {
        particleAlpha = alpha;
    }

    public void setParticleMaxAge(int max) {
        lifetime = max;
    }

    public void setParticleGravity(float f) {
        particleGravity = f;
    }

    public int getFXLayer() {
        return 1;
    }

    public void setSpeed(double vx, double vy, double vz) {
        xd = vx;
        yd = vy;
        zd = vz;
    }

    public void boom() {
        float f = (float)(Math.random() + Math.random() + 1.0) * 0.15f;
        double f2 = Math.sqrt(xd * xd + yd * yd + zd * zd);
        if (f2 > 0) {
            xd = xd / f2 * f * 0.964;
            yd = yd / f2 * f * 0.964 + 0.1;
            zd = zd / f2 * f * 0.964;
        }
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
            return;
        }
        move(xd, yd, zd);
        xd *= 0.98;
        yd -= 0.04 * particleGravity;
        zd *= 0.98;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXBreakingFade visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
