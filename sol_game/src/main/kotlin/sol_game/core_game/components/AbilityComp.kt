package sol_game.core_game.components

import sol_engine.ecs.Component
import sol_game.core_game.Ability

class AbilityComp(
        abilities: List<Ability> = listOf()
) : Component() {

    var isExecuting: Boolean = false
    var executingAbility: Ability? = null
    var executingAbilityExecutionTimer: Int = -1
    var executingAbilityStartupDelayTimer: Int = -1

    val abilities: MutableList<Ability> = ArrayList()

    init {
        this.abilities.addAll(abilities);
    }

    override fun clone(): AbilityComp {
        val comp = AbilityComp()
        comp.abilities.addAll(abilities)
        comp.isExecuting = isExecuting;
        comp.executingAbility = executingAbility
        return comp
    }
}