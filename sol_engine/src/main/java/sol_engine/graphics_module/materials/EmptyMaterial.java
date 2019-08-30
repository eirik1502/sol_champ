package sol_engine.graphics_module.materials;

import sol_engine.graphics_module.Color;
import sol_engine.graphics_module.shaders.ColorShader;
import sol_engine.graphics_module.shaders.MVPShader;
import sol_engine.graphics_module.shaders.Shaders;

public class EmptyMaterial extends Material {
    private static ColorShader shader = null;


    public static MattMaterial STANDARD = new MattMaterial(new Color(1,1,1));


    private Color color;

    private EmptyMaterial(Color color) {
        this.color = color;

        if (shader == null) {
            shader = Shaders.get(ColorShader.class);
        }

    }

    public void bind() {

        shader.bind();
        shader.setColor(color.getRGBVec());
    }

    @Override
    public MVPShader getShader() {
        return shader;
    }
}
