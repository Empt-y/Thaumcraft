package thaumcraft.client.renderers.entity.construct;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Placeholder renderer for EntityTurretCrossbowAdvanced.
 * Using NoopRenderer as placeholder; proper block-entity style model needed.
 */
@OnlyIn(Dist.CLIENT)
public class RenderTurretCrossbowAdvanced extends NoopRenderer<Entity> {
    public RenderTurretCrossbowAdvanced(EntityRendererProvider.Context ctx) { super(ctx); }
}
