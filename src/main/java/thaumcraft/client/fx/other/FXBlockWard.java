package thaumcraft.client.fx.other;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;


public class FXBlockWard extends Particle
{
    Identifier[] tex1;
    Direction side;
    int rotation;
    float sx;
    float sy;
    float sz;
    private float particleGravity;
    private float particleScale;
    private float particleAlpha;

    public FXBlockWard(Level world, double d, double d1, double d2, Direction side, float f, float f1, float f2) {
        super((ClientLevel) world, d, d1, d2, 0.0, 0.0, 0.0);
        tex1 = new Identifier[15];
        rotation = 0;
        this.side = side;
        particleGravity = 0.0f;
        xd = 0.0;
        yd = 0.0;
        zd = 0.0;
        lifetime = 12 + net.minecraft.util.RandomSource.create().nextInt(5);
        // setSize removed - dimensions in EntityType
        xo = x;
        yo = y;
        zo = z;
        particleScale = (float)(1.4 + random.nextGaussian() * 0.3);
        rotation = net.minecraft.util.RandomSource.create().nextInt(360);
        sx = Mth.clamp(f - 0.6f + net.minecraft.util.RandomSource.create().nextFloat() * 0.2f, -0.4f, 0.4f);
        sy = Mth.clamp(f1 - 0.6f + net.minecraft.util.RandomSource.create().nextFloat() * 0.2f, -0.4f, 0.4f);
        sz = Mth.clamp(f2 - 0.6f + net.minecraft.util.RandomSource.create().nextFloat() * 0.2f, -0.4f, 0.4f);
        if (side.getStepX() != 0) sx = 0.0f;
        if (side.getStepY() != 0) sy = 0.0f;
        if (side.getStepZ() != 0) sz = 0.0f;
        for (int a = 0; a < 15; ++a) {
            tex1[a] = Identifier.fromNamespaceAndPath("thaumcraft", "textures/models/hemis" + (a + 1) + ".png");
        }
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        float threshold = lifetime / 5.0f;
        if (age <= threshold) {
            particleAlpha = age / threshold;
        } else {
            particleAlpha = (lifetime - age) / (float) lifetime;
        }
        if (age++ >= lifetime) {
            remove();
            return;
        }
        yd -= 0.04 * particleGravity;
        x += xd;
        y += yd;
        z += zd;
    }

    public void setGravity(float value) {
        particleGravity = value;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXBlockWard visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
