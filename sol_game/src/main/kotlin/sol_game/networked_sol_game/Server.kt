package sol_game.networked_sol_game

import com.fasterxml.jackson.databind.ObjectMapper
import org.java_websocket.WebSocket
import org.java_websocket.drafts.Draft
import org.java_websocket.exceptions.InvalidDataException
import org.java_websocket.framing.CloseFrame
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.handshake.ServerHandshakeBuilder
import org.java_websocket.server.WebSocketServer
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

class Server(
        port: Int,
        private val gameId: String,
        private val playersKey: List<String>
) : WebSocketServer(InetSocketAddress(port)) {

    private val jsonMapper = ObjectMapper()
    private val connectedPlayers = HashMap<WebSocket, ConnectedPlayer>()
    private val playersInputQueue = HashMap<PlayerId, ArrayDeque<PlayerInput>>()

    init {
        playersInputQueue.put("1", ArrayDeque())
        playersInputQueue.put("2", ArrayDeque())
    }

    /**
     * Blocking until each playerKey is used to connect
     */
    fun waitForPlayerConnections(timeout: Int = Int.MAX_VALUE): Boolean {
        val checkInterval = 500L
        var timePassed = 0L
        while (connectedPlayers.size < playersKey.size) {
            Thread.sleep(checkInterval)
            timePassed += checkInterval
            if (timePassed >= timeout) {
                return false
            }
        }
        return true
    }

    fun pushGameState(gameState: StateOutput) {
        val gameStateStr = jsonMapper.writeValueAsString(gameState)
        this.broadcast(gameStateStr)
    }

    fun pollPlayersInput(): PlayersInput {
        val playersInput = playersKey
                .map { playerKey -> playersInputQueue.getOrDefault(playerKey, EMPTY_INPUT_QUEUE) }
                .map { inputQueue -> if (inputQueue.isEmpty()) PlayerInput() else inputQueue.poll() }
        return PlayersInput(playersInput)
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        val player = ConnectedPlayer(conn.getAttachment() as PlayerId, conn)
        connectedPlayers[conn] = player
        playersInputQueue[player.playerId] = ArrayDeque()
        println("player connected: ${conn.getAttachment() as PlayerId}")
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        val player = connectedPlayers[conn]
        println("Client disconnected ${player?.playerId ?: "player already gone"}")
        connectedPlayers.remove(conn)
    }

    override fun onMessage(conn: WebSocket, message: String) {
//        println("received message: $message");
        val player = connectedPlayers[conn]
        if (player != null) {
            val playerInput = jsonMapper.readValue(message, PlayerInput::class.java)
            playersInputQueue["1"]?.add(playerInput)

        } else {
            println("A non-connected player sendt a message")
        }

    }

    override fun onMessage(conn: WebSocket, message: ByteBuffer) {
        println("received ByteBuffer")
    }

    override fun onStart() {
        System.out.println("Server started! address: $address port: $port");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    override fun onError(conn: WebSocket, ex: Exception) {
        println("ERROR " + ex)
    }

    @Throws(InvalidDataException::class)
    override fun onWebsocketHandshakeReceivedAsServer(conn: WebSocket, draft: Draft, request: ClientHandshake):
            ServerHandshakeBuilder {
        println("Connection handshake")
        val builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request)
        val queryParams = QueryParamsParser("http://lok.com" + request.resourceDescriptor)

        if (!queryParams.hasAll("gameId", "playerKey")) {
            println("gameId and/or playerKey not present")
            throw InvalidDataException(CloseFrame.POLICY_VALIDATION, "gameId and/or playerKey not present")
        }

        val gameId = queryParams.get("gameId")
        val playerKey = queryParams.get("playerKey")

        if (!this.gameId.equals(gameId)) {
            println("gameId invalid")
            throw InvalidDataException(CloseFrame.POLICY_VALIDATION, "gameId invalid")
        }
        if (!this.playersKey.contains(playerKey)) {
            println("playerKey invalid")
            throw InvalidDataException(CloseFrame.POLICY_VALIDATION, "playerKey invalid")

        }
        if (this.connectedPlayers.any() { (_, p) -> p.playerId.equals(playerKey) }) {
            println("playerKey already used")
            throw InvalidDataException(CloseFrame.POLICY_VALIDATION, "playerKey already used")
        }
        println("connection passed checks")
        conn.setAttachment(playerKey)
        return builder
    }

}