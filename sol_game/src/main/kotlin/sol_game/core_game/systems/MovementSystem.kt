package sol_game.core_game.systems

import org.joml.Vector2f
import sol_engine.ecs.SystemBase
import sol_engine.input_module.InputComp
import sol_engine.physics_module.PhysicsBodyComp
import sol_game.core_game.components.MovementComp

class MovementSystem : SystemBase() {
    private val zeroVec = Vector2f()

    private val leftUnitVec = Vector2f(-1f, 0f)
    private val rightUnitVec = Vector2f(1f, 0f)
    private val upUnitVec = Vector2f(0f, -1f)
    private val downUnitVec = Vector2f(0f, 1f)

    private val directionalUnitVecs = listOf(leftUnitVec, rightUnitVec, upUnitVec, downUnitVec)

    override fun onSetup() {
        usingComponents(InputComp::class.java, MovementComp::class.java, PhysicsBodyComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(
                InputComp::class.java,
                MovementComp::class.java,
                PhysicsBodyComp::class.java
        ) { _, inputComp, movementComp, physComp ->
            if (movementComp.disabled) return@forEachWithComponents

            val inputDirectionVec = movementComp.inputActions
                    .subList(0, directionalUnitVecs.size)
                    .mapIndexed { i, inputAction ->
                        if (inputComp.checkTrigger(inputAction)) directionalUnitVecs[i]
                        else zeroVec
                    }
                    .fold(Vector2f()) { acc, inputVec -> acc.add(inputVec) }

            val addVelocity =
                    if (inputDirectionVec.lengthSquared() == 0f) inputDirectionVec
                    else inputDirectionVec.normalize(movementComp.acceleration)

            val newSpeed = physComp.velocity.add(addVelocity, Vector2f()).length()
            val currSpeed = physComp.velocity.length()

            if (newSpeed < currSpeed || newSpeed < movementComp.maxSpeed) {
                physComp.impulse.add(addVelocity)
            }
        }
    }
}