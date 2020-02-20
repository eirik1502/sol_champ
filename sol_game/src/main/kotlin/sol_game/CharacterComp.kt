package sol_game.core_game

import sol_engine.ecs.Component

class CharacterComp(
        vararg abilityInputActions: String
) : Component() {

    val abilityInputActions: MutableList<String> = ArrayList()

    init {
        this.abilityInputActions.addAll(abilityInputActions)
    }

    override fun clone(): CharacterComp {
        val comp = CharacterComp()
        comp.abilityInputActions.addAll(abilityInputActions)
        return comp
    }
}