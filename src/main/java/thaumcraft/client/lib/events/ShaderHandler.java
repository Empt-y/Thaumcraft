package thaumcraft.client.lib.events;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;


public class ShaderHandler
{
    public static int warpVignette;
    public static int SHADER_DESAT = 0;
    public static int SHADER_BLUR = 1;
    public static int SHADER_HUNGER = 2;
    public static int SHADER_SUNSCORNED = 3;
    public static Identifier[] shader_resources;

    protected void checkShaders(net.neoforged.neoforge.event.tick.PlayerTickEvent event, Minecraft mc) {
        // TODO: rewrite with modern effect/shader API (isPotionActive, ShaderGroup all removed)
    }

    void setShader(Object target, int shaderId) {
        // OpenGlHelper removed; shaders disabled in this port
    }

    public void deactivateShader(int shaderId) {
        RenderEventHandler.shaderGroups.remove(shaderId);
    }

    static {
        ShaderHandler.warpVignette = 0;
        ShaderHandler.shader_resources = new Identifier[] {
            Identifier.fromNamespaceAndPath("minecraft", "shaders/post/desaturatetc.json"),
            Identifier.fromNamespaceAndPath("minecraft", "shaders/post/blurtc.json"),
            Identifier.fromNamespaceAndPath("minecraft", "shaders/post/hunger.json"),
            Identifier.fromNamespaceAndPath("minecraft", "shaders/post/sunscorned.json")
        };
    }
}
