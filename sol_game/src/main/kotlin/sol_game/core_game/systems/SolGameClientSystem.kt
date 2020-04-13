package sol_game.core_game.systems

import sol_engine.ecs.SystemBase
import sol_engine.network.network_ecs.host_managing.ClientControlledComp
import sol_engine.network.network_ecs.host_managing.NetClientComp
import sol_engine.network.network_ecs.host_managing.TeamPlayerComp
import sol_game.core_game.components.SolGameComp

class SolGameClientSystem : SystemBase() {

    private var prevGameState = SolGameComp.GameState.BEFORE_START

    override fun onSetup() {
        usingComponents(SolGameComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(SolGameComp::class.java) { _, gameComp ->

            if (gameComp.gameState == SolGameComp.GameState.ENDED
                    && gameComp.gameState != prevGameState) {

                world.insight.entities
                        .filter { it.hasComponent(NetClientComp::class.java) }
                        .map { it.getComponent(NetClientComp::class.java) }
                        .first()
                        .requestDisconnect = true

                world.removeSystem(ClientNetworkOutputSystem::class.java)

            }
            // assuming there is only one of this comp
            prevGameState = gameComp.gameState
        }
    }

}