package thaumcraft.common.items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.config.ConfigItems;


public class ItemTCBase extends Item implements IThaumcraftItems
{
    protected String BASE_NAME;
    protected String[] VARIANTS;
    protected int[] VARIANTS_META;

    public ItemTCBase(String name, String... variants) {
        super(thaumcraft.common.config.TCItemInit.take());
        BASE_NAME = name;
        if (variants.length == 0) {
            VARIANTS = new String[] { name };
        } else {
            VARIANTS = variants;
        }
        VARIANTS_META = new int[VARIANTS.length];
        for (int m = 0; m < VARIANTS.length; ++m) {
            VARIANTS_META[m] = m;
        }
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }

    public String getDescriptionId(ItemStack stack) {
        return "item.thaumcraft." + BASE_NAME;
    }

    public Item getItem() {
        return this;
    }

    public String[] getVariantNames() {
        return VARIANTS;
    }

    public int[] getVariantMeta() {
        return VARIANTS_META;
    }

    public Object getCustomModelResourceLocation(String variant) {
        return null;
    }
}
