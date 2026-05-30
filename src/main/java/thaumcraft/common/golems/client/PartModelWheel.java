package thaumcraft.common.golems.client;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;
import thaumcraft.common.golems.parts.GolemLegWheels;


public class PartModelWheel extends PartModel
{
    public PartModelWheel(Identifier objModel, Identifier objTexture, EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
    }
    
    @Override
    public void preRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) {
        if (partName.equals("wheel")) {
            float lastRot = 0.0f;
            if (GolemLegWheels.ani.containsKey(golem.getGolemEntity().getId())) {
                lastRot = GolemLegWheels.ani.get(golem.getGolemEntity().getId());
            }
            /* TODO: use PoseStack */ // RenderSystem.translate(0.0, -0.375, 0.0);
            /* TODO: use PoseStack */ // RenderSystem.rotate(lastRot, -1.0f, 0.0f, 0.0f);
        }
    }
}
