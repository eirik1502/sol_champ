package sol_game.networked_sol_game

data class PlayerInput(
        val moveLeft: Boolean = false,
        val moveRight: Boolean = false,
        val moveUp: Boolean = false,
        val moveDown: Boolean = false,
        val ability1: Boolean = false,
        val ability2: Boolean = false,
        val ability3: Boolean = false,
        val aimX: Float = 0f,
        val aimY: Float = 0f
)

data class PlayersInput(
        val playersInput: List<PlayerInput> = listOf(PlayerInput(), PlayerInput())
)

data class PlayerStateOutput(
        val posX: Float = 0f,
        val posY: Float = 0f,
        val rotation: Float = 0f
)

data class StateOutput(
        val playersState: List<PlayerStateOutput> = listOf()
)