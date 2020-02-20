package sol_game.core_game

import sol_engine.core.TransformComp
import sol_engine.ecs.SystemBase
import sol_engine.input_module.InputComp

class PlayerSystem : SystemBase() {
    override fun onSetup() {
        usingComponents(PlayerComp::class.java, TransformComp::class.java, InputComp::class.java)
    }

    override fun onUpdate() {

    }
}