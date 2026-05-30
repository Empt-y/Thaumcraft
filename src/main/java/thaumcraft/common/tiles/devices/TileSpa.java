package thaumcraft.common.tiles.devices;
import javax.annotation.Nonnull;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import thaumcraft.common.items.consumables.ItemBathSalts;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TileSpa extends TileThaumcraftInventory implements IFluidHandler
{
    private boolean mix;
    private int counter;
    public FluidTank tank;

    public TileSpa(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(1);
        mix = true;
        counter = 0;
        tank = new FluidTank(5000);
    }

    public void toggleMix() {
        mix = !mix;
        syncTile(false);
        setChanged();
    }

    public boolean getMix() {
        return mix;
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        mix = nbt.getBooleanOr("mix", false);
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        nbt.putBoolean("mix", mix);
        return nbt;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
        mix = input.getBooleanOr("mix", false);
        tank.deserialize(input.childOrEmpty("tank"));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
        output.putBoolean("mix", mix);
        tank.serialize(output.child("tank"));
    }

    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack) {
        return stack.getItem() instanceof ItemBathSalts;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return (side != Direction.UP) ? new int[] { 0 } : new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction side) {
        return side != Direction.UP && isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction side) {
        return side != Direction.UP;
    }

    @Override
    public void update() {
        super.update();
        if (level == null) return;
        ++counter;
        // fluid placement logic stubbed — uses removed APIs (BlockFluidBase, FluidRegistry, getMetaFromState, etc.)
    }

    // IFluidHandler — delegate to internal tank
    @Override public int getTanks() { return tank.getTanks(); }
    @Override public @Nonnull FluidStack getFluidInTank(int t) { return tank.getFluidInTank(t); }
    @Override public int getTankCapacity(int t) { return tank.getTankCapacity(t); }
    @Override public boolean isFluidValid(int t, @Nonnull FluidStack fs) { return tank.isFluidValid(t, fs); }
    @Override public int fill(FluidStack resource, FluidAction action) { return tank.fill(resource, action); }
    @Override public @Nonnull FluidStack drain(FluidStack resource, FluidAction action) { return tank.drain(resource, action); }
    @Override public @Nonnull FluidStack drain(int maxDrain, FluidAction action) { return tank.drain(maxDrain, action); }
}
