package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Renderer for EntityFluxRift.
 * EntityFluxRift extends Entity (not LivingEntity/Mob), and its original renderer
 * used a custom animated geometry. Using NoopRenderer as placeholder until
 * a proper distortion/rift effect renderer is implemented.
 */
@OnlyIn(Dist.CLIENT)
public class RenderFluxRift extends NoopRenderer<Entity> {

    public RenderFluxRift(EntityRendererProvider.Context ctx) {
        super(ctx);
    }
}
