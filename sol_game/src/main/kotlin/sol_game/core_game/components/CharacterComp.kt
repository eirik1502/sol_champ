package sol_game.core_game.components

import sol_engine.ecs.Component

class CharacterComp(
        vararg abilityInputActions: String
) : Component() {

    val abilityInputActions: MutableList<String> = ArrayList()

    init {
        this.abilityInputActions.addAll(abilityInputActions)
    }

    override fun copy(other: Component) {
        val otherComp = other as CharacterComp
        abilityInputActions.clear()
        abilityInputActions.addAll(otherComp.abilityInputActions)
    }
}