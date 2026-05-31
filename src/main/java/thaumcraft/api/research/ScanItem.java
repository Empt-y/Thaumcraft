package thaumcraft.api.research;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.ThaumcraftInvHelper;


public class ScanItem implements IScanThing {

	String research;
	// Lazy: components are only bound after world load (MC 1.21.4+ DataComponentMap change)
	java.util.function.Supplier<ItemStack> stackSupplier;
	volatile ItemStack cachedStack;

	public ScanItem(String research, ItemStack stack) {
		this.research = research;
		this.stackSupplier = () -> stack;
		this.cachedStack = stack; // already constructed, cache immediately
	}

	public ScanItem(String research, net.minecraft.world.level.block.Block block) {
		this.research = research;
		this.stackSupplier = () -> new ItemStack(block);
	}

	// Lazy + null-safe: ItemLike covers both Item and Block; defers ItemStack creation to gameplay
	public ScanItem(String research, net.minecraft.world.level.ItemLike item) {
		this.research = research;
		this.stackSupplier = () -> (item != null) ? new ItemStack(item) : ItemStack.EMPTY;
	}

	private ItemStack getStack() {
		if (cachedStack == null) cachedStack = stackSupplier.get();
		return cachedStack;
	}

	@Override
	public boolean checkThing(Player player, Object obj) {
		if (obj == null) return false;

		ItemStack is = null;

		if (obj instanceof ItemStack)
			is = (ItemStack) obj;
		if (obj instanceof ItemEntity && ((ItemEntity)obj).getItem()!=null)
			is = ((ItemEntity)obj).getItem();

		return is!=null && !is.isEmpty() && ThaumcraftInvHelper.areItemStacksEqualForCrafting(is, getStack());
	}

	@Override
	public String getResearchKey(Player player, Object object) {
		return research;
	}
	
	
	
}
