package sol_game.core_game.systems

import mu.KotlinLogging
import sol_engine.core.ModuleSystemBase
import sol_engine.ecs.SystemBase
import sol_game.core_game.components.SolPlayerComp
import sol_game.core_game.modules.SolClientPlayerModule
import kotlin.reflect.full.primaryConstructor


/**
 *
 */
class SolPlayerSystem() : ModuleSystemBase() {
    private val logger = KotlinLogging.logger { }

    override fun onSetup() {
        usingComponents(SolPlayerComp::class.java)
        usingModules(SolClientPlayerModule::class.java)
    }

    override fun onStart() {
        forEachWithComponents(SolPlayerComp::class.java) { _, playerComp ->
            val constructor = playerComp.playerClass.primaryConstructor
            constructor
                    ?.let {
                        playerComp.player = it.call()
                    }
                    ?: run {
                        logger.error { "Could not instanciate player of class: ${playerComp.playerClass}" }
                    }
        }
    }

    override fun onUpdate() {
    }
}