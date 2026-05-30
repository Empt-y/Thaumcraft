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
    protected int energy;
    protected int capacity = 1000;
    protected int maxExtract = 20;
    
    public void update() {
        if (!getLevel().isClientSide() && BlockStateUtils.isEnabled(getBlockState().getBlockState())) {
            recharge();
            Direction face = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
            BlockState state = getLevel().getBlockState(getBlockPos().relative(face));
            Block block = state.getBlock();
            if (block.hasBlockEntity(state)) {
                BlockEntity tileentity = getLevel().getBlockEntity(getBlockPos().relative(face));
                if (tileentity != null && tileentity.hasCapability(CapabilityEnergy.ENERGY, face.getOpposite())) {
                    IEnergyStorage capability = tileentity.getCapability(CapabilityEnergy.ENERGY, face.getOpposite());
                    if (capability.canReceive()) {
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
    
    public boolean hasCapability(@Nonnull Object /* Capability removed */ capability, @Nullable Direction facing) {
        Direction face = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        return (face == facing && capability == CapabilityEnergy.ENERGY) || super.hasCapability(capability, facing);
    }
    
    @Nullable
    public <T> T getCapability(@Nonnull Object /* Capability removed */ capability, @Nullable Direction facing) {
        Direction face = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        if (face == facing && capability == CapabilityEnergy.ENERGY) {
            return (T)this;
        }
        return (T)super.getCapability((Object /* Capability removed */)capability, facing);
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
