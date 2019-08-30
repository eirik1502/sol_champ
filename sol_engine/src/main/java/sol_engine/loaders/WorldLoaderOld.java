package sol_engine.loaders;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import sol_engine.ecs.Component;
import sol_engine.ecs.EntityClass;
import sol_engine.ecs.SystemBase;
import sol_engine.ecs.World;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is the main loader,
 * used to load the world configs.
 * <p>
 * The world exists of
 * - modulesHandler
 * - entities
 * - components
 * - componentSystems
 */
public class WorldLoaderOld {

    private static final String
            COMPSYS_PACKAGES_FIELD = "compSysPackages",
            COMPONENT_SYSTEMS_FIELD = "componentSystems",
            ENTITY_CLASSES_FIELD = "entityClasses",
            ENTITY_CLASS_EXTENDS_CLASSES_FIELD = "extendsClasses",
            INITIAL_ENTITIES_FIELD = "initialEntities",
            COMPONENTS_FIELD = "components",
            INIT_ENTITIES_USE_CLASS_FIELD = "useClass",
            INIT_ENTITIES_NAME_FIELD = "name",
            INIT_ENTITIES_OVERRIDE_COMPONENTS_FIELD = "overrideComponents";

    public static final String ENGINE_SYS_COMP_PACKAGE = "sol_engine";


    private static Set<String> compSysPackages = new HashSet<>();
    private static ClassLoader classLoader = WorldLoaderOld.class.getClassLoader();
    private static Gson gson = new Gson();
    private static Type listStringType = new TypeToken<List<String>>() {
    }.getType();

    private enum ErrorType {
        ERROR("ERROR"),
        WARNING("WARNING");

        public final String name;

        ErrorType(String name) {
            this.name = name;
        }
    }

    private static String configPath;


    public static void loadIntoWorld(World world, String configPath) {
        WorldLoaderOld.configPath = configPath;

        Gson gson = new Gson();

        compSysPackages.add(ENGINE_SYS_COMP_PACKAGE);

        InputStream configInputStream = WorldLoaderOld.class.getClassLoader().getResourceAsStream(configPath);

        // check if the config file exist
        if (configInputStream == null) {
            System.err.println("ERROR loading world config. Filepath could not be located: " + configPath);
            return;
        }

        JsonReader jsonFileReader = new JsonReader(new InputStreamReader(configInputStream));
        JsonParser jsonParser = new JsonParser();

        JsonElement worldConfigElem = jsonParser.parse(jsonFileReader);

        // check if the world config starts with a jsonObject
        if (!worldConfigElem.isJsonObject()) {
            System.err.println("ERROR world config did not setup with a json object");
            return;
        }

        JsonObject worldConfig = worldConfigElem.getAsJsonObject();

        // check if there are compSys packages and store them
        if (!worldConfig.has(COMPSYS_PACKAGES_FIELD)) {
            System.err.println("WARNING no compSys packages are listed, looking only in the engine package." +
                    " Use the array field 'compSysPackages'");
        } else {
            // add all listed compSys packages
            JsonArray compSysPackagesJArr = worldConfig.getAsJsonArray(COMPSYS_PACKAGES_FIELD);
            compSysPackages.addAll(gson.fromJson(compSysPackagesJArr, listStringType));
        }


        // check if there are component systems and add them
        if (!worldConfig.has(COMPONENT_SYSTEMS_FIELD)) {
            System.err.println("warning world config has no systems");
        } else {
            JsonArray componentSystemNamesArr = worldConfig.getAsJsonArray(COMPONENT_SYSTEMS_FIELD);
            List<String> componentSystemNames = gson.fromJson(componentSystemNamesArr, listStringType);

            // add systems
            componentSystemNames.forEach(sysName -> {
                // check that the system is an existing class and retrieve it
                Class<?> sysClassGeneric = getClassInPackageList(sysName, compSysPackages);
                if (sysClassGeneric == null) {
                    System.err.println("ERROR system class not found for: " + sysName);
                    return;
                }

                // check that the given system is a component system and cast it
                Class<? extends SystemBase> sysClass;
                try {
                    sysClass = sysClassGeneric.asSubclass(SystemBase.class);
                } catch (ClassCastException e) {
                    System.err.println("ERROR component system given was not a subclass of component system: " + sysName);
                    return;
                }

                world.addSystem(sysClass);
            });
        }

        // check that there are entityClasses and add them
        if (worldConfig.has(ENTITY_CLASSES_FIELD)) {
            List<EntityClass> entityClasses = loadEntityClasses(worldConfig.getAsJsonObject(ENTITY_CLASSES_FIELD));
            entityClasses.forEach(world::addEntityClass);
        } else {
            System.err.println("WARNING no entitiy classes in config");
        }

        // check and add initial entities
        if (worldConfig.has(INITIAL_ENTITIES_FIELD)) {
            JsonElement initEntitiesJElem = worldConfig.get(INITIAL_ENTITIES_FIELD);
            if (initEntitiesJElem.isJsonArray()) {
                loadInitEntitiesIntoWorld(world, initEntitiesJElem.getAsJsonArray());
            } else System.err.println("ERROR initialEntities was not a json object");
        } else System.err.println("WARNING no initial entities listed. Use key 'initialEntities'");
    }

