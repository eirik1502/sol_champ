package sol_engine.graphics_module.materials;

import org.joml.Matrix4f;
import sol_engine.graphics_module.shaders.MVPShader;

public abstract class Material {

    abstract public void bind();

    abstract public MVPShader getShader();
}
