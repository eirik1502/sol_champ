package sol_game

import sol_engine.ecs.SystemBase
import sol_engine.game_utils.UserInputComp

class CharacterSystem : SystemBase() {
    override fun onSetup() {
        usingComponents(CharacterComp::class.java, UserInputComp::class.java, AbilityComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(UserInputComp::class.java, AbilityComp::class.java) { entity, userInputComp, abComp ->
            abComp.abilities.stream()
                    .filter() { ab -> userInputComp.checkPressed(ab.inputAction) }
                    .forEach() { ab -> ab.trigger = true }
        }


    }
}
