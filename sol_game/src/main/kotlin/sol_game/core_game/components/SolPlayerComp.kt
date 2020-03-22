package sol_game.core_game.components

import sol_engine.ecs.Component
import sol_game.game.SolClientPlayer
import kotlin.reflect.KClass

data class SolPlayerComp(
        val playerClass: KClass<SolClientPlayer>
) : Component() {

    lateinit var player: SolClientPlayer
}