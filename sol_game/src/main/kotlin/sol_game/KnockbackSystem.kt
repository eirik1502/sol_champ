package sol_game

import org.joml.Vector2f
import sol_engine.ecs.SystemBase
import sol_engine.physics_module.PhysicsBodyComp

class KnockbackSystem : SystemBase() {
    override fun onSetup() {
        usingComponents(HurtboxComp::class.java, PhysicsBodyComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(HurtboxComp::class.java, PhysicsBodyComp::class.java)
        { _, hurtboxComp, physicsComp ->
            val impulse = hurtboxComp.currDamageTakenVecs.fold(Vector2f()) { acc, curr -> acc.add(curr) }
//            if (impulse.length() > 0.000001f) println(impulse)
            physicsComp.impulse.add(impulse)
            hurtboxComp.currDamageTakenVecs.clear()
        }
    }
}