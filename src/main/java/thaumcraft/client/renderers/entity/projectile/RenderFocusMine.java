package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/** Placeholder renderer for FocusMine proximity mine entity. */
@OnlyIn(Dist.CLIENT)
public class RenderFocusMine extends NoopRenderer<Entity> {
    public RenderFocusMine(EntityRendererProvider.Context ctx) { super(ctx); }
}
