package sol_game.core_game.systems

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.SystemBase
import sol_engine.input_module.InputComp
import sol_engine.utils.math.MathF
import sol_game.core_game.components.FaceAimComp

class FaceAimSystem : SystemBase() {

    val cursorPosBuf: Vector2f = Vector2f()
    val transPosBuf: Vector2f = Vector2f()

    override fun onSetup() {
        usingComponents(FaceAimComp::class.java, InputComp::class.java, TransformComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(FaceAimComp::class.java, InputComp::class.java, TransformComp::class.java) { entity, faceCursorComp, inputComp, transComp ->
            if (faceCursorComp.disabled) return@forEachWithComponents

            cursorPosBuf.set(inputComp.floatInput("aimX"), inputComp.floatInput("aimY"));
            transPosBuf.set(transComp.x, transComp.y)
            transComp.rotationZ = MathF.pointDirection(transPosBuf, cursorPosBuf)
        }
    }
}