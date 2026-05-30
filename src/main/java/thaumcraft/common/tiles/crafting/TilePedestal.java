package thaumcraft.common.tiles.crafting;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.devices.BlockInlay;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TilePedestal extends TileThaumcraftInventory
{
    public TilePedestal(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(1);
        syncedSlots = new int[] { 0 };
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }
    
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 2, getBlockPos().getZ() + 1);
    }
    
    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack2) {
        return stack2.isEmpty() || getStackInSlot(par1).isEmpty();
    }
    
    public void setInventorySlotContentsFromInfusion(int par1, ItemStack stack2) {
        setInventorySlotContents(par1, stack2);
        setChanged();
        if (!getLevel().isClientSide()) {
            syncTile(false);
        }
    }
    
    public BlockPos findInstabilityMitigator() {
        if (getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING) > 0) {
            BlockPos pp = seekSourceRecursive(getBlockPos(), getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING));
            if (pp != null) {
                return pp;
            }
        }
        return null;
    }
    
    private BlockPos seekSourceRecursive(BlockPos pos, int lastCharge) {
        for (Direction face : Direction.Plane.HORIZONTAL) {
            BlockPos pp = getBlockPos().relative(face);
            int ss = BlockInlay.getSourceStrengthAt(world, pp);
            if (ss >= 5) {
                return pp;
            }
            BlockState bs = getLevel().getBlockState(pp);
            if (bs.getProperties().containsKey(BlockInlay.CHARGE)) {
                int charge = (int)bs.getValue(LiquidBlock.LEVEL);
                if (charge > lastCharge) {
                    BlockPos ob = seekSourceRecursive(pp, charge);
                    if (ob != null) {
                        return ob;
                    }
                }
            }
        }
        return null;
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 11) {
            if (getLevel().isClientSide()) {
                FXDispatcher.INSTANCE.drawBamf(getBlockPos().above(), 0.75f, 0.0f, 0.5f, true, true, null);
            }
            return true;
        }
        if (i == 12) {
            if (getLevel().isClientSide()) {
                FXDispatcher.INSTANCE.drawBamf(getBlockPos().above(), true, true, null);
            }
            return true;
        }
        if (i == 5) {
            if (getLevel().isClientSide()) {
                FXDispatcher.INSTANCE.drawPedestalShield(getBlockPos());
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
}
