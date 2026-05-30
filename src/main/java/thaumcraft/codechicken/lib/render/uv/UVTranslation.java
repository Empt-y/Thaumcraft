package thaumcraft.codechicken.lib.render.uv;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import net.minecraft.util.Mth;
import thaumcraft.codechicken.lib.vec.ITransformation;


public class UVTranslation extends UVTransformation
{
    public double du;
    public double dv;
    
    public UVTranslation(double u, double v) {
        du = u;
        dv = v;
    }
    
    @Override
    public void apply(UV uv) {
        uv.u += du;
        uv.v += dv;
    }
    
    @Override
    public UVTransformation at(UV point) {
        return this;
    }
    
    @Override
    public UVTransformation inverse() {
        return new UVTranslation(-du, -dv);
    }
    
    @Override
    public UVTransformation merge(UVTransformation next) {
        if (next instanceof UVTranslation) {
            UVTranslation t = (UVTranslation)next;
            return new UVTranslation(du + t.du, dv + t.dv);
        }
        return null;
    }
    
    @Override
    public boolean isRedundant() {
        return du >= -1.0E-5 && du <= 1.0E-5 && dv >= -1.0E-5 && dv <= 1.0E-5;
    }
    
    @Override
    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "UVTranslation(" + new BigDecimal(du, cont) + ", " + new BigDecimal(dv, cont) + ")";
    }
}
