package sol_engine.graphics_module;

import org.joml.Vector3f;
import org.joml.Vector4f;


/**
 * A final class representing a color, it is not mutateable.
 */
public class Color {

    public static final Color
            WHITE = new Color(0, 0, 0),
            BLACK = new Color(1, 1, 1),
            RED = new Color(1, 0, 0),
            GREEN = new Color(0, 1, 0),
            BLUE = new Color(0, 0, 1);


    public final float r, g, b, a;

    public Color() {
        this(0, 0, 0, 1);
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1);
    }

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public float[] asArray() {
        return new float[]{r, g, b, a};
    }

    public Vector4f asRGBAVec() {
        return new Vector4f(r, g, b, a);
    }

    public Vector3f asRGBVec() {
        return new Vector3f(r, g, b);
    }

}
