package thaumcraft.common.tiles.devices;
import java.util.Arrays;
import net.minecraft.nbt.CompoundTag;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;


public class TileDioptra extends TileThaumcraft 
{
    public int counter;
    public byte[] grid_amt;
    private byte[] grid_amt_p;
    
    public TileDioptra(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        counter = 0;
        grid_amt = new byte[169];
        grid_amt_p = new byte[169];
        Arrays.fill(grid_amt, (byte)0);
        Arrays.fill(grid_amt_p, (byte)0);
    }
    
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().getX() - 0.3, getBlockPos().getY() - 0.3, getBlockPos().getZ() - 0.3, getBlockPos().getX() + 1.3, getBlockPos().getY() + 2.3, getBlockPos().getZ() + 1.3);
    }
    
    public void update() {
        ++counter;
        if (!getLevel().isClientSide()) {
            if (counter % 20 == 0) {
                Arrays.fill(grid_amt, (byte)0);
                for (int xx = 0; xx < 13; ++xx) {
                    for (int zz = 0; zz < 13; ++zz) {
                        AuraChunk ac = AuraHandler.getAuraChunk((this.level instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)this.level).dimension().identifier().hashCode() : 0), (getBlockPos().getX() >> 4) + xx - 6, (getBlockPos().getZ() >> 4) + zz - 6);
                        if (ac != null) {
                            if (BlockStateUtils.isEnabled(getBlockState())) {
                                grid_amt[xx + zz * 13] = (byte)Math.min(64.0f, ac.getVis() / 500.0f * 64.0f);
                            }
                            else {
                                grid_amt[xx + zz * 13] = (byte)Math.min(64.0f, ac.getFlux() / 500.0f * 64.0f);
                            }
                        }
                    }
                }
                setChanged();
                syncTile(false);
            }
        }
        else {
            counter = 0;
        }
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbt) {
        if (nbt.contains("grid_a")) {
            grid_amt = nbt.getByteArray("grid_a").orElse(new byte[0]);
        }
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        nbt.putByteArray("grid_a", grid_amt);
        return nbt;
    }
}
