package thaumcraft.client.lib.obj;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Vector3;
import org.joml.Vector2f;


public class MeshModel
{
    public List<org.joml.Vector3f> positions;
    public List<org.joml.Vector3f> normals;
    public List<Vector2f> texCoords;
    public List<MeshPart> parts;
    
    public MeshModel() {
        parts = new ArrayList<MeshPart>();
    }
    
    public MeshModel clone() {
        MeshModel mm = new MeshModel();
        mm.parts = new ArrayList<MeshPart>();
        for (MeshPart mp : parts) {
            mm.parts.add(mp);
        }
        if (positions != null) {
            mm.positions = new ArrayList<org.joml.Vector3f>();
            for (org.joml.Vector3f mp2 : positions) {
                mm.positions.add(new org.joml.Vector3f(mp2));
            }
        }
        if (normals != null) {
            mm.normals = new ArrayList<org.joml.Vector3f>();
            for (org.joml.Vector3f mp2 : normals) {
                mm.normals.add(new org.joml.Vector3f(mp2));
            }
        }
        if (texCoords != null) {
            mm.texCoords = new ArrayList<Vector2f>();
            for (Vector2f mp3 : texCoords) {
                mm.texCoords.add(new Vector2f(mp3));
            }
        }
        return mm;
    }
    
    public void rotate(double d, Vector3 axis, Vector3 offset) {
        Rotation r = new Rotation(d, axis);
        List<org.joml.Vector3f> p = new ArrayList<org.joml.Vector3f>();
        for (org.joml.Vector3f v : positions) {
            Vector3 vec = new Vector3(v.x, v.y, v.z);
            r.apply(vec);
            vec = vec.add(offset);
            p.add(new org.joml.Vector3f((float)vec.getX(), (float)vec.getY(), (float)vec.getZ()));
        }
        positions = p;
    }
    
    public void addPosition(float x, float y, float z) {
        if (positions == null) {
            positions = new ArrayList<org.joml.Vector3f>();
        }
        positions.add(new org.joml.Vector3f(x, y, z));
    }
    
    public void addNormal(float x, float y, float z) {
        if (normals == null) {
            normals = new ArrayList<org.joml.Vector3f>();
        }
        normals.add(new org.joml.Vector3f(x, y, z));
    }
    
    public void addTexCoords(float x, float y) {
        if (texCoords == null) {
            texCoords = new ArrayList<Vector2f>();
        }
        texCoords.add(new Vector2f(x, y));
    }
    
    public void addPart(MeshPart part) {
        parts.add(part);
    }
    
    public void addPart(MeshPart part, int ti) {
        parts.add(new MeshPart(part, ti));
    }
    
    private int getColorValue(org.joml.Vector3f color) {
        int r = (int)color.x;
        int g = (int)color.y;
        int b = (int)color.z;
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
    
    public List<BakedQuad> bakeModel(ModelManager manager) {
        // manager.getTextureMap() removed in modern MC; stub
        return new ArrayList<BakedQuad>();
    }
    
    public List<BakedQuad> bakeModel(TextureAtlasSprite sprite) {
        List<BakedQuad> bakeList = new ArrayList<BakedQuad>();
        for (int j = 0; j < parts.size(); ++j) {
            MeshPart part = parts.get(j);
            int color = -1;
            for (int i = 0; i < part.indices.size(); i += 4) {
                BakedQuad quad = bakeQuad(part, i, sprite, color);
                bakeList.add(quad);
            }
        }
        return bakeList;
    }
    
    private BakedQuad bakeQuad(MeshPart part, int startIndex, TextureAtlasSprite sprite, int color) {
        // FaceBakery.getFacingFromVertexData and old BakedQuad constructor removed; stub
        return null;
    }

    private static void storeVertexData(int[] faceData, int storeIndex, org.joml.Vector3f position, Vector2f faceUV, TextureAtlasSprite sprite, int shadeColor) {
        // sprite.getInterpolatedU/V removed in modern MC; stub
    }
}
