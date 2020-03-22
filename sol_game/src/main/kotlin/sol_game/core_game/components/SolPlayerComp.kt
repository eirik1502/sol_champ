package sol_game.core_game.components

import sol_engine.ecs.Component
import sol_game.game.SolPlayer
import kotlin.reflect.KClass

data class SolPlayerComp(
        val playerClass: KClass<SolPlayer>
) : Component() {

    lateinit var player: SolPlayer
}