    private static void loadInitEntitiesIntoWorld(World world, JsonArray initEntitiesJArr) {
        initEntitiesJArr.forEach(initEntJElem -> {
            if (initEntJElem.isJsonObject()) {
                loadInitEntityIntoWorld(world, initEntJElem.getAsJsonObject());
            } else logLoadError(ErrorType.ERROR, "an initial entity is not a json object");
        });
    }
//    INIT_ENTITIES_USE_CLASS_FIELD = "useClass",
//    INIT_ENTITIES_NAME_FIELD = "name",
//    INIT_ENTITIES_OVERRIDE_COMPONENTS_FIELD = "overrideComponents";

    private static void loadInitEntityIntoWorld(World world, JsonObject initEntJObj) {
        String entName = "";
        if (initEntJObj.has(INIT_ENTITIES_NAME_FIELD)) {
            JsonElement entNameJElem = initEntJObj.get(INIT_ENTITIES_NAME_FIELD);
            if (entNameJElem.isJsonPrimitive() && entNameJElem.getAsJsonPrimitive().isString()) {
                entName = entNameJElem.getAsString();
            } else logLoadError(ErrorType.ERROR, "An initial entity name was not a string");
        } else logLoadError(ErrorType.WARNING, "An initial entity did not have a name");

        String useClass = null;
        if (initEntJObj.has(INIT_ENTITIES_USE_CLASS_FIELD)) {
            JsonElement entUseClassJElem = initEntJObj.get(INIT_ENTITIES_USE_CLASS_FIELD);
            if (entUseClassJElem.isJsonPrimitive() && entUseClassJElem.getAsJsonPrimitive().isString()) {
                useClass = entUseClassJElem.getAsString();
            } else logLoadError(ErrorType.ERROR, "An initial entity 'useClass' was not a string");
        } else logLoadError(ErrorType.ERROR, "An initial entity did not use a class");


        // check if useClass was not found
        if (useClass == null) return;

        EntityClass entityClass = world.getEntityClass(useClass);

        // check if the class doesn't exist
        if (entityClass == null) {
            logLoadError(ErrorType.ERROR, "An initial entity's 'useClass' was not an existing entityClass" +
                    "for initialEntity: " + entName + " for useClass: " + useClass);
            return;
        }

        if (initEntJObj.has(INIT_ENTITIES_OVERRIDE_COMPONENTS_FIELD)) {
            JsonElement entOverrideCompsJElem = initEntJObj.get(INIT_ENTITIES_OVERRIDE_COMPONENTS_FIELD);
            if (entOverrideCompsJElem.isJsonObject()) {
                initEntOverrideComponents(world, entityClass, entOverrideCompsJElem.getAsJsonObject());
            } else logLoadError(ErrorType.ERROR, "An initial entity 'overrideComponents' was not a json object");
        } // may not override any components

        //TODO: initial entities should be able to define new components

    }

    private static void initEntOverrideComponents(World world, EntityClass entClass, JsonObject overrideCompsJObj) {

    }

