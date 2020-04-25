package sol_game.sol_players

import org.joml.Vector2f
import sol_engine.utils.math.MathF
import sol_game.game_state.SolCharacterState
import sol_game.game_state.SolGameStateFuncs
import sol_game.game_state.SolStaticGameState

data class RuleOutput(
        val urgency: Float,
        val moveDirection: Vector2f? = null,
        val abilities: List<Boolean>? = null
)
typealias Rule = (myChar: SolCharacterState, otherChar: SolCharacterState, staticState: SolStaticGameState) -> RuleOutput

object SolPlayerRules {

    // should be 1 at domainMin and 0 at domainMax
    fun linear(valueZero: Float, valueOne: Float, value: Float): Float {
        return ((1 / (valueOne - valueZero)) * (value - valueZero)).coerceIn(0f, 1f)
    }

    // output directions from index 0 - 3: left, right, up, down
    fun directionToMoveInput(normalizedDirection: Vector2f): List<Boolean> {
        val coordThreshold = MathF.sin(MathF.PI / (4f * 2f))// ~0.7

        val mvLeft = normalizedDirection.x <= -coordThreshold
        val mvRight = normalizedDirection.x >= coordThreshold
        val mvUp = normalizedDirection.y <= -coordThreshold
        val mvDown = normalizedDirection.y >= coordThreshold

        return listOf(mvLeft, mvRight, mvUp, mvDown)
    }

    fun createAvoidHolesRule(maxDistance: Float = 500f, minDistance: Float = 100f): Rule {
        val avoidHolesRule: Rule = { myChar, otherChar, staticState ->

            val closestHole = SolGameStateFuncs.closestHole(myChar.physicalObject, staticState)
            val closestHoleDistSquared = closestHole.lengthSquared()
            val ruleOutput =
                    if (closestHoleDistSquared != 0f && closestHoleDistSquared < maxDistance * maxDistance) {
                        val holeDistanceLinearRatio = (minDistance / closestHole.length().coerceAtLeast(0.01f))
                        val holeDistanceExpRatio = holeDistanceLinearRatio * holeDistanceLinearRatio
                        val moveDir = closestHole.negate(Vector2f())
                        val urgency = holeDistanceExpRatio.coerceAtMost(1f)

                        RuleOutput(urgency, moveDirection = moveDir, abilities = listOf(false, false, false))
                    } else RuleOutput(0f)

            ruleOutput
        }
        return avoidHolesRule
    }

    fun createApproachRule(maxDistance: Float = 1600f): Rule =
            { myChar, otherChar, staticState ->
                val distToOtherChar = SolGameStateFuncs.distanceOuter(myChar.physicalObject, otherChar.physicalObject)
                val moveDir = distToOtherChar
                val urgency = linear(0f, maxDistance, distToOtherChar.length())
                RuleOutput(urgency, moveDir, listOf(false, false, false))
            }

    fun createAttackRule(): Rule = { myChar, otherChar, staticState ->
        val distToOtherChar = SolGameStateFuncs.distanceOuter(myChar.physicalObject, otherChar.physicalObject)
        val urgency = (500f / distToOtherChar.length().coerceAtLeast(0.01f)).coerceAtMost(1f)
        val shouldAttack = MathF.randInt(0, 20) == 0
        val abilities =
                if (shouldAttack) {
                    val abilityIndex = MathF.randInt(0, 2)
                    (0..2).map { it == abilityIndex }
                } else null

        RuleOutput(urgency, abilities = abilities)
    }

    fun createRetreatRule(minDistance: Float, maxDistance: Float): Rule = { myChar, otherChar, staticState ->
        val distToOtherChar = SolGameStateFuncs.distanceOuter(myChar.physicalObject, otherChar.physicalObject)
        val moveDir = distToOtherChar.negate(Vector2f())
        val urgency = linear(maxDistance, minDistance, distToOtherChar.length())
        RuleOutput(urgency, moveDir)
    }

    fun createMoveRandomRule(): Rule {
        val velocity = Vector2f()
        return { myChar, otherChar, staticState ->
            val moveDir = velocity.add(Vector2f(MathF.randRange(-0.5f, 0.5f), MathF.randRange(-0.5f, 0.5f))).normalize()
            RuleOutput(1f, moveDir)
        }
    }

}