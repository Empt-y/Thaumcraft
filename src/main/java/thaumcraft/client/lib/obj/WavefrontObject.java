package thaumcraft.client.lib.obj;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import thaumcraft.client.lib.UtilsFX;


public class WavefrontObject implements IModelCustom
{
    private static Pattern vertexPattern;
    private static Pattern vertexNormalPattern;
    private static Pattern textureCoordinatePattern;
    private static Pattern face_V_VT_VN_Pattern;
    private static Pattern face_V_VT_Pattern;
    private static Pattern face_V_VN_Pattern;
    private static Pattern face_V_Pattern;
    private static Pattern groupObjectPattern;
    private static Matcher vertexMatcher;
    private static Matcher vertexNormalMatcher;
    private static Matcher textureCoordinateMatcher;
    private static Matcher face_V_VT_VN_Matcher;
    private static Matcher face_V_VT_Matcher;
    private static Matcher face_V_VN_Matcher;
    private static Matcher face_V_Matcher;
    private static Matcher groupObjectMatcher;
    public ArrayList<Vertex> vertices;
    public ArrayList<Vertex> vertexNormals;
    public ArrayList<TextureCoordinate> textureCoordinates;
    public ArrayList<GroupObject> groupObjects;
    private GroupObject currentGroupObject;
    private String fileName;
    
    public WavefrontObject(Identifier resource) throws ModelFormatException {
        vertices = new ArrayList<Vertex>();
        vertexNormals = new ArrayList<Vertex>();
        textureCoordinates = new ArrayList<TextureCoordinate>();
        groupObjects = new ArrayList<GroupObject>();
        fileName = resource.toString();
        try {
            Resource res = Minecraft.getInstance().getResourceManager().getResource(resource).orElseThrow();
            loadObjModel(res.open());
        }
        catch (IOException e) {
            throw new ModelFormatException("IO Exception reading model format", e);
        }
    }
    
    public WavefrontObject(String filename, InputStream inputStream) throws ModelFormatException {
        vertices = new ArrayList<Vertex>();
        vertexNormals = new ArrayList<Vertex>();
        textureCoordinates = new ArrayList<TextureCoordinate>();
        groupObjects = new ArrayList<GroupObject>();
        fileName = filename;
        loadObjModel(inputStream);
    }
    
