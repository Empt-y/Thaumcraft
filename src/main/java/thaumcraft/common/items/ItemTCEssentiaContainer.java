package thaumcraft.common.items;
import net.minecraft.world.item.Item;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.common.config.ConfigItems;


public class ItemTCEssentiaContainer extends ItemGenericEssentiaContainer implements IEssentiaContainerItem, IThaumcraftItems
{
    private String BASE_NAME;
    protected String[] VARIANTS;
    protected int[] VARIANTS_META;
    
    public ItemTCEssentiaContainer(String name, int base, String... variants) {
        super(base);
        
        BASE_NAME = name;
        if (variants.length == 0) {
            VARIANTS = new String[] { name };
        }
        else {
            VARIANTS = variants;
        }
        VARIANTS_META = new int[VARIANTS.length];
        for (int m = 0; m < VARIANTS.length; ++m) {
            VARIANTS_META[m] = m;
        }
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    @Override
    public Item getItem() {
        return this;
    }
    
    @Override
    public String[] getVariantNames() {
        return VARIANTS;
    }
    
    @Override
    public int[] getVariantMeta() {
        return VARIANTS_META;
    }
    
    @Override
    public Object /* ModelResourceLocation removed */ getCustomModelResourceLocation(String variant) {
        if (variant.equals(BASE_NAME)) {
            return null /* removed */;
        }
        return null /* removed */;
    }
}
