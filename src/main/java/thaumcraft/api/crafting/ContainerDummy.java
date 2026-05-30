package thaumcraft.api.crafting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;


public class ContainerDummy extends AbstractContainerMenu {

	public ContainerDummy() {
		super(null, 0);
	}

	@Override
	public boolean stillValid(Player player) {
		return false;
	}

	@Override
	public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int index) {
		return net.minecraft.world.item.ItemStack.EMPTY;
	}

}
