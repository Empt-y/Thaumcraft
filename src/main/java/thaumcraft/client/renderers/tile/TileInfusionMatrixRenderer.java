package thaumcraft.client.renderers.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;

public class TileInfusionMatrixRenderer implements BlockEntityRenderer<TileInfusionMatrix, TileInfusionMatrixRenderer.State> {
    @Override
    public State createRenderState() { return new State(); }

    @Override
    public void extractRenderState(TileInfusionMatrix be, State state, float pt, Vec3 cam,
            ModelFeatureRenderer.CrumblingOverlay bp) {
        BlockEntityRenderState.extractBase(be, state, bp);
    }

    @Override
    public void submit(State state, PoseStack ps, SubmitNodeCollector c, CameraRenderState cam) {}

    public static class State extends BlockEntityRenderState {}
}