    private static List<EntityClass> loadEntityClasses(JsonObject entityClassesJObj) {
        return entityClassesJObj.entrySet().stream()
                .map(ecsEntry -> {

                    String entityClassName = ecsEntry.getKey();
                    JsonElement entityClassElem = ecsEntry.getValue();

                    if (entityClassElem.isJsonObject()) {
                        return loadEntityClass(entityClassName, entityClassElem.getAsJsonObject());
                    } else {
                        System.err.println("ERROR entity class component was not a json object");
                        return null;
                    }

                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static EntityClass loadEntityClass(String entityClassName, JsonObject entityClassValsJObj) {
        EntityClass entityClass = new EntityClass(entityClassName);

        // handle extending classes
        if (entityClassValsJObj.has(ENTITY_CLASS_EXTENDS_CLASSES_FIELD)) {
            JsonElement extendsObjectsElem = entityClassValsJObj.get(ENTITY_CLASS_EXTENDS_CLASSES_FIELD);
            if (extendsObjectsElem.isJsonArray()) {
                List<String> extendsClasses = gson.fromJson(extendsObjectsElem.getAsJsonArray(), listStringType);
                entityClass.addSuperClasses(extendsClasses);
            } else {
                System.err.println("ERROR extendsClasses field did not contain a jason array. EntityClass: " + entityClassName);
            }
        }

        // handle components
        if (entityClassValsJObj.has(COMPONENTS_FIELD)) {
            JsonElement compsValsJElem = entityClassValsJObj.get(COMPONENTS_FIELD);

            if (compsValsJElem.isJsonObject()) {
                List<Component> comps = loadComponents(compsValsJElem.getAsJsonObject());
                entityClass.addBaseComponents(comps);
            } else {
                System.err.println("ERROR entity class components is was not a json object. EntityClass: " + entityClassName);
            }
        } else {
            System.err.println("WARNING entity class loaded without any base components. EntityClass: " + entityClassName);
        }

        return entityClass;
    }

    private static List<Component> loadComponents(JsonObject compsJObj) {
        return compsJObj.entrySet().stream()
                .map(compEntry -> {
                    String compClass = compEntry.getKey();
                    JsonElement compValsElem = compEntry.getValue();

                    if (compValsElem.isJsonObject()) {
                        return loadComponent(compClass, compValsElem.getAsJsonObject());
                    } else {
                        System.err.println("ERROR the values of component class component was not a json object. " +
                                "comp class: " + compClass);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static Component loadComponent(String compClassName, JsonObject compVals) {
        // get the class of the component
        Class<? extends Component> compClass = loadComponentClass(compClassName);
        if (compClass == null) {
            return null;
        }

        Component comp = gson.fromJson(compVals, compClass);

        return comp;
    }

    private static Component instanciateComponentClass(Class<? extends Component> compClass) {
        try {
            Constructor<? extends Component> compConstructor = compClass.getDeclaredConstructor();
            return compConstructor.newInstance();

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("ERROR No public no-arg constructor exists for the loaded component: " + compClass);
            return null;
        }
    }

    private static Class<? extends Component> loadComponentClass(String compClassName) {
        Class<?> compClassGeneric = getClassInPackageList(compClassName, compSysPackages);

        if (compClassGeneric == null) {
            System.err.println("ERROR a component class was not found in the specified compSys packages. " +
                    "Component: " + compClassName);

            return null;
        }

        try {
            Class<? extends Component> compClass = compClassGeneric.asSubclass(Component.class);
            return compClass;

        } catch (ClassCastException e) {
            System.err.println("ERROR a component class was not a subclass of Component. Component: " + compClassName);

            return null;
        }
    }

    private static Class<?> getClassInPackageList(String className, Set<String> packages) {
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
        } else {
            if (classesFound.size() > 1) {
                System.err.println("WARNING multiple classes with equal name found in the specified packages. " +
                        "Returning the first. For class: " + className);
            }
            return classesFound.get(0);
        }
    }

    private static void logLoadError(ErrorType errType, String cause) {
        System.err.println(errType.name + " in " + WorldLoaderOld.class.getSimpleName() + " for file: " + configPath
                + "\n\tcause: " + cause);
    }
}


//        // get all relevant packages and sub packages
//        Arrays.stream(classLoader.getDefinedPackages())
//                .filter(p -> {
//                    Set<String> pnames = new HashSet<>(Arrays.asList(p.getName().split("\\.")));
//                    return ! Collections.disjoint(pnames, compSysPackages);
//                })
//                .forEach(p -> System.out.println(p.getName()));