package sol_game.core_game

import org.joml.Vector2f
import sol_engine.creator.CreatorSystem
import sol_engine.engine_interface.SolSimulation
import sol_engine.game_utils.DestroySelfTimedSystem
import sol_engine.game_utils.EmitterTimedSystem
import sol_engine.game_utils.MoveByVelocitySystem
import sol_engine.graphics_module.*
import sol_engine.input_module.*
import sol_engine.network.network_ecs.host_managing.NetClientSystem
import sol_engine.network.network_ecs.host_managing.NetEcsUtils
import sol_engine.network.network_ecs.world_syncing.NetSyncClientSystem
import sol_engine.network.network_game.game_client.ClientConfig
import sol_engine.network.network_sol_module.NetworkClientModule
import sol_engine.network.network_sol_module.NetworkClientModuleConfig
import sol_engine.physics_module.CollisionSystem
import sol_game.core_game.components.SolActionsPacketComp
import sol_game.core_game.modules.SolClientPlayerModule
import sol_game.core_game.modules.SolClientPlayerModuleConfig
import sol_game.core_game.systems.*
import sol_game.game.SolClientPlayer

class SolGameSimulationClient(
        val connectAddress: String,
        val connectPort: Int,
        val gameId: String,
        val connectionKey: String,
        val isObserver: Boolean,
        // if player is set, it will control the client, and graphical input will be disabled
        val playerClass: Class<out SolClientPlayer>? = null,
        val headless: Boolean = false,
        val debugUI: Boolean = false,  // cannot be set in headless mode
        val allowGui: Boolean = true
) : SolSimulation() {

    override fun onSetupModules() {
        if (!headless) {
            addModule(GraphicsModule(GraphicsModuleConfig(
                    WindowConfig(0.5f, 0.5f, "sol client", true),
                    RenderConfig(800f, 450f, 1600f, 900f, !allowGui)
            )))
        }
        val inputSourceModule = when (playerClass) {
            null -> when (headless) {
                false -> InputGuiSourceModule(InputGuiSourceModuleConfig(
                        Vector2f(1600f, 900f),
                        mapOf(
                                "mvLeft" to InputConsts.KEY_A,
                                "mvRight" to InputConsts.KEY_D,
                                "mvUp" to InputConsts.KEY_W,
                                "mvDown" to InputConsts.KEY_S,
                                "ability1" to InputConsts.MOUSE_BUTTON_LEFT,
                                "ability2" to InputConsts.MOUSE_BUTTON_RIGHT,
                                "ability3" to InputConsts.KEY_SPACE,
                                "aimX" to InputConsts.CURSOR_X,
                                "aimY" to InputConsts.CURSOR_Y
                        )
                ))
                else -> null
            }
            else -> SolClientPlayerModule(SolClientPlayerModuleConfig(playerClass))
        }
        inputSourceModule?.let {
            addModule(inputSourceModule)
            addModule(InputModule(InputModuleConfig(
                    inputSourceModule::class.java
            )));
        }

        addModule(NetworkClientModule(NetworkClientModuleConfig(
                ClientConfig(
                        connectAddress,
                        connectPort,
                        gameId,
                        connectionKey,
                        isObserver
                ),
                listOf(
                        CharactersConfigsPacket::class.java
                )
        )))
    }

    override fun onSetupWorld() {
        world.addSystems(
                NetClientSystem::class.java,
                NetSyncClientSystem::class.java,

                SolGameClientSystem::class.java,

//                FaceAimSystem::class.java,
//                CharacterSystem::class.java,
//                AbilitySystem::class.java,
//                EmitterTimedSystem::class.java,
//                DestroySelfTimedSystem::class.java,

//                CollisionSystem::class.java,
//                DamageSystem::class.java,
//                KnockbackSystem::class.java,
//                NaturalCollisionResolutionSystem::class.java,

                SceneChildSystem::class.java,
//                PhysicsSystem::class.java,

                // Regular inputs are not used to update game state, only the server state input
                // Take input as a result of the state update, hence put it last
                if (playerClass != null) SolClientPlayerSystem::class.java else null,  // takes input from the player
                InputSystem::class.java,  // retrieves input from the registered InputSourceModule
                InputToSolActionsSystem::class.java,
                if (!isObserver) ClientNetworkOutputSystem::class.java else null,  // send the inputs to the server

                // rendering
                if (!headless && debugUI && allowGui) CreatorSystem::class.java else null,
                if (!headless && allowGui) SolGuiSystem::class.java else null,
                if (!headless) RenderSystem::class.java else null
        )

        addGameEntity(false, world)

        NetEcsUtils.addNetClientEntity(world,
                CharactersConfigsPacket::class.java,
                ClientCharacterConfigsHandler::class.java
        );

        world.addEntity(
                world.createEntity("client-actions-output")
                        .addComponent(InputComp(
                                setOf("mvLeft", "mvRight", "mvUp", "mvDown", "ability1", "ability2", "ability3"),
                                setOf("aimX", "aimY")
                        ))
                        .addComponent(SolActionsPacketComp())
        )


        addStaticMapEntities(world, Vector2f(1600f, 900f))
    }

    override fun onStart() {
    }

    override fun onStepEnd() {
        world.insight.entities
                .filter { it.hasComponent(SolActionsPacketComp::class.java) }
                .map { it.getComponent(SolActionsPacketComp::class.java) }
//                .forEach { println(it.actionsPacket) }
    }
}