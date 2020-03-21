package sol_game.networked_sol_game

data class SolGameServerConfig(
        val playerCount: Int,
        val allowObservers: Boolean
)