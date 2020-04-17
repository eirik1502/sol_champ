package sol_game.game_state

import org.joml.Vector2f
import sol_engine.utils.math.MathF
import java.lang.IllegalArgumentException

object SolGameStateFuncs {

    fun closestObject(fromObject: ObjectState, toObjects: List<ObjectState>): Vector2f {
        if (toObjects.isEmpty()) throw IllegalArgumentException("toObjects must contain at least one object")
        val distances = toObjects.map { distanceOuter(fromObject, it) }
        return distances.reduce { minVec, newVec -> if (minVec.lengthSquared() < newVec.lengthSquared()) minVec else newVec }
    }

    fun closestWall(fromObject: ObjectState, staticState: SolStaticGameState): Vector2f =
            closestObject(fromObject, staticState.walls)

    fun closestHole(fromObject: ObjectState, staticState: SolStaticGameState): Vector2f =
            closestObject(fromObject, staticState.holes)

    /**
     * Distance from the center of the first object to the center of the second object
     */
    fun distanceInner(fromObject: ObjectState, toObject: ObjectState): Vector2f {
        return when (fromObject) {
            is CircleObjectState -> when (toObject) {
                is CircleObjectState -> distanceInnerCircCirc(fromObject, toObject)
                is RectangleObjectState -> distanceInnerCircRect(fromObject, toObject)
                else -> Vector2f()
            }
            is RectangleObjectState -> when (toObject) {
                is CircleObjectState -> distanceInnerRectCirc(fromObject, toObject)
                is RectangleObjectState -> distanceInnerRectRect(fromObject, toObject)
                else -> Vector2f()
            }
            else -> Vector2f()
        }
    }

    /**
     * Distance from the outside of the first object to the outside of the second object
     */
    fun distanceOuter(fromObject: ObjectState, toObject: ObjectState): Vector2f {
        return when (fromObject) {
            is CircleObjectState -> when (toObject) {
                is CircleObjectState -> distanceOuterCircCirc(fromObject, toObject)
                is RectangleObjectState -> distanceOuterCircRect(fromObject, toObject)
                else -> Vector2f()
            }
            is RectangleObjectState -> when (toObject) {
                is CircleObjectState -> distanceOuterRectCirc(fromObject, toObject)
                is RectangleObjectState -> throw NotImplementedError("outer distnace for rect rect is not yet implemented")
                else -> Vector2f()
            }
            else -> Vector2f()
        }
    }

    private fun distanceInnerCircCirc(fromObject: CircleObjectState, toObject: CircleObjectState): Vector2f {
        return toObject.position.sub(fromObject.position, Vector2f())
    }


    private fun distanceInnerCircRect(fromCircle: CircleObjectState, toRectangle: RectangleObjectState): Vector2f {
        return toRectangle.position.sub(fromCircle.position, Vector2f())
    }

    private fun distanceInnerRectCirc(fromRectangle: RectangleObjectState, toCircle: CircleObjectState): Vector2f {
        return distanceInnerCircRect(toCircle, fromRectangle).negate()
    }

    private fun distanceInnerRectRect(fromRectangle: RectangleObjectState, toRectangle: RectangleObjectState): Vector2f {
        return toRectangle.position.sub(fromRectangle.position)
    }

    private fun distanceOuterCircCirc(fromObject: CircleObjectState, toObject: CircleObjectState): Vector2f {
        val distanceInner = distanceInnerCircCirc(fromObject, toObject)
        return if (distanceInner.lengthSquared() == 0f)
            distanceInner
        else
            distanceInner.normalize(distanceInner.length() - fromObject.radius - toObject.radius)
    }

    private fun distanceOuterCircRect(fromCircle: CircleObjectState, toRectangle: RectangleObjectState): Vector2f {
        return distanceOuterRectCirc(toRectangle, fromCircle).negate()
    }

    private fun distanceOuterRectCirc(fromRectangle: RectangleObjectState, toCircle: CircleObjectState): Vector2f {
        if (fromRectangle.size.x == 0f || fromRectangle.size.y == 0f || toCircle.radius == 0f) {
            return Vector2f()
        }

        val rectCenterPoint = fromRectangle.position //fromRectangle.size.mul(0.5f, Vector2f()).add(fromRectangle.position)

        // inner distance from rect to circ
        val distanceInner = toCircle.position.sub(rectCenterPoint, Vector2f())

        // Calculate half extents along each axis
        val rectHalfExtentX: Float = fromRectangle.size.x / 2
        val rectHalfExtentY: Float = fromRectangle.size.y / 2

        val distanceClampedInsideRect = Vector2f(
                MathF.clamp(distanceInner.x, -rectHalfExtentX, rectHalfExtentX),
                MathF.clamp(distanceInner.y, -rectHalfExtentY, rectHalfExtentY)
        )

        //if the clamping above did not make closestRectPoint different from distVec, circ center inside rect
        val circCenterInsideRect = distanceInner == distanceClampedInsideRect

        val closestPointOnRectEdge = when {
            MathF.abs(distanceInner.x / fromRectangle.size.x)
                    > MathF.abs(distanceInner.y / fromRectangle.size.y) ->
                when {
                    distanceClampedInsideRect.x > 0 -> Vector2f(rectHalfExtentX, distanceClampedInsideRect.y)
                    else -> Vector2f(-rectHalfExtentX, distanceClampedInsideRect.y)
                }
            else ->
                when {
                    distanceClampedInsideRect.y > 0 ->
                        Vector2f(distanceClampedInsideRect.x, rectHalfExtentY)
                    else ->
                        Vector2f(distanceClampedInsideRect.x, -rectHalfExtentY)
                }
        }
        val globalClosestPointOnRectEdge = rectCenterPoint.add(closestPointOnRectEdge, Vector2f())
        val distanceClosestRectPointToCircCenter = toCircle.position.sub(globalClosestPointOnRectEdge, Vector2f())

        return if (circCenterInsideRect || distanceClosestRectPointToCircCenter.lengthSquared() <= toCircle.radius * toCircle.radius) {
            Vector2f()
        } else {
            distanceClosestRectPointToCircCenter.normalize(distanceClosestRectPointToCircCenter.length() - toCircle.radius)
        }
    }
}