package thaumcraft.client.lib.obj;
import net.minecraft.resources.Identifier;


public interface IModelCustomLoader
{
    String getType();
    
    String[] getSuffixes();
    
    IModelCustom loadInstance(Identifier p0) throws WavefrontObject.ModelFormatException;
}
