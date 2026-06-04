package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Renderer for golem dart projectiles, reusing the vanilla arrow model.
 */
@OnlyIn(Dist.CLIENT)
public class RenderDart extends ArrowRenderer<AbstractArrow, ArrowRenderState> {
    private static final Identifier TEX = Identifier.withDefaultNamespace("textures/entity/projectiles/arrow.png");

    public RenderDart(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    @Override
    protected Identifier getTextureLocation(ArrowRenderState state) {
        return TEX;
    }
}
