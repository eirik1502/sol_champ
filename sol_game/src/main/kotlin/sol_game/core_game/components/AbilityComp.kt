package sol_game.core_game.components

import sol_engine.ecs.Component
import sol_game.core_game.Ability

class AbilityComp(
        abilities: List<Ability> = listOf()
) : Component() {
    var executionDisabled: Boolean = false
    var triggerInterrupt: Boolean = false
    var isExecuting: Boolean = false
    var executingAbility: Ability? = null
    var executingAbilityExecutionTimer: Int = -1
    var executingAbilityStartupDelayTimer: Int = -1

    val abilities: MutableList<Ability> = ArrayList()

    init {
        this.abilities.addAll(abilities);
    }

    override fun copy(other: Component) {
        val otherComp = other as AbilityComp

        executionDisabled = other.executionDisabled
        triggerInterrupt = other.triggerInterrupt

        val executingAbilityIndex = otherComp.abilities.indexOf(otherComp.executingAbility)
        abilities.clear()
        abilities.addAll(otherComp.abilities.map { it.copy() })

        isExecuting = otherComp.isExecuting;
        executingAbility = if (executingAbilityIndex == -1) null else abilities[executingAbilityIndex]
        executingAbilityExecutionTimer = otherComp.executingAbilityExecutionTimer
        executingAbilityStartupDelayTimer = otherComp.executingAbilityStartupDelayTimer
    }
}