package sol_game.ai_interface

import sol_engine.core.ModuleSystemBase

class AiInputSystem : ModuleSystemBase() {
    override fun onSetup() {
        usingModules(AiInputModule::class.java)
        usingComponents(AiInputComp::class.java)
    }

    override fun onUpdate() {
        val inputModule: AiInputModule = getModule(AiInputModule::class.java)
        forEachWithComponents(AiInputComp::class.java)
        { entity, aiInpComp ->
            aiInpComp.actions.addAll(inputModule.currActions)
        }
    }
}