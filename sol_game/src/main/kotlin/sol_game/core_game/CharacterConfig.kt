package sol_game.core_game

data class AbilityConfig(
        val type: String = "melee",
        val radius: Float = 32f,
        val distanceFromChar: Float = 16f,
        val knockbackPoint: Float = 32f,
        val knockbackTowardsPoint: Boolean = false,
        val speed: Float = 0f,

        val activeHitboxTime: Float = 10f,
        val startupTime: Float = 5f,
        val endlagTime: Float = 3f,
        val rechargeTime: Float = 10f,

        val damage: Float = 20f,
        val baseKnockback: Float = 100f,
        val knockbackRatio: Float = 1f
)

data class CharacterConfig(
        val radius: Float = 32f,
        val moveAccel: Float = 500f,
        val abilities: List<AbilityConfig> = listOf(AbilityConfig(), AbilityConfig(), AbilityConfig())
)