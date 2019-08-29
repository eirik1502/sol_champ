package sol_engine.physics_module;

import sol_engine.utils.AbstractCloneable;

public abstract class PhysicsBodyShape extends AbstractCloneable<PhysicsBodyShape> {

    public static abstract class Simple extends PhysicsBodyShape {
    }

    public static class Circ extends PhysicsBodyShape.Simple {
        public float radius;

        public Circ(float radius) {
            this.radius = radius;
        }
    }

    public static class Rect extends PhysicsBodyShape.Simple {
        public float width, height;

        public Rect(float width, float height) {
            this.width = width;
            this.height = height;
        }
    }

//    public static class Composed extends PhysicsBodyShape {
//        public List<PhysicsBodyShape> shapes;
//    }

}