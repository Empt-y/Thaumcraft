package thaumcraft.client.lib.obj;
import net.minecraft.resources.Identifier;


public class ObjModelLoader implements IModelCustomLoader
{
    private static String[] types;
    
    @Override
    public String getType() {
        return "OBJ model";
    }
    
    @Override
    public String[] getSuffixes() {
        return ObjModelLoader.types;
    }
    
    @Override
    public IModelCustom loadInstance(Identifier resource) throws WavefrontObject.ModelFormatException {
        return new WavefrontObject(resource);
    }
    
    static {
        types = new String[] { "obj" };
    }
}
