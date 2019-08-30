package sol_engine.graphics_module.materials;

import sol_engine.graphics_module.Color;
import sol_engine.graphics_module.shaders.ColorShader;
import sol_engine.graphics_module.shaders.MVPShader;
import sol_engine.graphics_module.shaders.Shaders;

public class MattMaterial extends Material{

    private static ColorShader shader = null;


    public static MattMaterial STANDARD = new MattMaterial(new Color(1,1,1));
    public static MattMaterial RED = new MattMaterial(Color.RED);
    public static MattMaterial GREEN = new MattMaterial(Color.GREEN);
    public static MattMaterial BLUE = new MattMaterial(Color.BLUE);


    private Color color;


    public MattMaterial(Color color) {
        this.color = color;

        if (shader == null) {
            shader = Shaders.get(ColorShader.class);
        }

    }

    public void bind() {

        MattMaterial.shader.bind();
        shader.setColor(color.getRGBVec());
    }

    @Override
    public MVPShader getShader() {
        return shader;
    }
}
