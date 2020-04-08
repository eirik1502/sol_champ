package sol_game.core_game.systems

import org.joml.Vector2f
import org.joml.Vector3f
import sol_engine.core.TransformComp
import sol_engine.ecs.SystemBase
import sol_game.core_game.components.SceneChildComp

class SceneChildSystem : SystemBase() {
    override fun onSetup() {
        usingComponents(SceneChildComp::class.java, TransformComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(SceneChildComp::class.java, TransformComp::class.java)
        { _, hasSceneParentComp, transComp ->
            if (hasSceneParentComp.parent == null) return@forEachWithComponents
            val parent = hasSceneParentComp.parent!!
            if (!parent.hasComponent(TransformComp::class.java)) return@forEachWithComponents
            val parentTransComp = parent.getComponent(TransformComp::class.java)

            val localPosition = transComp.position.sub(hasSceneParentComp.parentPrevPosition, Vector2f())

            val newPositionVec3 = Vector3f(localPosition, 0f)
                    .mul(parentTransComp.scale.x, parentTransComp.scale.y, 0f)
                    .rotateZ(parentTransComp.rotationZ)
                    .add(parentTransComp.position.x, parentTransComp.position.y, 0f)
            val addPosition = Vector2f(newPositionVec3.x, newPositionVec3.y).sub(localPosition)

            transComp.position
                    .sub(hasSceneParentComp.parentPrevPosition)
                    .add(addPosition)
            transComp.scale.sub(hasSceneParentComp.parentPrevScale).add(parentTransComp.scale)
            transComp.rotationZ += -hasSceneParentComp.parentPrevRotationZ + parentTransComp.rotationZ

            hasSceneParentComp.parentPrevPosition.set(addPosition)
            hasSceneParentComp.parentPrevScale.set(parentTransComp.scale)
            hasSceneParentComp.parentPrevRotationZ = parentTransComp.rotationZ
        }
    }
}