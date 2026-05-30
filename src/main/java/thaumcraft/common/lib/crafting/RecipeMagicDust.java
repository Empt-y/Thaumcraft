package thaumcraft.common.lib.crafting;
import java.util.ArrayList;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
// removed: 
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;


public class RecipeMagicDust implements Recipe
{
    public boolean matches(CraftingContainer inv, Level worldIn) {
        boolean bowl = false;
        boolean flint = false;
        boolean redstone = false;
        ArrayList<String> crystals = new ArrayList<String>();
        for (int a = 0; a < 3; ++a) {
            for (int b = 0; b < 3; ++b) {
                if (inv.getStackInRowAndColumn(a, b) != null) {
                    if (!inv.getStackInRowAndColumn(a, b).isEmpty()) {
                        ItemStack stack = inv.getStackInRowAndColumn(a, b).copy();
                        if (stack.getItem() == Items.BOWL && bowl) {
                            return false;
                        }
                        if (stack.getItem() == Items.BOWL && !bowl) {
                            bowl = true;
                        }
                        else {
                            if (stack.getItem() == Items.FLINT && flint) {
                                return false;
                            }
                            if (stack.getItem() == Items.FLINT && !flint) {
                                flint = true;
                            }
                            else {
                                if (stack.getItem() == Items.REDSTONE && redstone) {
                                    return false;
                                }
                                if (stack.getItem() == Items.REDSTONE && !redstone) {
                                    redstone = true;
                                }
                                else {
                                    if (stack.getItem() != ItemsTC.crystalEssence) {
                                        return false;
                                    }
                                    ItemCrystalEssence ice = (ItemCrystalEssence)stack.getItem();
                                    if (crystals.contains(ice.getAspects(stack).getAspects()[0].get()) || crystals.size() >= 3) {
                                        return false;
                                    }
                                    crystals.add(ice.getAspects(stack).getAspects()[0].get());
                                }
                            }
                        }
                    }
                }
            }
        }
        return bowl && redstone && flint && crystals.size() == 3;
    }
    
    public ItemStack getCraftingResult(CraftingContainer inv) {
        return new ItemStack(ItemsTC.salisMundus);
    }
    
    public boolean canFit(int width, int height) {
        return width * height >= 6;
    }
    
    public ItemStack getRecipeOutput() {
        return new ItemStack(ItemsTC.salisMundus);
    }
    
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            ItemStack itemstack2 = ForgeHooks.getContainerItem(itemstack);
            if (itemstack != null && !itemstack.isEmpty() && (itemstack.getItem() == Items.FLINT || itemstack.getItem() == Items.BOWL)) {
                ItemStack is = itemstack.copy();
                is.setCount(1);
                itemstack2 = is;
            }
            ret.set(i, itemstack2);
        }
        return ret;
    }
}
