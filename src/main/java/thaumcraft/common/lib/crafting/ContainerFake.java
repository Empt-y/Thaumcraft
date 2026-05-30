package thaumcraft.common.lib.crafting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;


public class ContainerFake extends AbstractContainerMenu
{
    public ContainerFake() {
        super(null, 0);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
    }
}
