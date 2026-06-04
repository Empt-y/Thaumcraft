package thaumcraft.client.renderers.entity.mob;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Renderer for Taintacle — uses NoopRenderer as placeholder since the tentacle
 * geometry requires a completely custom skeletal model.
 */
@OnlyIn(Dist.CLIENT)
public class RenderTaintacle extends NoopRenderer<Monster> {

    public RenderTaintacle(EntityRendererProvider.Context ctx) {
        super(ctx);
    }
}
