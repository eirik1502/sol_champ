package sol_engine.graphics_module.materials;

import sol_engine.graphics_module.shaders.MVPShader;

public abstract class Material {

    private Class<? extends MVPShader> shaderType;


    protected Material(Class<? extends MVPShader> shaderType) {
        this.shaderType = shaderType;
    }

    abstract public void applyShaderProps(MVPShader shader);

    public Class<? extends MVPShader> getShaderType() {
        return shaderType;
    }
}
