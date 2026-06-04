package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/** Placeholder renderer for FocusCloud area-of-effect entity. */
@OnlyIn(Dist.CLIENT)
public class RenderFocusCloud extends NoopRenderer<Entity> {
    public RenderFocusCloud(EntityRendererProvider.Context ctx) { super(ctx); }
}
