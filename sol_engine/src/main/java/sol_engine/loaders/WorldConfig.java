package sol_engine.loaders;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

public class WorldConfig {

    public class EntityClassConfig {
        public List<String> extendsClass;
        public Map<String, Map<String, JsonObject>> components;
    }
    public class InitialEntityConfig {
        public String useClass;
        public String name;
        public Map<String, Map<String, JsonObject>> overrideComponents;
    }

    public List<String> componentSystems;
    public Map<String, EntityClassConfig> entityClasses;
    public List<InitialEntityConfig> initialEntities;
}
