package thaumcraft.common.golems.client;
import java.util.HashMap;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;


public class PartModelBreakers extends PartModel
{
    private HashMap<Integer, Float[]> ani;
    
    public PartModelBreakers(Identifier objModel, Identifier objTexture, EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
        ani = new HashMap<Integer, Float[]>();
    }
    
    @Override
    public void preRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) {
        if (partName.equals("grinder")) {
            float lastSpeed = 0.0f;
            float lastRot = 0.0f;
            if (ani.containsKey(golem.getGolemEntity().getId())) {
                lastSpeed = ani.get(golem.getGolemEntity().getId())[0];
                lastRot = ani.get(golem.getGolemEntity().getId())[1];
            }
            float f = Math.max(lastSpeed, golem.getGolemEntity().getSwingProgress(partialTicks) * 20.0f);
            float rot = lastRot + f;
            lastSpeed = f * 0.99f;
            ani.put(golem.getGolemEntity().getId(), new Float[] { lastSpeed, rot });
            /* TODO: use PoseStack */ // RenderSystem.translate(0.0, -0.34, 0.0);
            /* TODO: use PoseStack */ // RenderSystem.rotate((golem.getGolemEntity().tickCount + partialTicks) / 2.0f + rot + ((side == EnumLimbSide.LEFT) ? 22 : 0), (side == EnumLimbSide.LEFT) ? -1.0f : 1.0f, 0.0f, 0.0f);
        }
    }
}
