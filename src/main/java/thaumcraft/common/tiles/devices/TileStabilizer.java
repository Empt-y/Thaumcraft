package thaumcraft.common.tiles.devices;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileStabilizer extends TileThaumcraft 
{
    private int ticks;
    private int delay;
    int lastEnergy;
    protected int energy;
    protected int capacity = 15;
    
    public TileStabilizer(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        ticks = 0;
        delay = 0;
        lastEnergy = 0;
        energy = 0;
    }
    
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 1.5, getBlockPos().getZ() + 1);
    }
    
    public void update() {
        if (!getLevel().isClientSide()) {
            ++ticks;
            int energy = this.energy;
            getClass();
            if (energy < 15 && ticks % 20 == 0) {
                ++this.energy;
                AuraHelper.polluteAura(getLevel(), getBlockPos(), 0.25f, true);
                setChanged();
                syncTile(false);
                getLevel().updateNeighborsAt(getBlockPos(), getLevel().getBlockState(getBlockPos()).getBlock());
            }
            if (this.energy > 0 && delay <= 0 && ticks % 5 == 0) {
                int q = this.energy;
                tryAddStability();
                if (q != this.energy) {
                    setChanged();
                    syncTile(false);
                }
            }
            if (delay > 0) {
                --delay;
            }
        }
        if (getLevel().isClientSide() && energy != lastEnergy) {
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            lastEnergy = energy;
        }
    }
    
    private void tryAddStability() {
        Direction facing = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        List<EntityFluxRift> targets = getLevel().getEntitiesOfClass(EntityFluxRift.class, new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 1, getBlockPos().getZ() + 1).inflate(8.0));
        if (targets.size() > 0) {
            for (EntityFluxRift e : targets) {
                if (!e.isAlive()) {
                    continue;
                }
                if (e.getStability() == EntityFluxRift.EnumStability.VERY_STABLE || !mitigate(1)) {
                    continue;
                }
                e.addStability();
                delay += 5;
                if (energy <= 0) {
                    return;
                }
            }
        }
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbt) {
        energy = Math.min(nbt.getIntOr("energy", 0), 15);
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        nbt.putInt("energy", energy);
        return nbt;
    }
    
    public int getEnergy() {
        return energy;
    }
    
    public boolean mitigate(int e) {
        if (energy >= e) {
            energy -= e;
            getLevel().updateNeighborsAt(getBlockPos(), getLevel().getBlockState(getBlockPos()).getBlock());
            return true;
        }
        return false;
    }
}
