package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/** Placeholder renderer for EldritchOrb projectile. */
@OnlyIn(Dist.CLIENT)
public class RenderEldritchOrb extends NoopRenderer<Entity> {
    public RenderEldritchOrb(EntityRendererProvider.Context ctx) { super(ctx); }
}
