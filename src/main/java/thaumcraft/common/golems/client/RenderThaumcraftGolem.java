package thaumcraft.common.golems.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import thaumcraft.common.golems.EntityThaumcraftGolem;


public class RenderThaumcraftGolem extends EntityRenderer<EntityThaumcraftGolem, EntityRenderState> {
    public RenderThaumcraftGolem(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void extractRenderState(EntityThaumcraftGolem entity, EntityRenderState reusable, float partialTick) {
        super.extractRenderState(entity, reusable, partialTick);
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return Identifier.fromNamespaceAndPath("thaumcraft", "textures/entity/golem/base.png");
    }
}
