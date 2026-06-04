package thaumcraft.client.renderers.entity.mob;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.entities.monster.EntityWisp;

/**
 * Placeholder renderer for EntityWisp using slime model geometry.
 * The wisp is rendered as a small glowing orb. A proper custom model
 * would be needed for full visual fidelity.
 */
@OnlyIn(Dist.CLIENT)
public class RenderWisp extends MobRenderer<EntityWisp, LivingEntityRenderState, SlimeModel> {
    private static final Identifier TEX = Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/wisp.png");

    public RenderWisp(EntityRendererProvider.Context ctx) {
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
