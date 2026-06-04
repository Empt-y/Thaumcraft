package thaumcraft.client.renderers.entity.construct;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Placeholder renderer for EntityTurretCrossbow.
 * The crossbow turret needs a custom block-like model; using NoopRenderer
 * since the turret is also rendered as a block entity in the world.
 */
@OnlyIn(Dist.CLIENT)
public class RenderTurretCrossbow extends NoopRenderer<Entity> {
    public RenderTurretCrossbow(EntityRendererProvider.Context ctx) { super(ctx); }
}
