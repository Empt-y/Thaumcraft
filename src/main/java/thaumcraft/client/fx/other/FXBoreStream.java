package thaumcraft.client.fx.other;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import java.awt.Color;


public class FXBoreStream extends Particle
{
    private Entity target;
    private double startX;
    private double startY;
    private double startZ;
    private int count;
    public int length;
    private String key;
    private BlockPos startPos;
    int layer;
    int growing;
    private float particleScale;
    private float particleGravity;
    protected float rCol;
    protected float gCol;
    protected float bCol;

    public FXBoreStream(Level w, double par2, double par4, double par6, Entity target, int count, int color, float scale, int extend, double my) {
        super((ClientLevel) w, par2, par4, par6, 0.0, 0.0, 0.0);
        this.count = 0;
        length = 5;
        key = "";
        startPos = null;
        layer = 1;
        growing = -1;
        particleScale = (float)(scale * (1.0 + random.nextGaussian() * 0.15));
        length = Math.max(5, extend);
        this.count = count;
        this.target = target;
        lifetime = length * 10;
        xd = Mth.sin(count / 4.0f) * 0.15f;
        yd = my + Mth.sin(count / 3.0f) * 0.15f;
        zd = Mth.sin(count / 2.0f) * 0.15f;
        Color c = new Color(color);
        rCol = c.getRed() / 255.0f;
        gCol = c.getGreen() / 255.0f;
        bCol = c.getBlue() / 255.0f;
        particleGravity = 0.2f;
        startX = x;
        startY = y;
        startZ = z;
        startPos = BlockPos.containing(startX, startY, startZ);
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
        if (target != null) {
            double dx = target.getX() - x;
            double dy = target.getY() + target.getEyeHeight() - y;
            double dz = target.getZ() - z;
            double d11 = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double clamp = d11 / 10.0;
            xd = Mth.clamp((float) xd, -clamp, clamp);
            yd = Mth.clamp((float) yd, -clamp, clamp);
            zd = Mth.clamp((float) zd, -clamp, clamp);
            dx /= d11;
            dy /= d11;
            dz /= d11;
            xd += dx * (clamp / Math.min(1.0, d11));
            yd += dy * (clamp / Math.min(1.0, d11));
            zd += dz * (clamp / Math.min(1.0, d11));
            if (d11 < 1.0) {
                float f = Mth.sin((float)(d11 * Math.PI / 2.0));
                particleScale *= f;
            }
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
        // Rendering stub - FXBoreStream visual not ported to MC 26 render API (required CoreGLE library)
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
