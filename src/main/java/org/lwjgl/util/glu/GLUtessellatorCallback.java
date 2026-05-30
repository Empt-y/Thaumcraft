package org.lwjgl.util.glu;

/** Compile-time stub for LWJGL2 GLUtessellatorCallback — not functional at runtime. */
public interface GLUtessellatorCallback {
    void begin(int type);
    void end();
    void vertex(Object vertexData);
    void error(int errnum);
    void combine(double[] coords, Object[] data, float[] weight, Object[] outData);
}
