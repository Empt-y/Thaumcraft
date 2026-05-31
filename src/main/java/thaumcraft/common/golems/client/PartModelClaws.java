package thaumcraft.common.golems.client;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;


public class PartModelClaws extends PartModel
{
    float f;
    
    public PartModelClaws(Identifier objModel, Identifier objTexture, EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
        f = 0.0f;
    }
    
    @Override
    public void preRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) {
        if (partName.startsWith("claw")) {
            f = 0.0f;
            float swingFrac = golem.getGolemEntity().swinging ? Math.max(0, 1.0f - golem.getGolemEntity().swingTime / 6.0f) : 0.0f;
            f = swingFrac * 4.1f;
            f *= f;
            /* TODO: use PoseStack */ // RenderSystem.translate(0.0, -0.2, 0.0);
            /* TODO: use PoseStack */ // RenderSystem.rotate(f, partName.endsWith("1") ? 1.0f : -1.0f, 0.0f, 0.0f);
        }
    }
}
