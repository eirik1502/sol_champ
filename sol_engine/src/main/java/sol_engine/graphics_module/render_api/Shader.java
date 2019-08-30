package sol_engine.graphics_module.render_api;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import sol_engine.utils.BufferUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Unmodifyable shader representation
 * <p>
 * Created by eirik on 13.06.2017.
 */
public abstract class Shader {


    private int programId;

    private boolean bound;

    private Map<String, Integer> uniformLocations = new HashMap<>();


    public Shader(String vertex, String frag) {
        System.out.println("Compiling shader: " + vertex + " " + frag);
        this.programId = ShaderLoader.loadShader(vertex, frag);
        System.out.println("Shadered compiled: " + vertex + " " + frag);
    }

    public void bind() {
        bound = true;
        GL20.glUseProgram(programId);
    }

    public void unbind() {
        bound = false;
        GL20.glUseProgram(0);
    }


    private int getUniformLocation(String name) {

        return uniformLocations.computeIfAbsent(name, key -> {
            int loc = GL20.glGetUniformLocation(programId, name);
            if (loc == -1)
                throw new IllegalStateException("Could not find uniform variable '" + name + "'!");
            return loc;
        });
    }


    protected void setUniform1i(String prop, int value) {
        if (!bound) throw new IllegalStateException("Trying to use a shader while it is disabled");
        GL20.glUniform1i(getUniformLocation(prop), value);
    }

    protected void setUniform1f(String prop, float value) {
        if (!bound) throw new IllegalStateException("Trying to use a shader while it is disabled");
        GL20.glUniform1f(getUniformLocation(prop), value);
    }

    protected void setUniform2f(String prop, float x, float y) {
        if (!bound) throw new IllegalStateException("Trying to use a shader while it is disabled");
        GL20.glUniform2f(getUniformLocation(prop), x, y);
    }

    protected void setUniform3f(String prop, Vector3f vector) {
        if (!bound) throw new IllegalStateException("Trying to use a shader while it is disabled");
        GL20.glUniform3f(getUniformLocation(prop), vector.x, vector.y, vector.z);
    }

    protected void setUniform4f(String prop, Vector4f vector) {
        if (!bound) throw new IllegalStateException("Trying to use a shader while it is disabled");
        GL20.glUniform4f(getUniformLocation(prop), vector.x, vector.y, vector.z, vector.w);
    }

    protected void setUniformMat4f(String prop, Matrix4f matrix) {
        if (!bound) throw new IllegalStateException("Trying to use a shader while it is disabled");
        GL20.glUniformMatrix4fv(getUniformLocation(prop), false, BufferUtils.createFloatBuffer(matrix));
    }
}
