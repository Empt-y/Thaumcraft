package thaumcraft.client.fx.beams;
import java.util.ArrayList;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.utils.Utils;


public class FXArc extends Particle
{
    public int particle;
    ArrayList<Vec3> points;
    private Entity targetEntity;
    private double tX;
    private double tY;
    private double tZ;
    Identifier beam;
    public int blendmode;
    public float length;

    public FXArc(Level par1World, double px, double py, double pz, double tx, double ty, double tz, float red, float green, float blue, double hg) {
        super((ClientLevel) par1World, px, py, pz, 0.0, 0.0, 0.0);
        particle = 16;
        points = new ArrayList<Vec3>();
        targetEntity = null;
        tX = 0.0;
        tY = 0.0;
        tZ = 0.0;
        beam = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/beamh.png");
        blendmode = 1;
        length = 1.0f;
        tX = tx - px;
        tY = ty - py;
        tZ = tz - pz;
        lifetime = 3;
        double xx = 0.0;
        double yy = 0.0;
        double zz = 0.0;
        double gravity = 0.115;
        double noise = 0.25;
        Vec3 vs = new Vec3(xx, yy, zz);
        Vec3 ve = new Vec3(tX, tY, tZ);
        Vec3 vc = new Vec3(xx, yy, zz);
        length = (float)ve.length();
        Vec3 vv = Utils.calculateVelocity(vs, ve, hg, gravity);
        double l = Utils.distanceSquared3d(new Vec3(0.0, 0.0, 0.0), vv);
        points.add(vs);
        for (int c = 0; Utils.distanceSquared3d(ve, vc) > l && c < 50; ++c) {
            Vec3 vt = vc.add(vv.x, vv.y, vv.z);
            vc = new Vec3(vt.x, vt.y, vt.z);
            vt = vt.add((random.nextDouble() - random.nextDouble()) * noise,
                        (random.nextDouble() - random.nextDouble()) * noise,
                        (random.nextDouble() - random.nextDouble()) * noise);
            points.add(vt);
            FXGeneric fb = new FXGeneric(par1World, px + vt.x, py + vt.y, pz + vt.z, 0.0, 0.0, 0.0);
            int fbage = 30 + net.minecraft.util.RandomSource.create().nextInt(20);
            fb.setMaxAge(fbage);
            fb.setRBGColorF(Mth.clamp(red * 3.0f, 0.0f, 1.0f), Mth.clamp(green * 3.0f, 0.0f, 1.0f), Mth.clamp(blue * 3.0f, 0.0f, 1.0f), net.minecraft.util.RandomSource.create().nextFloat(), net.minecraft.util.RandomSource.create().nextFloat(), net.minecraft.util.RandomSource.create().nextFloat());
            float[] alphas = new float[6 + random.nextInt(fbage / 3)];
            for (int a = 1; a < alphas.length - 1; ++a) {
                alphas[a] = net.minecraft.util.RandomSource.create().nextFloat();
            }
            alphas[0] = 1.0f;
            fb.setAlphaF(alphas);
            boolean sp = net.minecraft.util.RandomSource.create().nextFloat() < 0.2;
            fb.setParticles(sp ? 320 : 512, 16, 1);
            fb.setLoop(true);
            fb.setGravity(sp ? 0.0f : 0.125f);
            fb.setScale(0.5f, 0.125f);
            fb.setLayer(0);
            fb.setSlowDown(0.995);
            fb.setRandomMovementScale(0.0025f, 0.001f, 0.0025f);
            ParticleEngine.addEffectWithDelay(par1World, fb, 2 + net.minecraft.util.RandomSource.create().nextInt(3));
            vv = vv.subtract(0.0, gravity / 1.9, 0.0);
        }
        points.add(ve);
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
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXArc visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }
}
