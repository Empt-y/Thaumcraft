package thaumcraft.codechicken.lib.vec;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


public class Translation extends Transformation
{
    public Vector3 vec;
    
    public Translation(Vector3 vec) {
        this.vec = vec;
    }
    
    public Translation(double x, double y, double z) {
        this(new Vector3(x, y, z));
    }
    
    @Override
    public void apply(Vector3 vec) {
        vec.add(this.vec);
    }
    
    @Override
    public void applyN(Vector3 normal) {
    }
    
    @Override
    public void apply(Matrix4 mat) {
        mat.translate(vec);
    }
    
    @Override
    public Transformation at(Vector3 point) {
        return this;
    }
    
    @Override
    public void glApply() {
        // RenderSystem.translate removed in modern MC; matrix stack is gone
    }
    
    @Override
    public Transformation inverse() {
        return new Translation(-vec.getX(), -vec.getY(), -vec.getZ());
    }
    
    @Override
    public Transformation merge(Transformation next) {
        if (next instanceof Translation) {
            return new Translation(vec.copy().add(((Translation)next).vec));
        }
        return null;
    }
    
    @Override
    public boolean isRedundant() {
        return vec.equalsT(Vector3.zero);
    }
    
    @Override
    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Translation(" + new BigDecimal(vec.getX(), cont) + ", " + new BigDecimal(vec.getY(), cont) + ", " + new BigDecimal(vec.getZ(), cont) + ")";
    }
}
