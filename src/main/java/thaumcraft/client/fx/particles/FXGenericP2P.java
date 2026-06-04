package thaumcraft.client.fx.particles;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;


public class FXGenericP2P extends FXGeneric
{
    private double targetX;
    private double targetY;
    private double targetZ;

    public FXGenericP2P(Level world, double x, double y, double z, double xx, double yy, double zz) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        xo = x;
        yo = y;
        zo = z;
        targetX = xx;
        targetY = yy;
        targetZ = zz;
        double dx = xx - x;
        double dy = yy - y;
        double dz = zz - z;
        int base = (int)(Math.sqrt(dx * dx + dy * dy + dz * dz) * 3.0);
        if (base < 1) base = 1;
        lifetime = base / 2 + net.minecraft.util.RandomSource.create().nextInt(base);
        float f3 = 0.01f;
        xd = (float) random.nextGaussian() * f3;
        yd = (float) random.nextGaussian() * f3;
        zd = (float) random.nextGaussian() * f3;
        gravity = 0.2f;
    }

    @Override
    public void tick() {
        super.tick();
        if (removed) return;
        double dx = targetX - x;
        double dy = targetY - y;
        double dz = targetZ - z;
        double d13 = 0.3;
        double d14 = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (d14 < 4.0) {
            quadSize *= 0.9f;
            d13 = 0.6;
        }
        dx /= d14;
        dy /= d14;
        dz /= d14;
        xd += dx * d13;
        yd += dy * d13;
        zd += dz * d13;
        xd = Mth.clamp((float) xd, -0.35f, 0.35f);
        yd = Mth.clamp((float) yd, -0.35f, 0.35f);
        zd = Mth.clamp((float) zd, -0.35f, 0.35f);
    }
}
