package thaumcraft.api;
import net.minecraft.world.Container;
import java.util.List;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import thaumcraft.common.lib.utils.InventoryUtils;


public class ThaumcraftInvHelper {

	public static class InvFilter {
		public boolean igDmg;
		public boolean igNBT;
		public boolean useOre;
		public boolean useMod;
		public boolean relaxedNBT = false;

		public InvFilter(boolean ignoreDamage, boolean ignoreNBT, boolean useOre, boolean useMod) {
			igDmg = ignoreDamage;
			igNBT = ignoreNBT;
			this.useOre = useOre;
			this.useMod = useMod;
		}

		public InvFilter setRelaxedNBT() {
			relaxedNBT = true;
			return this;
		}

		public static InvFilter STRICT = new InvFilter(false, false, false, false);
		public static InvFilter BASEORE = new InvFilter(false, false, true, false);
	}

	public static IItemHandler getItemHandlerAt(Level world, BlockPos pos, Direction side) {
		var raw = Capabilities.Item.BLOCK.getCapability(world, pos, world.getBlockState(pos), world.getBlockEntity(pos), side);
		if (raw != null) return IItemHandler.of(raw);
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof Container inv) {
			return wrapInventory(inv, side);
		}
		return null;
	}

	public static IItemHandler wrapInventory(Container inventory, Direction side) {
		return inventory instanceof WorldlyContainer sided
			? new SidedInvWrapper(sided, side)
			: new InvWrapper(inventory);
	}

	private static CompoundTag getCustomTag(ItemStack stack) {
		CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
		return cd != null ? cd.copyTag() : null;
	}

	public static boolean areItemStackTagsEqualRelaxed(ItemStack prime, ItemStack other) {
		if (prime.isEmpty() && other.isEmpty()) return true;
		if (!prime.isEmpty() && !other.isEmpty()) {
			CompoundTag primeTag = getCustomTag(prime);
			CompoundTag otherTag = getCustomTag(other);
			return (primeTag == null || compareTagsRelaxed(primeTag, otherTag));
		}
		return false;
	}

	public static boolean compareTagsRelaxed(CompoundTag prime, CompoundTag other) {
		if (prime == null) return true;
		if (other == null) return false;
		for (String key : prime.keySet()) {
			if (!other.contains(key) || !prime.get(key).equals(other.get(key))) {
				return false;
			}
		}
		return true;
	}

	public static boolean areItemStacksEqualForCrafting(ItemStack stack0, Object in) {
		if (stack0 == null && in != null) return false;
		if (stack0 != null && in == null) return false;
		if (stack0 == null) return true;

		if (in instanceof Object[]) return true;

		// TODO: replace String ore-dict check with tag-based matching
		if (in instanceof String) return false;

		if (in instanceof ItemStack target) {
			boolean t1 = !stack0.has(DataComponents.CUSTOM_DATA) || areItemStackTagsEqualForCrafting(stack0, target);
			if (!t1) return false;
			return ItemStack.isSameItem(target, stack0);
		}

		return false;
	}

	public static boolean containsMatch(boolean strict, ItemStack[] inputs, List<ItemStack> targets) {
		for (ItemStack input : inputs) {
			for (ItemStack target : targets) {
				if (ItemStack.isSameItem(target, input) &&
					(!strict || ItemStack.isSameItemSameComponents(target, input))) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean areItemsEqual(ItemStack s1, ItemStack s2) {
		if (s1.isDamageableItem() && s2.isDamageableItem()) {
			return s1.getItem() == s2.getItem();
		} else {
			return s1.getItem() == s2.getItem() && s1.getDamageValue() == s2.getDamageValue();
		}
	}

	public static boolean areItemStackTagsEqualForCrafting(ItemStack slotItem, ItemStack recipeItem) {
		if (recipeItem == null || slotItem == null) return false;
		CompoundTag recipeTag = getCustomTag(recipeItem);
		CompoundTag slotTag = getCustomTag(slotItem);
		if (recipeTag != null && slotTag == null) return false;
		if (recipeTag == null) return true;

		for (String s : recipeTag.keySet()) {
			if (!slotTag.contains(s)) return false;
			if (!slotTag.get(s).toString().equals(recipeTag.get(s).toString())) {
				return false;
			}
		}
		return true;
	}

	public static ItemStack insertStackAt(Level world, BlockPos pos, Direction side, ItemStack stack, boolean simulate) {
		IItemHandler inventory = getItemHandlerAt(world, pos, side);
		if (inventory != null) {
			return ItemHandlerHelper.insertItemStacked(inventory, stack, simulate);
		}
		return stack;
	}

	public static ItemStack hasRoomFor(Level world, BlockPos pos, Direction side, ItemStack stack) {
		ItemStack testStack = insertStackAt(world, pos, side, stack.copy(), true);
		if (testStack.isEmpty()) {
			return stack.copy();
		}
		testStack.setCount(stack.getCount() - testStack.getCount());
		return testStack;
	}

	public static boolean hasRoomForSome(Level world, BlockPos pos, Direction side, ItemStack stack) {
		ItemStack testStack = insertStackAt(world, pos, side, stack.copy(), true);
		return stack.getCount() == 0 || testStack.getCount() != stack.getCount();
	}

	public static boolean hasRoomForAll(Level world, BlockPos pos, Direction side, ItemStack stack) {
		return insertStackAt(world, pos, side, stack.copy(), true).isEmpty();
	}

	public static int countTotalItemsIn(IItemHandler inventory, ItemStack stack, InvFilter filter) {
		int count = 0;
		if (inventory != null) {
			for (int a = 0; a < inventory.getSlots(); a++) {
				if (InventoryUtils.areItemStacksEqual(stack, inventory.getStackInSlot(a), filter)) {
					count += inventory.getStackInSlot(a).getCount();
				}
			}
		}
		return count;
	}

	public static int countTotalItemsIn(Level world, BlockPos pos, Direction side, ItemStack stack, InvFilter filter) {
		return countTotalItemsIn(getItemHandlerAt(world, pos, side), stack, filter);
	}

}
