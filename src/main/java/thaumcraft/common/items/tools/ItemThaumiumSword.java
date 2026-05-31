package thaumcraft.common.items.tools;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.item.Item /* SwordItem removed */; // removed
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemThaumiumSword extends Item /* ItemSword removed */ implements IThaumcraftItems
{
    public ItemThaumiumSword(ToolMaterial enumtoolmaterial) {
        super(new net.minecraft.world.item.Item.Properties());
        // ItemTCBase constructor
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
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.ingots, 1)) || false;
    }
}
