package thaumcraft.client.renderers.entity.mob;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.spider.SpiderModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Placeholder renderer for the Eldritch Crab using spider model geometry.
 * The spider's multi-legged body shape approximates a crab.
 */
@OnlyIn(Dist.CLIENT)
public class RenderEldritchCrab extends MobRenderer<Monster, LivingEntityRenderState, SpiderModel> {
    private static final Identifier TEX = Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/eldritch_crab.png");

    public RenderEldritchCrab(EntityRendererProvider.Context ctx) {
        super(ctx, new SpiderModel(ctx.bakeLayer(ModelLayers.SPIDER)), 0.8f);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEX;
    }
}
