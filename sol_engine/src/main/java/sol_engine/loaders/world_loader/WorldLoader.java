package sol_engine.loaders.world_loader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import sol_engine.ecs.*;
import sol_engine.loaders.JsonLoader;
import sol_engine.loaders.JsonSchemaValidator;
import sol_engine.loaders.JsonUtils;
import sol_engine.loaders.LoadersLogger;
import sol_engine.utils.Function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WorldLoader {

    private class LoadedComponent {
        public final Class<? extends Component> compType;
        public final ObjectNode values;

        public LoadedComponent(Class<? extends Component> compType, ObjectNode values) {
            this.compType = compType;
            this.values = values;
        }
    }

    private static final String WORLD_CONFIG_SCHEMA_PATH = "worldConfigSchema.json";
    public static final String ENGINE_PACKAGE_ROOT_PATH = "sol_engine";

    private static final ObjectMapper defaultMapper = new ObjectMapper();

    private String configPath;
    private CompSystemScanner compSystemScanner = new CompSystemScanner();


    public WorldLoader() {
    }


    public void loadIntoWorld(World world, String configPath) {
        System.out.println("Loading world");

        JsonNode configNode = JsonLoader.loadJson(configPath);
        if (configNode == null) return;  // return if the json wasn't loaded

        if (!JsonSchemaValidator.validateJson(WORLD_CONFIG_SCHEMA_PATH, configNode)) return;
        this.configPath = configPath;

        compSystemScanner.addPackage("sol_engine");
        withFieldIfExists(configNode, WorldLoaderFields.ROOT_PACKAGE_FIELD, rootPackageNode -> {
            compSystemScanner.addPackage(rootPackageNode.asText());
        });
        LoadersLogger.logger.info(compSystemScanner.toString());


        List<Class<? extends SystemBase>> systemsLoaded = withFieldIfExistsReturn(configNode, WorldLoaderFields.COMPONENT_SYSTEMS_FIELD,
                systemField -> SystemsLoader.loadComponentSystems(systemField, compSystemScanner),
                ArrayList::new
        );

        List<EntityClass> entityClassesLoaded = withFieldIfExistsReturn(configNode, WorldLoaderFields.ENTITY_CLASSES_FIELD,
                this::loadEntityClasses,
                ArrayList::new
        );

        systemsLoaded.forEach(world::addSystem);
        entityClassesLoaded.forEach(world::addEntityClass);

        List<Entity> initialEntitiesLoaded = withFieldIfExistsReturn(configNode, WorldLoaderFields.INITIAL_ENTITIES_FIELD,
                initialEntitiesNode -> loadInitEntities(world, (ArrayNode) initialEntitiesNode),
                ArrayList::new
        );

        initialEntitiesLoaded.forEach(world::addEntity);

        // log results
        LoadersLogger.logger.info("Systems loaded: " + systemsLoaded.stream()
                .map(Class::getSimpleName).collect(Collectors.joining(", ")));
        LoadersLogger.logger.info("Entity classes loaded: " + entityClassesLoaded.stream()
                .map(ec -> ec.className + "\n\t" + ec.getComponentsView().stream()
                        .map(c -> c.getClass().getSimpleName())
                        .collect(Collectors.joining("\n\t"))
                )
                .collect(Collectors.joining("\n")));
        LoadersLogger.logger.info("Initial entities loaded: " + initialEntitiesLoaded.stream()
                .map(e -> e.name + "\n\t" + e.getComponentTypeGroup().stream()
                        .map(c -> c.getSimpleName())
                        .collect(Collectors.joining("\n\t"))
                )
                .collect(Collectors.joining("\n")));
    }


    private List<EntityClass> loadEntityClasses(JsonNode entityClassesNode) {
        return JsonUtils.streamArrayChildNodes(entityClassesNode)
                .map(entityClassNode -> loadEntityClass(entityClassNode))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private EntityClass loadEntityClass(JsonNode entityClassNode) {
        // get entity class name: required
        String entityClassName = entityClassNode.get(WorldLoaderFields.ENTITY_CLASS_CLASS_NAME_FIELD).asText();

        // get extending classes: optional
        List<String> extendingClasses = new ArrayList<>();
        withFieldIfExists(entityClassNode, WorldLoaderFields.ENTITY_CLASS_EXTENDS_CLASSES_FIELD, extendsField ->
                extendingClasses.addAll(JsonUtils.getArrayChildNodesAsText(extendsField))
        );

        // get components: optional
        List<Component> components = withFieldIfExistsReturn(entityClassNode, WorldLoaderFields.ENTITY_CLASS_COMPONENTS_FIELD,
                entityClassCompsNode -> loadComponents(entityClassCompsNode).stream()
                        .map(loadedComp -> newComponent(loadedComp))
                        .collect(Collectors.toList()),
                ArrayList::new
        );

        // create the entity class
        return new EntityClass(entityClassName)
                .addSuperClasses(extendingClasses)
                .addBaseComponents(components);
    }

    private List<LoadedComponent> loadComponents(JsonNode compsNode) {
        return JsonUtils.streamArrayChildNodes(compsNode)
                .map(compNode -> loadComponent(compNode))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private LoadedComponent loadComponent(JsonNode compNode) {
        // get the comp type: required
        String compClassSimpleName = compNode.get(WorldLoaderFields.COMPONENT_TYPE_FIELD).asText();
//        Class<? extends Component> compClass = getComponentClass(componentType);
        Class<? extends Component> compClass = compSystemScanner.getCompClassBySimpleName(compClassSimpleName);

        if (compClass == null) {
            LoadersLogger.logger.warning("Loaded component that is not of a class. Component: " + compClassSimpleName);
            return null;  // if the compClass doesn't match an existing class
        }

        // get the comp values: optional, must be: object
        ObjectNode compValsObject = withFieldIfExistsReturn(compNode, WorldLoaderFields.COMPONENT_VALUES_FIELD,
                valuesNode -> (ObjectNode) valuesNode,
                defaultMapper::createObjectNode
        );

        return new LoadedComponent(compClass, compValsObject);
    }

    private Component newComponent(LoadedComponent loadedComp) {
        ObjectMapper compMapper = new ObjectMapper();
        compMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, "_subClass");
        Component newComp = compMapper.convertValue(loadedComp.values, loadedComp.compType);
        System.out.println("new component created: " + newComp);
        return newComp;
    }

    private void overrideComponent(LoadedComponent loadedComp, Entity entity) {
        Component comp = entity.getComponent(loadedComp.compType);
        if (comp == null) {
            LoadersLogger.logger.severe("initial entity tried to override non-existing component.\n\tComponent: "
                    + loadedComp.compType.getSimpleName() + ". Entity: " + entity.name);
            return;
        }
        try {
            defaultMapper.readerForUpdating(comp).readValue(loadedComp.values);
        } catch (IOException e) {
            e.printStackTrace();
            LoadersLogger.logger.severe("Some weird thing happened when overriding component: "
                    + loadedComp.compType.getSimpleName() + " for entity: " + entity.name);
        }
    }

    private List<Entity> loadInitEntities(World world, ArrayNode initEntitiesNode) {
        return JsonUtils.streamArrayChildNodes(initEntitiesNode)
                .map(initEntityNode -> loadInitEntity(world, (ObjectNode) initEntityNode))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Entity loadInitEntity(World world, ObjectNode initEntityNode) {
        //TODO: initial entities should be able to define new components

        String entityName = withFieldIfExistsReturn(initEntityNode, WorldLoaderFields.INIT_ENTITIES_NAME_FIELD,
                JsonNode::asText,
                () -> ""
        );

        // required
        String useClass = initEntityNode.get(WorldLoaderFields.INIT_ENTITIES_USE_CLASS_FIELD).asText();

        // create the entity from the class
        final Entity entity = world.addEntity(entityName, useClass);

        // override components: optional
        withFieldIfExists(initEntityNode, WorldLoaderFields.INIT_ENTITIES_COMPONENTS_FIELD, compsNode ->
                loadComponents(compsNode).forEach(loadedComp -> overrideComponent(loadedComp, entity))
        );

        return entity;
    }


    private void withFieldIfExists(JsonNode fromNode, String fieldName, Consumer<JsonNode> withFieldNode) {
        if (fromNode.has(fieldName)) {
            withFieldNode.accept(fromNode.get(fieldName));
        }
    }

    private <T> T withFieldIfExistsReturn(JsonNode fromNode, String fieldName,
                                          Function.OneArgReturn<JsonNode, T> withFieldNode, Function.NoArgReturn<T> otherwise) {
        return fromNode.has(fieldName)
                ? withFieldNode.invoke(fromNode.get(fieldName))
                : otherwise.invoke();
    }
}


//    private enum ErrorType {
//        ERROR("ERROR"),
//        WARNING("WARNING");
//
//        public final String name;
//
//        ErrorType(String name) {
//            this.name = name;
//        }
//    }

// create a component of the specified values
//        StdDeserializer<Object> objectDeserializer = createStandardDeserializer(Object.class, (jp, ctx) -> {
//            jp.getParsingContext().
//                    JsonNode node = jp.getCodec().readTree(jp);
//            System.out.println();
//            return null;
//        });

//                jacksonMapper.conv

//    private Class<?> getClassInPackageList(String className, Set<String> packages) {
//        ClassLoader classLoader = WorldLoaderOld.class.getClassLoader();
//        List<Class<?>> classesFound = packages.stream()
//                .map(pname -> {
//                    try {
//                        return classLoader.loadClass(pname + '.' + className);
//                    } catch (ClassNotFoundException e) {
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        if (classesFound.size() == 0) {
//            // the class wasn't found
//            return null;
//        } else {
//            if (classesFound.size() > 1) {
//                logLoadError(ErrorType.WARNING, "multiple classes with equal name found in the specified packages. " +
//                        "Returning the first. For class: " + className);
//            }
//            return classesFound.get(0);
//        }
//    }

//    private Class<? extends Component> getComponentClass(String compClassName) {
//        Class<?> compClassGeneric = getClassInPackageList(compClassName, compSysPackages);
//        if (compClassGeneric == null) {
//            logLoadError(ErrorType.ERROR, "component was not found in the specified compSysPackages: " + compClassName);
//            return null;
//        }
//
//        try {
//            return compClassGeneric.asSubclass(Component.class);
//
//        } catch (ClassCastException e) {
//            logLoadError(ErrorType.ERROR, "Component was not a subclass of Component: " + compClassName);
//            return null;
//        }
//    }


//    private void logLoadError(ErrorType errType, String cause) {
//        System.err.println(errType.name + " in " + WorldLoader.class.getSimpleName() + " for file: " + configPath
//                + "\n\tcause: " + cause);
//    }


//    private Pair<Set<String>, Set<String>> getDescendingCompSysClassPaths(String rootPackage) {
//        LinkedList<Path> remaindingFiles = new LinkedList<>();
//        try {
//            URL rootUrl = getClass().getClassLoader().getResource(rootPackage);
//            if (rootUrl == null) {
//                return new Pair<>(new HashSet<>(), new HashSet<>());
//            }
//            remaindingFiles.add(Paths.get(rootUrl.toURI()));
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//
//        Function.TwoArgReturn<String, String, Boolean> fileFirstNameEndsWith = (filename, suffix) -> {
//            return filename.split(Pattern.quote("."))[0].endsWith(suffix);
//        };
//
//        Set<String> compClassPaths = new HashSet<>();
//        Set<String> systemClassPaths = new HashSet<>();
//
//        while (!remaindingFiles.isEmpty()) {
//            Path path = remaindingFiles.poll();
//            try {
//                Files.newDirectoryStream(path).forEach(p -> {
//                    System.out.println(p);
//                    String fileName = p.toFile().getName();
//                    if (p.toFile().isDirectory()) remaindingFiles.add(p);
//                    else if (fileFirstNameEndsWith.invoke(fileName, "Comp")) {
//                        compClassPaths.add(generateClassPathTo(p, rootPackage));
//                    } else if (fileFirstNameEndsWith.invoke(fileName, "System")) {
//                        systemClassPaths.add(generateClassPathTo(p, rootPackage));
//                    }
//
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return new Pair<>(compClassPaths, systemClassPaths);
//    }

//    private String generateClassPathTo(Path childPath, String ancestorDirName) {
//        String className = childPath.toFile().getName().split(Pattern.quote("."))[0];
//        Path traversePath = childPath;
//        while (true) {
//            traversePath = traversePath.getParent();
//            String pName = traversePath.toFile().getName();
//            className = pName + "." + className;
//            if (pName.equals(ancestorDirName)) break;
//        }
//        return className;
//    }

//    private interface StdDeserializerI<T> {
//        T deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException;
//    }
//
//    private <
//            T> StdDeserializer<T> createStandardDeserializer(Class<T> forClass, StdDeserializerI<T> deserializer) {
//        return new StdDeserializer<T>(forClass) {
//            @Override
//            public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//                return deserializer.deserialize(jp, ctxt);
//            }
//        };
//    }