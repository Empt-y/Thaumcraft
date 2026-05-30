package thaumcraft.common.container.slot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;


public class SlotGhost extends Slot
{
    int limit;
    
    public SlotGhost(Container par1iInventory, int par2, int par3, int par4, int par5) {
        super(par1iInventory, par2, par3, par4);
        limit = Integer.MAX_VALUE;
        limit = par5;
    }
    
    public SlotGhost(Container par1iInventory, int par2, int par3, int par4) {
        super(par1iInventory, par2, par3, par4);
        limit = Integer.MAX_VALUE;
    }
    
    public int getSlotStackLimit() {
        return limit;
    }
    
    public boolean canTakeStack(Player par1Player) {
        return false;
    }
}
