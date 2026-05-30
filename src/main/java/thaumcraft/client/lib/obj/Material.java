package thaumcraft.client.lib.obj;


public class Material
{
    public String Name;
    public org.joml.Vector3f AmbientColor;
    public org.joml.Vector3f DiffuseColor;
    public org.joml.Vector3f SpecularColor;
    public float SpecularCoefficient;
    public float Transparency;
    public int IlluminationModel;
    public String AmbientTextureMap;
    public String DiffuseTextureMap;
    public String SpecularTextureMap;
    public String SpecularHighlightTextureMap;
    public String BumpMap;
    public String DisplacementMap;
    public String StencilDecalMap;
    public String AlphaTextureMap;
    
    public Material(String materialName) {
        Name = materialName;
    }
}
