package thaumcraft.codechicken.lib.raytracer;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.HitResult;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Vector3;


public class ExtendedMOP extends HitResult implements Comparable<ExtendedMOP>
{
    public double dist;
    public int subHit;
    public Object hitInfo;

    public ExtendedMOP(Entity entity, Vector3 hit, Object data, double dist) {
        super(hit.vec3());
        setData(data);
        this.dist = dist;
    }

    public ExtendedMOP(Vector3 hit, int side, BlockCoord pos, Object data, double dist) {
        super(hit.vec3());
        setData(data);
        this.dist = dist;
    }

    public void setData(Object data) {
        if (data instanceof Integer) {
            subHit = (int)data;
        }
        hitInfo = data;
    }

    @Override
    public HitResult.Type getType() {
        return HitResult.Type.MISS;
    }

    public int compareTo(ExtendedMOP o) {
        return (dist == o.dist) ? 0 : ((dist < o.dist) ? -1 : 1);
    }
}
