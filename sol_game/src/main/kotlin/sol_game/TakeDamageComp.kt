package sol_game

import sol_engine.ecs.Component

class TakeDamageComp : Component() {
    var currDamageTaken: Float = 0f
    var totalDamageTaken: Float = 0f
}