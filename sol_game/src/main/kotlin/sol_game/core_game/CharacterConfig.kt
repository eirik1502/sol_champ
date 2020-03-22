package sol_game.core_game

data class AbilityConfig(
        val name: String = "default-ability-name",
        val type: String = "melee",
        val radius: Float = 32f,
        val distanceFromChar: Float = 16f,
        val knockbackPoint: Float = 32f,
        val knockbackTowardsPoint: Boolean = false,
        val speed: Float = 0f,

        val activeHitboxTime: Int = 10,
        val startupTime: Int = 5,
        val endlagTime: Int = 3,
        val rechargeTime: Int = 10,

        val damage: Float = 20f,
        val baseKnockback: Float = 100f,
        val knockbackRatio: Float = 1f
)

data class CharacterConfig(
        val name: String = "default-name",
        val radius: Float = 32f,
        val moveAccel: Float = 500f,
        val abilities: List<AbilityConfig> = listOf(AbilityConfig(), AbilityConfig(), AbilityConfig())
)