package sol_engine.graphics_module.shaders;

import sol_engine.utils.reflection_utils.ClassUtils;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {

    private Map<Class<? extends Shader>, Shader> shaderInstances = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Shader> T get(Class<T> shaderType) {
        return (T) shaderInstances.computeIfAbsent(shaderType, key -> {
            Shader s = ClassUtils.instanciateNoarg(key);
            return s;
        });
    }
}
