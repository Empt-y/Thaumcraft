package thaumcraft.api.research;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import thaumcraft.api.internal.CommonInternals;


public class ScanOreDictionary implements IScanThing {

	String research;
	String[] entries;

	public ConcurrentHashMap<Integer,Boolean> cache = new ConcurrentHashMap<>();

	public ScanOreDictionary(String research, String ... entries) {
		this.research = research;
		this.entries = entries;
	}

	@Override
	public boolean checkThing(Player player, Object obj) {
		ItemStack stack = null;
		if (obj!=null) {
			if (obj instanceof BlockPos) {
				BlockState state = player.level().getBlockState((BlockPos) obj);
				stack = new ItemStack(state.getBlock());
			} else if (obj instanceof ItemStack) {
				stack = (ItemStack) obj;
			} else if (obj instanceof ItemEntity && ((ItemEntity)obj).getItem()!=null) {
				stack = ((ItemEntity)obj).getItem();
			}
		}

		if (stack!=null && !stack.isEmpty()) {
			int hid = CommonInternals.generateUniqueItemstackId(stack);
			if (cache.containsKey(hid)) {
				return cache.get(hid);
			}
			// OreDictionary removed in MC 26: tag-based checks not implemented here
			synchronized(cache) { cache.put(hid, false); }
		}

		return false;
	}

	@Override
	public String getResearchKey(Player player, Object object) {
		return research;
	}
}
