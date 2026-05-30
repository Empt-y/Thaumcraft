package thaumcraft.common.tiles.crafting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.common.container.InventoryArcaneWorkbench;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;


public class TileArcaneWorkbench extends TileThaumcraft
{
    public InventoryArcaneWorkbench inventoryCraft;
    public int auraVisServer;
    public int auraVisClient;
    public static BlockEntityType<TileArcaneWorkbench> TYPE;

    public TileArcaneWorkbench(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        auraVisServer = 0;
        auraVisClient = 0;
        inventoryCraft = new InventoryArcaneWorkbench(this, new ContainerDummy());
    }

    public TileArcaneWorkbench(BlockPos pos, BlockState state) {
        this(TYPE, getBlockPos(), state);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
        NonNullList<ItemStack> stacks = NonNullList.withSize(inventoryCraft.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, stacks);
        for (int a = 0; a < stacks.size(); ++a) {
            inventoryCraft.setItem(a, stacks.get(a));
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
        NonNullList<ItemStack> stacks = NonNullList.withSize(inventoryCraft.getContainerSize(), ItemStack.EMPTY);
        for (int a = 0; a < stacks.size(); ++a) {
            stacks.set(a, inventoryCraft.getItem(a));
        }
        ContainerHelper.saveAllItems(output, stacks);
    }

    public void getAura() {
        if (level != null && !getLevel().isClientSide()) {
            int t = 0;
            if (getLevel().getBlockState(worldPosition.above()).getBlock() == BlocksTC.arcaneWorkbenchCharger) {
                int sx = worldPosition.getX() >> 4;
                int sz = worldPosition.getZ() >> 4;
                for (int xx = -1; xx <= 1; ++xx) {
                    for (int zz = -1; zz <= 1; ++zz) {
                        AuraChunk ac = AuraHandler.getAuraChunk((level instanceof net.minecraft.server.level.ServerLevel sl ? sl.dimension().hashCode() : 0), sx + xx, sz + zz);
                        if (ac != null) {
                            t += (int)ac.getVis();
                        }
                    }
                }
            } else {
                t = (int)AuraHandler.getVis(level, worldPosition);
            }
            auraVisServer = t;
        }
    }

    public void spendAura(int vis) {
        if (level != null && !getLevel().isClientSide()) {
            if (getLevel().getBlockState(worldPosition.above()).getBlock() == BlocksTC.arcaneWorkbenchCharger) {
                int q = vis;
                int z = Math.max(1, vis / 9);
                int attempts = 0;
                Label_0144:
                while (q > 0) {
                    ++attempts;
                    for (int xx = -1; xx <= 1; ++xx) {
                        for (int zz = -1; zz <= 1; ++zz) {
                            if (z > q) z = q;
                            q -= (int)AuraHandler.drainVis(level, worldPosition.offset(xx * 16, 0, zz * 16), (float)z, false);
                            if (q <= 0) break Label_0144;
                            if (attempts > 1000) break Label_0144;
                        }
                    }
                }
            } else {
                AuraHandler.drainVis(level, worldPosition, (float)vis, false);
            }
        }
    }
}
