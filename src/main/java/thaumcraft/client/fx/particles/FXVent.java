package thaumcraft.client.fx.particles;
import java.awt.Color;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;


public class FXVent extends Particle
{
    float psm;
    protected float rCol;
    protected float gCol;
    protected float bCol;
    private float particleScale;
    private float particleAlpha = 1.0f;

    public FXVent(Level par1World, double par2, double par4, double par6, double par8, double par10, double par12, int color) {
        super((ClientLevel) par1World, par2, par4, par6, par8, par10, par12);
        psm = 1.0f;
        particleScale = net.minecraft.util.RandomSource.create().nextFloat() * 0.1f + 0.05f;
        xd = par8;
        yd = par10;
        zd = par12;
        Color c = new Color(color);
        rCol = c.getRed() / 255.0f;
        bCol = c.getBlue() / 255.0f;
        gCol = c.getGreen() / 255.0f;
        setHeading(xd, yd, zd, 0.125f, 5.0f);
        Entity renderentity = Minecraft.getInstance().getCameraEntity();
        int visibleDistance = 50;
        if (renderentity != null && renderentity.distanceToSqr(par2, par4, par6) > (double)(visibleDistance * visibleDistance)) {
            lifetime = 0;
        }
        xo = x;
        yo = y;
        zo = z;
    }

    public void setScale(float f) {
        particleScale *= f;
        psm *= f;
    }

    public void setHeading(double dx, double dy, double dz, float sp, float noise) {
        float mag = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (mag > 0) { dx /= mag; dy /= mag; dz /= mag; }
        dx += random.nextGaussian() * (random.nextBoolean() ? -1 : 1) * 0.007499999832361937 * noise;
        dy += random.nextGaussian() * (random.nextBoolean() ? -1 : 1) * 0.007499999832361937 * noise;
        dz += random.nextGaussian() * (random.nextBoolean() ? -1 : 1) * 0.007499999832361937 * noise;
        xd = dx * sp;
        yd = dy * sp;
        zd = dz * sp;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        ++age;
        if (particleScale >= psm) {
            remove();
            return;
        }
        yd += 0.0025;
        move(xd, yd, zd);
        xd *= 0.8500000190734863;
        yd *= 0.8500000190734863;
        zd *= 0.8500000190734863;
        if (particleScale < psm) {
            particleScale *= 1.15f;
        }
        if (particleScale > psm) {
            particleScale = psm;
        }
        if (onGround) {
            xd *= 0.699999988079071;
            zd *= 0.699999988079071;
        }
    }

    public void setAlphaF(float alpha) {
        particleAlpha = alpha;
    }

    public void setRGB(float r, float g, float b) {
        rCol = r; gCol = g; bCol = b;
    }

    public int getFXLayer() {
        return 1;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXVent visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
