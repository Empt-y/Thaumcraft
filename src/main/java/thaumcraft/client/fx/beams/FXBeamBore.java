package thaumcraft.client.fx.beams;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;


public class FXBeamBore extends Particle
{
    public int particle;
    private double offset;
    private double tX;
    private double tY;
    private double tZ;
    private double ptX;
    private double ptY;
    private double ptZ;
    private float length;
    private float rotYaw;
    private float rotPitch;
    private float prevYaw;
    private float prevPitch;
    private int type;
    private float endMod;
    private boolean reverse;
    private boolean pulse;
    private int rotationspeed;
    private float prevSize;
    public int impact;
    protected float rCol;
    protected float gCol;
    protected float bCol;

    public FXBeamBore(Level par1World, double px, double py, double pz, double tx, double ty, double tz, float red, float green, float blue, int age) {
        super((ClientLevel) par1World, px, py, pz, 0.0, 0.0, 0.0);
        particle = 16;
        offset = 0.0;
        tX = tx;
        tY = ty;
        tZ = tz;
        endMod = 1.0f;
        pulse = true;
        rotationspeed = 5;
        rCol = red;
        gCol = green;
        bCol = blue;
        // setSize removed - dimensions in EntityType
        xd = 0.0;
        yd = 0.0;
        zd = 0.0;
        lifetime = age;

        int visibleDistance = 64;
        var renderentity = net.minecraft.client.Minecraft.getInstance().getCameraEntity();
        if (renderentity != null && renderentity.distanceToSqr(px, py, pz) > (double)(visibleDistance * visibleDistance)) {
            lifetime = 0;
        }
    }

    public void updateBeam(double sx, double sy, double sz, double tx2, double ty2, double tz2) {
        x = sx;
        y = sy;
        z = sz;
        tX = tx2;
        tY = ty2;
        tZ = tz2;
        while (lifetime - age < 4) {
            ++lifetime;
        }
    }

    @Override
    public void tick() {
        xo = x;
        yo = y + offset;
        zo = z;
        ptX = tX;
        ptY = tY;
        ptZ = tZ;
        prevYaw = rotYaw;
        prevPitch = rotPitch;
        float xd2 = (float)(x - tX);
        float yd2 = (float)(y - tY);
        float zd2 = (float)(z - tZ);
        length = Mth.sqrt((float)(xd2 * xd2 + yd2 * yd2 + zd2 * zd2));
        double var7 = Mth.sqrt((float)(xd2 * xd2 + zd2 * zd2));
        rotYaw = (float)(Math.atan2(xd2, zd2) * 180.0 / Math.PI);
        rotPitch = (float)(Math.atan2(yd2, var7) * 180.0 / Math.PI);
        prevYaw = rotYaw;
        prevPitch = rotPitch;
        if (impact > 0) {
            --impact;
        }
        if (age++ >= lifetime) {
            remove();
        }
    }

    public void setRGB(float r, float g, float b) {
        rCol = r;
        gCol = g;
        bCol = b;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setEndMod(float endMod) {
        this.endMod = endMod;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public void setPulse(boolean pulse) {
        this.pulse = pulse;
    }

    public void setRotationspeed(int rotationspeed) {
        this.rotationspeed = rotationspeed;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXBeamBore visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
