package sol_engine.graphics_module;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Color {

    public static final Color
            RED = new Color(1, 0, 0),
            GREEN = new Color(0, 1, 0),
            BLUE = new Color(0, 0, 1);


    private float r, g, b, a;


    public Color(float r, float g, float b) {
        this(r, g, b, 1);
    }

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public float[] getAsArray() {
        return new float[] {r, g, b, a};
    }
    public Vector4f getRGBAVec() {
        return new Vector4f(r, g, b, a);
    }
    public Vector3f getRGBVec() {
        return new Vector3f(r, g, b);
    }
}
