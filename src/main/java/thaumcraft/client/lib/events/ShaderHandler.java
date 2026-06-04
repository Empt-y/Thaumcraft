package thaumcraft.client.lib.events;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import thaumcraft.common.lib.potions.PotionDeathGaze;
import thaumcraft.common.lib.potions.PotionBlurredVision;
import thaumcraft.common.lib.potions.PotionUnnaturalHunger;
import thaumcraft.common.lib.potions.PotionSunScorned;


public class ShaderHandler
{
    public static int warpVignette;
    public static int SHADER_DESAT = 0;
    public static int SHADER_BLUR = 1;
    public static int SHADER_HUNGER = 2;
    public static int SHADER_SUNSCORNED = 3;
    public static Identifier[] shader_resources;

    protected void checkShaders(net.neoforged.neoforge.event.tick.PlayerTickEvent event, Minecraft mc) {
        // ShaderGroup removed in MC 26; update warpVignette from player effects only
        Player player = mc.player;
        if (player == null) return;

        if (PotionDeathGaze.instance != null && player.hasEffect(Holder.direct(PotionDeathGaze.instance))) {
            ShaderHandler.warpVignette = 10;
        }
        // SHADER_BLUR, SHADER_HUNGER, SHADER_SUNSCORNED — post-process shaders not available in MC 26
        // (ShaderGroup API removed; these would require modern PostChain/RenderPipeline setup)
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
