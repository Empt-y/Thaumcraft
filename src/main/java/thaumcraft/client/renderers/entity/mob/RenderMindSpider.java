package thaumcraft.client.renderers.entity.mob;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.entities.monster.EntityMindSpider;

@OnlyIn(Dist.CLIENT)
public class RenderMindSpider extends SpiderRenderer<EntityMindSpider> {
    private static final Identifier TEX = Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/mind_spider.png");

    public RenderMindSpider(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEX;
    }
}
