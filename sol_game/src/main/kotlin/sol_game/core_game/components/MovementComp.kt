package sol_game.core_game.components

import sol_engine.ecs.Component

class MovementComp(
        // in the order: left - right - up - down
        var inputActions: List<String> = listOf(),
        var maxSpeed: Float = 60f,
        var acceleration: Float = 60f,
        var disabled: Boolean = false
) : Component()