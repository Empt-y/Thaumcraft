package thaumcraft.common.tiles.devices;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
// import net.neoforged.neoforge.capabilities.Object /* Capability removed */; // API changed
import net.neoforged.neoforge.energy.IEnergyStorage;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.world.aura.AuraHandler;


public class TileVisGenerator extends TileThaumcraft implements IEnergyStorage
{
    public TileVisGenerator(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected int energy;
    protected int capacity = 1000;
    protected int maxExtract = 20;
    
    public void update() {
        if (!getLevel().isClientSide() && BlockStateUtils.isEnabled(getBlockState())) {
            recharge();
            Direction face = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
            BlockState state = getLevel().getBlockState(getBlockPos().relative(face));
            Block block = state.getBlock();
            BlockEntity tileentity = getLevel().getBlockEntity(getBlockPos().relative(face));
            if (tileentity != null) {
                IEnergyStorage capability = getLevel().getCapability(
                    net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                    getBlockPos().relative(face), face.getOpposite());
                if (capability != null && capability.canReceive()) {
                    int energyExtracted = Math.min(energy, 20);
                    energyExtracted = capability.receiveEnergy(energyExtracted, false);
                    if (energyExtracted > 0) {
                        energy -= energyExtracted;
                        setChanged();
                        if (energy == 0) {
                            syncTile(false);
                        }
                    }
                }
            }
        }
    }
    
    private void recharge() {
        if (energy == 0) {
            float vis = AuraHandler.drainVis(getLevel(), getBlockPos(), 1.0f, false);
            energy = (int)(vis * 1000.0f);
            setChanged();
            syncTile(false);
        }
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbt) {
        energy = nbt.getIntOr("energy", 0);
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        nbt.putInt("energy", energy);
        return nbt;
    }
    
    
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }
    
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }
    
    public int getEnergyStored() {
        return energy;
    }
    
    public int getMaxEnergyStored() {
        return 1000;
    }
    
    public boolean canExtract() {
        return true;
    }
    
    public boolean canReceive() {
        return false;
    }
}
