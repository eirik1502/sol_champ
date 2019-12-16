package sol_game.ai_interface;

import sol_engine.module.Module;


class AiInputModule(
        var ai: SolAI
) : Module() {

    val currActions: MutableSet<String> = HashSet()

    override fun onSetup() {
        ai.onSetup()
    }

    override fun onStart() {
    }

    override fun onEnd() {
    }

    override fun onUpdate() {
        currActions.clear()
        currActions.add(ai.onNewFrame(StateObservation()))
    }

    public fun isAction(action: String): Boolean {
        return currActions.contains(action)
    }
}