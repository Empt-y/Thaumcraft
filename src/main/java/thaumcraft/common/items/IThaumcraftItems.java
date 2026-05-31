package thaumcraft.common.items;
import net.minecraft.world.item.Item;


public interface IThaumcraftItems
{
    Item getItem();
    
    String[] getVariantNames();
    
    int[] getVariantMeta();
    
    default Object getCustomMesh() { return null; }

    default Object getCustomModelResourceLocation(String p0) { return null; }
}
