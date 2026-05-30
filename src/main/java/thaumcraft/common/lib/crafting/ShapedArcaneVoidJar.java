package thaumcraft.common.lib.crafting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ShapedArcaneRecipe;


public class ShapedArcaneVoidJar extends ShapedArcaneRecipe
{
    public ShapedArcaneVoidJar(Identifier group, String res, int vis, AspectList crystals, ItemStack result, Object... recipe) {
        super(new net.minecraft.world.item.Item.Properties());
    }
    
    @Override
    public ItemStack getCraftingResult(CraftingContainer var1) {
        CompoundTag nbt = null;
        for (int a = 0; a < var1.getContainerSize(); ++a) {
            if (Block.byItem(var1.getItem(a).getItem()) == BlocksTC.jarNormal) {
                nbt = var1.getItem(a).get();
                break;
            }
        }
        ItemStack res = super.getCraftingResult(var1);
        res.put(nbt);
        return res;
    }
}
