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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.crafting.TileCrucible;

public class TileCrucibleRenderer implements BlockEntityRenderer<TileCrucible, TileCrucibleRenderer.State> {

    @Override
    public State createRenderState() { return new State(); }

    @Override
    public void extractRenderState(TileCrucible be, State state, float pt, Vec3 cam,
            ModelFeatureRenderer.CrumblingOverlay bp) {
        BlockEntityRenderState.extractBase(be, state, bp);
        state.aspects = (be.aspects != null) ? be.aspects.copy() : new AspectList();
        state.heat = be.heat;
    }

    @Override
    public void submit(State state, PoseStack ps, SubmitNodeCollector collector, CameraRenderState cam) {
        if (state.aspects == null || state.aspects.size() == 0) return;

        Aspect[] aspectArr = state.aspects.getAspects();
        int count = Math.min(aspectArr.length, 8);

        for (int i = 0; i < count; i++) {
            Aspect a = aspectArr[i];
            if (a == null) continue;
            final Identifier tex = a.getImage();
            int color = a.getColor();
            final int fr = (color >> 16) & 0xFF;
            final int fg = (color >> 8) & 0xFF;
            final int fb = color & 0xFF;

            float angle = (float)(i * Math.PI * 2.0 / count);
            float ox = (float)(Math.cos(angle) * 0.3);
            float oz = (float)(Math.sin(angle) * 0.3);
            float oy = 0.7f;

            ps.pushPose();
            ps.translate(0.5 + ox, oy, 0.5 + oz);
            final float size = 0.06f;

            collector.submitCustomGeometry(ps, RenderTypes.entityTranslucent(tex), (p, vc) -> {
                addVertex(vc, p, -size, 0, -size, 0, 0, fr, fg, fb, 220);
                addVertex(vc, p,  size, 0, -size, 1, 0, fr, fg, fb, 220);
                addVertex(vc, p,  size, 0,  size, 1, 1, fr, fg, fb, 220);
                addVertex(vc, p, -size, 0,  size, 0, 1, fr, fg, fb, 220);
            });

            ps.popPose();
        }
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
        public AspectList aspects = new AspectList();
        public short heat = 0;
    }
}
