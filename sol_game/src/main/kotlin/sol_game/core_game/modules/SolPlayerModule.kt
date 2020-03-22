package sol_game.core_game.modules

import mu.KotlinLogging
import org.joml.Vector2f
import sol_engine.input_module.InputSourceModule
import sol_engine.module.Module
import sol_engine.utils.ClassUtils
import sol_game.game.SolPlayer

data class SolPlayerInputSourceModuleConfig(
        val playerClass: Class<SolPlayer>
)

class SolPlayerModule(
        val config: SolPlayerInputSourceModuleConfig
) : InputSourceModule() {
    private val logger = KotlinLogging.logger { }

    private lateinit var player: SolPlayer

    override fun onSetup() {
        ClassUtils.instanciateNoarg(config.playerClass)
                ?.let { player = it }
                ?: run {
                    logger.error { "Could not instanciate player of class: ${config.playerClass}" }
                }
    }

    override fun onStart() {
//        player.onStart(super.)
    }


    override fun vectorInput(label: String?): Vector2f {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEnd() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkAction(label: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun floatInput(label: String?): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpdate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}