package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Intentionally invisible renderer for projectiles that should have no visual
 * (BottleTaint, Alumentum use particle effects instead of entity geometry).
 */
@OnlyIn(Dist.CLIENT)
public class RenderNoProjectile extends NoopRenderer<Entity> {
    public RenderNoProjectile(EntityRendererProvider.Context ctx) { super(ctx); }
}
