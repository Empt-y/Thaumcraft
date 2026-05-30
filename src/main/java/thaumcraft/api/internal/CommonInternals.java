package thaumcraft.api.internal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import thaumcraft.api.ThaumcraftApi.SmeltBonus;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IThaumcraftRecipe;



/**
 * @author Azanor
 *
 * Internal variables and methods used by Thaumcraft and the api that should normally not be accessed directly by addon mods.
 *
 */
public class CommonInternals {
	
	public static HashMap<String,Identifier> jsonLocs = new  HashMap<>();
	public static ArrayList<ThaumcraftApi.EntityTags> scanEntities = new ArrayList<>();	
	public static HashMap<Identifier,IThaumcraftRecipe> craftingRecipeCatalog = new HashMap<>();
	public static HashMap<Identifier,Object> craftingRecipeCatalogFake = new HashMap<>();
	public static ArrayList<SmeltBonus> smeltingBonus = new ArrayList<SmeltBonus>();
	public static ConcurrentHashMap<Integer,AspectList> objectTags = new ConcurrentHashMap<>();
	public static HashMap<Object,Integer> warpMap = new HashMap<Object,Integer>();
	public static HashMap<String,ItemStack> seedList = new HashMap<String,ItemStack>();
	
	public static IThaumcraftRecipe getCatalogRecipe(Identifier key) {	
		return craftingRecipeCatalog.get(key);
	}
	
	public static Object getCatalogRecipeFake(Identifier key) {	
		return craftingRecipeCatalogFake.get(key);
	}
	
	/**
	 * Obviously the int generated is not truly unique, but it is unique enough for this purpose.
	 * @param stack
	 * @return
	 */
	public static int generateUniqueItemstackId(ItemStack stack) {
    	ItemStack sc = stack.copy();
    	sc.setCount(1);
    	return java.util.Objects.hash(sc.getItem().getDescriptionId(), sc.getComponentsPatch());
    }

	/**
	 * Obviously the int generated is not truly unique, but it is unique enough for this purpose.
	 * Strips all nbt data from itemstack
	 * @param stack
	 * @return
	 */
	public static int generateUniqueItemstackIdStripped(ItemStack stack) {
    	return stack.getItem().getDescriptionId().hashCode();
    }
}
