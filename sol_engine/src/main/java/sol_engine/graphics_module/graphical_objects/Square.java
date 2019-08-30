package sol_engine.graphics_module.graphical_objects;

import sol_engine.graphics_module.Color;
import sol_engine.graphics_module.materials.EmptyMaterial;
import sol_engine.graphics_module.materials.Material;
import sol_engine.graphics_module.render_api.Mesh;

public class Square implements Renderable{

    private float x, y, width, height;
    private Mesh mesh;
    private Material material;

    public Square() {
        this(0, 0, 0, 0, EmptyMaterial.STANDARD);
    }

    public Square(float x, float y, float width, float height, Material material) {
        setProps(x, y, width, height, material);

        mesh = Mesh.UNIT_CORNERED_RECTANGLE_MESH;
    }

    public void setProps(float x, float y, float width, float height, Material material) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.material = material;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public Material getMaterial() {
        return material;
    }


}
