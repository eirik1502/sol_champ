package sol_engine.utils.math;

import org.joml.Vector2f;

public class MathF {

    public static final float PI = (float) Math.PI;


    public static float min(float x, float y) {
        return Math.min(x, y);
    }

    public static float max(float x, float y) {
        return Math.max(x, y);
    }

    public static float abs(float x) {
        return Math.abs(x);
    }

    public static float clamp(float x, float min, float max) {
        return min(max(x, min), max);
    }

    public static float sign(float x) {
        return Math.signum(x);
    }

    public static float round(float x) {
        return Math.round(x);
    }

    public static int roundi(float x) {
        return Math.round(x);
    }

    public static float floor(float x) {
        return (float) Math.floor(x);
    }

    public static float ceil(float x) {
        return (float) Math.ceil(x);
    }

    public static int floori(float x) {
        return (int) floor(x);
    }

    public static int ceili(float x) {
        return (int) ceil(x);
    }

    public static float random() {
        return (float) Math.random();
    }

    public static float cos(float angle) {
        return (float) Math.cos((double) angle);
    }

    public static float sin(float angle) {
        return (float) Math.sin((double) angle);
    }

    public static float tan(float angle) {
        return (float) Math.tan((double) angle);
    }

    public static float atan(float n) {
        return (float) Math.atan((double) n);
    }

    public static float atan2(float n1, float n2) {
        return (float) Math.atan2(n1, n2);
    }


    public static float pow2(float n) {
        return n * n;
    }

    public static float pow(float n, int p) {
        return (float) (Math.pow((float) (n), p));
    }

    public static float sqrt(float n) {
        return (float) Math.sqrt((double) n);
    }

    public static float pointDirection(float x1, float y1, float x2, float y2) {
        float deltaY = y2 - y1;
        float deltaX = x2 - x1;
        float angle = atan2(deltaY, deltaX);
        return angle;
    }

    public static float pointDirection(Vector2f v1, Vector2f v2) {
        return pointDirection(v1.x, v1.y, v2.x, v2.y);
    }

    /**
     * calculate the direction between two {@link Vector2f} that represents two points.
     *
     * @param p1 from point.
     * @param p2 to point.
     * @return a normalized vector with the calculated direction.
     */
    public static Vector2f pointDirectionVec(Vector2f p1, Vector2f p2) {
        Vector2f v = new Vector2f();
        p2.sub(p1, v).normalize();
        return v;
    }

    public static float lengthdirX(float length, float dir) {
        return cos(dir) * length;
    }

    public static float lengthdirY(float length, float dir) {
        return sin(dir) * length;
    }
}
