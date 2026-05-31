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
        super(group, res, vis, crystals, result, recipe);
    }
    
    @Override
    public ItemStack assemble(net.minecraft.world.item.crafting.CraftingInput var1) {
        net.minecraft.world.item.component.CustomData nbt = null;
        for (int a = 0; a < var1.size(); ++a) {
            if (Block.byItem(var1.getItem(a).getItem()) == BlocksTC.jarNormal) {
                nbt = var1.getItem(a).get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
                break;
            }
        }
        ItemStack res = super.assemble(var1);
        if (nbt != null) {
            res.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, nbt);
        }
        return res;
    }
}
