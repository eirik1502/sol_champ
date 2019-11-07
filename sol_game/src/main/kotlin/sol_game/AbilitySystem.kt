package sol_game

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.SystemBase
import sol_engine.game_utils.UserInputComp
import sol_engine.physics_module.PhysicsBodyComp

class AbilitySystem : SystemBase() {
    override fun onSetup() {
        usingComponents(AbilityComp::class.java, TransformComp::class.java, UserInputComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(
                AbilityComp::class.java,
                TransformComp::class.java,
                UserInputComp::class.java
        )
        { entity, abComp, transComp, userInputComp ->
            if (abComp.currentCooldown-- <= 0 && userInputComp.checkPressed("action1")) {
                val impulse = userInputComp.getCursorPosition().sub(Vector2f(transComp.x, transComp.y), Vector2f())
                world.instanciateEntityClass(abComp.abilityEntityClass, abComp.abilityEntityClass)
                        .modifyComponent(TransformComp::class.java) { comp -> comp.setXY(transComp.x, transComp.y) }
                        .modifyComponent(PhysicsBodyComp::class.java) { comp -> comp.impulse.add(impulse) }
                abComp.currentCooldown = abComp.cooldown
            }
        }
    }
}
