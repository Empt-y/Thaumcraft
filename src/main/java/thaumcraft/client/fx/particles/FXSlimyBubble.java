package thaumcraft.client.fx.particles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.level.Level;


public class FXSlimyBubble extends Particle
{
    int particle;
    protected float rCol;
    protected float gCol;
    protected float bCol;
    private float particleGravity;
    private float particleScale;
    private float particleAlpha;

    public FXSlimyBubble(Level world, double d, double d1, double d2, float f) {
        super((ClientLevel) world, d, d1, d2, 0.0, 0.0, 0.0);
        particle = 144;
        rCol = 1.0f;
        gCol = 1.0f;
        bCol = 1.0f;
        particleGravity = 0.0f;
        particleScale = f;
        lifetime = 15 + net.minecraft.util.RandomSource.create().nextInt(5);
    }

    public int getFXLayer() {
        return 1;
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
        if (age - 1 < 6) {
            particle = 144 + age / 2;
            if (age == 5) {
                y += 0.1;
            }
        } else if (age < lifetime - 4) {
            yd += 0.005;
            particle = 147 + age % 4 / 2;
        } else {
            yd /= 2.0;
            particle = 150 - (lifetime - age) / 2;
        }
        y += yd;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXSlimyBubble visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
