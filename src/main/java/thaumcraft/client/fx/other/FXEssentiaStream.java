package thaumcraft.client.fx.other;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import java.awt.Color;
import java.util.HashMap;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.essentia.BlockEssentiaTransport;
import thaumcraft.common.lib.utils.BlockStateUtils;


public class FXEssentiaStream extends Particle
{
    private double targetX;
    private double targetY;
    private double targetZ;
    private double startX;
    private double startY;
    private double startZ;
    private int count;
    public int length;
    private String key;
    private BlockPos startPos;
    private BlockPos endPos;
    static HashMap<String, FXEssentiaStream> pt;
    private static Identifier TEX0;
    int layer;
    int growing;
    private float particleScale;
    private float particleGravity;
    protected float rCol;
    protected float gCol;
    protected float bCol;

    public FXEssentiaStream(Level w, double par2, double par4, double par6, double tx, double ty, double tz, int count, int color, float scale, int extend, double my) {
        super((ClientLevel) w, par2, par4, par6, 0.0, 0.0, 0.0);
        this.count = 0;
        length = 20;
        key = "";
        startPos = null;
        endPos = null;
        layer = 1;
        growing = -1;
        particleScale = (float)(scale * (1.0 + random.nextGaussian() * 0.15));
        length = Math.max(20, extend);
        this.count = count;
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        BlockPos bp1 = BlockPos.containing(x, y, z);
        BlockPos bp2 = BlockPos.containing(targetX, targetY, targetZ);
        BlockState bs = w.getBlockState(bp1);
        if (bs.getBlock() instanceof BlockEssentiaTransport) {
            net.minecraft.core.Direction f = BlockStateUtils.getFacing(bs);
            x += f.getStepX() * 0.05f;
            y += f.getStepY() * 0.05f;
            z += f.getStepZ() * 0.05f;
        }
        double dx = tx - x;
        double dy = ty - y;
        double dz = tz - z;
        int base = (int)(Math.sqrt(dx * dx + dy * dy + dz * dz) * 21.0);
        if (base < 1) base = 1;
        lifetime = base;
        String k = bp1.asLong() + "" + bp2.asLong() + "" + color;
        if (FXEssentiaStream.pt.containsKey(k)) {
            FXEssentiaStream trail2 = FXEssentiaStream.pt.get(k);
            if (!trail2.removed && trail2.length < trail2.length) {
                trail2.length += Math.max(extend, 5);
                trail2.lifetime += Math.max(extend, 5);
                lifetime = 0;
            }
        }
        if (lifetime > 0) {
            FXEssentiaStream.pt.put(k, this);
            key = k;
        }
        xd = Mth.sin(count / 4.0f) * 0.015f;
        yd = my + Mth.sin(count / 3.0f) * 0.015f;
        zd = Mth.sin(count / 2.0f) * 0.015f;
        Color c = new Color(color);
        rCol = c.getRed() / 255.0f;
        gCol = c.getGreen() / 255.0f;
        bCol = c.getBlue() / 255.0f;
        particleGravity = 0.2f;
        startX = x;
        startY = y;
        startZ = z;
        startPos = bp1;
        endPos = bp2;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime || length < 1) {
            remove();
            if (FXEssentiaStream.pt.containsKey(key) && FXEssentiaStream.pt.get(key).removed) {
                FXEssentiaStream.pt.remove(key);
            }
            return;
        }
        yd += 0.01 * particleGravity;
        move(xd, yd, zd);
        xd *= 0.985;
        yd *= 0.985;
        zd *= 0.985;
        xd = Mth.clamp((float) xd, -0.05f, 0.05f);
        yd = Mth.clamp((float) yd, -0.05f, 0.05f);
        zd = Mth.clamp((float) zd, -0.05f, 0.05f);
        double dx = targetX - x;
        double dy = targetY - y;
        double dz = targetZ - z;
        double d14 = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double d13 = 0.01;
        dx /= d14;
        dy /= d14;
        dz /= d14;
        xd += dx * (d13 / Math.min(1.0, d14));
        yd += dy * (d13 / Math.min(1.0, d14));
        zd += dz * (d13 / Math.min(1.0, d14));
        if (d14 < 1.0) {
            float f = Mth.sin((float)(d14 * Math.PI / 2.0));
            particleScale *= f;
        }
        if (particleScale <= 0.001) {
            if (growing < 0) growing = age;
            --length;
            FXDispatcher.INSTANCE.essentiaDropFx(
                targetX + random.nextGaussian() * 0.075,
                targetY + random.nextGaussian() * 0.075,
                targetZ + random.nextGaussian() * 0.075,
                rCol, gCol, bCol, 0.5f);
        }
    }

    public void setFXLayer(int l) { layer = l; }
    public int getFXLayer() { return layer; }
    public void setGravity(float value) { particleGravity = value; }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXEssentiaStream visual not ported to MC 26 render API (required CoreGLE library)
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }

    static {
        FXEssentiaStream.pt = new HashMap<String, FXEssentiaStream>();
        TEX0 = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/essentia.png");
    }
}
