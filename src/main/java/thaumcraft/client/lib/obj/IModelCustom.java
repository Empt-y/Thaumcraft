package thaumcraft.client.lib.obj;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


public interface IModelCustom
{
    String getType();
    
    @OnlyIn(Dist.CLIENT)
    void renderAll();
    
    @OnlyIn(Dist.CLIENT)
    void renderOnly(String... p0);
    
    @OnlyIn(Dist.CLIENT)
    void renderPart(String p0);
    
    @OnlyIn(Dist.CLIENT)
    void renderAllExcept(String... p0);
    
    @OnlyIn(Dist.CLIENT)
    String[] getPartNames();
}
