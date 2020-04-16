package sol_game.game

import org.joml.Vector2f

data class RectangleObstacle(
        val position: Vector2f,  // position of upper left corner
        val size: Vector2f  // x = width, y = height
)

data class CircleObstacle(
        val position: Vector2f,  // position in the center
        val radius: Float
)

data class SolStaticGameState(
        val rectangleWalls: List<RectangleObstacle>,
        val circleWalls: List<CircleObstacle>,
        var rectangleHoles: List<RectangleObstacle>,
        val circleHoles: List<CircleObstacle>
)