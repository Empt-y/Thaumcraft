package thaumcraft.common.items.consumables;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemChunksEdible extends Item /* ItemFood removed */ implements IThaumcraftItems
{
    public int itemUseDuration;
    private String[] variants;
    
    public ItemChunksEdible() {
        super(new net.minecraft.world.item.Item.Properties());
        variants = new String[] { "beef", "chicken", "pork", "fish", "rabbit", "mutton" };
        itemUseDuration = 10;
        /* setMaxDamage removed - use Item.Properties */;
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public int getMaxItemUseDuration(ItemStack stack1) {
        return itemUseDuration;
    }
    
    public void getSubItems(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == null /* SEARCH tab removed */) {
            for (int a = 0; a < variants.length; ++a) {
                items.add(new ItemStack(this.asItem(), 1));
            }
        }
    }
    
    public String getUnlocalizedName(ItemStack itemStack) {
        if (hasSubtypes && itemStack.getMetadata() < variants.length && variants[itemStack.getMetadata()] != "chunk") {
            return String.format(super.getName() + ".%s", variants[itemStack.getMetadata()]);
        }
        return super.getUnlocalizedName(itemStack);
    }
    
    public String[] getVariantNames() {
        return variants;
    }
    
    public int[] getVariantMeta() {
        return new int[] { 0, 1, 2, 3, 4, 5 };
    }
    
    public Item getItem() {
        return this;
    }
    
    public Object /* ItemMeshDefinition removed */ getCustomMesh() {
        return null;
    }
    
    public Object /* ModelResourceLocation removed */ getCustomModelResourceLocation(String variant) {
        if (variant.equals("chunk")) {
            return null /* removed */;
        }
        return null /* removed */;
    }
}
