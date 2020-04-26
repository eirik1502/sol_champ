package sol_game.game_state

import org.joml.Vector2f
import sol_engine.ecs.World


data class SolGameState(
        val gameStarted: Boolean,
        val gameEnded: Boolean,
        val playerIndexWon: Int,
        val staticGameState: SolStaticGameState,
        val charactersState: List<SolCharacterState>,
        val world: World
)

data class SolCharacterState(
        val physicalObject: CircleObjectState = CircleObjectState(Vector2f(), 0f),
        val velocity: Vector2f = Vector2f(),
        val acceleration: Vector2f = Vector2f(),
        val rotation: Float = 0f,
        val damage: Float = 0f,
        val stocks: Int = 0,
        val stateTag: SolCharacterStateTag = SolCharacterStateTag.NO_PLAYER_PRESENT,
        val currentHitboxes: List<SolHitboxState> = emptyList()
)

enum class SolCharacterStateTag {
    NO_PLAYER_PRESENT,
    CONTROLLED,
    HITSTUN,
    ABILITY_STARTUP,
    ABILITY_EXECUTION,
    ABILITY_ENDLAG
}

data class SolHitboxState(
        val physicalObject: CircleObjectState,
        val velocity: Vector2f,
        val damage: Float,
        val baseKnockback: Float,
        val knockbackRatio: Float,
        val knockbackPoint: Float,
        val knockbackTowardsPoint: Boolean
)

data class SolStaticGameState(
        val worldSize: Vector2f,
        val walls: List<ObjectState>,
        val holes: List<ObjectState>
)

abstract class ObjectState {
    abstract val position: Vector2f
}

data class CircleObjectState(
        override val position: Vector2f,
        val radius: Float
) : ObjectState()

// center position
data class RectangleObjectState(
        override val position: Vector2f,
        val size: Vector2f
) : ObjectState()