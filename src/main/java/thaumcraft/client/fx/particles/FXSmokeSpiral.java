package thaumcraft.client.fx.particles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.level.Level;


public class FXSmokeSpiral extends Particle
{
    private float radius;
    private int start;
    private int miny;
    private float particleGravity;
    private float particleScale;
    private float particleAlpha;
    protected float rCol;
    protected float gCol;
    protected float bCol;

    public FXSmokeSpiral(Level world, double d, double d1, double d2, float radius, int start, int miny) {
        super((ClientLevel) world, d, d1, d2, 0.0, 0.0, 0.0);
        this.radius = 1.0f;
        this.start = 0;
        this.miny = 0;
        particleGravity = -0.01f;
        lifetime = 20 + net.minecraft.util.RandomSource.create().nextInt(10);
        xo = d;
        yo = d1;
        zo = d2;
        this.radius = radius;
        this.start = start;
        this.miny = miny;
    }

    public void setRBGColorF(float r, float g, float b) {
        rCol = r; gCol = g; bCol = b;
    }

    public int getFXLayer() {
        return 1;
    }

    @Override
    public void tick() {
        particleAlpha = (lifetime - age) / (float) lifetime;
        if (age++ >= lifetime) {
            remove();
        }
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXSmokeSpiral visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
