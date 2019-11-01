package sol_engine.graphics_module.materials;

import sol_engine.graphics_module.shaders.MVPShader;


public abstract class Material implements Cloneable {

    private final Class<? extends MVPShader> shaderType;

    public float transparancy = 1;

    protected Material(Class<? extends MVPShader> shaderType) {
        this.shaderType = shaderType;
    }

    abstract public void applyShaderProps(MVPShader shader);

    public Class<? extends MVPShader> getShaderType() {
        return shaderType;
    }

    public Material clone() {
        try {
            return (Material) super.clone();
        } catch (ClassCastException | CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
