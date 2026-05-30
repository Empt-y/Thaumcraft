package thaumcraft.common.container.slot;

import java.util.Collections;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

public class SlotCraftingArcaneWorkbench extends Slot
{
    private final Container craftMatrix;
    private final Player player;
    private int amountCrafted;
    private final TileArcaneWorkbench tile;

    public SlotCraftingArcaneWorkbench(TileArcaneWorkbench te, Player par1Player,
            Container inventory, Container par3IInventory, int par4, int par5, int par6) {
        super(par3IInventory, par4, par5, par6);
        player = par1Player;
        craftMatrix = inventory;
        tile = te;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack remove(int amount) {
        if (hasItem()) {
            amountCrafted += Math.min(amount, getItem().getCount());
        }
        return super.remove(amount);
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        amountCrafted += amount;
    }

    protected void onCrafting(ItemStack stack) {
        if (amountCrafted > 0) {
            stack.onCraftedBy(player, amountCrafted);
        }
        amountCrafted = 0;
        if (this.container instanceof ResultContainer resultContainer) {
            RecipeHolder<?> recipeHolder = resultContainer.getRecipeUsed();
            if (recipeHolder != null) {
                player.awardRecipes(Collections.singletonList(recipeHolder));
                resultContainer.setRecipeUsed(null);
            }
        }
    }

    @Override
    public void onTake(Player thePlayer, ItemStack stack) {
        onCrafting(stack);
        IArcaneRecipe recipe = null; // FIXME: ThaumcraftCraftingManager needs Container parameter update

        int vis = 0;
        AspectList crystals = null;
        if (recipe != null) {
            vis = (int)(recipe.getVis() * (1.0f - CasterManager.getTotalVisDiscount(thePlayer)));
            crystals = recipe.getCrystals();
            if (vis > 0 && tile != null) {
                tile.spendAura(vis);
            }
        }

        // Consume matrix items
        for (int i = 0; i < Math.min(9, craftMatrix.getContainerSize()); ++i) {
            craftMatrix.removeItem(i, 1);
        }

        // Consume crystals
        if (crystals != null) {
            for (Aspect aspect : crystals.getAspects()) {
                ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect));
                for (int j = 0; j < 6; ++j) {
                    int slot = 9 + j;
                    if (slot < craftMatrix.getContainerSize()) {
                        ItemStack slotItem = craftMatrix.getItem(slot);
                        if (!slotItem.isEmpty() && slotItem.getItem() == ItemsTC.crystalEssence
                                && ItemStack.isSameItemSameComponents(cs, slotItem)) {
                            craftMatrix.removeItem(slot, cs.getCount());
                            break;
                        }
                    }
                }
            }
        }
    }
}
