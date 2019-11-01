package sol_engine.graphics_module.materials;


import sol_engine.graphics_module.Color;
import sol_engine.graphics_module.shaders.ColorShader;
import sol_engine.graphics_module.shaders.MVPShader;

public class MattMaterial extends Material {

    public static MattMaterial STANDARD() {
        return new MattMaterial(new Color(1, 1, 1));
    }

    public static MattMaterial RED() {
        return new MattMaterial(Color.RED);
    }

    public static MattMaterial GREEN() {
        return new MattMaterial(Color.GREEN);
    }

    public static MattMaterial BLUE() {
        return new MattMaterial(Color.BLUE);
    }


    public Color color;


    public MattMaterial() {
        this(Color.BLACK);
    }

    public MattMaterial(Color color) {
        this(color, 1);
    }

    public MattMaterial(Color color, float transparancy) {
        super(ColorShader.class);
        this.color = color;
        this.transparancy = transparancy;
    }

    @Override
    public void applyShaderProps(MVPShader shader) {
        ColorShader colorShader = (ColorShader) shader;
        colorShader.setColor(color.asRGBVec());
    }
}
