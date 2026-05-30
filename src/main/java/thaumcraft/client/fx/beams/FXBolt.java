package thaumcraft.client.fx.beams;
import java.util.ArrayList;
import java.util.Random;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;


public class FXBolt extends Particle
{
    float width;
    ArrayList<Vec3> points;
    ArrayList<Float> pointsWidth;
    float dr;
    long seed;
    private double tX;
    private double tY;
    private double tZ;
    public float length;
    protected float rCol;
    protected float gCol;
    protected float bCol;

    public FXBolt(Level par1World, double px, double py, double pz, double tx, double ty, double tz, float red, float green, float blue, float width) {
        super((ClientLevel) par1World, px, py, pz, 0.0, 0.0, 0.0);
        this.width = 0.0f;
        points = new ArrayList<Vec3>();
        pointsWidth = new ArrayList<Float>();
        dr = 0.0f;
        seed = 0L;
        rCol = red;
        gCol = green;
        bCol = blue;
        // setSize removed - dimensions in EntityType
        xd = 0.0;
        yd = 0.0;
        zd = 0.0;
        tX = tx - px;
        tY = ty - py;
        tZ = tz - pz;
        this.width = width;
        lifetime = 3;
        Vec3 vs = new Vec3(0.0, 0.0, 0.0);
        Vec3 ve = new Vec3(tX, tY, tZ);
        length = (float)(ve.length() * Math.PI);
        int steps = (int) length;
        points.add(vs);
        pointsWidth.add(width);
        dr = (float)(net.minecraft.util.RandomSource.create().nextInt(50) * Math.PI);
        float ampl = 0.1f;
        for (int a = 1; a < steps - 1; ++a) {
            float dist = a * (length / steps) + dr;
            double dx = tX / steps * a + Mth.sin(dist / 4.0f) * ampl;
            double dy = tY / steps * a + Mth.sin(dist / 3.0f) * ampl;
            double dz = tZ / steps * a + Mth.sin(dist / 2.0f) * ampl;
            dx += (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * 0.1f;
            dy += (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * 0.1f;
            dz += (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * 0.1f;
            points.add(new Vec3(dx, dy, dz));
            pointsWidth.add(width);
        }
        pointsWidth.add(width);
        points.add(ve);
        seed = net.minecraft.util.RandomSource.create().nextInt(1000);
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
        }
    }

    public void setRGB(float r, float g, float b) {
        rCol = r;
        gCol = g;
        bCol = b;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXBolt visual not ported to MC 26 render API (required CoreGLE library)
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
