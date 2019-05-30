package sol_engine.ecs;

import com.google.gson.Gson;

public abstract class Component implements Cloneable {

    private static Gson gson = new Gson();

    /** check equality by gson conversion. Field order should be consistent */
    public static boolean areEqual(Component comp1, Component comp2) {
        return gson.toJson(comp1).equals(gson.toJson(comp2));
    }

    public Component clone() {
        try {
            return (Component) super.clone();
        }
        catch (ClassCastException e) {
            // this should not happen as every descending class is a component
            e.printStackTrace();
            return null;
        }
        catch (CloneNotSupportedException e) {
            // this should not happen as we implement cloneable
            e.printStackTrace();
            return null;
        }
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
