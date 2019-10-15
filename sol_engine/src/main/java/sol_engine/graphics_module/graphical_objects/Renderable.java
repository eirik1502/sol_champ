package sol_engine.graphics_module.graphical_objects;

import sol_engine.graphics_module.materials.Material;

public class Renderable implements Cloneable {

    public float width, height;
    public String meshName;
    public Material material;


    public Renderable(float width, float height, String meshName, Material material) {
        this.meshName = meshName;
        setProps(width, height, material);
    }

    public void setProps(float width, float height, Material material) {
        this.width = width;
        this.height = height;
        this.material = material;
    }

    public Renderable clone() {
        try {
            Renderable clone = (Renderable) super.clone();
            clone.material = material.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
