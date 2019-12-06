package sol_game

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.SystemBase
import sol_engine.game_utils.InputComp
import sol_engine.utils.math.MathF

class FaceCursorSystem : SystemBase() {

    val cursorPosBuf: Vector2f = Vector2f()
    val transPosBuf: Vector2f = Vector2f()

    override fun onSetup() {
        usingComponents(FaceCursorComp::class.java, InputComp::class.java, TransformComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(FaceCursorComp::class.java, InputComp::class.java, TransformComp::class.java) { entity, faceCursorComp, userInpComp, transComp ->
            if (faceCursorComp.disabled) return@forEachWithComponents

            cursorPosBuf.set(userInpComp.cursorPosition);
            transPosBuf.set(transComp.x, transComp.y)
            transComp.rotationZ = MathF.pointDirection(transPosBuf, cursorPosBuf)
        }
    }
}