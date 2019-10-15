package sol_engine.graphics_module.render;

import java.util.HashMap;
import java.util.Map;

public class MeshManager {

    public static final String NULL_MESH = "NULL_MESH";

    private Map<String, Mesh> meshesByName = new HashMap<>();


    public MeshManager() {
        addMesh(NULL_MESH, new Mesh(new float[0], new byte[0]));
    }

    public void addMesh(String name, Mesh mesh) {
        this.meshesByName.put(name, mesh);
    }

//    public boolean loadMesh(String name, String filename)

    public Mesh getMesh(String name) {
        if (meshesByName.containsKey(name)) {
            return meshesByName.get(name);
        } else {
            return meshesByName.get(NULL_MESH);
        }
    }
}
