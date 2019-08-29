package sol_engine.physics_module;

import org.joml.Vector2f;
import sol_engine.utils.MathF;

public class CollisionFunctions {

    private static final Vector2f distVec = new Vector2f();
    private static final Vector2f closestRectPointToCircCenter = new Vector2f();
    private static final Vector2f rectCircVec = new Vector2f();

    public static boolean collisionCircCirc(final Vector2f pos1, final float radius1,
                                            final Vector2f pos2, final float radius2,
                                            final CollisionData modifyableCollisionData) {

        float maxDist = radius1 + radius2;
        float maxDistSquared = MathF.pow2(maxDist);

        pos2.sub(pos1, distVec);

        if (distVec.lengthSquared() >= maxDistSquared) {
            return false;
        }

        float dist = distVec.length();

        if (dist != 0) {
            modifyableCollisionData.penetrationDepth = maxDist - dist;
            distVec.mul(1 / dist, modifyableCollisionData.collisionVector); //optimized normalize
            return true;
        }

        //set default values if circles on same pos
        modifyableCollisionData.penetrationDepth = radius1;
        modifyableCollisionData.collisionVector.set(1, 0);
        return true;
    }

    public static boolean collisionRectRect(final Vector2f pos1, final Vector2f size1,
                                            final Vector2f pos2, final Vector2f size2,
                                            final CollisionData modifyableCollisionData) {
        pos2.sub(pos1, distVec);

        // Calculate half extents along x axis for each object
        float rect1HExtentX = size1.x / 2.0f;
        float rect2HExtentX = size2.x / 2.0f;

        // Calculate overlap on x axis
        float xOverlap = rect1HExtentX + rect2HExtentX - MathF.abs(distVec.x);

        // SAT test on x axis
        if (xOverlap > 0) {
            // Calculate half extents along y axis for each object
            float rect1HExtentY = size1.y / 2.0f;
            float rect2HExtentY = size2.y / 2.0f;

            // Calculate overlap on y axis
            float yOverlap = rect1HExtentY + rect2HExtentY - MathF.abs(distVec.y);

            // SAT test on y axis
            if (yOverlap > 0) {
                // Find out which axis is axis of least penetration
                if (xOverlap < yOverlap) {
                    // Point towards B knowing that n points from A to B
                    if (distVec.x < 0)
                        modifyableCollisionData.collisionVector.set(-1, 0);
                    else
                        modifyableCollisionData.collisionVector.set(1, 0);

                    modifyableCollisionData.penetrationDepth = xOverlap;
                    return true;
                } else {
                    // Point toward B knowing that n points from A to B
                    if (distVec.y < 0)
                        modifyableCollisionData.collisionVector.set(0, -1);
                    else
                        modifyableCollisionData.collisionVector.set(0, 1);

                    modifyableCollisionData.penetrationDepth = yOverlap;
                    return true;
                }
            }
        }

        return false;
    }


    public static boolean collisionRectCirc(final Vector2f posRect, final Vector2f sizeRect,
                                            final Vector2f posCirc, final float radiusCirc,
                                            final CollisionData modifyableCollisionData) {

        // Vector from A to B
        posCirc.sub(posRect, distVec);

        // Calculate half extents along each axis
        float rectHalfExtentX = sizeRect.x / 2;
        float rectHalfExtentY = sizeRect.y / 2;

        // Closest point on rect to center of circ
        closestRectPointToCircCenter.set(MathF.clamp(distVec.x, -rectHalfExtentX, rectHalfExtentX),
                MathF.clamp(distVec.y, -rectHalfExtentY, rectHalfExtentY));

        boolean circInsideRect = false;

        // Circle is inside the AABB, so we need to clamp the circle's center
        // to the closest edge
        if (distVec.equals(closestRectPointToCircCenter)) {
            //if the clamping above did not make closestRectPoint different from distVec, circ center inside rect
            circInsideRect = true;

            // Find closest axis
            if (MathF.abs(distVec.x / sizeRect.x) > MathF.abs(distVec.y / sizeRect.y)) {

                // Clamp to rect edge in x direction
                if (closestRectPointToCircCenter.x > 0)
                    closestRectPointToCircCenter.x = rectHalfExtentX;
                else
                    closestRectPointToCircCenter.x = -rectHalfExtentX;
            } else { //clamp to rect edge in y direction
                if (closestRectPointToCircCenter.y > 0)
                    closestRectPointToCircCenter.y = rectHalfExtentY;
                else
                    closestRectPointToCircCenter.y = -rectHalfExtentY;
            }
        }

        distVec.sub(closestRectPointToCircCenter, rectCircVec);
        float rectCircDistSquared = rectCircVec.lengthSquared();

        // Can now determine if there is a collision
        if (rectCircDistSquared > MathF.pow2(radiusCirc) && !circInsideRect) {
            return false;
        }

        // Avoided sqrt if no collision is found
        float rectCircDist = MathF.sqrt(rectCircDistSquared);


        if (rectCircDist == 0) {
            return false;
        }

        rectCircVec.mul(1 / rectCircDist, modifyableCollisionData.collisionVector);
        modifyableCollisionData.penetrationDepth = radiusCirc - rectCircDist;

        // Flip normal if circ inside rect
        if (circInsideRect) {
            modifyableCollisionData.collisionVector.negate();
            //penetration = rectCircDist; <--- this seems right, but works better without
        }
        return true;
    }

    public static boolean collisionCircRect(final Vector2f posCirc, final float radiusCirc,
                                            final Vector2f posRect, final Vector2f sizeRect,
                                            final CollisionData modifyableCollisionData) {
        boolean result = collisionRectCirc(posRect, sizeRect, posCirc, radiusCirc, modifyableCollisionData);
        if (result) {
            modifyableCollisionData.collisionVector.negate();
        }
        return result;
    }
}
