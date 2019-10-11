package sol_engine.graphics_module.graphical_objects;

import sol_engine.graphics_module.materials.Material;
import sol_engine.graphics_module.render.Mesh;

public interface Renderable {

    float getX();

    float getY();

    float getWidth();

    float getHeight();

    void setX(float x);

    void setY(float y);

    Mesh getMesh();

    Material getMaterial();

}
