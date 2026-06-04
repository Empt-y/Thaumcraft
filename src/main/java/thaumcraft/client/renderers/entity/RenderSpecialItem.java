package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Renderer for EntitySpecialItem — delegates to vanilla ItemEntityRenderer
 * since EntitySpecialItem extends ItemEntity.
 */
@OnlyIn(Dist.CLIENT)
public class RenderSpecialItem extends ItemEntityRenderer {

    public RenderSpecialItem(EntityRendererProvider.Context ctx) {
        super(ctx);
    }
}
