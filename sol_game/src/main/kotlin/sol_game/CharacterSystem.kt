package sol_game

import sol_engine.ecs.SystemBase
import sol_engine.game_utils.MoveByVelocityComp
import sol_engine.game_utils.UserInputComp
import sol_engine.physics_module.PhysicsBodyComp
import sol_engine.utils.stream.WithIndex

class CharacterSystem : SystemBase() {
    override fun onSetup() {
        usingComponents(CharacterComp::class.java, UserInputComp::class.java, AbilityComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(CharacterComp::class.java, UserInputComp::class.java, AbilityComp::class.java)
        { entity, charComp, userInputComp, abComp ->

            // trigger abilities
            charComp.abilityInputActions.stream()
                    .map(WithIndex.map())
                    .filter { inputI -> userInputComp.checkPressed(inputI.value) }
                    .filter { inputI -> inputI.i < abComp.abilities.size }
                    .map { inputI -> abComp.abilities[inputI.i] }
                    .forEach { ab -> ab.trigger = true }

            entity.modifyIfHasComponent(PhysicsBodyComp::class.java)
            { comp -> if (abComp.isExecuting) comp.velocity.mul(0.9f) }
            entity.modifyIfHasComponent(MoveByVelocityComp::class.java) { comp -> comp.disabled = abComp.isExecuting }
            entity.modifyIfHasComponent(FaceCursorComp::class.java) { comp -> comp.disabled = abComp.isExecuting }
        }


    }
}
