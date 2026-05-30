package thaumcraft.client.fx.particles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;


public class FXWispEG extends Particle
{
    Entity target;
    double rx;
    double ry;
    double rz;
    public int blendmode;
    protected float rCol;
    protected float gCol;
    protected float bCol;
    private float particleScale;

    public FXWispEG(Level world, double x, double y, double z, Entity target2) {
        super((ClientLevel) world, x, y, z, 0.0, 0.0, 0.0);
        target = null;
        rx = 0.0;
        ry = 0.0;
        rz = 0.0;
        blendmode = 1;
        target = target2;
        xd = random.nextGaussian() * 0.03;
        yd = -0.05;
        zd = random.nextGaussian() * 0.03;
        particleScale = 0.4f;
        lifetime = (int)(40.0 / (Math.random() * 0.3 + 0.7));
        xo = x;
        yo = y;
        zo = z;
        blendmode = 771;
        rCol = net.minecraft.util.RandomSource.create().nextFloat() * 0.05f;
        gCol = net.minecraft.util.RandomSource.create().nextFloat() * 0.05f;
        bCol = net.minecraft.util.RandomSource.create().nextFloat() * 0.05f;
    }

    public int getFXLayer() {
        return (blendmode != 1) ? 1 : 0;
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
        yd *= 0.98;
        zd *= 0.98;
        if (target != null && !onGround) {
            x += target.getDeltaMovement().x;
            z += target.getDeltaMovement().z;
        }
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXWispEG visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
