package org.lwjgl.util.glu;

/** Compile-time stub for LWJGL2 GLU — not functional at runtime. */
public class GLU {
    public GLUtessellator gluNewTess() { return new GLUtessellator(); }
    public void gluTessCallback(GLUtessellator tess, int which, GLUtessellatorCallback cb) {}
    public void gluTessBeginPolygon(GLUtessellator tess, Object data) {}
    public void gluTessBeginContour(GLUtessellator tess) {}
    public void gluTessVertex(GLUtessellator tess, double[] coords, int offset, Object data) {}
    public void gluTessEndContour(GLUtessellator tess) {}
    public void gluTessEndPolygon(GLUtessellator tess) {}
    public void gluTessProperty(GLUtessellator tess, int which, double value) {}
    public void gluDeleteTess(GLUtessellator tess) {}
    public static final int GLU_TESS_VERTEX = 100101;
    public static final int GLU_TESS_BEGIN = 100100;
    public static final int GLU_TESS_END = 100102;
    public static final int GLU_TESS_ERROR = 100103;
    public static final int GLU_TESS_COMBINE = 100105;
    public static final int GLU_TESS_BEGIN_DATA = 100106;
    public static final int GLU_TESS_VERTEX_DATA = 100107;
    public static final int GLU_TESS_END_DATA = 100108;
    public static final int GLU_TESS_ERROR_DATA = 100109;
    public static final int GLU_TESS_COMBINE_DATA = 100110;
    public static final int GLU_TESS_WINDING_RULE = 100140;
    public static final int GLU_TESS_WINDING_ODD = 100130;
    public static final int GLU_TESS_WINDING_NONZERO = 100131;
    public static final int GLU_TESS_WINDING_POSITIVE = 100132;
    public static final int GLU_TESS_WINDING_NEGATIVE = 100133;
    public static final int GLU_TESS_WINDING_ABS_GEQ_TWO = 100134;
    public static final int GLU_TESS_TOLERANCE = 100142;
    public static String gluErrorString(int errnum) { return "GLU error " + errnum; }
}
