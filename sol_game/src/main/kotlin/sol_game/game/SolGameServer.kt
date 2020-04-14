package sol_game.game

import org.slf4j.LoggerFactory
import sol_engine.engine_interface.SimulationLoop
import sol_engine.engine_interface.ThreadedSimulationLoop
import sol_engine.network.network_ecs.host_managing.NetHostComp
import sol_engine.network.network_ecs.host_managing.TeamPlayerComp
import sol_engine.network.network_game.GameHost
import sol_engine.network.network_game.game_client.ClientConnectionData
import sol_engine.network.network_game.game_server.GameServerConfig
import sol_engine.network.network_game.game_server.ServerConnectionData
import sol_engine.network.network_sol_module.NetworkServerModule
import sol_game.core_game.CharacterConfig
import sol_game.core_game.SolGameSimulationServer
import sol_game.core_game.components.CharacterComp
import sol_game.core_game.components.HurtboxComp
import sol_game.core_game.components.SolGameComp
import sol_game.networked_sol_game.Server
import java.util.*

typealias TerminationCallback = (gameServer: SolGameServer) -> Unit

class SolGameServer(
        charactersConfigs: List<CharacterConfig> = listOf(),
        requestPort: Int = -1,
        allowObservers: Boolean = true,
        updateFrameTime: Float = 1f / 60f,  // set to run the game at a custom fixed frame time
        headless: Boolean = false,
        debugUI: Boolean = false,  // cannot be set in headless mode
        // gui is not supported when running multiple instances of SolGameServer / Client on multiple threads
        allowGui: Boolean = true
) {

    private lateinit var connectionData: ServerConnectionData

    private val serverSim: SolGameSimulationServer = SolGameSimulationServer(
            charactersConfigs,
            requestPort,
            allowObservers,
            headless,
            debugUI,
            allowGui
    )

    private val threadedLoop: ThreadedSimulationLoop = ThreadedSimulationLoop(serverSim, updateFrameTime);

    fun onTermination(callback: TerminationCallback) {
        threadedLoop.onTermination() { threadedLoop, loop, sim -> callback(this) }
    }

    fun setup(): ServerConnectionData {
        threadedLoop.setup();
        connectionData = serverSim.modulesHandler.getModule(NetworkServerModule::class.java).connectionData
        threadedLoop.onTermination() { threadedLoop, loop, sim -> println("sol server finished") }
        return connectionData
    }

    fun start() {
        threadedLoop.start()
    }

    fun getConnectionData(): ServerConnectionData {
        return connectionData
    }

    fun getPlayersConnectedCount(): Int {
        return threadedLoop.waitForNextStepFinish { simulation ->
            simulation.world.insight.entities
                    .filter { it.hasComponent(CharacterComp::class.java) }
                    .count()
        }
    }

    fun getPlayersConnectionData(): List<GameHost> {
        return threadedLoop.waitForNextStepFinish { simulation ->
            simulation.world.insight.entities
                    .filter { it.hasComponent(NetHostComp::class.java) }
                    .map { it.getComponent(NetHostComp::class.java).host }
        }
    }

    fun getPlayersDamage(): List<Float> {
        return threadedLoop.waitForNextStepFinish { simulation ->
            simulation.world.insight.entities
                    .filter { it.hasComponent(CharacterComp::class.java) }
                    .filter { it.hasComponent(HurtboxComp::class.java) }
                    .map { it.getComponent(HurtboxComp::class.java).totalDamageTaken }
        }
    }

    // not thread safe, should only be called after termination
    fun getTeamIndexWon(): Int {
        return serverSim.world.insight.entities
                .find { it.hasComponent(SolGameComp::class.java) }
                ?.getComponent(SolGameComp::class.java)?.teamIndexWon
                ?: -1
    }

    fun waitUntilFinished() {
        threadedLoop.waitUntilFinished()
    }

    fun terminate() {
        threadedLoop.terminate()
    }
}