package thaumcraft.client.lib.obj;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;


public class AdvancedModelLoader
{
    private static Map<String, IModelCustomLoader> instances;
    
    public static void registerModelHandler(IModelCustomLoader modelHandler) {
        for (String suffix : modelHandler.getSuffixes()) {
            AdvancedModelLoader.instances.put(suffix, modelHandler);
        }
    }
    
    public static IModelCustom loadModel(Identifier resource) throws IllegalArgumentException, WavefrontObject.ModelFormatException {
        String name = resource.getPath();
        int i = name.lastIndexOf(46);
        if (i == -1) {
            LogManager.getLogger("THAUMCRAFT").error("The resource name %s is not valid", resource);
            throw new IllegalArgumentException("The resource name is not valid");
        }
        String suffix = name.substring(i + 1);
        IModelCustomLoader loader = AdvancedModelLoader.instances.get(suffix);
        if (loader == null) {
            LogManager.getLogger("THAUMCRAFT").error("The resource name %s is not supported", resource);
            throw new IllegalArgumentException("The resource name is not supported");
        }
        return loader.loadInstance(resource);
    }
    
    public static Collection<String> getSupportedSuffixes() {
        return AdvancedModelLoader.instances.keySet();
    }
    
    static {
        AdvancedModelLoader.instances = Maps.newHashMap();
        registerModelHandler(new ObjModelLoader());
    }
}
