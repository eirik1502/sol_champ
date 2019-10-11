package sol_examples.simple_shooter;

import org.joml.Vector2f;
import sol_engine.core.ModuleSystemBase;
import sol_engine.core.SimpleKeyControlComp;
import sol_engine.core.SimpleKeyControlSystem;
import sol_engine.core.TransformComp;
import sol_engine.ecs.EntityClass;
import sol_engine.engine_interface.SimulationLoop;
import sol_engine.engine_interface.SolSimulation;
import sol_engine.game_utils.*;
import sol_engine.graphics_module.*;
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

    @Override
    protected void setup() {

        float worldWidth = 1600;
        float worldHeight = 900;


        modulesHandler.addModule(new GraphicsModule(
                new GraphicsModuleConfig(
                        new WindowConfig(0.7f, 0.7f, "Hello SOL"),
                        new RenderConfig(worldWidth / 2, worldHeight / 2, worldWidth, worldHeight)
                )
        ));

        modulesHandler.addModule(new InputModule(new InputModuleConfig(
                new Vector2f(worldWidth, worldHeight)
        )));

        world.addSystem(PhysicsSystem.class);

        world.addSystem(CollisionSystem.class);
        world.addSystem(NaturalCollisionResolutionSystem.class);
        world.addSystem(CollisionInteractionSystem.class);
        world.addSystemInstance(createDebugSystem());
        world.addSystem(SimpleKeyControlSystem.class);
        world.addSystem(ShootSystem.class);
        world.addSystem(TornadoWetherSystem.class);
        world.addSystem(FollowCursorSystem.class);
        world.addSystem(RenderSystem.class);


        float pwidth = 32, pheight = 32;
        world.addEntityClass(new EntityClass("player").addBaseComponents(
                new TransformComp(100, 100),
                new RenderSquareComp(pwidth, pheight, MattMaterial.RED(), -pwidth / 2, -pheight / 2),
                new PhysicsBodyComp(1, 1, 0.9f),
                new CollisionComp(new PhysicsBodyShape.Rect(pwidth, pheight)),
                new NaturalCollisionResolutionComp(),
                new SimpleKeyControlComp(10),
                new ShootComp(InputConsts.MOUSE_BUTTON_LEFT, "bullet", 1000, 10)
        ));
        world.addEntityClass(new EntityClass("marker").addBaseComponents(
                new TransformComp(100, 100),
                new RenderSquareComp(pwidth / 2, pwidth / 2, MattMaterial.RED(), -pwidth / 4, -pwidth / 4)
        ));

        world.addEntity(world.createEntity("cursor")
                .addComponent(new TransformComp())
                .addComponent(new FollowCursorComp())
                .addComponent(
                        new RenderSquareComp(pwidth / 2, pwidth / 2, MattMaterial.RED(), -pwidth / 4, -pwidth / 4)
                )
        );

        world.addEntityClass(new EntityClass("circ").addBaseComponents(
                new TransformComp(100, 100),
                new RenderSquareComp(pwidth, pwidth, MattMaterial.GREEN(), -pwidth / 2, -pwidth / 2),
                new PhysicsBodyComp(1, 1, 1.2f),
                new CollisionComp(new PhysicsBodyShape.Circ(pwidth / 2)),
                new NaturalCollisionResolutionComp(),
                new CollisionInteractionComp()
                        .addTag("enemy")
                        .addInteraction("bullet",
                                CollisionInteraction.Create("marker", "marker")
                        )
        ));

        world.addEntityClass(new EntityClass("bullet").addBaseComponents(
                new TransformComp(100, 100),
                new RenderSquareComp(pwidth, pwidth, MattMaterial.BLUE(), -pwidth / 2, -pwidth / 2),
                new PhysicsBodyComp(1, 1, 1.2f),
                new CollisionComp(new PhysicsBodyShape.Circ(pwidth / 2)),
                new NaturalCollisionResolutionComp(),
                new CollisionInteractionComp()
                        .addTag("bullet")
                        .addInteraction("bullet", CollisionInteraction.DestroySelf())
                        .addInteraction("enemy", new CollisionInteraction().destroySelf().destroyOther())
        ));

        world.addEntityClass(new EntityClass("wall").addBaseComponents(
                new TransformComp(),
                new RenderSquareComp(100, 100, MattMaterial.BLUE()),
                new PhysicsBodyComp(PhysicsBodyComp.INF_MASS, 1, 0.2f),
                new CollisionComp(new PhysicsBodyShape.Rect(0, 0)),
                new NaturalCollisionResolutionComp()
        ));

        Function.FiveArgReturn<String, Float, Float, Float, Float, Void> createWall = (name, x, y, width, height) -> {
            world.instanciateEntityClass("wall", name)
                    .modifyComponent(TransformComp.class, transComp -> transComp.setXY(x + width / 2, y + height / 2))
                    .modifyComponent(RenderSquareComp.class, comp -> {
                        comp.width = width;
                        comp.height = height;
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
                    .modifyComponent(TransformComp.class, transComp -> transComp.setXY(startX, startY))
                    .modifyComponent(PhysicsBodyComp.class, comp -> comp.impulse.set(startImpulse));
        });

        world.instanciateEntityClass("player", "player")
                .modifyComponent(TransformComp.class, transComp -> transComp.setXY(worldWidth / 2, worldHeight / 2))
                .modifyComponent(PhysicsBodyComp.class, comp -> comp.impulse.set(new Vector2f(60, 0)));

    }

    @Override
    protected void onStepEnd() {
        System.out.println("entity count: " + world.getEntities().size());
    }

    private ModuleSystemBase createDebugSystem() {
        return new ModuleSystemBase() {
            private int lastCollCount = 0;

            @Override
            public void onSetup() {
                usingComponents(CollisionComp.class);
            }

            public void onStart() {
            }

            public void onUpdate() {
                entities.stream().findFirst().ifPresent(entity -> {
                    CollisionComp collComp = entity.getComponent(CollisionComp.class);
                    if (lastCollCount == 0 && collComp.collidingEntities.size() > 0) {
                        System.out.println("Collision!");
                    } else if (lastCollCount > 0 && collComp.collidingEntities.size() == 0) {
                        System.out.println("Collision END!");
                    }
                    lastCollCount = collComp.collidingEntities.size();
                });
            }

            public void onEnd() {
            }
        };
    }

    private Vector2f randomNormalizedVector2f() {
        float angle = MathF.random() * MathF.PI * 2;
        return new Vector2f(MathF.cos(angle), MathF.sin(angle));
    }
}