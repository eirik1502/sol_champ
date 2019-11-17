package sol_game

import sol_engine.ecs.Component
import sol_engine.ecs.Entity

class HitboxComp(
        var damage: Float = 10f,
        var owner: Entity? = null,
        var onTeam: String = ""
) : Component() {
    var currDamageDealt: Float = 0f
}