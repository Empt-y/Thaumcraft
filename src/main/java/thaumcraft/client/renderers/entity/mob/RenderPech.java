package thaumcraft.client.renderers.entity.mob;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.zombie.ZombieModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Placeholder renderer for the Pech using zombie model geometry.
 * Pech is a small humanoid gnome-like creature; proper custom model needed for final visuals.
 */
@OnlyIn(Dist.CLIENT)
public class RenderPech extends MobRenderer<Monster, ZombieRenderState, ZombieModel<ZombieRenderState>> {
    private static final Identifier TEX = Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/pech.png");

    public RenderPech(EntityRendererProvider.Context ctx) {
        super(ctx, new ZombieModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5f);
    }

    @Override
    public ZombieRenderState createRenderState() {
        return new ZombieRenderState();
    }

    @Override
    public Identifier getTextureLocation(ZombieRenderState state) {
        return TEX;
    }
}
