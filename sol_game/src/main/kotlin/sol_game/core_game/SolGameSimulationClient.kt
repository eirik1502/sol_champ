package sol_game.core_game

import org.joml.Vector2f
import sol_engine.engine_interface.SolSimulation
import sol_engine.graphics_module.GraphicsModule
import sol_engine.graphics_module.GraphicsModuleConfig
import sol_engine.graphics_module.RenderConfig
import sol_engine.graphics_module.WindowConfig
import sol_engine.input_module.*
import sol_engine.network.network_game.game_client.ClientConfig
import sol_engine.network.network_game.game_server.GameServerConfig
import sol_engine.network.network_input.NetworkInputSourceModule
import sol_engine.network.network_input.NetworkInputSourceModuleConfig
import sol_engine.network.network_sol_module.NetworkClientModule
import sol_engine.network.network_sol_module.NetworkClientModuleConfig
import sol_engine.network.network_sol_module.NetworkServerModule
import sol_engine.network.network_sol_module.NetworkServerModuleConfig
import sol_game.game.SolPlayer

class SolGameSimulationClient(
        val connectAddress: String,
        val connectPort: Int,
        val gameId: String,
        val connectionKey: String,
        val isObserver: Boolean,
        // if player is set, it will control the client, and graphical input will be disabled
        val player: SolPlayer? = null,
        val headless: Boolean = false,
        val debugUI: Boolean = false  // cannot be set in headless mode
) : SolSimulation() {

    override fun onSetupModules() {
        if (!headless) {
            addModule(GraphicsModule(GraphicsModuleConfig(
                    WindowConfig(0.5f, 0.5f, "sol client", true),
                    RenderConfig(800f, 450f, 1600f, 900f)
            )))
        }
        if (player == null) {
            addModule(InputGuiSourceModule(InputGuiSourceModuleConfig(
                    Vector2f(1600f, 900f),
                    mapOf(
                            "moveLeft" to InputConsts.KEY_A,
                            "moveRight" to InputConsts.KEY_D,
                            "moveUp" to InputConsts.KEY_W,
                            "moveDown" to InputConsts.KEY_S,
                            "ability1" to InputConsts.MOUSE_BUTTON_LEFT,
                            "ability2" to InputConsts.MOUSE_BUTTON_RIGHT,
                            "ability3" to InputConsts.KEY_SPACE,
                            "aimX" to InputConsts.CURSOR_X,
                            "aimY" to InputConsts.CURSOR_Y,
                            "aimXY" to InputConsts.CURSOR_VEC
                    )
            )))
        }
        addModule(NetworkClientModule(NetworkClientModuleConfig(
                ClientConfig(
                        connectAddress,
                        connectPort,
                        gameId,
                        connectionKey,
                        isObserver
                ),
                listOf()
        )))
    }

    override fun onSetupWorld() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


//    if (!useGraphicsInput) {
//        val netMod = NetworkModule(NetworkModuleConfig(
//                true,
//                7778,
//                ""
//        ))
//        val netInputMod = NetworkInputSourceModule(NetworkInputSourceModuleConfig(
//                SolInputPacket::class.java
//        ))
//    }
//    val inputSourceModule =
//            if (useGraphicsInput)
//                InputGuiSourceModule(InputGuiSourceModuleConfig(
//                        Vector2f(1600f, 900f),
//                        mapOf(
//                                "player${controlPlayerIndex}:moveLeft" to InputConsts.KEY_A,
//                                "player${controlPlayerIndex}:moveRight" to InputConsts.KEY_D,
//                                "player${controlPlayerIndex}:moveUp" to InputConsts.KEY_W,
//                                "player${controlPlayerIndex}:moveDown" to InputConsts.KEY_S,
//                                "player${controlPlayerIndex}:ability1" to InputConsts.MOUSE_BUTTON_LEFT,
//                                "player${controlPlayerIndex}:ability2" to InputConsts.MOUSE_BUTTON_RIGHT,
//                                "player${controlPlayerIndex}:ability3" to InputConsts.KEY_SPACE,
//                                "player${controlPlayerIndex}:aimX" to InputConsts.CURSOR_X,
//                                "player${controlPlayerIndex}:aimY" to InputConsts.CURSOR_Y,
//                                "player${controlPlayerIndex}:aimXY" to InputConsts.CURSOR_VEC
//                        )
//                ))
}