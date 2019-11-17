package sol_game

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.SystemBase
import sol_engine.physics_module.CollisionComp

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
                    .peek() { otherEntity -> println("collision! " + otherEntity.name) }

//                            .map() { otherEntity -> otherEntity.getComponent(HurtboxComp::class.java) }
                    .forEach() { otherEntity ->
                        val otherHurtboxComp = otherEntity.getComponent(HurtboxComp::class.java)
                        val otherTransComp = otherEntity.getComponent(TransformComp::class.java)
                        val damageToDealVec = otherTransComp.position.sub(transComp.position, Vector2f())
                                .normalize().mul(damageToDeal)
                        otherHurtboxComp.currDamageTakenVecs.add(damageToDealVec)
                        otherHurtboxComp.totalDamageTaken += damageToDeal
                        hitboxComp.currDamageDealt += damageToDeal
                    }
        }
    }
}