package sol_game.core_game.modules

import sol_engine.module.Module

class SolGameClientModule : Module() {


    var gameStarted: Boolean = false
    var gameEnded: Boolean = false
    var playerIndexWon: Int = -1

    override fun onSetup() {

    }

    override fun onStart() {
    }

    override fun onUpdate() {

        if (gameEnded) {

        }
    }


    override fun onEnd() {
    }


}