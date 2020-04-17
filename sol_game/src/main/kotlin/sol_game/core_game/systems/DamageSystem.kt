package sol_game.core_game.systems

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.SystemBase
import sol_engine.physics_module.CollisionComp
import sol_engine.utils.math.MathF
import sol_game.core_game.components.HitboxComp
import sol_game.core_game.components.HurtboxComp

class DamageSystem : SystemBase() {


    override fun onSetup() {
        usingComponents(HitboxComp::class.java, CollisionComp::class.java, TransformComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(HitboxComp::class.java) { _, hitboxComp ->
            hitboxComp.currDamageDealt = 0f
        }

        forEachWithComponents(HitboxComp::class.java, CollisionComp::class.java, TransformComp::class.java)
        { entity, hitboxComp, collComp, transComp ->
            val damageToDeal = hitboxComp.damage
            collComp.collidingEntities.keys.stream()
                    .filter() { otherEntity -> otherEntity.hasComponent(HurtboxComp::class.java) }
                    .filter() { otherEntity -> otherEntity.hasComponent(TransformComp::class.java) }
                    .filter() { otherEntity -> otherEntity != hitboxComp.owner }
                    .forEach() { otherEntity ->
                        val otherHurtboxComp = otherEntity.getComponent(HurtboxComp::class.java)
                        val otherTransComp = otherEntity.getComponent(TransformComp::class.java)
                        val interactionVec = otherTransComp.position.sub(transComp.position, Vector2f())
                        otherHurtboxComp.currHitsTaken.add(HurtboxComp.Hit(
                                interactionVec,
                                damageToDeal,
                                hitboxComp.baseKnockback,
                                hitboxComp.knockbackRatio
                        ))
                        otherHurtboxComp.totalDamageTaken += damageToDeal
                        otherHurtboxComp.totalDamageTaken = MathF.min(otherHurtboxComp.totalDamageTaken, 9999f)
                        hitboxComp.currDamageDealt += damageToDeal

                        world.removeEntity(entity)
                    }
        }
    }
}