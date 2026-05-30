package net.minecraft.client.model;

/** Stub - PositionTextureVertex was removed in MC 1.20+ */
public class PositionTextureVertex {
    public float texturePositionX;
    public float texturePositionY;
    public float vector3D_x;
    public float vector3D_y;
    public float vector3D_z;
    
    public PositionTextureVertex(float x, float y, float z, float u, float v) {
        this.vector3D_x = x;
        this.vector3D_y = y;
        this.vector3D_z = z;
        this.texturePositionX = u;
        this.texturePositionY = v;
    }
    
    public PositionTextureVertex setTextureUV(float u, float v) {
        return new PositionTextureVertex(vector3D_x, vector3D_y, vector3D_z, u, v);
    }
}
