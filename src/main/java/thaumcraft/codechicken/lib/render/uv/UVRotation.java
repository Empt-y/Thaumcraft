package thaumcraft.codechicken.lib.render.uv;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import net.minecraft.util.Mth;
import thaumcraft.codechicken.lib.vec.ITransformation;


public class UVRotation extends UVTransformation
{
    public double angle;
    
    public UVRotation(double angle) {
        this.angle = angle;
    }
    
    @Override
    public void apply(UV uv) {
        double c = Mth.cos(angle);
        double s = Mth.sin(angle);
        double u2 = c * uv.u + s * uv.v;
        uv.v = -s * uv.u + c * uv.v;
        uv.u = u2;
    }
    
    @Override
    public UVTransformation inverse() {
        return new UVRotation(-angle);
    }
    
    @Override
    public UVTransformation merge(UVTransformation next) {
        if (next instanceof UVRotation) {
            return new UVRotation(angle + ((UVRotation)next).angle);
        }
        return null;
    }
    
    @Override
    public boolean isRedundant() {
        return angle >= -1.0E-5 && angle <= 1.0E-5;
    }
    
    @Override
    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "UVRotation(" + new BigDecimal(angle, cont) + ")";
    }
}
