package sol_game.core_game.systems

import sol_engine.core.ModuleSystemBase
import sol_engine.input_module.ExternalInputSourceModule
import sol_game.core_game.components.SolGameComp
import sol_game.core_game.modules.SolClientPlayerModule

class SolClientPlayerSystem : ModuleSystemBase() {

    override fun onSetup() {
        usingComponents(SolGameComp::class.java)
        usingModules(SolClientPlayerModule::class.java)
    }

    override fun onSetupEnd() {
        // give the player module a reference to the world
        getModule(SolClientPlayerModule::class.java).world = world
    }

    override fun onStart() {

    }

    override fun onUpdate() {
        forEachWithComponents(SolGameComp::class.java) { _, gameComp ->
            if (gameComp.gameState == SolGameComp.GameState.RUNNING) {
                getModule(SolClientPlayerModule::class.java).gameStarted = true
            }
        }
    }
}