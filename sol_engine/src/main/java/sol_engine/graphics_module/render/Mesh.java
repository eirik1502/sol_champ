package sol_engine.graphics_module.render;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class Mesh {

    public static final Mesh NULL_MESH = new Mesh(new float[0], new byte[0]);

    public static final Mesh UNIT_CORNERED_RECTANGLE_MESH = new Mesh(
            new float[]{
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    1.0f, 1.0f, 0.0f,
                    1.0f, 0.0f, 0.0f
            },
            new byte[]{
                    0, 1, 2,
                    2, 3, 0
            }
    );

    public static final int VERTEX_LOCATION = 0;

    int vaoId;
    int verticesId;
    int indicesId;

    int indicesCount;

    public Mesh(float[] vertices, byte[] indices) {
        indicesCount = indices.length;
        vaoId = createVertexArray(vertices, indices);
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public void bind() {
        //bind vao
        GL30.glBindVertexArray(vaoId);

        // this is a bug, that this has to be bound
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesId);
    }

    public void unbind() {
        //unbind vao
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private int createVertexArray(float[] vertices, byte[] indices) {
        int id = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(id);

        verticesId = VertexArrayUtils.createVertexBuffer(VERTEX_LOCATION, 3, vertices);
        indicesId = VertexArrayUtils.createIndicesBuffer(indices);


        GL30.glBindVertexArray(0);

        return id;
    }
}
