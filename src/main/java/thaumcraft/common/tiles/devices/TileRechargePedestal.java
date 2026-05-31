package thaumcraft.common.tiles.devices;
import java.util.ArrayList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TileRechargePedestal extends TileThaumcraftInventory implements IAspectContainer
{
    private static int[] slots;
    int counter;
    
    public TileRechargePedestal(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state, 1);
        counter = 0;
        syncedSlots = new int[] { 0 };
    }
    
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 1, getBlockPos().getZ() + 1).inflate(2.0, 2.0, 2.0);
    }
    
    @Override
    public void update() {
        super.update();
        if (!getLevel().isClientSide() && counter++ % 10 == 0 && getStackInSlot(0) != null && RechargeHelper.rechargeItem(getLevel(), getStackInSlot(0), getBlockPos(), null, 5) > 0.0f) {
            syncTile(false);
            setChanged();
            ArrayList<Aspect> al = Aspect.getPrimalAspects();
            getLevel().blockEvent(getBlockPos(), getBlockState().getBlock(), 5, al.get(getLevel().getRandom().nextInt(al.size())).getColor());
        }
    }
    
    public void setInventorySlotContentsFromInfusion(int par1, ItemStack stack2) {
        setInventorySlotContents(par1, stack2);
        setChanged();
        if (!getLevel().isClientSide()) {
            syncTile(false);
        }
    }
    
    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack) {
        return stack.getItem() instanceof IRechargable;
    }
    
    @Override
    public int[] getSlotsForFace(Direction side) {
        return TileRechargePedestal.slots;
    }
    
    @Override
    public boolean canInsertItem(int par1, ItemStack stack, Direction par3) {
        return stack.getItem() instanceof IRechargable;
    }
    
    @Override
    public boolean canExtractItem(int par1, ItemStack stack2, Direction par3) {
        return true;
    }
    
    @Override
    public AspectList getAspects() {
        ItemStack s = (level == null || getLevel().isClientSide()) ? getSyncedStackInSlot(0) : getStackInSlot(0);
        if (s != null && s.getItem() instanceof IRechargable) {
            float c = (float)RechargeHelper.getCharge(s);
            return new AspectList().add(Aspect.ENERGY, Math.round(c));
        }
        return null;
    }
    
    @Override
    public void setAspects(AspectList aspects) {
    }
    
    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }
    
    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }
    
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }
    
    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
    
    public boolean triggerEvent(int i, int j) {
        if (i == 5) {
            if (getLevel().isClientSide()) {
                FXDispatcher.INSTANCE.visSparkle(getBlockPos().getX() + getLevel().getRandom().nextInt(3) - getLevel().getRandom().nextInt(3), getBlockPos().above().getY() + getLevel().getRandom().nextInt(3), getBlockPos().getZ() + getLevel().getRandom().nextInt(3) - getLevel().getRandom().nextInt(3), getBlockPos().getX(), getBlockPos().above().getY(), getBlockPos().getZ(), j);
            }
            return true;
        }
        return super.triggerEvent(i, j);
    }
    
    static {
        slots = new int[] { 0 };
    }
}
