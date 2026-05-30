package thaumcraft.client.fx.particles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;


public class FXSwarmRunes extends Particle
{
    private Entity target;
    private float turnSpeed;
    private float speed;
    int deathtimer;
    public int particle;
    protected float rCol;
    protected float gCol;
    protected float bCol;
    private float particleGravity;
    private float particleScale;
    protected float xRot;
    protected float yRot;
    public float getXRot() { return xRot; }
    public float getYRot() { return yRot; }

    public FXSwarmRunes(Level par1World, double x, double y, double z, Entity target, float r, float g, float b) {
        super((ClientLevel) par1World, x, y, z, 0.0, 0.0, 0.0);
        turnSpeed = 10.0f;
        speed = 0.2f;
        deathtimer = 0;
        particle = 0;
        rCol = r; gCol = g; bCol = b;
        particleScale = net.minecraft.util.RandomSource.create().nextFloat() * 0.5f + 1.0f;
        this.target = target;
        float f3 = 0.2f;
        xd = (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * f3;
        yd = (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * f3;
        zd = (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * f3;
        particleGravity = 0.1f;
    }

    public FXSwarmRunes(Level par1World, double x, double y, double z, Entity target, float r, float g, float b, float sp, float ts, float pg) {
        this(par1World, x, y, z, target, r, g, b);
        speed = sp;
        turnSpeed = ts;
        particleGravity = pg;
        particle = net.minecraft.util.RandomSource.create().nextInt(16);
    }

    public int getFXLayer() {
        return 0;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        ++age;
        if (age > 200 || target == null || !target.isAlive() || (target instanceof LivingEntity && ((LivingEntity) target).deathTime > 0)) {
            ++deathtimer;
            xd *= 0.9;
            zd *= 0.9;
            yd -= particleGravity / 2.0f;
            if (deathtimer > 50) {
                remove();
                return;
            }
        } else {
            yd += particleGravity;
        }
        move(xd, yd, zd);
        xd *= 0.985;
        yd *= 0.985;
        zd *= 0.985;
        if (age < 200 && target != null && target.isAlive() && (!(target instanceof LivingEntity) || ((LivingEntity) target).deathTime <= 0)) {
            boolean hurt = target instanceof LivingEntity && ((LivingEntity) target).hurtTime > 0;
            double bbW = target.getBbWidth();
            double distSq = (x - target.getX()) * (x - target.getX()) + (y - target.getY()) * (y - target.getY()) + (z - target.getZ()) * (z - target.getZ());
            if (distSq > bbW * bbW && !hurt) {
                /* faceEntity removed */
            } else {
                if (hurt && distSq < bbW * bbW) {
                    age += 100;
                }
                /* faceEntity removed */
            }
            xd = -Mth.sin(getYRot() / 180.0f * 3.1415927f) * Mth.cos(getXRot() / 180.0f * 3.1415927f);
            zd = Mth.cos(getYRot() / 180.0f * 3.1415927f) * Mth.cos(getXRot() / 180.0f * 3.1415927f);
            yd = -Mth.sin(getXRot() / 180.0f * 3.1415927f);
            setHeading(xd, yd, zd, speed, 15.0f);
        }
    }

    private void faceEntity(net.minecraft.world.entity.Entity par1Entity, float par2, float par3) {
        double d0 = par1Entity.getX() - x;
        double d2 = par1Entity.getZ() - z;
        double d3 = (par1Entity.getBoundingBox().minY + par1Entity.getBoundingBox().maxY) / 2.0 - (getBoundingBox().minY + getBoundingBox().maxY) / 2.0;
        double d4 = Math.sqrt(d0 * d0 + d2 * d2);
        float f2 = (float)(Math.atan2(d2, d0) * 180.0 / 3.141592653589793) - 90.0f;
        float f3 = (float)(-(Math.atan2(d3, d4) * 180.0 / 3.141592653589793));
        xRot = updateRotation(getXRot(), f3, par3);
        yRot = updateRotation(getYRot(), f2, par2);
    }

    private float updateRotation(float current, float target, float maxDelta) {
        float delta = (float)(Mth.wrapDegrees(target - current));
        if (delta > maxDelta) delta = maxDelta;
        if (delta < -maxDelta) delta = -maxDelta;
        return current + delta;
    }

    public void setHeading(double dx, double dy, double dz, float sp, float noise) {
        float mag = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= mag; dy /= mag; dz /= mag;
        dx += random.nextGaussian() * (random.nextBoolean() ? -1 : 1) * 0.007499999832361937 * noise;
        dy += random.nextGaussian() * (random.nextBoolean() ? -1 : 1) * 0.007499999832361937 * noise;
        dz += random.nextGaussian() * (random.nextBoolean() ? -1 : 1) * 0.007499999832361937 * noise;
        xd = dx * sp;
        yd = dy * sp;
        zd = dz * sp;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXSwarmRunes visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
