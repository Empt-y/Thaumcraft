package thaumcraft.common.golems.client;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
// import net.minecraft.client.renderer.block.model.Object /* ItemCameraTransforms removed */; // removed
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;


public class PartModelHauler extends PartModel
{
    public PartModelHauler(Identifier objModel, Identifier objTexture, EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
    }
    
    @Override
    public void postRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) {
        if (golem.getCarrying().size() > 1 && golem.getCarrying().get(1) != null) {
            ItemStack itemstack = golem.getCarrying().get(1);
            if (itemstack != null && !itemstack.isEmpty()) {
                RenderSystem.pushMatrix();
                Item item = itemstack.getItem();
                Minecraft minecraft = Minecraft.getInstance();
                RenderSystem.scale(0.375, 0.375, 0.375);
                /* TODO: use PoseStack */ // RenderSystem.translate(0.0f, 0.33f, 0.825f);
                if (!(item instanceof BlockItem)) {
                    /* TODO: use PoseStack */ // RenderSystem.translate(0.0f, 0.0f, -0.25f);
                }
                minecraft.getItemRenderer().renderItem(golem.getGolemEntity(), itemstack.TransformType.HEAD);
                RenderSystem.popMatrix();
            }
        }
    }
}
