package sol_engine.ecs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class Component implements Cloneable {
    private static Logger logger = LoggerFactory.getLogger(Component.class);
    private static Gson gson = new Gson();
    private static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS);

    /**
     * check equality by gson conversion. Field order should be consistent
     */
    public static boolean areEqual(Component comp1, Component comp2) {
        return gson.toJson(comp1).equals(gson.toJson(comp2));
    }

    private Component internalClone() {
        try {
            return (Component) super.clone();
        } catch (ClassCastException e) {
            // this should not happen as every descending class is a component
            e.printStackTrace();
            return null;
        } catch (CloneNotSupportedException e) {
            // this should not happen as we implement cloneable
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Copys the given component values into this one.
     * This default method uses json serialization and deserialisation, and is thus slow.
     * OVerride this method to improve performance in often copied components
     *
     * @param fromComp component with values to be copied
     */
    public void copy(Component fromComp) {
        if (getClass().equals(fromComp.getClass())) {
            JsonNode fromCompTree = objectMapper.valueToTree(fromComp);
            try {
                objectMapper.readerForUpdating(this).readValue(fromCompTree);
                return;
            } catch (IOException e) {
                logger.warn("Could not copy the given component into this using default method of json (de)serializing." +
                        " From component: " + fromComp + ", this: " + this
                        + ". Because " + e);
                return;
            }
        }

        logger.warn("Trying to copy a component of another type into this using default method of json (de)serializing." +
                " From component: " + fromComp + ", this: " + this);
    }

    public Component clone() {
        return internalClone();
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T cloneAs(Class<T> compType) {
        return (T) this.internalClone();
    }

    // Don't think that cloning to class is necessary
//    @SuppressWarnings("unchecked")
//    default <T extends Component> T cloneComponentAs(Class<T> asCompType) {
//        try {
//            return (T)clone();
//        }
//        catch (ClassCastException e) {
//            System.err.println("Casting a cloned component to wrong component type");
//            e.printStackTrace();
//            return null;
//        }
//        catch (CloneNotSupportedException e) {
//            // this should not happen as we implement cloneable
//            e.printStackTrace();
//            return null;
//        }
//    }


    // don't want this equlas method, because we would have to use a slow hashCode function
//    /** check equality by gson conversion. Field order should be consistent */
//    @Override
//    public boolean equals(Object o) {
//        if (o instanceof Component) {
//            return gson.toJson(this).equals(gson.toJson(o));
//        }
//        return false;
//    }
}
