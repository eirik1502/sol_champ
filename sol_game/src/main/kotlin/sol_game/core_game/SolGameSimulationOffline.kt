package sol_game.core_game


import org.joml.Vector2f
import sol_engine.creator.CreatorSystem
import sol_engine.engine_interface.SolSimulation
import sol_engine.game_utils.*
import sol_engine.graphics_module.*
import sol_engine.input_module.*

import sol_engine.physics_module.*
import sol_game.core_game.components.SolGameComp
import sol_game.core_game.entities_factory.CharacterEntities
import sol_game.core_game.entities_factory.StaticEntities
import sol_game.core_game.systems.*
import sol_game.game_state.SolGameState
import sol_game.game_state.SolStaticGameState
import sol_game.game_state.SolGameStateRetrieval

open class SolGameSimulationOffline(
        private val charactersConfigs: List<CharacterConfig>,
        private val graphicsSettings: GraphicsSettings = GraphicsSettings()
) : SolSimulation() {

    data class GraphicsSettings(
            val headless: Boolean = false,
            val graphicalInput: Boolean = true,
            val controlPlayerIndex: Int = 0,
            val debugUI: Boolean = true,
            val allowGui: Boolean = true
    )

    val worldSize = Vector2f(1600f, 900f)

    init {
        if (charactersConfigs.size != 2) {
            throw IllegalArgumentException("Two character configs must be given")
        }
    }

    public override fun onSetupModules() {
        if (!graphicsSettings.headless) {
            addModule(GraphicsModule(GraphicsModuleConfig(
                    WindowConfig(1f, 1f, "sol server", false),
                    RenderConfig(worldSize.x / 2, worldSize.y / 2, worldSize.x, worldSize.y, !graphicsSettings.allowGui)
            )))
        }

        val inputSourceModules = listOfNotNull(
                if (!graphicsSettings.headless && graphicsSettings.graphicalInput) run {
                    val inputGroupPrefix = "t${graphicsSettings.controlPlayerIndex}p0:"
                    InputGuiSourceModule(InputGuiSourceModuleConfig(
                            Vector2f(worldSize.x, worldSize.y),
                            mapOf(
                                    "${inputGroupPrefix}mvLeft" to InputConsts.KEY_A,
                                    "${inputGroupPrefix}mvRight" to InputConsts.KEY_D,
                                    "${inputGroupPrefix}mvUp" to InputConsts.KEY_W,
                                    "${inputGroupPrefix}mvDown" to InputConsts.KEY_S,
                                    "${inputGroupPrefix}ability1" to InputConsts.MOUSE_BUTTON_LEFT,
                                    "${inputGroupPrefix}ability2" to InputConsts.MOUSE_BUTTON_RIGHT,
                                    "${inputGroupPrefix}ability3" to InputConsts.KEY_SPACE,
                                    "${inputGroupPrefix}aimX" to InputConsts.CURSOR_X,
                                    "${inputGroupPrefix}aimY" to InputConsts.CURSOR_Y
                            )
                    ))
                } else null,

                ExternalInputSourceModule(ExternalInputSourceModuleConfig())
        )

        inputSourceModules
                .forEach { addModule(it) }

        addModule(InputModule(InputModuleConfig(inputSourceModules.map { it::class.java })))
    }

    public override fun onSetupWorld() {
        world.addSystems(
                InputSystem::class.java,
                SolGameServerSystem::class.java,

                MovementSystem::class.java,
                FaceAimSystem::class.java,
                CharacterSystem::class.java,
                AbilitySystem::class.java,
                DisableControlOnAbilityExecutionSystem::class.java,

                ControlDisabledSystem::class.java,

                EmitterTimedSystem::class.java,
                DestroySelfTimedSystem::class.java,

                CollisionSystem::class.java,

                HitboxInteractionSystem::class.java,
                HurtboxSystem::class.java,
                StunOnKnockbackSystem::class.java,

                FallIntoHoleSystem::class.java,
                LoseStockByHoleSystem::class.java,
                RespawnOnStockLossSystem::class.java,
                ResetDamageOnStockLossSystem::class.java,

                NaturalCollisionResolutionSystem::class.java,
                PhysicsSystem::class.java,

                SceneChildSystem::class.java,

                if (!graphicsSettings.headless && graphicsSettings.debugUI && graphicsSettings.allowGui)
                    CreatorSystem::class.java
                else null,
                if (graphicsSettings.allowGui) SolGuiSystem::class.java else null,

                RenderSystem::class.java
        )

        world.addEntity("sol-game-status").addComponents(
                SolGameComp(
                        gameState = SolGameComp.GameState.BEFORE_START,
                        worldSize = Vector2f(worldSize)
                )
        )

        StaticEntities.addStaticMapEntities(world, worldSize)

        charactersConfigs
                .flatMap { CharacterEntities.createCharacterEntityClass(true, it) }
                .forEach { world.addEntityClass(it) }

        val startPositions = listOf(Vector2f(200f, worldSize.y / 2), Vector2f(worldSize.x - 200, worldSize.y / 2))

        charactersConfigs.forEachIndexed { index, characterConfig ->
            val startPosition = startPositions[index]
            CharacterEntities.instanciateCharacter(
                    true,
                    world,
                    characterConfig.name,
                    characterConfig.characterId,
                    teamIndex = index,
                    playerIndex = 0,
                    startPosition = startPosition
            )
        }


    }

    public override fun onStepEnd() {
    }
    
    var cachedStaticGameState: SolStaticGameState? = null

    fun retrieveGameState(): SolGameState {
        val gameState = SolGameStateRetrieval.retrieveSolGameState(world, cachedStaticGameState)
        cachedStaticGameState = gameState.staticGameState
        return gameState
    }

    // should be called on step end
    fun setInputs(playerIndex: Int, actions: SolActions) {
        val inputGroupPrefix = "t${playerIndex}p0:"
        val inputSource = modulesHandler.getModule(ExternalInputSourceModule::class.java)
        inputSource.updateTriggerInputs(mapOf(
                "${inputGroupPrefix}mvLeft" to actions.mvLeft,
                "${inputGroupPrefix}mvRight" to actions.mvRight,
                "${inputGroupPrefix}mvUp" to actions.mvUp,
                "${inputGroupPrefix}mvDown" to actions.mvDown,
                "${inputGroupPrefix}ability1" to actions.ability1,
                "${inputGroupPrefix}ability2" to actions.ability2,
                "${inputGroupPrefix}ability3" to actions.ability3
        ))
        inputSource.updateFloatInputs(mapOf(
                "${inputGroupPrefix}aimX" to actions.aimX,
                "${inputGroupPrefix}aimY" to actions.aimY
        ))
    }
}