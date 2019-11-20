package sol_game

class Ability(
        var abilityEntityClass: String = "",
        var cooldown: Int = 60,
        var initialOffset: Float = 0f,
        var inputAction: String,
        var initialImpulse: Float = 0f,
        var executeTime: Int = 0,
        var startupDelay: Int = 0
) {

    var currentCooldown: Int = 0;
    var trigger: Boolean = false

}