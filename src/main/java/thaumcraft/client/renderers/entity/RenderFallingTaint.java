package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Renderer for EntityFallingTaint.
 * EntityFallingTaint extends Entity and represents a falling taint block.
 * Using NoopRenderer as placeholder until a proper falling-block-style renderer
 * is implemented.
 */
@OnlyIn(Dist.CLIENT)
public class RenderFallingTaint extends NoopRenderer<Entity> {

    public RenderFallingTaint(EntityRendererProvider.Context ctx) {
        super(ctx);
    }
}
