package thaumcraft.common.container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.entities.monster.EntityPech;


public class InventoryPech extends SimpleContainer
{
    private EntityPech theMerchant;
    private Player thePlayer;

    public InventoryPech(Player par1Player, EntityPech par2IMerchant) {
        super(5);
        thePlayer = par1Player;
        theMerchant = par2IMerchant;
    }

    public boolean isUsableByPlayer(Player player) {
        return theMerchant.isTamed();
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0;
    }
}
