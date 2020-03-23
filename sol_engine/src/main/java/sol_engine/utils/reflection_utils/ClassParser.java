package sol_engine.utils.reflection_utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.network_input.NetInputPacket;
import sol_engine.utils.collections.Pair;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ClassParser<K> {
    private final Logger logger = LoggerFactory.getLogger(ClassParser.class);

    private final Class<K> classType;
    private final Map<Class<?>, Set<String>> fieldNamesByType;
    private final Map<String, Field> fieldsByName;


    public ClassParser(Class<K> classType) {
        boolean classHasNoArgConstructor = Arrays.stream(classType.getConstructors())
                .anyMatch(constr -> constr.getParameterCount() == 0);

        if (!classHasNoArgConstructor) {
            logger.error("The class given does not have a no arg constructor");
            throw new IllegalArgumentException("The class given does not have a no arg constructor");
        }

        this.classType = classType;
        fieldsByName = Arrays.stream(classType.getDeclaredFields())
                .peek(field -> field.setAccessible(true))  // in case fields are private, like kotlin classes
                .collect(Collectors.toMap(
                        Field::getName,
                        Function.identity()
                ));
        fieldNamesByType = new HashMap<>();
        fieldsByName.forEach((fieldName, field) -> {
            fieldNamesByType.computeIfAbsent(field.getType(), type -> new HashSet<>())
                    .add(fieldName);
        });
    }

    @SuppressWarnings("unchecked")
    public <T> Map<String, T> parseFieldsOfType(Class<T> type, K o) {
        if (!classType.isInstance(o)) {
            logger.error("Parsing an object not of the specified class. Empty map will be returned");
            return new HashMap<>();
        }

        return fieldNamesByType.get(type).stream()
                .map(fieldName -> new Pair<>(fieldName, fieldsByName.get(fieldName)))
                .map(fieldByName -> {
                    String fieldName = fieldByName.getFirst();
                    Field field = fieldByName.getLast();
                    try {
                        T value = (T) field.get(o);
                        return new Pair<>(fieldName, value);
                    } catch (IllegalAccessException e) {
                        logger.warn("Could not get the value of the field: " + fieldName + ", for object: " + o + ". Skipping field");
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getLast));
    }

//    public K createObject(Map<String, ?> valuesByName) {
//        K obj = ClassUtils.instanciateNoarg(classType);
//
//    }
}
