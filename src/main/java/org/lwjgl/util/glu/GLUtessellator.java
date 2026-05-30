package org.lwjgl.util.glu;

/** Compile-time stub for LWJGL2 GLUtessellator — not functional at runtime. */
public class GLUtessellator {
    public void gluTessProperty(int which, double value) {}
    public void gluTessCallback(int which, GLUtessellatorCallback cb) {}
    public void gluTessBeginPolygon(Object data) {}
    public void gluTessBeginContour() {}
    public void gluTessVertex(double[] coords, int offset, Object data) {}
    public void gluTessEndContour() {}
    public void gluTessEndPolygon() {}
    public void gluDeleteTess() {}
}
