package thaumcraft.common.tiles.devices;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Transformation;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileRedstoneRelay extends TileThaumcraft
{
    private int in;
    private int out;
    
    public TileRedstoneRelay(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        in = 1;
        out = 15;
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbt) {
        setIn(nbt.getByteOr("in", (byte)0));
        setOut(nbt.getByteOr("out", (byte)0));
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        nbt.putByte("in", (byte) getIn());
        nbt.putByte("out", (byte) getOut());
        return nbt;
    }
    
    public void increaseIn() {
        if (!getLevel().isClientSide()) {
            setIn(getIn() + 1);
            if (getIn() > 15) {
                setIn(1);
            }
            setChanged();
            syncTile(false);
        }
    }
    
    public void increaseOut() {
        if (!getLevel().isClientSide()) {
            setOut(getOut() + 1);
            if (getOut() > 15) {
                setOut(1);
            }
            setChanged();
            syncTile(false);
        }
    }
    
    public HitResult rayTrace(Level world, Vec3 vec3d, Vec3 vec3d1, HitResult fullblock) {
        return fullblock;
    }
    
    public void addTraceableCuboids(List<IndexedCuboid6> cuboids) {
        Direction facing = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        cuboids.add(new IndexedCuboid6(0, getCuboid0(facing)));
        cuboids.add(new IndexedCuboid6(1, getCuboid1(facing)));
    }
    
    public Cuboid6 getCuboid0(Direction facing) {
        Transformation rot = Rotation.quarterRotations[0];
        switch (facing) {
            case WEST: {
                rot = Rotation.quarterRotations[1];
                break;
            }
            case NORTH: {
                rot = Rotation.quarterRotations[2];
                break;
            }
            case EAST: {
                rot = Rotation.quarterRotations[3];
                break;
            }
        }
        return new Cuboid6(-0.375, 0.0625, -0.375, -0.125, 0.25, -0.125).apply(rot).add(new Vector3(getBlockPos().getX() + 0.5, getBlockPos().getY(), getBlockPos().getZ() + 0.5));
    }
    
    public Cuboid6 getCuboid1(Direction facing) {
        Transformation rot = Rotation.quarterRotations[0];
        switch (facing) {
            case WEST: {
                rot = Rotation.quarterRotations[1];
                break;
            }
            case NORTH: {
                rot = Rotation.quarterRotations[2];
                break;
            }
            case EAST: {
                rot = Rotation.quarterRotations[3];
                break;
            }
        }
        return new Cuboid6(-0.125, 0.0625, 0.125, 0.125, 0.25, 0.375).apply(rot).add(new Vector3(getBlockPos().getX() + 0.5, getBlockPos().getY(), getBlockPos().getZ() + 0.5));
    }
    
    public int getOut() {
        return out;
    }
    
    public void setOut(int out) {
        this.out = out;
    }
    
    public int getIn() {
        return in;
    }
    
    public void setIn(int in) {
        this.in = in;
    }
}
