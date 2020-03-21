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
                PlayerSystem::class.java,
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

        world.addEntityClass(
                EntityClass("ab1").addBaseComponents(
                        TransformComp(),
                        RenderShapeComp(RenderableShape.Circle(16f, MattMaterial.BLUE())),
                        PhysicsBodyComp(),
                        EmitterTimedComp(1, 20, "ab1-particle", "ab1-particle", -1f, 700f),
                        DestroySelfTimedComp(60)
                )
        )

        createCharacterClass()

        instanciatePlayer(0, 0);

        world.instanciateEntityClass("character", "opponent")
                .modifyComponent(TransformComp::class.java) { comp -> comp.setPosition(500f, 500f) }
                .modifyComponent(CollisionInteractionComp::class.java) { comp -> comp.addTag("team2") }

        createWalls()
    }

    private fun createWalls() {
        world.addEntityClass(EntityClass("wall").addBaseComponents(
                TransformComp(),
                RenderShapeComp(RenderableShape.Rectangle(100f, 100f, MattMaterial.BLUE())),
                PhysicsBodyComp(PhysicsBodyComp.INF_MASS, 1f, 0.2f),
                CollisionComp(PhysicsBodyShape.Rect(0f, 0f)),
                NaturalCollisionResolutionComp()
        ))

        val createWall = { name: String, x: Float, y: Float, width: Float, height: Float ->
            world.instanciateEntityClass("wall", name)
                    .modifyComponent(TransformComp::class.java) { transComp -> transComp.setPosition(x + width / 2, y + height / 2) }
                    .modifyComponent(RenderShapeComp::class.java) { comp ->
                        comp.renderable.width = width
                        comp.renderable.height = height
                        comp.offsetX = (-width) / 2
                        comp.offsetY = (-height) / 2
                    }
                    .modifyComponent(CollisionComp::class.java) { c -> c.bodyShape = PhysicsBodyShape.Rect(width, height) }
        }

        val wallThickness = 128f
        val worldHeight = 900f
        val worldWidth = 1600f
        createWall("wall1", 0f, 0f, wallThickness, worldHeight)
        createWall("wall2", 0f, 0f, worldWidth, wallThickness)
        createWall("wall3", worldWidth - wallThickness, 0f, wallThickness, worldHeight)
        createWall("wall4", 0f, worldHeight - wallThickness, worldWidth, wallThickness)
    }

    fun createCharacterClass() {
        world.addEntityClass(EntityClass("character")
                .addBaseComponents(
                        TransformComp(100f, 100f),
                        RenderShapeComp(RenderableShape.CirclePointing(32f, MattMaterial.RED())),
                        PhysicsBodyComp(10f, 0.05f, 0.5f),
                        MoveByVelocityComp(30f, "moveLeft", "moveRight", "moveUp", "moveDown"),
                        AbilityComp(),
                        CollisionComp(PhysicsBodyShape.Circ(32f)),
                        CollisionInteractionComp("character"),
                        NaturalCollisionResolutionComp(),
                        CharacterComp("ability1", "ability2", "ability3"),
                        FaceCursorComp(),
                        HurtboxComp()
                )
        )
    }

    fun instanciatePlayer(teamIndex: Int, playerIndex: Int) {
        val playerName = "player$playerIndex"
        val inputGroup = "t${teamIndex}p$playerIndex"
        world.instanciateEntityClass("character", playerName)
                .addComponent(InputComp(
                        inputGroup,
                        setOf("moveLeft", "moveRight", "moveUp", "moveDown", "ability1", "ability2", "ability3"),
                        setOf("aimX", "aimY"),
                        setOf("aimXY")
                ))
                .addComponent(PlayerComp())
                .modifyComponent(TransformComp::class.java) { comp -> comp.setPosition(200f, 200f) }
                .modifyComponent(AbilityComp::class.java) { ab ->
                    ab.abilities.addAll(
                            listOf(
                                    createMeleeAbility("meleeAb",
                                            64f, 48f,
                                            15, 5, 1, 60,
                                            100f, 200f, 0.6f),
                                    createMeleeAbility("haha",
                                            256f, 0f,
                                            15, 5, 1, 60,
                                            1000f, 200f, 0.05f),
                                    Ability("ab1", 60, 48f, 3f)
                            )
                    )
                }
    }

    fun createMeleeAbility(name: String, radius: Float, initialOffset: Float,
                           executionTime: Int, startupDelay: Int, persistTime: Int, cooldown: Int,
                           damage: Float, baseKnockback: Float, knockbackRatio: Float): Ability {
        world.addEntityClass(
                EntityClass(name).addBaseComponents(
                        TransformComp(),
                        RenderShapeComp(RenderableShape.CirclePointing(radius, MattMaterial.BLUE())),
                        DestroySelfTimedComp(persistTime),
                        CollisionComp(PhysicsBodyShape.Circ(radius)),
                        HitboxComp(damage, baseKnockback, knockbackRatio),
                        SceneChildComp()
                )
        )
        return Ability(name, cooldown, initialOffset,
                0f, executionTime, startupDelay)
    }

    override fun onStart() {
    }

    override fun onStepEnd() {
//        if (world.getEntityByName("player") != null)
//            println(world.getEntityByName("player").getComponent(CollisionInteractionComp::class.java).tags)
    }
}