    private void loadObjModel(InputStream inputStream) throws ModelFormatException {
        BufferedReader reader = null;
        String currentLine = null;
        int lineCount = 0;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((currentLine = reader.readLine()) != null) {
                ++lineCount;
                currentLine = currentLine.replaceAll("\\s+", " ").trim();
                if (!currentLine.startsWith("#")) {
                    if (currentLine.length() == 0) {
                        continue;
                    }
                    if (currentLine.startsWith("v ")) {
                        Vertex vertex = parseVertex(currentLine, lineCount);
                        if (vertex == null) {
                            continue;
                        }
                        vertices.add(vertex);
                    }
                    else if (currentLine.startsWith("vn ")) {
                        Vertex vertex = parseVertexNormal(currentLine, lineCount);
                        if (vertex == null) {
                            continue;
                        }
                        vertexNormals.add(vertex);
                    }
                    else if (currentLine.startsWith("vt ")) {
                        TextureCoordinate textureCoordinate = parseTextureCoordinate(currentLine, lineCount);
                        if (textureCoordinate == null) {
                            continue;
                        }
                        textureCoordinates.add(textureCoordinate);
                    }
                    else if (currentLine.startsWith("f ")) {
                        if (currentGroupObject == null) {
                            currentGroupObject = new GroupObject("Default");
                        }
                        Face face = parseFace(currentLine, lineCount);
                        if (face == null) {
                            continue;
                        }
                        currentGroupObject.faces.add(face);
                    }
                    else {
                        if (!(currentLine.startsWith("g ") | currentLine.startsWith("o "))) {
                            continue;
                        }
                        GroupObject group = parseGroupObject(currentLine, lineCount);
                        if (group != null && currentGroupObject != null) {
                            groupObjects.add(currentGroupObject);
                        }
                        currentGroupObject = group;
                    }
                }
            }
            groupObjects.add(currentGroupObject);
        }
        catch (IOException e) {
            throw new ModelFormatException("IO Exception reading model format", e);
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException ex) {}
            try {
                inputStream.close();
            }
            catch (IOException ex2) {}
        }
    }
    
    @Override
    public void renderAll() {
        renderAll(UtilsFX.currentTexture != null ? UtilsFX.currentTexture : UtilsFX.nodeTexture);
    }

    public void renderAll(Identifier texture) {
        if (UtilsFX.currentCollector == null || UtilsFX.currentPoseStack == null || texture == null) return;
        for (GroupObject groupObject : groupObjects) {
            if (groupObject != null) groupObject.render(texture);
        }
    }

    public void tessellateAll(Tesselator tessellator) {
        renderAll();
    }
    
    @Override
    public void renderOnly(String... groupNames) {
        Identifier tex = UtilsFX.currentTexture != null ? UtilsFX.currentTexture : UtilsFX.nodeTexture;
        for (GroupObject groupObject : groupObjects) {
            if (groupObject == null) continue;
            for (String groupName : groupNames) {
                if (groupName.equalsIgnoreCase(groupObject.name)) {
                    if (tex != null) groupObject.render(tex);
                }
            }
        }
    }
    
    public void tessellateOnly(Tesselator tessellator, String... groupNames) {
        renderOnly(groupNames);
    }
    
    @Override
    public String[] getPartNames() {
        ArrayList<String> l = new ArrayList<String>();
        for (GroupObject groupObject : groupObjects) {
            l.add(groupObject.name);
        }
        return l.toArray(new String[0]);
    }
    
    @Override
    public void renderPart(String partName) {
        renderPart(partName, UtilsFX.currentTexture != null ? UtilsFX.currentTexture : UtilsFX.nodeTexture);
    }

    public void renderPart(String partName, Identifier texture) {
        if (texture == null) return;
        for (GroupObject groupObject : groupObjects) {
            if (groupObject != null && partName.equalsIgnoreCase(groupObject.name)) {
                groupObject.render(texture);
            }
        }
    }

    public void tessellatePart(Tesselator tessellator, String partName) {
        renderPart(partName);
    }
    
    @Override
    public void renderAllExcept(String... excludedGroupNames) {
        Identifier tex = UtilsFX.currentTexture != null ? UtilsFX.currentTexture : UtilsFX.nodeTexture;
        for (GroupObject groupObject : groupObjects) {
            if (groupObject == null) continue;
            boolean skipPart = false;
            for (String excludedGroupName : excludedGroupNames) {
                if (excludedGroupName.equalsIgnoreCase(groupObject.name)) {
                    skipPart = true;
                    break;
                }
            }
            if (!skipPart && tex != null) {
                groupObject.render(tex);
            }
        }
    }
    
    public void tessellateAllExcept(Tesselator tessellator, String... excludedGroupNames) {
        renderAllExcept(excludedGroupNames);
    }
    
    private Vertex parseVertex(String line, int lineCount) throws ModelFormatException {
        Vertex vertex = null;
        if (isValidVertexLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");
            try {
                if (tokens.length == 2) {
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]));
                }
                if (tokens.length == 3) {
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                }
            }
            catch (NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
            return vertex;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
    }
    
    private Vertex parseVertexNormal(String line, int lineCount) throws ModelFormatException {
        Vertex vertexNormal = null;
        if (isValidVertexNormalLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");
            try {
                if (tokens.length == 3) {
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                }
            }
            catch (NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
            return vertexNormal;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
    }
    
    private TextureCoordinate parseTextureCoordinate(String line, int lineCount) throws ModelFormatException {
        TextureCoordinate textureCoordinate = null;
        if (isValidTextureCoordinateLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");
            try {
                if (tokens.length == 2) {
                    return new TextureCoordinate(Float.parseFloat(tokens[0]), 1.0f - Float.parseFloat(tokens[1]));
                }
                if (tokens.length == 3) {
                    return new TextureCoordinate(Float.parseFloat(tokens[0]), 1.0f - Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                }
            }
            catch (NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
            return textureCoordinate;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
    }
    
    private Face parseFace(String line, int lineCount) throws ModelFormatException {
        Face face = null;
        if (isValidFaceLine(line)) {
            face = new Face();
            String trimmedLine = line.substring(line.indexOf(" ") + 1);
            String[] tokens = trimmedLine.split(" ");
            String[] subTokens = null;
            if (tokens.length == 3) {
                if (currentGroupObject.glDrawingMode == -1) {
                    currentGroupObject.glDrawingMode = 4;
                }
                else if (currentGroupObject.glDrawingMode != 4) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Invalid number of points for face (expected 4, found " + tokens.length + ")");
                }
            }
            else if (tokens.length == 4) {
                if (currentGroupObject.glDrawingMode == -1) {
                    currentGroupObject.glDrawingMode = 7;
                }
                else if (currentGroupObject.glDrawingMode != 7) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Invalid number of points for face (expected 3, found " + tokens.length + ")");
                }
            }
            if (isValidFace_V_VT_VN_Line(line)) {
                face.vertices = new Vertex[tokens.length];
                face.textureCoordinates = new TextureCoordinate[tokens.length];
                face.vertexNormals = new Vertex[tokens.length];
                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("/");
                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                    face.vertexNormals[i] = vertexNormals.get(Integer.parseInt(subTokens[2]) - 1);
                }
                face.faceNormal = face.calculateFaceNormal();
            }
            else if (isValidFace_V_VT_Line(line)) {
                face.vertices = new Vertex[tokens.length];
                face.textureCoordinates = new TextureCoordinate[tokens.length];
                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("/");
                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                }
                face.faceNormal = face.calculateFaceNormal();
            }
            else if (isValidFace_V_VN_Line(line)) {
                face.vertices = new Vertex[tokens.length];
                face.vertexNormals = new Vertex[tokens.length];
                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("//");
                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.vertexNormals[i] = vertexNormals.get(Integer.parseInt(subTokens[1]) - 1);
                }
                face.faceNormal = face.calculateFaceNormal();
            }
            else {
                if (!isValidFace_V_Line(line)) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
                }
                face.vertices = new Vertex[tokens.length];
                for (int i = 0; i < tokens.length; ++i) {
                    face.vertices[i] = vertices.get(Integer.parseInt(tokens[i]) - 1);
                }
                face.faceNormal = face.calculateFaceNormal();
            }
            return face;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
    }
    
    private GroupObject parseGroupObject(String line, int lineCount) throws ModelFormatException {
        GroupObject group = null;
        if (isValidGroupObjectLine(line)) {
            String trimmedLine = line.substring(line.indexOf(" ") + 1);
            if (trimmedLine.length() > 0) {
                group = new GroupObject(trimmedLine);
            }
            return group;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "', line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
    }
    
    private static boolean isValidVertexLine(String line) {
        if (WavefrontObject.vertexMatcher != null) {
            WavefrontObject.vertexMatcher.reset();
        }
        WavefrontObject.vertexMatcher = WavefrontObject.vertexPattern.matcher(line);
        return WavefrontObject.vertexMatcher.matches();
    }
    
    private static boolean isValidVertexNormalLine(String line) {
        if (WavefrontObject.vertexNormalMatcher != null) {
            WavefrontObject.vertexNormalMatcher.reset();
        }
        WavefrontObject.vertexNormalMatcher = WavefrontObject.vertexNormalPattern.matcher(line);
        return WavefrontObject.vertexNormalMatcher.matches();
    }
    
    private static boolean isValidTextureCoordinateLine(String line) {
        if (WavefrontObject.textureCoordinateMatcher != null) {
            WavefrontObject.textureCoordinateMatcher.reset();
        }
        WavefrontObject.textureCoordinateMatcher = WavefrontObject.textureCoordinatePattern.matcher(line);
        return WavefrontObject.textureCoordinateMatcher.matches();
    }
    
    private static boolean isValidFace_V_VT_VN_Line(String line) {
        if (WavefrontObject.face_V_VT_VN_Matcher != null) {
            WavefrontObject.face_V_VT_VN_Matcher.reset();
        }
        WavefrontObject.face_V_VT_VN_Matcher = WavefrontObject.face_V_VT_VN_Pattern.matcher(line);
        return WavefrontObject.face_V_VT_VN_Matcher.matches();
    }
    
    private static boolean isValidFace_V_VT_Line(String line) {
        if (WavefrontObject.face_V_VT_Matcher != null) {
            WavefrontObject.face_V_VT_Matcher.reset();
        }
        WavefrontObject.face_V_VT_Matcher = WavefrontObject.face_V_VT_Pattern.matcher(line);
        return WavefrontObject.face_V_VT_Matcher.matches();
    }
    
    private static boolean isValidFace_V_VN_Line(String line) {
        if (WavefrontObject.face_V_VN_Matcher != null) {
            WavefrontObject.face_V_VN_Matcher.reset();
        }
        WavefrontObject.face_V_VN_Matcher = WavefrontObject.face_V_VN_Pattern.matcher(line);
        return WavefrontObject.face_V_VN_Matcher.matches();
    }
    
    private static boolean isValidFace_V_Line(String line) {
        if (WavefrontObject.face_V_Matcher != null) {
            WavefrontObject.face_V_Matcher.reset();
        }
        WavefrontObject.face_V_Matcher = WavefrontObject.face_V_Pattern.matcher(line);
        return WavefrontObject.face_V_Matcher.matches();
    }
    
    private static boolean isValidFaceLine(String line) {
        return isValidFace_V_VT_VN_Line(line) || isValidFace_V_VT_Line(line) || isValidFace_V_VN_Line(line) || isValidFace_V_Line(line);
    }
    
    private static boolean isValidGroupObjectLine(String line) {
        if (WavefrontObject.groupObjectMatcher != null) {
            WavefrontObject.groupObjectMatcher.reset();
        }
        WavefrontObject.groupObjectMatcher = WavefrontObject.groupObjectPattern.matcher(line);
        return WavefrontObject.groupObjectMatcher.matches();
    }
    
    @Override
    public String getType() {
        return "obj";
    }
    
    static {
        WavefrontObject.vertexPattern = Pattern.compile("(v( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(v( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
        WavefrontObject.vertexNormalPattern = Pattern.compile("(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
        WavefrontObject.textureCoordinatePattern = Pattern.compile("(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *\\n)|(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *$)");
        WavefrontObject.face_V_VT_VN_Pattern = Pattern.compile("(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)");
        WavefrontObject.face_V_VT_Pattern = Pattern.compile("(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)");
        WavefrontObject.face_V_VN_Pattern = Pattern.compile("(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)");
        WavefrontObject.face_V_Pattern = Pattern.compile("(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)");
        WavefrontObject.groupObjectPattern = Pattern.compile("([go]( [\\w\\d\\.]+) *\\n)|([go]( [\\w\\d\\.]+) *$)");
    }
    
    public class TextureCoordinate
    {
        public float u;
        public float v;
        public float w;
        
        public TextureCoordinate(float u, float v) {
            this(u, v, 0.0f);
        }
        
        public TextureCoordinate(float u, float v, float w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }
    
    public class Face
    {
        public Vertex[] vertices;
        public Vertex[] vertexNormals;
        public Vertex faceNormal;
        public TextureCoordinate[] textureCoordinates;
        
        public void addFaceForRender(Tesselator tessellator) {
            addFaceForRender(tessellator, 5.0E-4f);
        }

        public void addFaceForRender(Tesselator tessellator, float textureOffset) {
            // Delegated through GroupObject.render(texture) which calls Face.submitGeometry
        }

        /**
         * Submit this face's geometry to the provided VertexConsumer using the given pose.
         */
        public void submitGeometry(com.mojang.blaze3d.vertex.PoseStack.Pose pose, com.mojang.blaze3d.vertex.VertexConsumer buf) {
            if (vertices == null || vertices.length < 3) return;
            Vertex normal = faceNormal != null ? faceNormal : new Vertex(0, 1, 0);
            // Emit as individual triangles (or quads if 4 verts)
            int count = vertices.length;
            for (int i = 0; i < count; i++) {
                Vertex v = vertices[i];
                float u = 0, vv = 0;
                if (textureCoordinates != null && textureCoordinates.length > i && textureCoordinates[i] != null) {
                    u = textureCoordinates[i].u;
                    vv = textureCoordinates[i].v;
                }
                float nx = normal.x, ny = normal.y, nz = normal.z;
                if (vertexNormals != null && vertexNormals.length > i && vertexNormals[i] != null) {
                    nx = vertexNormals[i].x; ny = vertexNormals[i].y; nz = vertexNormals[i].z;
                }
                buf.addVertex(pose, v.x, v.y, v.z)
                   .setColor(1f, 1f, 1f, 1f)
                   .setUv(u, vv)
                   .setOverlay(0).setLight(0xF000F0).setNormal(nx, ny, nz);
            }
        }
        
        public Vertex calculateFaceNormal() {
            Vec3 v1 = new Vec3(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
            Vec3 v2 = new Vec3(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);
            Vec3 normalVector = null;
            normalVector = v1.cross(v2).normalize();
            return new Vertex((float)normalVector.x, (float)normalVector.y, (float)normalVector.z);
        }
    }
    
    public class GroupObject
    {
        public String name;
        public ArrayList<Face> faces;
        public int glDrawingMode;
        
        public GroupObject() {
            this("");
        }
        
        public GroupObject(String name) {
            this(name, -1);
        }
        
        public GroupObject(String name, int glDrawingMode) {
            faces = new ArrayList<Face>();
            this.name = name;
            this.glDrawingMode = glDrawingMode;
        }


        public void render() {
            Identifier tex = UtilsFX.currentTexture != null ? UtilsFX.currentTexture : UtilsFX.nodeTexture;
            if (tex != null) render(tex);
        }

        public void render(Tesselator tessellator) {
            render();
        }

        public void render(Identifier texture) {
            if (UtilsFX.currentCollector == null || UtilsFX.currentPoseStack == null) return;
            if (faces.isEmpty()) return;
            UtilsFX.currentCollector.submitCustomGeometry(
                UtilsFX.currentPoseStack,
                RenderTypes.entityTranslucent(texture),
                (pose, buf) -> {
                    for (Face face : faces) {
                        face.submitGeometry(pose, buf);
                    }
                }
            );
        }
    }
    
    public class ModelFormatException extends RuntimeException
    {
        private static final long serialVersionUID = 2023547503969671835L;
        
        public ModelFormatException() {
        }
        
        public ModelFormatException(String message, Throwable cause) {
            super(message, cause);
        }
        
        public ModelFormatException(String message) {
            super(message);
        }

        public ModelFormatException(Throwable cause) {
            super(cause);
        }
    }
}
