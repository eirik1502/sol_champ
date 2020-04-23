package sol_game.core_game

data class Ability(
        var abilityEntityClass: String = "",
        var name: String,
        var cooldown: Int = 60,
        var initialOffset: Float = 0f,
        var initialImpulse: Float = 0f,
        var executeTime: Int = 0,
        var startupDelay: Int = 0
) {

    var currentCooldown: Int = 0;
    var trigger: Boolean = false

}