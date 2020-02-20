package sol_game.sol_game_server

import com.fasterxml.jackson.module.kotlin.*
import mu.KotlinLogging
import org.java_websocket.WebSocket
import org.java_websocket.drafts.Draft
import org.java_websocket.exceptions.InvalidDataException
import org.java_websocket.framing.CloseFrame
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.handshake.ServerHandshakeBuilder
import org.java_websocket.server.WebSocketServer
import sol_game.core_game_wrappers.GameState
import sol_game.core_game_wrappers.PlayerInput
import sol_game.core_game_wrappers.PlayersInput
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.HashMap

typealias PlayerId = String

data class ConnectedPlayer(
        val playerId: PlayerId,
        val socket: WebSocket,
        val name: String = ""
)

val EMPTY_INPUT_QUEUE = ArrayDeque<PlayerInput>()

private val logger = KotlinLogging.logger { }

class Server(
        port: Int,
        private val gameId: String,
        private val playersKey: List<String>
) : WebSocketServer(InetSocketAddress(port)) {

    private val jsonMapper = jacksonObjectMapper()
    private val connectedPlayers = HashMap<WebSocket, ConnectedPlayer>()
    private val playersInputQueue = HashMap<PlayerId, ArrayDeque<PlayerInput>>()
    private var terminated = false

    /**
     * Blocking until each playerKey is used to connect
     */
    fun waitForPlayerConnections(timeout: Int = Int.MAX_VALUE): Boolean {
        val checkInterval = 500L
        var timePassed = 0L
        while (connectedPlayers.size < playersKey.size) {
            Thread.sleep(checkInterval)
            timePassed += checkInterval
            if (timePassed >= timeout || terminated) {
                return false
            }
        }
        return true
    }

    fun pushGameState(gameState: GameState) {
        val gameStateStr = jsonMapper.writeValueAsString(gameState)
        this.broadcast(gameStateStr)
    }

    fun pollPlayersInput(): PlayersInput {
        val playersInput = playersKey
                .map { playerKey -> playersInputQueue.getOrDefault(playerKey, EMPTY_INPUT_QUEUE) }
                .map { inputQueue -> if (inputQueue.isEmpty()) PlayerInput() else inputQueue.poll() }
        return PlayersInput(playersInput)
    }

    override fun stop() {
        terminated = true
        super.stop()
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        val player = ConnectedPlayer(conn.getAttachment() as PlayerId, conn)
        connectedPlayers[conn] = player
        playersInputQueue[player.playerId] = ArrayDeque()
        logger.info { "player connected: ${conn.getAttachment() as PlayerId}" }
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        val player = connectedPlayers[conn]
        logger.info { "Client disconnected ${player?.playerId ?: "player already gone"}" }
        connectedPlayers.remove(conn)
    }

    override fun onMessage(conn: WebSocket, message: String) {
//        println("received message: $message");
        val player = connectedPlayers[conn]
        if (player != null) {
            val playerInput: PlayerInput = jsonMapper.readValue(message)
            playersInputQueue["1"]?.add(playerInput)

        } else {
            logger.info { "A non-connected player sendt a message" }
        }

    }

    override fun onMessage(conn: WebSocket, message: ByteBuffer) {
        logger.info { "received ByteBuffer" }
    }

    override fun onStart() {
        logger.info { "Server started! address: $address port: $port" };
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    override fun onError(conn: WebSocket, ex: Exception) {
        logger.warn { "ERROR " + ex }
    }

    @Throws(InvalidDataException::class)
    override fun onWebsocketHandshakeReceivedAsServer(conn: WebSocket, draft: Draft, request: ClientHandshake):
            ServerHandshakeBuilder {
        logger.info { "Connection handshake from ${conn.remoteSocketAddress}" }
        val builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request)
        val queryParams = QueryParamsParser("http://lok.com" + request.resourceDescriptor)

        if (!queryParams.hasAll("gameId", "playerKey")) {
            logger.info { "gameId and/or playerKey not present" }
            throw InvalidDataException(CloseFrame.POLICY_VALIDATION, "gameId and/or playerKey not present")
        }

        val gameId = queryParams.get("gameId")
        val playerKey = queryParams.get("playerKey")

        if (!this.gameId.equals(gameId)) {
            logger.info { "gameId invalid" }
            throw InvalidDataException(CloseFrame.POLICY_VALIDATION, "gameId invalid")
        }
        if (!this.playersKey.contains(playerKey)) {
            logger.info { "playerKey invalid" }
            throw InvalidDataException(CloseFrame.POLICY_VALIDATION, "playerKey invalid")

        }
        if (this.connectedPlayers.any() { (_, p) -> p.playerId.equals(playerKey) }) {
            logger.info { "playerKey already used" }
            throw InvalidDataException(CloseFrame.POLICY_VALIDATION, "playerKey already used")
        }
        logger.info { "connection handshake successfull for ${conn.remoteSocketAddress}" }
        conn.setAttachment(playerKey)
        return builder
    }

}