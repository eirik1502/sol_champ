package sol_game

import sol_engine.core.TransformComp
import sol_engine.ecs.SystemBase
import sol_engine.game_utils.InputComp

class PlayerSystem : SystemBase() {
    override fun onSetup() {
        usingComponents(PlayerComp::class.java, TransformComp::class.java, InputComp::class.java)
    }

    override fun onUpdate() {

    }
}