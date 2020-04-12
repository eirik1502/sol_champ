package sol_game.core_game

import sol_engine.core.TransformComp
import sol_engine.creator.CreatorSystem
import sol_engine.engine_interface.SolSimulation
import sol_engine.game_utils.*
import sol_engine.graphics_module.*
import sol_engine.input_module.*
import sol_engine.network.network_ecs.host_managing.EntityHostStartData
import sol_engine.network.network_ecs.host_managing.NetEcsUtils
import sol_engine.network.network_ecs.host_managing.NetServerSystem
import sol_engine.network.network_ecs.world_syncing.NetSyncServerSystem
import sol_engine.network.network_game.game_server.GameServerConfig

import sol_engine.network.network_sol_module.NetworkServerModuleConfig
import sol_engine.network.network_input.NetworkInputSourceModule
import sol_engine.network.network_input.NetworkInputSourceModuleConfig
import sol_engine.network.network_sol_module.NetworkServerModule
import sol_engine.physics_module.*
import sol_game.core_game.components.SolGameComp
import sol_game.core_game.systems.*

open class SolGameSimulationServer(
        private val charactersConfigs: List<CharacterConfig>,
        private val requestPort: Int = -1,
        private val allowObservers: Boolean = true,
        private val headless: Boolean = false,
        private val debugUI: Boolean = false,
        private val allowGui: Boolean
) : SolSimulation() {

    init {
        if (charactersConfigs.size != 2) {
            throw IllegalArgumentException("Two character configs must be given")
        }
    }

    override fun onSetupModules() {
        if (!headless) {
            addModule(GraphicsModule(GraphicsModuleConfig(
                    WindowConfig(0.5f, 0.5f, "sol server", false),
                    RenderConfig(800f, 450f, 1600f, 900f)
            )))
        }
        addModule(NetworkServerModule(NetworkServerModuleConfig(
                GameServerConfig(
                        requestPort,
                        listOf(1, 1),
                        allowObservers
                ),
                listOf(
                        CharactersConfigsPacket::class.java
                ),
                false
        )))
        addModule(NetworkInputSourceModule(NetworkInputSourceModuleConfig(
                SolActionsPacket::class.java
        )))
        addModule(InputModule(InputModuleConfig(
                NetworkInputSourceModule::class.java
        )))
    }

    override fun onSetupWorld() {
        world.addSystems(
                InputSystem::class.java,
                SolGameSystem::class.java,

//                MoveByVelocitySystem::class.java,
                MovementSystem::class.java,

                FaceAimSystem::class.java,
                CharacterSystem::class.java,
                AbilitySystem::class.java,
                EmitterTimedSystem::class.java,
                DestroySelfTimedSystem::class.java,

                CollisionSystem::class.java,
                DamageSystem::class.java,
                KnockbackSystem::class.java,
                ControlDisabledSystem::class.java,
                NaturalCollisionResolutionSystem::class.java,

                SceneChildSystem::class.java,
                PhysicsSystem::class.java,

                NetServerSystem::class.java,
                NetSyncServerSystem::class.java,

                if (!headless && debugUI && allowGui) CreatorSystem::class.java else null,
                if (allowGui) SolGuiSystem::class.java else null,

                RenderSystem::class.java
        )

        addGameEntity(true, world)

        createWalls(world)

        charactersConfigs
                .flatMap { createCharacterEntityClass(true, it) }
                .forEach { world.addEntityClass(it) }

        NetEcsUtils.addNetServerEntity(
                world,
                CharactersConfigsPacket(charactersConfigs),
                listOf(
                        listOf(
                                EntityHostStartData(charactersConfigs[0].name, setOf(TransformComp(200f, 200f)))
                        ),
                        listOf(
                                EntityHostStartData(charactersConfigs[1].name, setOf(TransformComp(1400f, 700f)))
                        )
                )
        )

//        instanciateCharacter(
//                true,
//                world,
//                charactersConfigs[0].name,
//                0,
//                0,
//                200f,
//                300f
//        )
    }


    override fun onStart() {
    }

    override fun onStepEnd() {
//        if (world.getEntityByName("player") != null)
//            println(world.getEntityByName("player").getComponent(CollisionInteractionComp::class.java).tags)
    }
}