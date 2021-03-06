package sol_game.core_game.systems

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.EntitiesUtils
import sol_engine.ecs.Entity
import sol_engine.ecs.SystemBase
import sol_engine.physics_module.CollisionComp
import sol_engine.utils.math.MathF
import sol_game.core_game.components.HitboxComp
import sol_game.core_game.components.HurtboxComp

class HitboxInteractionSystem : SystemBase() {


    override fun onSetup() {
        usingComponents(HitboxComp::class.java, CollisionComp::class.java, TransformComp::class.java)
    }

    override fun onUpdate() {

        // remove and do not consider hitbox entities that hit last frame
        val hitboxEntitiesNeverHit = entities.filter {
            val hitboxComp = it.getComponent(HitboxComp::class.java)
            if (hitboxComp.hitsGivenNow.isNotEmpty()) {
                world.removeEntity(it)
                false
            } else {
                hitboxComp.hitsGivenNow.clear()
                true
            }
        }
        EntitiesUtils.ForEachWithComponents(hitboxEntitiesNeverHit.stream(),
                HitboxComp::class.java,
                CollisionComp::class.java,
                TransformComp::class.java
        ) { _, hitboxComp, collComp, transComp ->
            collComp.collidingEntities.keys.stream()
                    .filter() { otherEntity -> otherEntity.hasComponent(HurtboxComp::class.java) }
                    .filter() { otherEntity -> otherEntity.hasComponent(TransformComp::class.java) }
                    .filter() { otherEntity -> otherEntity != hitboxComp.owner }
                    .forEach() { otherEntity ->
                        val otherHurtboxComp = otherEntity.getComponent(HurtboxComp::class.java)
                        val otherTransComp = otherEntity.getComponent(TransformComp::class.java)

                        handleDeliverHit(hitboxComp, transComp, otherHurtboxComp, otherTransComp)
                    }
        }
    }

    private fun handleDeliverHit(
            fromHitboxComp: HitboxComp,
            fromTransformComp: TransformComp,
            toHurtboxComp: HurtboxComp,
            toTransformComp: TransformComp
    ) {
        val damageToDeal = fromHitboxComp.damage

        val knockbackTowardsPoint = fromHitboxComp.knockbackTowardsPoint
        val relativeKnockbackPoint = fromHitboxComp.knockbackPoint

        val hitboxDirection = fromTransformComp.rotationZ

        val relativeKnockbackOrigin = Vector2f(
                MathF.lengthdirX(relativeKnockbackPoint, hitboxDirection),
                MathF.lengthdirY(relativeKnockbackPoint, hitboxDirection)
        )

        val knockbackOrigin = relativeKnockbackOrigin.add(fromTransformComp.position, Vector2f())

        val interactionVec = toTransformComp.position.sub(knockbackOrigin, Vector2f()).let {
            if (knockbackTowardsPoint) it.negate()
            else it
        }

        val hit = HurtboxComp.Hit(
                interactionVec,
                damageToDeal,
                fromHitboxComp.baseKnockback,
                fromHitboxComp.knockbackRatio
        )

        toHurtboxComp.currHitsTaken.add(hit)
        fromHitboxComp.hitsGivenNow.add(hit)
    }
}