package sol_game

import sol_engine.ecs.Component

class DealDamageComp(
        var damage: Float = 10f
) : Component() {
    var currDamageDealt: Float = 0f
}