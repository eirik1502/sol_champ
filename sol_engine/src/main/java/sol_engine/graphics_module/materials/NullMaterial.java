package sol_engine.graphics_module.materials;

import sol_engine.graphics_module.shaders.MVPShader;

public class NullMaterial extends Material {

    public NullMaterial() {
        super(MVPShader.NULL.class);
    }

    @Override
    public void applyShaderProps(MVPShader shader) {

    }
}
