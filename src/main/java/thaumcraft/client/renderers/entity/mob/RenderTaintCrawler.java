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
 * Placeholder renderer for TaintCrawler using spider model geometry.
 * TaintCrawler is a low-profile tainted creature; proper custom model needed for final visuals.
 */
@OnlyIn(Dist.CLIENT)
public class RenderTaintCrawler extends MobRenderer<Monster, LivingEntityRenderState, SpiderModel> {
    private static final Identifier TEX = Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/taint_crawler.png");

    public RenderTaintCrawler(EntityRendererProvider.Context ctx) {
        super(ctx, new SpiderModel(ctx.bakeLayer(ModelLayers.SPIDER)), 0.7f);
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
