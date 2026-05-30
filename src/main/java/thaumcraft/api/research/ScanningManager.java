package thaumcraft.api.research;
import java.util.ArrayList;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.neoforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;



public class ScanningManager {
	
	static ArrayList<IScanThing> things = new ArrayList<IScanThing>();
	
	/**
	 * Add things to scan
	 * @example 
	 * <i>ScanManager.addScannableThing(new ScanItem("HIPSTER",new ItemStack(Items.apple.asItem(), 1)));</i><br>
	 * This will unlock the <b>HIPSTER</b> research if you scan any kind of apple.
	 */	
	public static void addScannableThing(IScanThing obj) {		
		things.add(obj);		
	}
	
	/**
	 * @param player
	 * @param object this could in theory be anything, but vanilla tc scanning tools only pass in Entity, BlockPos, Itemstack or null
	 */
	public static void scanTheThing(Player player, Object object) {
		boolean found = false;
		boolean suppress = false;
		for (IScanThing thing:things) {
			if (thing.checkThing(player, object)) {						
				if (thing.getResearchKey(player, object)==null || thing.getResearchKey(player, object).isEmpty() ||
						ThaumcraftApi.internalMethods.progressResearch(player, thing.getResearchKey(player, object))) {		
					if (thing.getResearchKey(player, object)==null || thing.getResearchKey(player, object).isEmpty())
						suppress = true;
					found = true;
					thing.onSuccess(player, object);
				}
			}			
		}
		if (!suppress) {
			if (!found) {
				player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("\u00a75\u00a7o"+I18n.get("tc.unknownobject")));
			} else {
				player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("\u00a7a\u00a7o"+I18n.get("tc.knownobject")));
			}
		}
		
		// scan contents of inventories
		if (object instanceof BlockPos) {
			IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(player.level(), (BlockPos) object, Direction.UP);
			if (handler != null) {
				int scanned = 0;
				for (int slot=0;slot<handler.getSlots();slot++) {
					ItemStack stack = handler.getStackInSlot(slot);
					if (stack!=null && !stack.isEmpty()) {
						scanTheThing(player,stack);
						scanned++;
					}
					if (scanned>=100) {
						player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("\u00a75\u00a7o"+I18n.get("tc.invtoolarge")));
						break; // to prevent lag with massive inventories
					}
				}
			}			
			return;
		}
		
	}
	
	/**
	 * @param player
	 * @param object
	 * @return true if the object can be scanned for research the player has not yet discovered
	 */
	public static boolean isThingStillScannable(Player player, Object object) {		
		for (IScanThing thing:things) {
			if (thing.checkThing(player, object)) {
				try {
					if (!ThaumcraftCapabilities.knowsResearch(player,thing.getResearchKey(player, object))) {					
						return true;
					}
				} catch (Exception e) {	}
			}			
		}
		return false;
	}
		
	
	public static ItemStack getItemFromParms(Player player, Object obj) {
		ItemStack is = ItemStack.EMPTY;
		if (obj instanceof ItemStack)
			is = (ItemStack) obj;
		if (obj instanceof ItemEntity && ((ItemEntity)obj).getItem()!=null)
			is = ((ItemEntity)obj).getItem();
		if (obj instanceof BlockPos) {
			BlockState state = player.level().getBlockState((BlockPos) obj);
			try {
				if (state.getFluidState().is(net.minecraft.world.level.material.Fluids.WATER))
					is = new ItemStack(Items.WATER_BUCKET);
				else if (state.getFluidState().is(net.minecraft.world.level.material.Fluids.LAVA))
					is = new ItemStack(Items.LAVA_BUCKET);
				else
					is = new ItemStack(state.getBlock().asItem());
			} catch (Exception e) { }
		}
		return is;
	}
}
