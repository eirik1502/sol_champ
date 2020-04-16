package sol_game.game

import org.joml.Vector2f

data class SolGameState(
        val gameStarted: Boolean,
        val gameEnded: Boolean,
        val playerIndexWon: Int,
        val charactersState: List<SolCharacterState>
)

data class SolCharacterState(
        val position: Vector2f = Vector2f(),
        val velocity: Vector2f = Vector2f(),
        val acceleration: Vector2f = Vector2f(),
        val rotation: Float = 0f,
        val damage: Float = 0f,
        val radius: Float = 0f,
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
        val position: Vector2f,
        val velocity: Vector2f,
        val radius: Float,
        val damage: Float,
        val baseKnockback: Float,
        val knockbackRatio: Float,
        val knockbackPoint: Float,
        val knockbackTowardsPoint: Boolean
)