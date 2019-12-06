package sol_examples.simple_shooter;

import org.joml.Vector2f;
import sol_engine.core.ModuleSystemBase;
import sol_engine.core.TransformComp;
import sol_engine.creator.CreatorSystem;
import sol_engine.creator.EditorEditableComp;
import sol_engine.ecs.EntityClass;
import sol_engine.engine_interface.SimulationLoop;
import sol_engine.engine_interface.SolSimulation;
import sol_engine.game_utils.*;
import sol_engine.graphics_module.*;
import sol_engine.graphics_module.graphical_objects.RenderableShape;
import sol_engine.graphics_module.materials.MattMaterial;
import sol_engine.input_module.InputConsts;
import sol_engine.input_module.InputModule;
import sol_engine.input_module.InputModuleConfig;
import sol_engine.physics_module.*;
import sol_engine.utils.Function;
import sol_engine.utils.math.MathF;

import java.util.stream.IntStream;

public class SimpleShooter extends SolSimulation {

    public static void main(String... args) {
        System.out.println("Simple shooter");

//        SolSimulation game = new SimpleShooter();
//        game.start();
//        long prevTime = System.nanoTime();
//        long currTime;
//        while (true) {
//            game.step();
//            currTime = System.nanoTime();
//            System.out.println("step time: " + (currTime - prevTime) * 0.000001);
//            prevTime = currTime;
//        }

        SimulationLoop game = new SimulationLoop(new SimpleShooter());
        game.start();
    }

    private float worldWidth = 1600;
    private float worldHeight = 900;

    @Override
    protected void onSetupModules() {
        modulesHandler.addModule(new GraphicsModule(
                new GraphicsModuleConfig(
                        new WindowConfig(0.9f, 0.9f, "Hello SOL", false),
//                        new WindowConfig(0.7f, 0.7f, "Hello SOL", false),
                        new RenderConfig(worldWidth / 2, worldHeight / 2, worldWidth, worldHeight)
                )
        ));

        modulesHandler.addModule(new InputModule(new InputModuleConfig(
                new Vector2f(worldWidth, worldHeight)
        )));
    }

