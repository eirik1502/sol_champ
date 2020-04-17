package sol_game.core_game.systems

import glm_.vec2.Vec2
import glm_.vec4.Vec4
import imgui.WindowFlag
import org.joml.Vector2f
import sol_engine.core.ModuleSystemBase
import sol_engine.core.TransformComp
import sol_engine.graphics_module.GraphicsModule
import sol_engine.network.network_ecs.host_managing.TeamPlayerComp
import sol_engine.utils.mutable_primitives.MInt
import sol_game.core_game.components.CharacterComp
import sol_game.core_game.components.HurtboxComp
import sol_game.core_game.components.StockComp

class SolGuiSystem : ModuleSystemBase() {
    override fun onSetup() {
        usingComponents(
                CharacterComp::class.java,
                HurtboxComp::class.java,
                StockComp::class.java,
                TransformComp::class.java,
                TeamPlayerComp::class.java
        )
        usingModules(GraphicsModule::class.java)
    }

    override fun onUpdate() {
        val graphicsMod = getModule(GraphicsModule::class.java)
        val windowSize = graphicsMod.window.windowSize
        val boxSize = Vector2f(100f, 50f * 2)
        val worldSize = Vector2f(1600f, 900f)
        val posToWindowSize = { pos: Vector2f -> Vector2f(windowSize.x / worldSize.x, windowSize.y / worldSize.y).mul(pos) }

        forEachWithComponents(
                HurtboxComp::class.java,
                StockComp::class.java,
                TransformComp::class.java,
                TeamPlayerComp::class.java
        )
        { entity, hurtboxComp, stockComp, transComp, teamPlayerComp ->
            graphicsMod.renderer.guiRenderer.draw { imgui ->
                val posWindowSpace = posToWindowSize(transComp.position)
                val x: Float =
                        if (teamPlayerComp.teamIndex == 0) 3f
                        else windowSize.x - boxSize.x - 3f
                val stocksText = "O".repeat(stockComp.currentStockCount.coerceAtLeast(0))
                val damagePercent = (hurtboxComp.totalDamageTaken / 3000f).coerceAtMost(0.999f)
                val inverseDamagePercent = 1 - damagePercent
                val damageColor = Vec4(damagePercent, 0.567f * inverseDamagePercent, 0.342f, 1f)

                imgui.getNative().setNextWindowPos(Vec2(x, posWindowSpace.y))
                imgui.getNative().setNextWindowSize(Vec2(boxSize.x, boxSize.y))
                imgui.getNative().setNextWindowBgAlpha(0.2f)
                if (imgui.getNative().begin(entity.name + "##" + entity.hashCode(), booleanArrayOf(true), WindowFlag.NoDecoration.i or WindowFlag.AlwaysAutoResize.i)) {
                    imgui.getNative().setWindowFontScale(3f)
                    imgui.getNative().textColored(damageColor, "%.0f", hurtboxComp.totalDamageTaken)
                    imgui.getNative().textColored(Vec4(0f, 1f, 1f, 1f), "%s", stocksText)
                    imgui.getNative().end()
                }
            }
        }
    }
}