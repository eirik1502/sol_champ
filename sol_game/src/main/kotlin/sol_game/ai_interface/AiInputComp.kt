package sol_game.ai_interface

import sol_engine.ecs.Component

class AiInputComp(
        val actions: MutableSet<String> = HashSet()
) : Component() {
}