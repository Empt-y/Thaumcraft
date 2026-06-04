package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/** Placeholder renderer for RiftBlast projectile. */
@OnlyIn(Dist.CLIENT)
public class RenderRiftBlast extends NoopRenderer<Entity> {
    public RenderRiftBlast(EntityRendererProvider.Context ctx) { super(ctx); }
}
