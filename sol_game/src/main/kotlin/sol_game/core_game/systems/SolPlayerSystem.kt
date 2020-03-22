package sol_game.core_game.systems

import mu.KotlinLogging
import sol_engine.ecs.SystemBase
import sol_engine.utils.ClassUtils
import sol_game.core_game.components.SolPlayerComp
import kotlin.reflect.full.primaryConstructor

class SolPlayerSystem() : SystemBase() {
    private val logger = KotlinLogging.logger { }

    override fun onSetup() {
        usingComponents(SolPlayerComp::class.java)
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