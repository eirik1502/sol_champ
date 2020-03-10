package sol_game.core_game

data class AbilityConfig(
        val type: String,
        val radius: Float,
        val distanceFromChar: Float,
        val knockbackPoint: Float,
        val knockbackTowardsPoint: Boolean,
        val speed: Float,

        val activeHitboxTime: Float,
        val startupTime: Float,
        val endlagTime: Float,
        val rechargTime: Float,

        val damage: Float,
        val baseKnockback: Float,
        val knockbackRatio: Float
)

data class CharacterConfig(
        val radius: Float,
        val moveAccel: Float,
        val abilities: List<AbilityConfig>
)

data class CharacterTeamConfig(
        val characters: List<CharacterConfig>
)

data class CharacterTeamsConfig(
        val teams: List<CharacterTeamConfig> = listOf()
)