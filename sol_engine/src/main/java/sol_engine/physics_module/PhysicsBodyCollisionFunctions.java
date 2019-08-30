package sol_engine.physics_module;

import org.joml.Vector2f;

public class PhysicsBodyCollisionFunctions {

    public static boolean collision(final Vector2f pos1, final PhysicsBodyShape shape1,
                                    final Vector2f pos2, final PhysicsBodyShape shape2,
                                    final CollisionData modifyableCollisionData) {

        if (shape1 instanceof PhysicsBodyShape.Circ && shape2 instanceof PhysicsBodyShape.Circ) {
            PhysicsBodyShape.Circ circ1 = (PhysicsBodyShape.Circ) shape1;
            PhysicsBodyShape.Circ circ2 = (PhysicsBodyShape.Circ) shape2;
            return CollisionFunctions.collisionCircCirc(
                    new Vector2f(pos1.x, pos1.y), circ1.radius,
                    new Vector2f(pos2.x, pos2.y), circ2.radius,
                    modifyableCollisionData);

        } else if (shape1 instanceof PhysicsBodyShape.Rect && shape2 instanceof PhysicsBodyShape.Rect) {
            PhysicsBodyShape.Rect rect1 = (PhysicsBodyShape.Rect) shape1;
            PhysicsBodyShape.Rect rect2 = (PhysicsBodyShape.Rect) shape2;
            return CollisionFunctions.collisionRectRect(
                    new Vector2f(pos1.x, pos1.y), new Vector2f(rect1.width, rect1.height),
                    new Vector2f(pos2.x, pos2.y), new Vector2f(rect2.width, rect2.height),
                    modifyableCollisionData);

        } else if (shape1 instanceof PhysicsBodyShape.Circ && shape2 instanceof PhysicsBodyShape.Rect) {
            PhysicsBodyShape.Circ circ = (PhysicsBodyShape.Circ) shape1;
            PhysicsBodyShape.Rect rect = (PhysicsBodyShape.Rect) shape2;
            return CollisionFunctions.collisionCircRect(
                    new Vector2f(pos1.x, pos1.y), circ.radius,
                    new Vector2f(pos2.x, pos2.y), new Vector2f(rect.width, rect.height),
                    modifyableCollisionData);

        } else if (shape1 instanceof PhysicsBodyShape.Rect && shape2 instanceof PhysicsBodyShape.Circ) {
            PhysicsBodyShape.Rect rect = (PhysicsBodyShape.Rect) shape1;
            PhysicsBodyShape.Circ circ = (PhysicsBodyShape.Circ) shape2;
            return CollisionFunctions.collisionRectCirc(
                    new Vector2f(pos1.x, pos1.y), new Vector2f(rect.width, rect.height),
                    new Vector2f(pos2.x, pos2.y), circ.radius,
                    modifyableCollisionData);

        }

        throw new IllegalArgumentException("Cannot calculate collision between the given PhysicsBodyShapes: " + shape1.getClass().getName() + ", " + shape2.getClass().getName());
    }
}
