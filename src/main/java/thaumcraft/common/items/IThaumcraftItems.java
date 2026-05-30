package thaumcraft.common.items;
import net.minecraft.world.item.Item;


public interface IThaumcraftItems
{
    Item getItem();
    
    String[] getVariantNames();
    
    int[] getVariantMeta();
    
    Object /* ItemMeshDefinition removed */ getCustomMesh();
    
    Object /* ModelResourceLocation removed */ getCustomModelResourceLocation(String p0);
}
