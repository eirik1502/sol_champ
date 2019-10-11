package sol_engine.graphics_module.shaders;

import org.joml.Matrix4f;

public abstract class MVPShader extends Shader {

    private static final String UNIFORM_MODEL_TRANSFORM = "modelTransform";
    private static final String UNIFORM_VIEW_TRANSFORM = "viewTransform";
    private static final String UNIFORM_PROJECTION_TRANSFORM = "projectionTransform";


    public MVPShader(String vertexPath, String fragPath) {
        super(vertexPath, fragPath);
    }


    public void setModelTransform(Matrix4f transform) {
        super.setUniformMat4f(UNIFORM_MODEL_TRANSFORM, transform);
    }

    public void setViewTransform(Matrix4f transform) {
        super.setUniformMat4f(UNIFORM_VIEW_TRANSFORM, transform);
    }

    public void setProjectionTransform(Matrix4f transform) {
        super.setUniformMat4f(UNIFORM_PROJECTION_TRANSFORM, transform);
    }
}
