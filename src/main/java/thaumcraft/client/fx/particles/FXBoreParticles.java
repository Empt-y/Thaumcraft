package thaumcraft.client.fx.particles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;


public class FXBoreParticles extends Particle
{
    private BlockState blockInstance;
    private ItemStack itemInstance;
    private int side;
    private Entity target;
    private double targetX;
    private double targetY;
    private double targetZ;
    private float particleScale;
    protected float rCol;
    protected float gCol;
    protected float bCol;

    public FXBoreParticles(Level par1World, double par2, double par4, double par6, double tx, double ty, double tz, BlockState par14Block, int par15) {
        super((ClientLevel) par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        blockInstance = par14Block;
        rCol = 0.6f;
        gCol = 0.6f;
        bCol = 0.6f;
        particleScale = net.minecraft.util.RandomSource.create().nextFloat() * 0.3f + 0.4f;
        side = par15;
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
    }

    public FXBoreParticles(Level par1World, double par2, double par4, double par6, double tx, double ty, double tz, double sx, double sy, double sz, ItemStack item) {
        super((ClientLevel) par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        itemInstance = item;
        rCol = 0.6f;
        gCol = 0.6f;
        bCol = 0.6f;
        particleScale = net.minecraft.util.RandomSource.create().nextFloat() * 0.3f + 0.4f;
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
        xd = sx + random.nextGaussian() * f3;
        yd = sy + random.nextGaussian() * f3;
        zd = sz + random.nextGaussian() * f3;
        var renderentity = net.minecraft.client.Minecraft.getInstance().getCameraEntity();
        int visibleDistance = 64;
        if (renderentity != null && renderentity.distanceToSqr(x, y, z) > (double)(visibleDistance * visibleDistance)) {
            lifetime = 0;
        }
    }

    public void setTarget(Entity target) {
        this.target = target;
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
        if (d11 < 2.0) {
            particleScale *= 0.9f;
        }
        dx /= d11;
        dy /= d11;
        dz /= d11;
        xd += dx * clamp;
        yd += dy * clamp;
        zd += dz * clamp;
        xd = Mth.clamp((float) xd, -(float)clamp, (float)clamp);
        yd = Mth.clamp((float) yd, -(float)clamp, (float)clamp);
        zd = Mth.clamp((float) zd, -(float)clamp, (float)clamp);
        xd += random.nextGaussian() * 0.005;
        yd += random.nextGaussian() * 0.005;
        zd += random.nextGaussian() * 0.005;
    }

    public int getFXLayer() {
        return 1;
    }

    public void setAlphaF(float alpha) {
        // alpha stub - NO_RENDER
    }

    public FXBoreParticles getObjectColor(BlockPos pos) {
        return this;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXBoreParticles visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
