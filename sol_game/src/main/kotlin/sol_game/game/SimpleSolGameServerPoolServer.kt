package sol_game.game

import com.fasterxml.jackson.module.kotlin.*
import com.sun.net.httpserver.HttpServer
import mu.KotlinLogging
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_game.core_game.CharacterConfig
import sol_game.networked_sol_game.SolGameServerConfig
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration


fun requestGameServerInstance(address: String, port: Int): ServerConnectionData? {
    val objectMapper = jacksonObjectMapper()

    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
            .uri(URI.create("http://$address:$port/createGameServer"))
            .timeout(Duration.ofSeconds(10))
            .header("Content-Type", "application/json")
            .GET()
            .build()
    val response = client.send(request, BodyHandlers.ofString())
    val serializedConnectionData = response.body()
    return objectMapper.readValue(serializedConnectionData)
}

class SimpleSolGameServerPoolServer(
        headless: Boolean = true
) {
    private val logger = KotlinLogging.logger { }
    private val objectMapper = jacksonObjectMapper()

    private val serverPool: SolGameServerPool = SolGameServerPool(headless)


    fun serve(port: Int, charactersConfigs: List<CharacterConfig>) {
        HttpServer.create(InetSocketAddress(port), 0).apply {
            createContext("/createGameServer") { http ->
                logger.info { "request createGameServer by: ${http.remoteAddress}" }
                http.responseHeaders.add("Content-type", "application/json")
                val serverConnectionData = serverPool.createServer(charactersConfigs)
                logger.info { "Created server with connection data: ${serverConnectionData}" }
                val serializedConnectionData = objectMapper.writeValueAsString(serverConnectionData)

                logger.info { "response body created" }

                http.sendResponseHeaders(200, 0)
                val bodyPrinter = PrintWriter(http.responseBody)
                bodyPrinter.println(serializedConnectionData)
                bodyPrinter.close()
            }
            start()
            logger.info { "Serving at port: $port" }
        }
    }

}