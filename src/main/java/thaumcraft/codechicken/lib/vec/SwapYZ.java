package thaumcraft.codechicken.lib.vec;


public class SwapYZ extends VariableTransformation
{
    public SwapYZ() {
        super(new Matrix4(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0));
    }
    
    @Override
    public void apply(Vector3 vec) {
        double vz = vec.getZ();
        vec.getZ() = vec.getY();
        vec.getY() = vz;
    }
    
    @Override
    public Transformation inverse() {
        return this;
    }
}
