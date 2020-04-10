package sol_game.core_game.systems

import sol_engine.ecs.SystemBase
import sol_game.core_game.components.CharacterComp
import sol_game.core_game.components.SolGameComp

class SolGameSystem : SystemBase() {

    override fun onSetup() {
        usingComponents(SolGameComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(SolGameComp::class.java) { _, solGameComp ->

            if (!solGameComp.gameStarted) {
                val charactersCount = world.insight.entities
                        .filter { it.hasComponent(CharacterComp::class.java) }
                        .count()

                if (charactersCount == 2) {
                    solGameComp.gameStarted = true
                }
            }
            if (solGameComp.gameStarted) {
                // check if last update set gameEnded to true
                if (solGameComp.gameEnded) {
                    // terminate
                } else if (false) {
                    solGameComp.gameEnded = true
                }
            }
        }
    }
}