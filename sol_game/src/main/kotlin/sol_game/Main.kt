@file:JvmName("Main")

package sol_game

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.creator.EditorEditableComp
import sol_engine.creator.EditorSystem
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
        world.addSystem(UserInputSystem::class.java)
        world.addSystem(PlayerSystem::class.java)
        world.addSystem(AbilitySystem::class.java)
        world.addSystem(EmitterTimedSystem::class.java)
        world.addSystem(DestroySelfTimedSystem::class.java)
        world.addSystem(CollisionSystem::class.java)
        world.addSystem(CollisionInteractionSystem::class.java)
        world.addSystem(DamageSystem::class.java)
        world.addSystem(MoveByVelocitySystem::class.java)
        world.addSystem(NaturalCollisionResolutionSystem::class.java)
        world.addSystem(PhysicsSystem::class.java)
        world.addSystem(RenderSystem::class.java)


//        world.addSystem(WorldProfilerSystem::class.java)
        world.addSystem(EditorSystem::class.java)

        val damageCollisionInteraction = CollisionInteraction.Custom() { world, self, other ->
            other.getComponent(TakeDamageComp::class.java).currDamageTaken +=
                    self.getComponent(DealDamageComp::class.java).damage
        }

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

        world.addEntityClass(EntityClass("character")
                .addBaseComponents(
                        TransformComp(100f, 100f),
                        RenderShapeComp(RenderableShape.Circle(32f, MattMaterial.RED())),
                        PhysicsBodyComp(10f, 0.9f, 0.5f),
                        AbilityComp("ab1", 60),
                        CollisionComp(PhysicsBodyShape.Circ(32f)),
                        CollisionInteractionComp("character"),
                        NaturalCollisionResolutionComp()
                )
        )
        world.instanciateEntityClass("character", "player")
                .addComponent(UserInputComp(
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
                .addComponent(MoveByVelocityComp(10f, "mvLeft", "mvRight", "mvUp", "mvDown"))
                .addComponent(PlayerComp())
                .modifyComponent(CollisionInteractionComp::class.java) { comp -> comp.addTag("team1") }
                .modifyComponent(TransformComp::class.java) { comp -> comp.setXY(200f, 200f) }

        world.instanciateEntityClass("character", "opponent")
                .modifyComponent(TransformComp::class.java) { comp -> comp.setXY(500f, 500f) }
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
                    .modifyComponent(TransformComp::class.java) { transComp -> transComp.setXY(x + width / 2, y + height / 2) }
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