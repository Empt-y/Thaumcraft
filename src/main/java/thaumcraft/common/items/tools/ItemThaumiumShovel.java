package thaumcraft.common.items.tools;
import net.minecraft.world.item.ToolMaterial;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemThaumiumShovel extends ShovelItem implements IThaumcraftItems
{
    public ItemThaumiumShovel(ToolMaterial enumtoolmaterial) {
        super(enumtoolmaterial, -3, -1.0f, new net.minecraft.world.item.Item.Properties());
        // Entity requires EntityType; use factory method
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public Item getItem() {
        return this;
    }
    
    public String[] getVariantNames() {
        return new String[] { "normal" };
    }
    
    public int[] getVariantMeta() {
        return new int[] { 0 };
    }    
    public Object /* ModelResourceLocation removed */ getCustomModelResourceLocation(String variant) {
        return null /* removed */;
    }
    
    public Set<String> getToolClasses(ItemStack stack) {
        return ImmutableSet.of("shovel");
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.ingots, 1)) || false;
    }
}
