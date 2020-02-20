package sol_game.core_game

import sol_engine.ecs.Component
import sol_engine.ecs.Entity

class HitboxComp(
        var damage: Float = 10f,
        var baseKnockback: Float = 0f,
        var knockbackRatio: Float = 0f,
        var owner: Entity? = null
) : Component() {
    var currDamageDealt: Float = 0f
}