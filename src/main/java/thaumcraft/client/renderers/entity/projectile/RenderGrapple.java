package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/** Placeholder renderer for Grapple hook projectile. */
@OnlyIn(Dist.CLIENT)
public class RenderGrapple extends NoopRenderer<Entity> {
    public RenderGrapple(EntityRendererProvider.Context ctx) { super(ctx); }
}
