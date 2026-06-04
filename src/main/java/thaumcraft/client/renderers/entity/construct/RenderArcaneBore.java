package thaumcraft.client.renderers.entity.construct;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Placeholder renderer for EntityArcaneBore.
 * The arcane bore needs a custom animated drill-head model; using NoopRenderer
 * until that model is implemented.
 */
@OnlyIn(Dist.CLIENT)
public class RenderArcaneBore extends NoopRenderer<Entity> {
    public RenderArcaneBore(EntityRendererProvider.Context ctx) { super(ctx); }
}
