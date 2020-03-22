package sol_game.core_game

import sol_engine.core.TransformComp
import sol_engine.creator.CreatorSystem
import sol_engine.ecs.EntityClass
import sol_engine.engine_interface.SolSimulation
import sol_engine.game_utils.*
import sol_engine.graphics_module.*
import sol_engine.graphics_module.graphical_objects.RenderableShape
import sol_engine.graphics_module.materials.MattMaterial
import sol_engine.input_module.InputComp
import sol_engine.input_module.*
import sol_engine.network.network_game.game_server.GameServerConfig
import sol_engine.network.network_sol_module.NetworkServerModule
import sol_engine.network.network_sol_module.NetworkServerModuleConfig
import sol_engine.network.network_input.NetworkInputSourceModule
import sol_engine.network.network_input.NetworkInputSourceModuleConfig
import sol_engine.physics_module.*
import sol_game.core_game.components.*
import sol_game.core_game.systems.*

open class SolGameSimulationServer(
        private val charactersConfigs: List<CharacterConfig>,
        private val requestPort: Int = -1,
        private val allowObservers: Boolean = true,
        private val headless: Boolean = false,
        private val debugUI: Boolean = false
) : SolSimulation() {

    init {
        if (charactersConfigs.size != 2) {
            throw IllegalArgumentException("Two character configs must be given")
        }
    }

    override fun onSetupModules() {
        if (!headless) {
            addModule(GraphicsModule(GraphicsModuleConfig(
                    WindowConfig(0.5f, 0.5f, "sol server", true),
                    RenderConfig(800f, 450f, 1600f, 900f)
            )))
        }
        addModule(NetworkServerModule(NetworkServerModuleConfig(
                GameServerConfig(
                        requestPort,
                        listOf(1, 1),
                        allowObservers
                ),
                listOf(),
                false
        )))
        addModule(NetworkInputSourceModule(NetworkInputSourceModuleConfig(
                SolInputPacket::class.java
        )))
        addModule(InputModule(InputModuleConfig(
                NetworkInputSourceModule::class.java
        )))
    }

    override fun onSetupWorld() {
        world.addSystems(
                InputSystem::class.java,
                MoveByVelocitySystem::class.java,

                FaceCursorSystem::class.java,
                CharacterSystem::class.java,
                AbilitySystem::class.java,
                EmitterTimedSystem::class.java,
                DestroySelfTimedSystem::class.java,

                CollisionSystem::class.java,
                CollisionInteractionSystem::class.java,
                DamageSystem::class.java,
                KnockbackSystem::class.java,
                NaturalCollisionResolutionSystem::class.java,

                SceneChildSystem::class.java,
                PhysicsSystem::class.java,

                if (!headless && debugUI) CreatorSystem::class.java else null,
                SolGuiSystem::class.java,

                RenderSystem::class.java
        )

        charactersConfigs
                .flatMap { createCharacterEntityClass(it) }
                .forEach { world.addEntityClass(it) }

        instanciateCharacter(
                world,
                charactersConfigs[0].name,
                0,
                0,
                200f,
                300f
        )

        createWalls(world)
    }


    override fun onStart() {
    }

    override fun onStepEnd() {
//        if (world.getEntityByName("player") != null)
//            println(world.getEntityByName("player").getComponent(CollisionInteractionComp::class.java).tags)
    }
}