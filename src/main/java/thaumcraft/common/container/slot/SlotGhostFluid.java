package thaumcraft.common.container.slot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidUtil;


public class SlotGhostFluid extends SlotGhost
{
    public SlotGhostFluid(Container par1iInventory, int par2, int par3, int par4) {
        super(par1iInventory, par2, par3, par4);
    }
    
    @Override
    public int getSlotStackLimit() {
        return 1;
    }
    
    public boolean isItemValid(ItemStack stack1) {
        return FluidUtil.getFluidHandler(stack1) != null;
    }
    
    @Override
    public boolean canTakeStack(Player par1Player) {
        return false;
    }
}
