package sol_game.core_game.systems

import sol_engine.ecs.IteratingSystemBase
import sol_engine.physics_module.CollisionComp
import sol_game.core_game.components.FallIntoHoleComp
import sol_game.core_game.components.HoleComp

class FallIntoHoleSystem : IteratingSystemBase() {
    override fun onSetupWithUpdate() {
        updateWithComponents(
                FallIntoHoleComp::class.java,
                CollisionComp::class.java
        ) { _, fallIntoHoleComp, collisionComp ->
            collisionComp.collidingEntities.keys.find { collidingEntity ->
                collidingEntity.hasComponent(HoleComp::class.java)
            }
                    ?.run { fallIntoHoleComp.fallenInHoleNow = true }
                    ?: run { fallIntoHoleComp.fallenInHoleNow = false }
        }
    }
}