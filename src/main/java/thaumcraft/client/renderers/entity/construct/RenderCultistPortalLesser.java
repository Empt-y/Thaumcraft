package thaumcraft.client.renderers.entity.construct;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Placeholder renderer for the Lesser Cultist Portal entity.
 * Using NoopRenderer until the portal animation renderer is implemented.
 */
@OnlyIn(Dist.CLIENT)
public class RenderCultistPortalLesser extends NoopRenderer<Entity> {
    public static final Identifier PORTAL_TEX = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/cultist_portal_lesser.png");

    public RenderCultistPortalLesser(EntityRendererProvider.Context ctx) { super(ctx); }
}
