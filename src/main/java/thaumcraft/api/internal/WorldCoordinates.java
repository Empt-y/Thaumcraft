package thaumcraft.api.internal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;


public class WorldCoordinates implements Comparable
{
    public BlockPos pos;
    
    public int dim;

    public WorldCoordinates() {}

    public WorldCoordinates(BlockPos pos, int d)
    {
        this.pos = pos;
        dim = d;
    }
    
    public WorldCoordinates(BlockEntity tile)
    {
        pos = tile.getBlockPos();
        dim = tile.getLevel() != null ? tile.getLevel().dimension().identifier().hashCode() : 0;
    }

    public WorldCoordinates(WorldCoordinates par1ChunkCoordinates)
    {
        pos = par1ChunkCoordinates.pos;
        dim = par1ChunkCoordinates.dim;
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof WorldCoordinates))
        {
            return false;
        }
        else
        {
        	WorldCoordinates coordinates = (WorldCoordinates)par1Obj;
            return pos.equals(coordinates.pos) && dim == coordinates.dim ;
        }
    }

    public int hashCode()
    {
        return pos.getX() + pos.getY() << 8 + pos.getZ() << 16 + dim << 24;
    }

    /**
     * Compare the coordinate with another coordinate
     */
    public int compareWorldCoordinate(WorldCoordinates par1)
    {
        return dim == par1.dim ? pos.compareTo(par1.pos) : -1;
    }

    public void set(BlockPos pos, int d)
    {
        this.pos = pos;
        dim = d;
    }

    /**
     * Returns the squared distance between this coordinates and the coordinates given as argument.
     */
    public double getDistanceSquared(BlockPos pos)
    {
        return this.pos.distSqr(pos);
    }

    /**
     * Return the squared distance between this coordinates and the ChunkCoordinates given as argument.
     */
    public double getDistanceSquaredToWorldCoordinates(WorldCoordinates par1ChunkCoordinates)
    {
        return getDistanceSquared(par1ChunkCoordinates.pos);
    }

    public int compareTo(Object par1Obj)
    {
        return compareWorldCoordinate((WorldCoordinates)par1Obj);
    }
    
    public void readNBT(CompoundTag nbt) {
    	int x = nbt.getIntOr("w_x", 0);
    	int y = nbt.getIntOr("w_y", 0);
    	int z = nbt.getIntOr("w_z", 0);
    	pos = new BlockPos(x,y,z);
    	dim = nbt.getIntOr("w_d", 0);
    }
    
    public void writeNBT(CompoundTag nbt) {
    	nbt.putInt("w_x",pos.getX());
    	nbt.putInt("w_y",pos.getY());
    	nbt.putInt("w_z",pos.getZ());
    	nbt.putInt("w_d",dim);
    }
    
}
