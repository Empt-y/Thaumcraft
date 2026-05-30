package thaumcraft.client.fx.particles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;


public class FXBoreSparkle extends Particle
{
    private Entity target;
    private double targetX;
    private double targetY;
    private double targetZ;
    public int particle;
    private float particleScale;
    private float particleGravity;
    protected float rCol;
    protected float gCol;
    protected float bCol;

    public FXBoreSparkle(Level par1World, double par2, double par4, double par6, double tx, double ty, double tz) {
        super((ClientLevel) par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        particle = 24;
        particleScale = net.minecraft.util.RandomSource.create().nextFloat() * 0.5f + 0.5f;
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        double dx = tx - x;
        double dy = ty - y;
        double dz = tz - z;
        int base = (int)(Math.sqrt(dx * dx + dy * dy + dz * dz) * 10.0);
        if (base < 1) base = 1;
        lifetime = base / 2 + net.minecraft.util.RandomSource.create().nextInt(base);
        float f3 = 0.01f;
        xd = random.nextGaussian() * f3;
        yd = random.nextGaussian() * f3;
        zd = random.nextGaussian() * f3;
        rCol = 0.2f;
        gCol = 0.6f + net.minecraft.util.RandomSource.create().nextFloat() * 0.3f;
        bCol = 0.2f;
        particleGravity = 0.2f;
        var renderentity = net.minecraft.client.Minecraft.getInstance().getCameraEntity();
        int visibleDistance = 64;
        if (renderentity != null && renderentity.distanceToSqr(x, y, z) > (double)(visibleDistance * visibleDistance)) {
            lifetime = 0;
        }
    }

    public FXBoreSparkle(Level par1World, double par2, double par4, double par6, Entity t) {
        this(par1World, par2, par4, par6, t.getX(), t.getY() + t.getEyeHeight(), t.getZ());
        target = t;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (target != null) {
            targetX = target.getX();
            targetY = target.getY() + target.getEyeHeight();
            targetZ = target.getZ();
        }
        if (age++ >= lifetime || (Mth.floor(x) == Mth.floor(targetX) && Mth.floor(y) == Mth.floor(targetY) && Mth.floor(z) == Mth.floor(targetZ))) {
            remove();
            return;
        }
        move(xd, yd, zd);
        xd *= 0.985;
        yd *= 0.95;
        zd *= 0.985;
        double dx = targetX - x;
        double dy = targetY - y;
        double dz = targetZ - z;
        double d11 = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double clamp = Math.min(0.25, d11 / 15.0);
        if (d11 < 2.0) particleScale *= 0.9f;
        dx /= d11;
        dy /= d11;
        dz /= d11;
        xd += dx * clamp;
        yd += dy * clamp;
        zd += dz * clamp;
        xd = Mth.clamp((float) xd, -(float)clamp, (float)clamp);
        yd = Mth.clamp((float) yd, -(float)clamp, (float)clamp);
        zd = Mth.clamp((float) zd, -(float)clamp, (float)clamp);
        xd += random.nextGaussian() * 0.01;
        yd += random.nextGaussian() * 0.01;
        zd += random.nextGaussian() * 0.01;
    }

    public void setRBGColorF(float r, float g, float b) {
        rCol = r; gCol = g; bCol = b;
    }

    public void setGravity(float value) {
        particleGravity = value;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXBoreSparkle visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
