package thaumcraft.client.lib.obj;


public interface IModelCustom
{
    String getType();
    
    void renderAll();
    
    void renderOnly(String... p0);
    
    void renderPart(String p0);
    
    void renderAllExcept(String... p0);
    
    String[] getPartNames();
}
