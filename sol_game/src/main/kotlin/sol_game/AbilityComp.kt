package sol_game

import sol_engine.ecs.Component

class AbilityComp(
        var abilityEntityClass: String = "",
        var cooldown: Int
) : Component() {
    var currentCooldown: Int = 0;
}