    @Override
    protected void onSetupWorld() {
        world.addSystem(PhysicsSystem.class);

        world.addSystem(CollisionSystem.class);
        world.addSystem(NaturalCollisionResolutionSystem.class);
        world.addSystem(CollisionInteractionSystem.class);
        world.addSystemInstance(createDebugSystem());
        world.addSystem(MoveByVelocitySystem.class);
        world.addSystem(ShootSystem.class);
//        world.addSystem(TornadoWetherSystem.class);
        world.addSystem(FollowCursorSystem.class);
        world.addSystem(CreatorSystem.class);
        world.addSystem(RenderSystem.class);
        world.addSystem(EmitterTimedSystem.class);
        world.addSystem(DestroySelfTimedSystem.class);

        float pwidth = 32, pheight = 32;
        world.addEntityClass(new EntityClass("player").addBaseComponents(
                new TransformComp(100, 100),
                new RenderShapeComp(new RenderableShape.CirclePointing(pwidth / 2, MattMaterial.RED())),
                new PhysicsBodyComp(1, 1, 0.9f),
                new CollisionComp(new PhysicsBodyShape.Circ(pwidth / 2)),
                new NaturalCollisionResolutionComp(),
                new MoveByVelocityComp(10),
                new ShootComp(InputConsts.MOUSE_BUTTON_LEFT, "bullet", 1000, 10),
                new EditorEditableComp()
        ));
        world.addEntityClass(new EntityClass("marker").addBaseComponents(
                new TransformComp(500, 500),
                new RenderShapeComp(new RenderableShape.Rectangle(pwidth / 2, pheight / 2, MattMaterial.RED()), -pwidth / 4, -pwidth / 4),
                new PhysicsBodyComp(1, 1, 0.8f),
                new CollisionComp(new PhysicsBodyShape.Circ(pwidth / 4)),
                new NaturalCollisionResolutionComp(),
                new EmitterTimedComp(60, -1, "marker", "", -1, 100),
                new DestroySelfTimedComp(120),
                new EditorEditableComp()
        ));

        world.addEntity(world.createEntity("cursor")
                .addComponent(new TransformComp())
                .addComponent(new FollowCursorComp())
                .addComponent(
                        new RenderShapeComp(new RenderableShape.Rectangle(pwidth / 2, pheight / 2, MattMaterial.RED()), -pwidth / 4, -pwidth / 4)
                )
                .addComponent(new EditorEditableComp())
        );

        world.addEntityClass(new EntityClass("circ").addBaseComponents(
                new TransformComp(100, 100),
                new RenderShapeComp(new RenderableShape.Rectangle(pwidth, pwidth, MattMaterial.GREEN()), -pwidth / 2, -pwidth / 2),
                new PhysicsBodyComp(1, 1, 0.4f),
                new CollisionComp(new PhysicsBodyShape.Circ(pwidth / 2)),
                new NaturalCollisionResolutionComp(),
                new CollisionInteractionComp()
                        .addTag("enemy")
                        .addInteraction("bullet",
                                CollisionInteraction.Create("marker", "marker")
                        ),
//                new EmitterTimedComp(60, -1, "marker", "", -1, 300),
                new EmitterTimedComp(60, 1, "circ", "", -1, 300),
                new EditorEditableComp()

        ));

        world.addEntityClass(new EntityClass("bullet").addBaseComponents(
                new TransformComp(100, 100),
                new RenderShapeComp(new RenderableShape.Rectangle(pwidth, pwidth, MattMaterial.BLUE()), -pwidth / 2, -pwidth / 2),
                new PhysicsBodyComp(1, 1, 1.2f),
                new CollisionComp(new PhysicsBodyShape.Circ(pwidth / 2)),
                new NaturalCollisionResolutionComp(),
                new CollisionInteractionComp()
                        .addTag("bullet")
                        .addInteraction("bullet", CollisionInteraction.DestroySelf())
                        .addInteraction("enemy", new CollisionInteraction().destroySelf().destroyOther()),
                new EditorEditableComp()

        ));

        world.addEntityClass(new EntityClass("wall").addBaseComponents(
                new TransformComp(),
                new RenderShapeComp(new RenderableShape.Rectangle(100, 100, MattMaterial.BLUE())),
                new PhysicsBodyComp(PhysicsBodyComp.INF_MASS, 1, 0.2f),
                new CollisionComp(new PhysicsBodyShape.Rect(0, 0)),
                new NaturalCollisionResolutionComp(),
                new EditorEditableComp()

        ));

        Function.FiveArgReturn<String, Float, Float, Float, Float, Void> createWall = (name, x, y, width, height) -> {
            world.instanciateEntityClass("wall", name)
                    .modifyComponent(TransformComp.class, transComp -> transComp.setPosition(x + width / 2, y + height / 2))
                    .modifyComponent(RenderShapeComp.class, comp -> {
                        comp.renderable.width = width;
                        comp.renderable.height = height;
                        comp.offsetX = -width / 2;
                        comp.offsetY = -height / 2;
                    })
                    .modifyComponent(CollisionComp.class, c ->
                            c.bodyShape = new PhysicsBodyShape.Rect(width, height));
            return null;
        };

        float wallThickness = 128f;
        createWall.invoke("wall1", 0f, 0f, wallThickness, worldHeight);
        createWall.invoke("wall2", 0f, 0f, worldWidth, wallThickness);
        createWall.invoke("wall3", worldWidth - wallThickness, 0f, wallThickness, worldHeight);
        createWall.invoke("wall4", 0f, worldHeight - wallThickness, worldWidth, wallThickness);

        IntStream.range(0, 10).forEach(i -> {
            float startX = wallThickness + MathF.random() * (worldWidth - wallThickness * 2);
            float startY = wallThickness + MathF.random() * (worldHeight - wallThickness * 2);
            float startVelocity = 120 * 5;
            Vector2f startImpulse = randomNormalizedVector2f().mul(startVelocity);
            world.instanciateEntityClass("circ", "circ" + i)
                    .modifyComponent(TransformComp.class, transComp -> transComp.setPosition(startX, startY))
                    .modifyComponent(PhysicsBodyComp.class, comp -> comp.impulse.set(startImpulse));
        });

//        world.instanciateEntityClass("marker", "marker");

        world.instanciateEntityClass("player", "player")
                .modifyComponent(TransformComp.class, transComp -> transComp.setPosition(worldWidth / 2, worldHeight / 2))
                .modifyComponent(PhysicsBodyComp.class, comp -> comp.impulse.set(new Vector2f(60, 0)));

    }

    @Override
    protected void onStepEnd() {
    }

    private ModuleSystemBase createDebugSystem() {
        return new ModuleSystemBase() {

            public void onSetup() {
            }

            public void onUpdate() {
            }
        };
    }

    private Vector2f randomNormalizedVector2f() {
        float angle = MathF.random() * MathF.PI * 2;
        return new Vector2f(MathF.cos(angle), MathF.sin(angle));
    }
}