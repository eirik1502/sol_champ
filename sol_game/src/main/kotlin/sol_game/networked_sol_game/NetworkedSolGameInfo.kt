package sol_game.sol_game_server

data class NetworkedSolGameInfo(
        val error: Boolean,
        val gameId: String,
        val gameServerAddress: String,
        val gameServerPort: Int,
        val playersKeys: List<String>,
        val allowObservers: Boolean,
        val observerKey: String?
)