package sol_engine.graphics_module.shaders;

import sol_engine.graphics_module.render_api.Shader;
import sol_engine.utils.ClassUtils;

import java.util.HashMap;
import java.util.Map;

public class Shaders {

    private static Map<Class<? extends Shader>, Shader> shaderInstances = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Shader> T get(Class<T> shaderType) {
        return (T) shaderInstances.computeIfAbsent(shaderType, key -> {
            Shader s = ClassUtils.instanciateNoarg(key);
            System.out.println(s);
            return s;
        });
    }
}
