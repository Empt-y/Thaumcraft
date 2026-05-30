package thaumcraft.client.lib;
import java.awt.Color;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;


// PositionTextureVertex was removed in MC 26; this class is a stub pending a rendering rewrite.
public class TexturedQuadTC
{
    public Object[] vertexPositions;
    public int nVertices;
    private boolean invertNormal;
    private boolean flipped;

    public TexturedQuadTC(Object[] vertices) {
        flipped = false;
        vertexPositions = vertices;
        nVertices = vertices.length;
    }

    public TexturedQuadTC(Object[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight) {
        this(vertices);
        // UV remapping removed — PositionTextureVertex gone in MC 26
    }

    public void flipFace() {
        flipped = true;
        Object[] copy = new Object[vertexPositions.length];
        for (int i = 0; i < vertexPositions.length; ++i) {
            copy[i] = vertexPositions[vertexPositions.length - i - 1];
        }
        vertexPositions = copy;
    }

    public void draw(BufferBuilder renderer, float scale, int bright, int color, float alpha) {
        // TODO: rewrite with modern rendering API
    }

    public void draw(BufferBuilder renderer, float scale, int bright, int[] color, float[] alpha) {
        // TODO: rewrite with modern rendering API
    }
}
