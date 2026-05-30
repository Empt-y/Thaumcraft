package thaumcraft.client.fx.other;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;


public class FXShieldRunes extends Particle
{
    Entity target;
    float yaw;
    float pitch;

    public FXShieldRunes(Level world, double d, double d1, double d2, Entity target, int age, float yaw, float pitch) {
        super((ClientLevel) world, d, d1, d2, 0.0, 0.0, 0.0);
        xd = 0.0;
        yd = 0.0;
        zd = 0.0;
        lifetime = age + random.nextInt(Math.max(1, age / 2));
        // setSize removed - dimensions in EntityType
        this.target = target;
        this.yaw = yaw;
        this.pitch = pitch;
        if (target != null) {
            x = target.getX();
            y = (target.getBoundingBox().minY + target.getBoundingBox().maxY) / 2.0;
            z = target.getZ();
        }
        xo = x;
        yo = y;
        zo = z;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
        }
        if (target != null) {
            x = target.getX();
            y = (target.getBoundingBox().minY + target.getBoundingBox().maxY) / 2.0;
            z = target.getZ();
        }
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXShieldRunes visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
