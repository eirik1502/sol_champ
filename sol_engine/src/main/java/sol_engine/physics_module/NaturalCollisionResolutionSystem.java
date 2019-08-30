package sol_engine.physics_module;

import org.joml.Vector2f;
import sol_engine.core.TransformComp;
import sol_engine.ecs.Entity;
import sol_engine.ecs.SystemBase;
import sol_engine.utils.math.MathF;

import java.util.HashSet;
import java.util.Set;

public class NaturalCollisionResolutionSystem extends SystemBase {

    private Vector2f tempVec = new Vector2f();

    @Override
    public void onStart() {
        usingComponents(NaturalCollisionResolutionComp.class, TransformComp.class, PhysicsBodyComp.class, CollisionComp.class);
    }

    @Override
    public void onUpdate() {
        final Set<Entity> resolvedEntities = new HashSet<>();

        groupEntities.forEach(entity -> {
            PhysicsBodyComp physComp = entity.getComponent(PhysicsBodyComp.class);
            CollisionComp collComp = entity.getComponent(CollisionComp.class);
            TransformComp transComp = entity.getComponent(TransformComp.class);

            collComp.collidingEntities.forEach((collidingEntity, collisionData) -> {
                if (!resolvedEntities.contains(collidingEntity)) {
                    PhysicsBodyComp otherPhysComp = collidingEntity.getComponent(PhysicsBodyComp.class);
                    TransformComp otherTransComp = collidingEntity.getComponent(TransformComp.class);

                    if (resolveCollision(physComp, otherPhysComp, collisionData)) {
                        positionalCorrection(physComp, transComp, otherPhysComp, otherTransComp, collisionData);
                    }
                }
            });
            resolvedEntities.add(entity);
        });
    }

    @Override
    public void onEnd() {

    }

    private Vector2f relVelocity = new Vector2f();
    private Vector2f impulseVec = new Vector2f();
    private Vector2f impulseVecCopy = new Vector2f();

    private boolean resolveCollision(PhysicsBodyComp physComp, PhysicsBodyComp otherPhysComp, CollisionData data) {
        Vector2f collisionVector = data.collisionVector;

        otherPhysComp.velocity.sub(physComp.velocity, relVelocity);

        float velAlongNormal = relVelocity.dot(collisionVector);

        // do not esolve collisions if both objects have infinate mass
        if (physComp.mass == PhysicsBodyComp.INF_MASS && otherPhysComp.mass == PhysicsBodyComp.INF_MASS) {
            return false;
        }

        if (velAlongNormal > 0) { //do not resolve collision if objects are moving apart
            return false;
        }

        float invMass1 = inverseMass(physComp.mass);
        float invMass2 = inverseMass(otherPhysComp.mass);

        float elasticity = MathF.min(physComp.elasticity, otherPhysComp.elasticity);

        float impulseLength = -(1 + elasticity) * velAlongNormal;
        impulseLength /= invMass1 + invMass2;

        //apply impulse
        collisionVector.mul(impulseLength, impulseVec); //-(1 + elasticity)*velAlongNormal;

        physComp.impulse.add(impulseVec.mul(invMass1, impulseVecCopy).negate());
        otherPhysComp.impulse.add(impulseVec.mul(invMass2, impulseVecCopy));

//        positionalCorrection(data);
        return true;
    }

    private Vector2f correctionVec = new Vector2f();
    private Vector2f addPosVec = new Vector2f();

    private void positionalCorrection(PhysicsBodyComp physComp, TransformComp transComp,
                                      PhysicsBodyComp otherPhysComp, TransformComp otherTransComp,
                                      CollisionData data) {

        float percent = 0.2f; // usually 20% to 80%
        float slop = 0.01f; // usually 0.01 to 0.1

        float invMass1 = inverseMass(physComp.mass);
        float invMass2 = inverseMass(otherPhysComp.mass);

        float correctionMagnitude =
                percent * (MathF.max(data.penetrationDepth - slop, 0.0f) / (invMass1 + invMass2));

        data.collisionVector.mul(correctionMagnitude, correctionVec);
        correctionVec.mul(invMass1, addPosVec).negate();
        transComp.x += addPosVec.x;
        transComp.y += addPosVec.y;

        correctionVec.mul(invMass2, addPosVec);
        otherTransComp.x += addPosVec.x;
        otherTransComp.y += addPosVec.y;
    }

    private float inverseMass(float mass) {
        return mass == PhysicsBodyComp.INF_MASS ? 0 : 1.0f / mass;
    }
}
