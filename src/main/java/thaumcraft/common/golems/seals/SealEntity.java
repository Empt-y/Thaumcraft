package thaumcraft.common.golems.seals;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketSealToClient;


public class SealEntity implements ISealEntity
{
    SealPos sealPos;
    ISeal seal;
    byte priority;
    byte color;
    boolean locked;
    boolean redstone;
    String owner;
    boolean stopped;
    private BlockPos area;
    
    public SealEntity() {
        priority = 0;
        color = 0;
        locked = false;
        redstone = false;
        owner = "";
        stopped = false;
        area = new BlockPos(1, 1, 1);
    }
    
    public SealEntity(Level world, SealPos sealPos, ISeal seal) {
        priority = 0;
        color = 0;
        locked = false;
        redstone = false;
        owner = "";
        stopped = false;
        area = new BlockPos(1, 1, 1);
        this.sealPos = sealPos;
        this.seal = seal;
        if (seal instanceof ISealConfigArea) {
            int x = (sealPos.face.getStepX() == 0) ? 3 : 1;
            int y = (sealPos.face.getStepY() == 0) ? 3 : 1;
            int z = (sealPos.face.getStepZ() == 0) ? 3 : 1;
            area = new BlockPos(x, y, z);
        }
    }
    
    @Override
    public void tickSealEntity(Level world) {
        if (seal != null) {
            if (isStoppedByRedstone(world)) {
                if (!stopped) {
                    for (Task t : TaskHandler.getTasks((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0)).values()) {
                        if (t.getSealPos() != null && t.getSealPos().equals(sealPos)) {
                            t.setSuspended(true);
                        }
                    }
                }
                stopped = true;
                return;
            }
            stopped = false;
            seal.tickSeal(world, this);
        }
    }
    
    @Override
    public boolean isStoppedByRedstone(Level world) {
        return isRedstoneSensitive() && (world.hasNeighborSignal(getSealPos().pos) || world.hasNeighborSignal(getSealPos().pos.relative(getSealPos().face)));
    }
    
    @Override
    public ISeal getSeal() {
        return seal;
    }
    
    @Override
    public SealPos getSealPos() {
        return sealPos;
    }
    
    @Override
    public byte getPriority() {
        return priority;
    }
    
    @Override
    public void setPriority(byte priority) {
        this.priority = priority;
    }
    
    @Override
    public byte getColor() {
        return color;
    }
    
    @Override
    public void setColor(byte color) {
        this.color = color;
    }
    
    @Override
    public String getOwner() {
        return owner;
    }
    
    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    @Override
    public boolean isLocked() {
        return locked;
    }
    
    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    @Override
    public boolean isRedstoneSensitive() {
        return redstone;
    }
    
    @Override
    public void setRedstoneSensitive(boolean redstone) {
        this.redstone = redstone;
    }
    
    @Override
    public void readNBT(CompoundTag nbt) {
        BlockPos p = BlockPos.of(nbt.getLongOr("pos", 0L));
        Direction face = Direction.values()[nbt.getByteOr("face", (byte)0)];
        sealPos = new SealPos(p, face);
        setPriority(nbt.getByteOr("priority", (byte)0));
        setColor(nbt.getByteOr("color", (byte)0));
        setLocked(nbt.getBooleanOr("locked", false));
        setRedstoneSensitive(nbt.getBooleanOr("redstone", false));
        setOwner(nbt.getStringOr("owner", ""));
        try {
            seal = SealHandler.getSeal(nbt.getStringOr("type", "")).getClass().newInstance();
        }
        catch (Exception ex) {}
        if (seal != null) {
            seal.readCustomNBT(nbt);
            if (seal instanceof ISealConfigArea) {
                area = BlockPos.of(nbt.getLongOr("area", 0L));
            }
            if (seal instanceof ISealConfigToggles) {
                for (ISealConfigToggles.SealToggle prop : ((ISealConfigToggles) seal).getToggles()) {
                    if (nbt.contains(prop.getKey())) {
                        prop.setValue(nbt.getBooleanOr(prop.getKey(), false));
                    }
                }
            }
        }
    }
    
    @Override
    public CompoundTag writeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("pos", sealPos.pos.asLong());
        nbt.putByte("face", (byte) sealPos.face.ordinal());
        nbt.putString("type", seal.getKey());
        nbt.putByte("priority", getPriority());
        nbt.putByte("color", getColor());
        nbt.putBoolean("locked", isLocked());
        nbt.putBoolean("redstone", isRedstoneSensitive());
        nbt.putString("owner", getOwner());
        if (seal != null) {
            seal.writeCustomNBT(nbt);
            if (seal instanceof ISealConfigArea) {
                nbt.putLong("area", area.asLong());
            }
            if (seal instanceof ISealConfigToggles) {
                for (ISealConfigToggles.SealToggle prop : ((ISealConfigToggles) seal).getToggles()) {
                    nbt.putBoolean(prop.getKey(), prop.getValue());
                }
            }
        }
        return nbt;
    }
    
    @Override
    public void syncToClient(Level world) {
        if (!world.isClientSide()) {
            PacketHandler.INSTANCE.sendToDimension(new PacketSealToClient(this), (world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
        }
    }
    
    @Override
    public BlockPos getArea() {
        return area;
    }
    
    @Override
    public void setArea(BlockPos v) {
        area = v;
    }
}
