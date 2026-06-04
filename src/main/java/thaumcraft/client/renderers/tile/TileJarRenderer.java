package thaumcraft.client.renderers.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.TileJarFillable;

public class TileJarRenderer implements BlockEntityRenderer<TileJarFillable, TileJarRenderer.State> {

    @Override
    public State createRenderState() { return new State(); }

    @Override
    public void extractRenderState(TileJarFillable be, State state, float pt, Vec3 cam,
            ModelFeatureRenderer.CrumblingOverlay bp) {
        BlockEntityRenderState.extractBase(be, state, bp);
        state.aspect = be.aspect;
        state.amount = be.amount;
    }

    @Override
    public void submit(State state, PoseStack ps, SubmitNodeCollector collector, CameraRenderState cam) {
        if (state.aspect == null || state.amount <= 0) return;

        Identifier tex = state.aspect.getImage();
        int color = state.aspect.getColor();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        float fill = Math.min(1.0f, state.amount / 64.0f);

        ps.pushPose();
        ps.translate(0.5, 0.3 + fill * 0.35, 0.5);
        float size = 0.1f;
        PoseStack.Pose pose = ps.last();

        collector.submitCustomGeometry(ps, RenderTypes.entityTranslucent(tex), (p, vc) -> {
            // Simple flat quad facing up
            addVertex(vc, p, -size, 0, -size, 0, 0, r, g, b, 200);
            addVertex(vc, p,  size, 0, -size, 1, 0, r, g, b, 200);
            addVertex(vc, p,  size, 0,  size, 1, 1, r, g, b, 200);
            addVertex(vc, p, -size, 0,  size, 0, 1, r, g, b, 200);
        });

        ps.popPose();
    }

    private static void addVertex(VertexConsumer vc, PoseStack.Pose pose,
            float x, float y, float z, float u, float v,
            int r, int g, int b, int a) {
        vc.addVertex(pose, x, y, z)
          .setColor(r, g, b, a)
          .setUv(u, v)
          .setOverlay(0)
          .setLight(0xF000F0)
          .setNormal(0, 1, 0);
    }

    public static class State extends BlockEntityRenderState {
        public Aspect aspect = null;
        public int amount = 0;
    }
}
