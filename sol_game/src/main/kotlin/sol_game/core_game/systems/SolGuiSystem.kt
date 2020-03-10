package sol_game.core_game.systems

import glm_.vec2.Vec2
import glm_.vec4.Vec4
import imgui.WindowFlag
import org.joml.Vector2f
import sol_engine.core.ModuleSystemBase
import sol_engine.core.TransformComp
import sol_engine.graphics_module.GraphicsModule
import sol_engine.utils.mutable_primitives.MInt
import sol_game.core_game.components.CharacterComp
import sol_game.core_game.components.HurtboxComp

class SolGuiSystem : ModuleSystemBase() {
    override fun onSetup() {
        usingComponents(CharacterComp::class.java, HurtboxComp::class.java, TransformComp::class.java)
        usingModules(GraphicsModule::class.java)
    }

    override fun onUpdate() {
        val graphicsMod = getModule(GraphicsModule::class.java)
        val windowSize = graphicsMod.window.windowSize
        val boxSize = Vector2f(100f, 50f)
        val worldSize = Vector2f(1600f, 900f)
        val posToWindowSize = { pos: Vector2f -> Vector2f(windowSize.x / worldSize.x, windowSize.y / worldSize.y).mul(pos) }

        val i = MInt(0)
        forEachWithComponents(HurtboxComp::class.java, TransformComp::class.java)
        { entity, hurtboxComp, transComp ->
            graphicsMod.renderer.gui.draw { imgui ->
                val posWindowSpace = posToWindowSize(transComp.position)
                val x: Float =
                        if (i.value++ % 2 == 0) 3f
                        else windowSize.x - boxSize.x - 3f
                imgui.getNative().setNextWindowPos(Vec2(x, posWindowSpace.y))
                imgui.getNative().setNextWindowSize(Vec2(boxSize.x, boxSize.y))
                imgui.getNative().setNextWindowBgAlpha(0.2f)
                if (imgui.getNative().begin(entity.name, booleanArrayOf(true), WindowFlag.NoDecoration.i or WindowFlag.AlwaysAutoResize.i)) {
                    imgui.getNative().setWindowFontScale(3f)
                    imgui.getNative().textColored(Vec4(1f, 0f, 0f, 1f), "%.0f", hurtboxComp.totalDamageTaken)
                    imgui.getNative().end()
                }
            }
        }
    }
}