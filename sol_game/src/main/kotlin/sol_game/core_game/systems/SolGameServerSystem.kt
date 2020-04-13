package sol_game.core_game.systems

import sol_engine.ecs.Entity
import sol_engine.ecs.SystemBase
import sol_engine.network.network_ecs.host_managing.TeamPlayerComp
import sol_game.core_game.components.CharacterComp
import sol_game.core_game.components.HurtboxComp
import sol_game.core_game.components.SolGameComp

class SolGameServerSystem : SystemBase() {

    override fun onSetup() {
        usingComponents(SolGameComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(SolGameComp::class.java) { _, solGameComp ->

            val gameState = solGameComp.gameState
            solGameComp.gameState = when (gameState) {
                SolGameComp.GameState.BEFORE_START -> handleGameBeforeStart()
                SolGameComp.GameState.RUNNING -> handleGameRunning(solGameComp)
                SolGameComp.GameState.ENDING -> handleGameEnding()
                SolGameComp.GameState.ENDED -> handleGameEnded()
            }
        }
    }

    private fun handleGameBeforeStart(): SolGameComp.GameState {
        val charactersCount = world.insight.entities
                .filter { it.hasComponent(CharacterComp::class.java) }
                .count()

        return if (charactersCount == 1) SolGameComp.GameState.RUNNING else SolGameComp.GameState.BEFORE_START
    }

    private fun handleGameRunning(gameComp: SolGameComp): SolGameComp.GameState {
        val charEntitiesLost: Entity? = world.insight.entities
                .asSequence()
                .filter { it.hasComponent(CharacterComp::class.java) }
                .filter { it.hasComponent(HurtboxComp::class.java) }
                .filter { it.hasComponent(TeamPlayerComp::class.java) }
                .find { it.getComponent(HurtboxComp::class.java).totalDamageTaken > 1000f }

        charEntitiesLost
                ?.let {
                    gameComp.teamIndexWon = 1 - it.getComponent(TeamPlayerComp::class.java).teamIndex
                    return SolGameComp.GameState.ENDING
                }
                ?: return SolGameComp.GameState.RUNNING
    }

    // One frame where ending is accessible to other systems
    private fun handleGameEnding(): SolGameComp.GameState {
        return SolGameComp.GameState.ENDED
    }

    private fun handleGameEnded(): SolGameComp.GameState {
        val charactersCount = world.insight.entities
                .filter { it.hasComponent(CharacterComp::class.java) }
                .count()

        if (charactersCount == 0) {
            world.isFinished = true
        }

        return SolGameComp.GameState.ENDED
    }

}