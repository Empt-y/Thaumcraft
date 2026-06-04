package thaumcraft.client.renderers.entity.mob;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Placeholder renderer for TaintSwarm using slime model geometry.
 * The swarm is a floating cloud-like mob; proper custom model needed for final visuals.
 */
@OnlyIn(Dist.CLIENT)
public class RenderTaintSwarm extends MobRenderer<Monster, LivingEntityRenderState, SlimeModel> {
    private static final Identifier TEX = Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/taint_swarm.png");

    public RenderTaintSwarm(EntityRendererProvider.Context ctx) {
        super(ctx, new SlimeModel(ctx.bakeLayer(ModelLayers.SLIME)), 0.25f);
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
