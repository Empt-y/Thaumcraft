package thaumcraft.client.renderers.entity.mob;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBrainyZombie extends ZombieRenderer {
    private static final Identifier TEX = Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/brainy_zombie.png");

    public RenderBrainyZombie(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTextureLocation(ZombieRenderState state) {
        return TEX;
    }
}
