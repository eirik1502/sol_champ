package sol_game.core_game.modules

import mu.KotlinLogging
import org.joml.Vector2f
import sol_engine.ecs.World
import sol_engine.input_module.InputSourceModule
import sol_engine.network.network_sol_module.NetworkClientModule
import sol_engine.utils.ClassUtils
import sol_game.core_game.NetGameState
import sol_game.core_game.SolInputPacket
import sol_game.game.SolClientPlayer

data class SolClientPlayerModuleConfig(
        val playerClass: Class<SolClientPlayer>
)

class SolClientPlayerModule(
        private val config: SolClientPlayerModuleConfig
) : InputSourceModule() {
    private val logger = KotlinLogging.logger { }

    private lateinit var player: SolClientPlayer
    private lateinit var world: World

    private var currSolInput: SolInputPacket = SolInputPacket()

    // Should be called before onStart
    fun setWorld(world: World) {
        this.world = world
    }

    override fun onSetup() {
        ClassUtils.instanciateNoarg(config.playerClass)
                ?.let { player = it }
                ?: run {
                    logger.error { "Could not instanciate player of class: ${config.playerClass}" }
                }
        player.onSetup()
    }

    override fun onStart() {
        val clientConnectData = getModule(NetworkClientModule::class.java).connectionData
        if (!clientConnectData.isConnected) {
            logger.error { "Client not connected when starting player" }
        }
        player.onStart(world, clientConnectData.teamIndex, clientConnectData.playerIndex)
    }

    override fun onUpdate() {
        // TODO: retrieve game state
        currSolInput = player.onUpdate(world, NetGameState())
    }

    override fun onEnd() {
        // TODO: Register winner
        player.onEnd(world, false, -1, -1)
    }

    override fun vectorInput(label: String?): Vector2f {
        logger.error { "Does not support vector input" }
        return Vector2f()
    }

    override fun checkAction(label: String?): Boolean {
        return when (label) {
            "mvLeft" -> currSolInput.mvLeft
            "mvRight" -> currSolInput.mvRight
            "mvUp" -> currSolInput.mvUp
            "mvDown" -> currSolInput.mvDown
            "ability1" -> currSolInput.ability1
            "ability2" -> currSolInput.ability2
            "ability3" -> currSolInput.ability3
            else -> false
        }
    }

    override fun floatInput(label: String?): Float {
        return when (label) {
            "aimX" -> currSolInput.aimX
            "aimY" -> currSolInput.aimY
            else -> 0f
        }
    }
}