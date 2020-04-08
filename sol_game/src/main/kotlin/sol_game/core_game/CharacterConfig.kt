package sol_game.core_game

import sol_engine.network.network_utils.NetworkUtils


enum class AbilityType(val str: String) {
    MELEE("melee"),
    PROJECTILE("projectile")
}

data class AbilityConfig(
        val name: String = "default-ability-name",
        val type: AbilityType = AbilityType.MELEE,
        val radius: Float = 32f,
        val distanceFromChar: Float = 16f,
        val speed: Float = 0f,

        val activeTime: Int = 10,
        val startupTime: Int = 5,
        val executionTime: Int = 10,
        val endlagTime: Int = 3,
        val rechargeTime: Int = 10,

        val damage: Float = 20f,
        val baseKnockback: Float = 100f,
        val knockbackRatio: Float = 1f,
        val knockbackPoint: Float = 32f,
        val knockbackTowardPoint: Boolean = false
)

data class CharacterConfig(
        val characterId: String = NetworkUtils.uuid(),
        val name: String = "default-name",
        val radius: Float = 32f,
        val moveVelocity: Float = 32f,
        val abilities: List<AbilityConfig> = listOf(AbilityConfig(), AbilityConfig(), AbilityConfig())
)