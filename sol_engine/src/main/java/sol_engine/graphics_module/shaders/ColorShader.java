package sol_engine.graphics_module.shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ColorShader extends MVPShader {

    private static final String UNIFORM_COLOR = "color";
    private static final String UNIFORM_TIME = "time";


    private static String vertexPath = "shaders/color_shader.vert";
    private static String fragPath = "shaders/color_shader.frag";


    public ColorShader() {
        super(vertexPath, fragPath);
    }



    public void setColor(Vector3f color) {
        super.setUniform3f(UNIFORM_COLOR, color);
    }
    public void setTime(float time) {
        super.setUniform1f(UNIFORM_TIME, time);
    }
}
