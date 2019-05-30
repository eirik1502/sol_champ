package sol_engine.loaders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import sol_engine.ecs.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldLoader {

    private static final String
            COMPSYS_PACKAGES_FIELD = "compSysPackages",

            COMPONENT_SYSTEMS_FIELD = "componentSystems",

            ENTITY_CLASSES_FIELD = "entityClasses",
                ENTITY_CLASS_CLASS_NAME_FIELD = "className",
                ENTITY_CLASS_EXTENDS_CLASSES_FIELD = "extendsClasses",
                ENTITY_CLASS_COMPONENTS_FIELD = "components",

            INITIAL_ENTITIES_FIELD = "initialEntities",
                INIT_ENTITIES_NAME_FIELD = "name",
                INIT_ENTITIES_COMPONENTS_FIELD = "overrideComponents",
                INIT_ENTITIES_USE_CLASS_FIELD = "useClass",

            COMPONENT_TYPE_FIELD = "type",
            COMPONENT_VALUES_FIELD = "values";

    private static final String WORLD_CONFIG_SCHEMA_PATH = "worldConfigSchema.json";
    public static final List<String> ENGINE_COMP_SYS_PACKAGES = Arrays.asList("sol_engine");


    private static JsonSchema worldConfigSchema = null;

    private static final ObjectMapper jacksonMapper = new ObjectMapper();

    private enum ErrorType {
        ERROR("ERROR"),
        WARNING("WARNING");

        public final String name;
        ErrorType(String name) {
            this.name = name;
        }
    }
    private class LoadedComponent {
        public final Class<? extends Component> compType;
        public final ObjectNode values;
        public LoadedComponent(Class<? extends Component> compType, ObjectNode values) {
            this.compType = compType;
            this.values = values;
        }
    }

    private String configPath;
    private Set<String> compSysPackages = new HashSet<>();


    public WorldLoader() {
    }

    public Set<String> getCompSysPackages() {
        return compSysPackages;
    }

    public void loadIntoWorld(World world, String configPath) {
        this.configPath = configPath;

        compSysPackages.addAll(ENGINE_COMP_SYS_PACKAGES);

        JsonNode configNode = loadJson(configPath);
        if (configNode == null) return;  // return if the json wasn't loaded

        if (! testAgainstWorldConfigSchema(configNode)) return;

        // check if there are compSys packages and store them
        if (configNode.has(COMPSYS_PACKAGES_FIELD)) {
            // add all listed compSys packages
            compSysPackages.addAll(getArrayChildNodesAsText(configNode.get(COMPSYS_PACKAGES_FIELD)));
        }

        // check if there are component systems and add them
        if (configNode.has(COMPONENT_SYSTEMS_FIELD)) {
            // load and add component systems
            loadCompoentSystems(configNode.findValue(COMPONENT_SYSTEMS_FIELD))
                    .forEach(world::addSystem);
        }

        // check that there are entityClasses and add them
        if (configNode.has(ENTITY_CLASSES_FIELD)) {
            List<EntityClass> entityClasses = loadEntityClasses(configNode.get(ENTITY_CLASSES_FIELD));
            entityClasses.forEach(world::addEntityClass);
        }

        // check that there are initial entities and load them
        if (configNode.has(INITIAL_ENTITIES_FIELD)) {
            List<Entity> initEntities = loadInitEntities(world, (ArrayNode) configNode.get(INITIAL_ENTITIES_FIELD));
            initEntities.forEach(world::addEntity);
        }

    }

    private List<Class<? extends ComponentSystem>> loadCompoentSystems(JsonNode systemsNode) {
        return streamArrayChildNodesAsText(systemsNode)
                .map(sysName -> {
                    // check that the system is an existing class and retrieve it
                    Class<?> sysClassGeneric = getClassInPackageList(sysName, compSysPackages);
                    if (sysClassGeneric == null) {
                        logLoadError(ErrorType.ERROR, "component system class not found for: " + sysName);
                        return null;
                    }

                    // check that the given system is a component system and cast it
                    try {
                        Class<? extends ComponentSystem> sysClass = sysClassGeneric.asSubclass(ComponentSystem.class);
                        return sysClass;
                    } catch (ClassCastException e) {
                        logLoadError(ErrorType.ERROR, "component system was not a subclass of ComponentSystem: " + sysName);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<EntityClass> loadEntityClasses(JsonNode entityClassesNode) {
        return streamArrayChildNodes(entityClassesNode)
                .map(entityClassNode -> loadEntityClass(entityClassNode))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private EntityClass loadEntityClass(JsonNode entityClassNode) {
        // get entity class name: required
        String entityClassName = entityClassNode.get(ENTITY_CLASS_CLASS_NAME_FIELD).asText();

        // get extending classes: optional
        List<String> extendingClasses = new ArrayList<>();
        if (entityClassNode.has(ENTITY_CLASS_EXTENDS_CLASSES_FIELD)) {
            extendingClasses.addAll(
                    getArrayChildNodesAsText(entityClassNode.get(ENTITY_CLASS_EXTENDS_CLASSES_FIELD)));
        }

        // get components: optional
        List<Component> components = new ArrayList<>();
        if (entityClassNode.has(ENTITY_CLASS_COMPONENTS_FIELD)) {
            components.addAll(
                    loadComponents(entityClassNode.get(ENTITY_CLASS_COMPONENTS_FIELD)).stream()
                            .map(loadedComp -> newComponent(loadedComp))
                            .collect(Collectors.toList())
            );
        }

        // create the entity class
        EntityClass ec = new EntityClass(entityClassName);
        ec.addSuperClasses(extendingClasses);
        ec.addBaseComponents(components);

        return ec;
    }

    private List<LoadedComponent> loadComponents(JsonNode compsNode) {
        return streamArrayChildNodes(compsNode)
                .map(compNode -> loadComponent(compNode))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private LoadedComponent loadComponent(JsonNode compNode) {
        // get the comp type: required
        String componentType = compNode.get(COMPONENT_TYPE_FIELD).asText();
        Class<? extends Component> compClass = getComponentClass(componentType);

        if (compClass == null) return null;  // if the compClass doesn't match an existing class

        // get the comp values: optional, must be: object
        // if vals are not present, create an empty object
        ObjectNode compValsObject;
        if (compNode.has(COMPONENT_VALUES_FIELD)) {
            compValsObject = (ObjectNode)compNode.get(COMPONENT_VALUES_FIELD);
        } else {
            compValsObject = jacksonMapper.createObjectNode();  // create an empty object
        }

        return new LoadedComponent(compClass, compValsObject);
    }

    private Component newComponent(LoadedComponent loadedComp) {
        // create a component of the specified values
        return jacksonMapper.convertValue(loadedComp.values, loadedComp.compType);
    }
    private void overrideComponent(LoadedComponent loadedComp, Entity entity) {
        Component comp = entity.getComponent(loadedComp.compType);
        if (comp == null) {
            logLoadError(ErrorType.ERROR, "initial entity tried to override non-existing component in class: "
                    + loadedComp.compType.getSimpleName() + " in entity: " + entity.name);
            return;
        }
        try {
            jacksonMapper.readerForUpdating(comp).readValue(loadedComp.values);
        } catch (IOException e) {
            e.printStackTrace();
            logLoadError(ErrorType.ERROR, "Some weird thing happened when overriding component: "
                    + loadedComp.compType.getSimpleName() + " for entity: " + entity.name );
        }
    }

    private List<Entity> loadInitEntities(World world, ArrayNode initEntitiesNode) {
        return streamArrayChildNodes(initEntitiesNode)
                .map(initEntityNode -> loadInitEntity(world, (ObjectNode) initEntityNode))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Entity loadInitEntity(World world, ObjectNode initEntityNode) {
        //TODO: initial entities should be able to define new components

        // get entity name: optional
        String entityName = "";
        if (initEntityNode.has(INIT_ENTITIES_NAME_FIELD)) {
            entityName = initEntityNode.get(INIT_ENTITIES_NAME_FIELD).asText();
        }

        // get useClass: required
        String useClass = initEntityNode.get(INIT_ENTITIES_USE_CLASS_FIELD).asText();

        // create the entity from the class
        final Entity entity = world.instanciateEntityClass(useClass, entityName);

        // override components: optional
        if (initEntityNode.has(INIT_ENTITIES_COMPONENTS_FIELD)) {
            loadComponents(initEntityNode.get(INIT_ENTITIES_COMPONENTS_FIELD)).forEach( loadedComp ->
                    overrideComponent(loadedComp, entity)
            );
        }
        return entity;
    }

    private Class<?> getClassInPackageList(String className, Set<String> packages) {
        ClassLoader classLoader = WorldLoaderOld.class.getClassLoader();

        List<Class<?>> classesFound = packages.stream()
                .map(pname -> {
                    try {
                        return classLoader.loadClass(pname + '.' + className);
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (classesFound.size() == 0) {
            // the class wasn't found
            return null;
        }
        else {
            if (classesFound.size() > 1) {
                logLoadError(ErrorType.WARNING, "multiple classes with equal name found in the specified packages. " +
                        "Returning the first. For class: " + className);
            }
            return classesFound.get(0);
        }
    }

    private Class<? extends Component> getComponentClass(String compClassName) {
        Class<?> compClassGeneric = getClassInPackageList(compClassName, compSysPackages);
        if (compClassGeneric == null) {
            logLoadError(ErrorType.ERROR, "component was not found in the specified compSysPackages: " + compClassName);
            return null;
        }

        try {
            return compClassGeneric.asSubclass(Component.class);

        } catch (ClassCastException e) {
            logLoadError(ErrorType.ERROR, "Component was not a subclass of Component: " + compClassName);
            return null;
        }
    }

    private JsonNode loadJson(String jsonPath) {
        // load the config file
        InputStream configInputStream = WorldLoaderOld.class.getClassLoader().getResourceAsStream(jsonPath);

        // check if the config file exist
        if (configInputStream == null) {
            logLoadError(ErrorType.ERROR, "invalid path for: " + jsonPath);
            return null;
        }

        try {
            ObjectMapper jsonMapper = new ObjectMapper();
            return jsonMapper.readTree(configInputStream);
        } catch (IOException e) {
            logLoadError(ErrorType.ERROR, "invalid json syntax in: " + jsonPath);
            return null;
        }
    }

    private boolean loadWorldConfigSchema() {
        if (worldConfigSchema == null) {
            try {
                JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();

                JsonNode worldConfigSchemaNode = loadJson(WORLD_CONFIG_SCHEMA_PATH);
                if (worldConfigSchemaNode == null) return false;

                worldConfigSchema = schemaFactory.getJsonSchema(worldConfigSchemaNode);

            } catch (ProcessingException e) {
                e.printStackTrace();
                logLoadError(ErrorType.ERROR, "invalid schema syntax for: " + WORLD_CONFIG_SCHEMA_PATH);
                return false;
            }
        }
        return true;
    }

    private boolean testAgainstWorldConfigSchema(JsonNode jsonNode) {
        if (! loadWorldConfigSchema()) return false;

        try {
            ProcessingReport configSchemaReport = worldConfigSchema.validate(jsonNode);

            if (! configSchemaReport.isSuccess()) {
                logLoadError(ErrorType.ERROR, "world config syntax error");
                System.out.println(configSchemaReport);
                return false;
            }

            return true;

        } catch (ProcessingException e) {
            e.printStackTrace();
            logLoadError(ErrorType.ERROR, "hmmm world config syntax error? ..");
            return false;
        }
    }

    private List<String> getArrayChildNodesAsText(JsonNode node) {
        List<String> list = new ArrayList<>();
        for (JsonNode subNode : node) {
            list.add(subNode.asText());
        }
        return list;
    }

    private Stream<String> streamArrayChildNodesAsText(JsonNode parentNode) {
        return getArrayChildNodesAsText(parentNode).stream();
    }

    private List<JsonNode> getArrayChildNodes(JsonNode parentNode) {
        List<JsonNode> childNodes = new ArrayList<>();
        for (JsonNode childNode : parentNode) {
            childNodes.add(childNode);
        }
        return childNodes;
    }

    private Stream<JsonNode> streamArrayChildNodes(JsonNode parentArrayNode) {
        return getArrayChildNodes(parentArrayNode).stream();
    }

    private void logLoadError(ErrorType errType, String cause) {
        System.err.println(errType.name + " in " + WorldLoader.class.getSimpleName() + " for file: " + configPath
                + "\n\tcause: " + cause);
    }

}
