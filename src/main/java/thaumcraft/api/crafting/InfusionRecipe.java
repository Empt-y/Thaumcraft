package thaumcraft.api.crafting;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;



public class InfusionRecipe implements IThaumcraftRecipe
{
	public AspectList aspects;
	public String research;
	private String name;
	protected NonNullList<Ingredient> components = NonNullList.create();
	public Ingredient sourceInput; //Use Ingredient.of(net.minecraft.world.item.Items.AIR) of the source item can be anything
	public Object recipeOutput;
	public int instability;
	
	public InfusionRecipe(String research, Object outputResult, int inst, AspectList aspects2, Object centralItem, Object ... recipe) {
		name="";
		this.research = research;
		recipeOutput = outputResult;
		aspects = aspects2;
		instability = inst;
		sourceInput = ThaumcraftApiHelper.getIngredient(centralItem);
		if (sourceInput==null) {
			String ret = "Invalid infusion central item: "+centralItem;
            throw new RuntimeException(ret);
		}		
		for (Object in : recipe)
        {
            Ingredient ing = ThaumcraftApiHelper.getIngredient(in);
            if (ing != null) {
            	components.add(ing);
            } else {
                String ret = "Invalid infusion recipe: ";
                for (Object tmp :  recipe)
                {
                    ret += tmp + ", ";
                }
                ret += outputResult;
                throw new RuntimeException(ret);
            }
        }
	}

	/**
     * Used to check if a recipe matches current crafting inventory
     * @param player 
     */
	public boolean matches(List<ItemStack> input, ItemStack central, net.minecraft.world.level.Level world, Player player) {
		if (getRecipeInput()==null) return false;			
		if (!ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research)) {
    		return false;
    	}		
		if (!(getRecipeInput().items().findAny().isEmpty() || getRecipeInput().test(central))) return false;
		return matchesIngredients(input, getComponents());
    }
    
	@Override
    public String getResearch() {
		return research;
    }
    
	public Ingredient getRecipeInput() {
		return sourceInput;
	}

	public NonNullList<Ingredient> getComponents() {
		return components;
	}
	
	public Object getRecipeOutput() {
		return recipeOutput;
	}
	
	public AspectList getAspects() {
		return aspects;
	}			
	
	public Object getRecipeOutput(Player player, ItemStack input, List<ItemStack> comps ) {
		return recipeOutput;
    }
    
    public AspectList getAspects(Player player, ItemStack input, List<ItemStack> comps) {
		return aspects;
    }
    
    public int getInstability(Player player, ItemStack input, List<ItemStack> comps) {
		return instability;
    }
    
    private String group="";
	
	@Override
	public String getGroup() {
		return group;
	}
	
	public InfusionRecipe setGroup(Identifier s) {
		group=s.toString();
		return this;
	}

	// --- Recipe<CraftingInput> stubs: Infusion is handled in-world by the pedestal, not via grid ---

	@Override
	public boolean matches(CraftingInput inv, Level world) {
		return false;
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		return recipeOutput instanceof ItemStack r ? r.copy() : ItemStack.EMPTY;
	}

	@Override
	public boolean showNotification() { return false; }

	@Override
	public PlacementInfo placementInfo() { return PlacementInfo.NOT_PLACEABLE; }

	@Override
	public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }

	@Override
	@SuppressWarnings("unchecked")
	public RecipeSerializer<? extends Recipe<CraftingInput>> getSerializer() { return null; }

	@Override
	@SuppressWarnings("unchecked")
	public RecipeType<? extends Recipe<CraftingInput>> getType() { return null; }

	private static boolean matchesIngredients(List<ItemStack> available, NonNullList<Ingredient> required) {
		if (available.size() < required.size()) return false;
		java.util.List<ItemStack> pool = new java.util.ArrayList<>(available);
		for (Ingredient ing : required) {
			boolean found = false;
			for (int i = 0; i < pool.size(); i++) {
				if (ing.test(pool.get(i))) {
					pool.remove(i);
					found = true;
					break;
				}
			}
			if (!found) return false;
		}
		return true;
	}
}
