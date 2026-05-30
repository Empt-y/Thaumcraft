package thaumcraft.client.fx.beams;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;


public class FXBeamWand extends Particle
{
    public int particle;
    LivingEntity sourceEntity;
    private double offset;
    private float length;
    private float rotYaw;
    private float rotPitch;
    private float prevYaw;
    private float prevPitch;
    private double tX;
    private double tY;
    private double tZ;
    private double ptX;
    private double ptY;
    private double ptZ;
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

    public FXBeamWand(Level par1World, LivingEntity p, double tx, double ty, double tz, float red, float green, float blue, int age) {
        super((ClientLevel) par1World, p.getX(), p.getY(), p.getZ(), 0.0, 0.0, 0.0);
        particle = 16;
        endMod = 1.0f;
        pulse = true;
        rotationspeed = 5;
        offset = p.getBbHeight() / 2.0f + 0.25;
        rCol = red;
        gCol = green;
        bCol = blue;
        sourceEntity = p;
        // setSize removed - dimensions in EntityType
        xd = 0.0;
        yd = 0.0;
        zd = 0.0;
        tX = tx;
        tY = ty;
        tZ = tz;
        float dxf = (float)(p.getX() - tX);
        float dyf = (float)(p.getY() + offset - tY);
        float dzf = (float)(p.getZ() - tZ);
        length = Mth.sqrt((float)(dxf * dxf + dyf * dyf + dzf * dzf));
        double var7 = Mth.sqrt((float)(dxf * dxf + dzf * dzf));
        rotYaw = (float)(Math.atan2(dxf, dzf) * 180.0 / Math.PI);
        rotPitch = (float)(Math.atan2(dyf, var7) * 180.0 / Math.PI);
        prevYaw = rotYaw;
        prevPitch = rotPitch;
        lifetime = age;

        int visibleDistance = 50;
        var renderentity = net.minecraft.client.Minecraft.getInstance().getCameraEntity();
        if (renderentity != null && renderentity.distanceToSqr(p.getX(), p.getY(), p.getZ()) > (double)(visibleDistance * visibleDistance)) {
            lifetime = 0;
        }
    }

    public void updateBeam(double tx2, double ty2, double tz2) {
        tX = tx2;
        tY = ty2;
        tZ = tz2;
        while (lifetime - age < 4) {
            ++lifetime;
        }
    }

    @Override
    public void tick() {
        xo = sourceEntity.getX();
        yo = sourceEntity.getY() + offset;
        zo = sourceEntity.getZ();
        ptX = tX;
        ptY = tY;
        ptZ = tZ;
        prevYaw = rotYaw;
        prevPitch = rotPitch;
        float dxf = (float)(sourceEntity.getX() - tX);
        float dyf = (float)(sourceEntity.getY() + offset - tY);
        float dzf = (float)(sourceEntity.getZ() - tZ);
        length = Mth.sqrt((float)(dxf * dxf + dyf * dyf + dzf * dzf));
        double var7 = Mth.sqrt((float)(dxf * dxf + dzf * dzf));
        rotYaw = (float)(Math.atan2(dxf, dzf) * 180.0 / Math.PI);
        rotPitch = (float)(Math.atan2(dyf, var7) * 180.0 / Math.PI);
        while (rotPitch - prevPitch < -180.0f) prevPitch -= 360.0f;
        while (rotPitch - prevPitch >= 180.0f) prevPitch += 360.0f;
        while (rotYaw - prevYaw < -180.0f) prevYaw -= 360.0f;
        while (rotYaw - prevYaw >= 180.0f) prevYaw += 360.0f;
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

    public void setType(int type) { this.type = type; }
    public void setEndMod(float endMod) { this.endMod = endMod; }
    public void setReverse(boolean reverse) { this.reverse = reverse; }
    public void setPulse(boolean pulse) { this.pulse = pulse; }
    public void setRotationspeed(int rotationspeed) { this.rotationspeed = rotationspeed; }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXBeamWand visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
