@file:JvmName("Main")

package sol_game

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.creator.CreatorSystem
import sol_engine.creator.EditorEditableComp
import sol_engine.ecs.EntityClass
import sol_engine.engine_interface.SimulationLoop
import sol_engine.engine_interface.SolSimulation
import sol_engine.game_utils.*
import sol_engine.graphics_module.*
import sol_engine.graphics_module.graphical_objects.RenderableShape
import sol_engine.graphics_module.materials.MattMaterial
import sol_engine.input_module.InputConsts
import sol_engine.input_module.InputModule
import sol_engine.input_module.InputModuleConfig
import sol_engine.physics_module.*

fun main(args: Array<String>) {
    SimulationLoop(SolGame()).start()
}

public class SolGame : SolSimulation() {
    override fun onSetupModules() {
        addModules(
                GraphicsModule(GraphicsModuleConfig(
                        WindowConfig(0.5f, 0.5f, "sol game", true),
                        RenderConfig(800f, 450f, 1600f, 900f)
                )),
                InputModule(InputModuleConfig(Vector2f(1600f, 900f)))
        )
    }

    override fun onSetupWorld() {
        world.addSystems(
                UserInputSystem::class.java,
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

                CreatorSystem::class.java,
                SolGuiSystem::class.java,

                RenderSystem::class.java
        )
//        world.addSystem(EditorSystem::class.java)


        world.addEntityClass(
                EntityClass("ab1").addBaseComponents(
                        TransformComp(),
                        RenderShapeComp(RenderableShape.Circle(16f, MattMaterial.BLUE())),
                        PhysicsBodyComp(),
                        EmitterTimedComp(1, 20, "ab1-particle", "ab1-particle", -1f, 700f),
                        DestroySelfTimedComp(60)
                )
        )
        world.addEntityClass(
                EntityClass("ab1-particle").addBaseComponents(
                        TransformComp(),
                        RenderShapeComp(RenderableShape.Circle(8f, MattMaterial.BLUE())),
                        PhysicsBodyComp(),
                        DestroySelfTimedComp(20),
                        CollisionComp(PhysicsBodyShape.Circ(8f)),
                        CollisionInteractionComp("ab", "team1", "team-member1")
                                .addInteraction("team2",
                                        CollisionInteraction.Custom { world, self, other ->
                                            //                                    val impulse = self.
                                            other.getComponent(PhysicsBodyComp::class.java).impulse.add(Vector2f(200f, 200f))
                                        }.destroySelf()
                                )
                )
        )

        createCharacterClass()

        instanciatePlayer();

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
                        MoveByVelocityComp(30f, "mvLeft", "mvRight", "mvUp", "mvDown"),
                        AbilityComp(),
                        CollisionComp(PhysicsBodyShape.Circ(32f)),
                        CollisionInteractionComp("character"),
                        NaturalCollisionResolutionComp(),
                        CharacterComp("action1", "action2", "action3"),
                        FaceCursorComp(),
                        HurtboxComp()
                )
        )
    }

    fun instanciatePlayer() {
        world.instanciateEntityClass("character", "player")
                .addComponent(InputComp(
                        mapOf(
                                "mvLeft" to InputConsts.KEY_A,
                                "mvRight" to InputConsts.KEY_D,
                                "mvUp" to InputConsts.KEY_W,
                                "mvDown" to InputConsts.KEY_S,
                                "action3" to InputConsts.KEY_SPACE
                        ),
                        mapOf(
                                "action1" to InputConsts.MOUSE_BUTTON_LEFT,
                                "action2" to InputConsts.MOUSE_BUTTON_RIGHT
                        ),
                        true
                ))
                .addComponent(PlayerComp())
                .modifyComponent(CollisionInteractionComp::class.java) { comp -> comp.addTag("team1") }
                .modifyComponent(TransformComp::class.java) { comp -> comp.setPosition(200f, 200f) }
                .modifyComponent(AbilityComp::class.java) { ab ->
                    ab.abilities.addAll(
                            listOf(
                                    createMeleeAbility("meleeAb", "",
                                            64f, 48f,
                                            15, 5, 1, 60,
                                            100f, 200f, 0.6f),
                                    createMeleeAbility("haha", "",
                                            256f, 0f,
                                            15, 5, 1, 60,
                                            1000f, 200f, 0.05f),
                                    Ability("ab1", 60, 48f, "action2", 3f)
                            )
                    )
                }
    }

    fun createMeleeAbility(name: String, inputAction: String, radius: Float, initialOffset: Float,
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
        return Ability(name, cooldown, initialOffset, inputAction,
                0f, executionTime, startupDelay)
    }

    override fun onStart() {
        world.insight.entitiesScheduledForAdd.forEach { entity ->
            entity.addComponent(EditorEditableComp())
        }
    }

    override fun onStepEnd() {
//        if (world.getEntityByName("player") != null)
//            println(world.getEntityByName("player").getComponent(CollisionInteractionComp::class.java).tags)
    }
}