package thaumcraft.common.tiles.devices;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.util.ProblemReporter;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileWaterJug extends TileThaumcraft implements IFluidHandler
{
    int zone;
    int counter;
    ArrayList<Integer> handlers;
    int zc;
    int tcount;
    public FluidTank tank;

    public TileWaterJug(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        zone = 0;
        counter = 0;
        handlers = new ArrayList<Integer>();
        zc = 0;
        tcount = 0;
        tank = new FluidTank(1000);
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        if (level != null) {
            var input = TagValueInput.create(ProblemReporter.DISCARDING, getLevel().registryAccess(), nbt);
            tank.deserialize(input);
        }
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        if (level != null) {
            var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, getLevel().registryAccess());
            tank.serialize(output);
            nbt.merge(output.buildResult());
        }
        return nbt;
    }

        public void loadAdditional(CompoundTag nbt) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
        ListTag nbttaglist = nbt.getListOrEmpty("handlers");
        handlers = new ArrayList<Integer>();
        for (int i = 0; i < nbttaglist.size(); ++i) {
            IntTag tag = (IntTag)nbttaglist.get(i);
            handlers.add(tag.value());
        }
    }

        public CompoundTag saveAdditional(CompoundTag nbt) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
        ListTag nbttaglist = new ListTag();
        for (int i = 0; i < handlers.size(); ++i) {
            nbttaglist.add(IntTag.valueOf(handlers.get(i)));
        }
        nbt.put("handlers", nbttaglist);
        return nbt;
    }

    public void update() {
        ++counter;
        if (getLevel().isClientSide()) {
            if (tcount > 0) {
                if (tcount % 5 == 0) {
                    int x = zc / 5 % 5;
                    int y = zc / 5 / 5 % 3;
                    int z = zc % 5;
                    FXDispatcher.INSTANCE.waterTrailFx(getBlockPos(), getBlockPos().offset(x - 2, y - 1, z - 2), counter, 2650102, 0.1f);
                }
                --tcount;
            }
        } else if (counter % 5 == 0) {
            ++zone;
            int x = zone / 5 % 5;
            int y = zone / 5 / 5 % 3;
            int z = zone % 5;
            BlockPos target = getBlockPos().offset(x - 2, y - 1, z - 2);
            BlockEntity te = getLevel().getBlockEntity(target);
            if ((te instanceof IFluidHandler || getLevel().getBlockState(target).getBlock() == Blocks.CAULDRON) && !handlers.contains(zone)) {
                handlers.add(zone);
                setChanged();
            }
            int i = 0;
            while (i < handlers.size() && tank.getFluidAmount() >= 25) {
                int zz = handlers.get(i);
                x = zz / 5 % 5;
                y = zz / 5 / 5 % 3;
                z = zz % 5;
                BlockPos pp = getBlockPos().offset(x - 2, y - 1, z - 2);
                BlockState bs2 = getLevel().getBlockState(pp);
                BlockEntity tile = getLevel().getBlockEntity(pp);
                if (tile instanceof IFluidHandler fh) {
                    FluidStack water25 = new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 25);
                    int q = fh.fill(water25, IFluidHandler.FluidAction.SIMULATE);
                    if (q > 0) {
                        fh.fill(new FluidStack(net.minecraft.world.level.material.Fluids.WATER, q), IFluidHandler.FluidAction.EXECUTE);
                        drain(new FluidStack(net.minecraft.world.level.material.Fluids.WATER, q), IFluidHandler.FluidAction.EXECUTE);
                        setChanged();
                        break;
                    }
                } else {
                    if (bs2.getBlock() != Blocks.CAULDRON || tank.getFluidAmount() < 333) {
                        handlers.remove(i);
                        setChanged();
                        continue;
                    }
                }
                ++i;
            }
            if (tank.getFluidAmount() < 1000) {
                float da = (1000 - tank.getFluidAmount()) / 1000.0f;
                if (da > 0.1f) {
                    da = 0.1f;
                }
                float dv = AuraHelper.drainVis(getLevel(), getBlockPos(), da, false);
                int wa = (int)(1000.0f * dv);
                if (wa > 0) {
                    tank.fill(new FluidStack(net.minecraft.world.level.material.Fluids.WATER, wa), IFluidHandler.FluidAction.EXECUTE);
                    setChanged();
                    if (tank.getFluidAmount() >= tank.getCapacity()) {
                        syncTile(false);
                    }
                }
            }
        }
    }

    public boolean triggerEvent(int i, int j) {
        if (i == 1) {
            if (getLevel().isClientSide()) {
                zc = j;
                tcount = 5;
            }
            return true;
        }
        return super.triggerEvent(i, j);
    }

    @Override
    public int getTanks() {
        return tank.getTanks();
    }

    @Override
    public FluidStack getFluidInTank(int t) {
        return tank.getFluidInTank(t);
    }

    @Override
    public int getTankCapacity(int t) {
        return tank.getTankCapacity(t);
    }

    @Override
    public boolean isFluidValid(int t, @Nonnull FluidStack fs) {
        return tank.isFluidValid(t, fs);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        boolean f = tank.getFluidAmount() >= tank.getCapacity();
        FluidStack fs = tank.drain(resource, action);
        setChanged();
        if (f && tank.getFluidAmount() < tank.getCapacity()) {
            syncTile(false);
        }
        return fs;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        boolean f = tank.getFluidAmount() >= tank.getCapacity();
        FluidStack fs = tank.drain(maxDrain, action);
        setChanged();
        if (f && tank.getFluidAmount() < tank.getCapacity()) {
            syncTile(false);
        }
        return fs;
    }
}
