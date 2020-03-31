package sol_game.core_game.systems

import sol_engine.core.TransformComp
import sol_engine.ecs.SystemBase
import sol_engine.input_module.InputComp
import sol_game.core_game.components.PlayerComp

class PlayerSystem : SystemBase() {
    override fun onSetup() {
        usingComponents(PlayerComp::class.java, TransformComp::class.java, InputComp::class.java)
    }

    override fun onUpdate() {

    }
}