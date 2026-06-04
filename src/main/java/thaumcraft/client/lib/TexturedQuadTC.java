package thaumcraft.client.lib;
import java.awt.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;


/**
 * Textured quad used in world-space rendering.
 * Each vertex is stored as float[]{x, y, z, u, v}.
 * The old Object[] constructor still works — pass float[] elements.
 */
public class TexturedQuadTC
{
    public float[][] vertexPositions; // each entry: {x, y, z, u, v}
    public int nVertices;
    private boolean invertNormal;
    private boolean flipped;

    public TexturedQuadTC(float[][] vertices) {
        flipped = false;
        vertexPositions = vertices;
        nVertices = vertices.length;
    }

    /**
     * Legacy Object[] constructor — accepts float[][] elements cast as Object[]; re-casts internally.
     */
    public TexturedQuadTC(Object[] vertices) {
        flipped = false;
        vertexPositions = new float[vertices.length][];
        for (int i = 0; i < vertices.length; i++) {
            if (vertices[i] instanceof float[]) {
                vertexPositions[i] = (float[]) vertices[i];
            } else {
                vertexPositions[i] = new float[]{0, 0, 0, 0, 0};
            }
        }
        nVertices = vertexPositions.length;
    }

    public TexturedQuadTC(Object[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight) {
        this(vertices);
        // UV remapping preserved — remap first 4 vertices
        if (vertexPositions.length >= 4) {
            vertexPositions[0] = new float[]{ vertexPositions[0][0], vertexPositions[0][1], vertexPositions[0][2], texcoordU2 / textureWidth, texcoordV1 / textureHeight };
            vertexPositions[1] = new float[]{ vertexPositions[1][0], vertexPositions[1][1], vertexPositions[1][2], texcoordU1 / textureWidth, texcoordV1 / textureHeight };
            vertexPositions[2] = new float[]{ vertexPositions[2][0], vertexPositions[2][1], vertexPositions[2][2], texcoordU1 / textureWidth, texcoordV2 / textureHeight };
            vertexPositions[3] = new float[]{ vertexPositions[3][0], vertexPositions[3][1], vertexPositions[3][2], texcoordU2 / textureWidth, texcoordV2 / textureHeight };
        }
    }

    public void flipFace() {
        flipped = true;
        float[][] copy = new float[vertexPositions.length][];
        for (int i = 0; i < vertexPositions.length; ++i) {
            copy[i] = vertexPositions[vertexPositions.length - i - 1];
        }
        vertexPositions = copy;
    }

    /**
     * Render this quad using the UtilsFX world-space context.
     * @param renderer ignored (legacy BufferBuilder param, unused in MC 26)
     * @param scale    position scale factor
     * @param bright   packed lightmap value (0xF000F0 = full-bright)
     * @param color    ARGB packed color
     * @param alpha    opacity
     */
    public void draw(Object renderer, float scale, int bright, int color, float alpha) {
        if (UtilsFX.currentCollector == null || UtilsFX.currentPoseStack == null) return;
        Identifier tex = UtilsFX.currentTexture != null ? UtilsFX.currentTexture : UtilsFX.nodeTexture;
        if (tex == null) return;
        java.awt.Color c = new java.awt.Color(color, true);
        final float r = c.getRed() / 255f, g = c.getGreen() / 255f, b = c.getBlue() / 255f, a = alpha;
        final int brt = bright;
        final float sc = scale;
        final float[][] verts = vertexPositions;
        UtilsFX.currentCollector.submitCustomGeometry(UtilsFX.currentPoseStack, RenderTypes.entityTranslucent(tex), (pose, buf) -> {
            for (int i = 0; i < Math.min(4, verts.length); i++) {
                float[] v = verts[i];
                buf.addVertex(pose, v[0] * sc, v[1] * sc, v[2] * sc)
                   .setColor(r, g, b, a)
                   .setUv(v.length > 3 ? v[3] : 0f, v.length > 4 ? v[4] : 0f)
                   .setOverlay(0).setLight(brt).setNormal(0, 0, 1);
            }
        });
    }

    /**
     * Per-vertex color/alpha variant.
     */
    public void draw(Object renderer, float scale, int bright, int[] color, float[] alpha) {
        if (UtilsFX.currentCollector == null || UtilsFX.currentPoseStack == null) return;
        Identifier tex = UtilsFX.currentTexture != null ? UtilsFX.currentTexture : UtilsFX.nodeTexture;
        if (tex == null) return;
        final int brt = bright;
        final float sc = scale;
        final float[][] verts = vertexPositions;
        final int[] col = color;
        final float[] alp = alpha;
        final boolean fl = flipped;
        UtilsFX.currentCollector.submitCustomGeometry(UtilsFX.currentPoseStack, RenderTypes.entityTranslucent(tex), (pose, buf) -> {
            for (int i = 0; i < Math.min(4, verts.length); i++) {
                int idx = fl ? (3 - i) : i;
                java.awt.Color c = new java.awt.Color(col[idx], true);
                float[] v = verts[i];
                buf.addVertex(pose, v[0] * sc, v[1] * sc, v[2] * sc)
                   .setColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, alp[idx])
                   .setUv(v.length > 3 ? v[3] : 0f, v.length > 4 ? v[4] : 0f)
                   .setOverlay(0).setLight(brt).setNormal(0, 0, 1);
            }
        });
    }

    /**
     * Modern direct-submission variant with explicit VertexConsumer.
     */
    public void draw(VertexConsumer vc, PoseStack.Pose pose, float scale, int bright, int color, float alpha) {
        java.awt.Color c = new java.awt.Color(color, true);
        float r = c.getRed() / 255f, g = c.getGreen() / 255f, b = c.getBlue() / 255f;
        for (int i = 0; i < Math.min(4, vertexPositions.length); i++) {
            float[] v = vertexPositions[i];
            vc.addVertex(pose, v[0] * scale, v[1] * scale, v[2] * scale)
              .setColor(r, g, b, alpha)
              .setUv(v.length > 3 ? v[3] : 0f, v.length > 4 ? v[4] : 0f)
              .setOverlay(0).setLight(bright).setNormal(0, 0, 1);
        }
    }
}
