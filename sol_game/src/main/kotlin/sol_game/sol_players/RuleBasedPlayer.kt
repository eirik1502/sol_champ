package sol_game.sol_players

import org.joml.Vector2f
import sol_engine.ecs.World
import sol_engine.utils.math.MathF
import sol_game.core_game.SolActions
import sol_game.game.SolPlayer
import sol_game.game_state.SolCharacterState
import sol_game.game_state.SolGameState
import sol_game.game_state.SolGameStateFuncs
import sol_game.game_state.SolStaticGameState


class RuleBasedPlayer : SolPlayer {


    private val weightedRules: Map<Rule, Float> = mapOf(
            SolPlayerRules.createAvoidHolesRule(200f, 20f) to 1f,
            SolPlayerRules.createRetreatRule(0f, 300f) to 0.2f,
            SolPlayerRules.createAttackRule() to 0.7f,
            SolPlayerRules.createApproachRule(1600f) to 0.4f,
            SolPlayerRules.createMoveRandomRule() to 0.6f
    )

    data class WeightedRuleOutput(
            val ruleOutput: RuleOutput,
            val weight: Float
    )

    override fun onSetup() {
    }

    override fun onStart(controlledCharacterIndex: Int, gameState: SolGameState, world: World) {
    }

    override fun onUpdate(controlledCharacterIndex: Int, gameState: SolGameState, world: World): SolActions {
        val myChar = gameState.charactersState[controlledCharacterIndex]
        val otherChar = gameState.charactersState[(controlledCharacterIndex + 1) % 2]
        val staticState = gameState.staticGameState

        val aimX = otherChar.physicalObject.position.x
        val aimY = otherChar.physicalObject.position.y

        val rulesOutput: List<WeightedRuleOutput> = weightedRules
                .map { WeightedRuleOutput(it.key.invoke(myChar, otherChar, staticState), it.value) }

        val resultMoveDirection: Vector2f = rulesOutput
                .asSequence()
                .filter { it.ruleOutput.moveDirection != null }
                .map {
                    val weightedMoveDirection = it.ruleOutput.moveDirection!!.mul(it.weight, Vector2f())
                    weightedMoveDirection.mul(it.ruleOutput.urgency)
                }
                .map {
                    if (it.lengthSquared() == 0f) it
                    else it.normalize()
                }
                .fold(Vector2f()) { acc, new ->
                    acc.add(new)
                }
                .let {
                    // normalize again to not have a short vector that yields no movement
                    if (it.lengthSquared() == 0f) it
                    else it.normalize()
                }


        val resultAbilities: List<Boolean> = rulesOutput
                .filter { it.ruleOutput.abilities != null }
                .fold(Pair(0f, listOf(false, false, false))) { acc, weightedRuleOutput ->
                    val newUrgency = weightedRuleOutput.ruleOutput.urgency
                    val newWeight = weightedRuleOutput.weight
                    val compareMeasure = newUrgency * newWeight
                    if (compareMeasure < acc.first) acc
                    else Pair(compareMeasure, weightedRuleOutput.ruleOutput.abilities!!)
                }
                .second

        val moveDirectionActions = SolPlayerRules.directionToMoveInput(resultMoveDirection)

        return SolActions(
                mvLeft = moveDirectionActions[0],
                mvRight = moveDirectionActions[1],
                mvUp = moveDirectionActions[2],
                mvDown = moveDirectionActions[3],
                ability1 = resultAbilities[0],
                ability2 = resultAbilities[1],
                ability3 = resultAbilities[2],
                aimX = aimX,
                aimY = aimY
        )
    }

    override fun onEnd(controlledCharacterIndex: Int, gameState: SolGameState, world: World) {
    }


}