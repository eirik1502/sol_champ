package sol_examples.simple_shooter;

import org.joml.Vector2f;
import sol_engine.core.TransformComp;
import sol_engine.ecs.EntityClass;
import sol_engine.ecs.SystemBase;
import sol_engine.engine_interface.SimulationLoop;
import sol_engine.engine_interface.SolSimulation;
import sol_engine.graphics_module.*;
import sol_engine.graphics_module.materials.MattMaterial;
import sol_engine.physics_module.*;
import sol_engine.utils.MathF;

import java.util.stream.IntStream;

public class SimpleShooter extends SolSimulation {
    interface PentConsumer<A, B, C, D, E> {
        void accept(A param1, B param2, C param3, D param4, E param5);
    }

    public static void main(String... args) {
        System.out.println("Simple shooter");


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


//        world.addSystem(sol_game.MoveCircularSystem.class);
        world.addSystem(RenderSystem.class);
        world.addSystem(CollisionSystem.class);
        world.addSystem(PhysicsSystem.class);
        world.addSystem(NaturalCollisionResolutionSystem.class);
        world.addSystemInstance(new SystemBase() {
            private int lastCollCount = 0;

            public void onStart() {
                usingComponents(CollisionComp.class);
            }

            public void onUpdate() {
                groupEntities.stream().findFirst().ifPresent(entity -> {
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
        });

        world.addEntityClass(new EntityClass("wall").addBaseComponents(
                new TransformComp(),
                new RenderSquareComp(100, 100, MattMaterial.BLUE),
                new PhysicsBodyComp(PhysicsBodyComp.INF_MASS),
                new CollisionComp(new PhysicsBodyShape.Rect(0, 0)),
                new NaturalCollisionResolutionComp()
        ));

        PentConsumer<String, Float, Float, Float, Float> createWall = (name, x, y, width, height) -> {
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
        };

        float wallThickness = 128f;
        createWall.accept("wall1", 0f, 0f, wallThickness, worldHeight);
        createWall.accept("wall2", 0f, 0f, worldWidth, wallThickness);
        createWall.accept("wall3", worldWidth - wallThickness, 0f, wallThickness, worldHeight);
        createWall.accept("wall4", 0f, worldHeight - wallThickness, worldWidth, wallThickness);

        float width = 32, height = 32;
        world.addEntityClass(new EntityClass("rect").addBaseComponents(
                new TransformComp(100, 100),
                new RenderSquareComp(width, height, MattMaterial.RED, -width / 2, -height / 2),
                new PhysicsBodyComp(1, 1, 0.9f),
                new CollisionComp(new PhysicsBodyShape.Rect(width, height)),
                new NaturalCollisionResolutionComp()
        ));

        world.addEntityClass(new EntityClass("circ").addBaseComponents(
                new TransformComp(100, 100),
                new RenderSquareComp(width, width, MattMaterial.GREEN, -width / 2, -width / 2),
                new PhysicsBodyComp(1, 1, 0.9f),
                new CollisionComp(new PhysicsBodyShape.Circ(width / 2)),
                new NaturalCollisionResolutionComp()
        ));

        IntStream.range(0, 300).forEach(i -> {
            float startX = wallThickness + MathF.random() * (worldWidth - wallThickness * 2);
            float startY = wallThickness + MathF.random() * (worldHeight - wallThickness * 2);
            float startVelocity = 120 * 5;
            Vector2f startImpulse = randomNormalizedVector2f().mul(startVelocity);
            world.instanciateEntityClass("circ", "rect" + i)
                    .modifyComponent(TransformComp.class, transComp -> transComp.setXY(startX, startY))
                    .modifyComponent(PhysicsBodyComp.class, comp -> comp.impulse.set(startImpulse));
        });

        world.instanciateEntityClass("rect", "rect1")
                .modifyComponent(TransformComp.class, transComp -> transComp.setXY(worldWidth / 2, worldHeight / 2))
                .modifyComponent(PhysicsBodyComp.class, comp -> comp.impulse.set(new Vector2f(60, 0)));

//        world.instanciateEntityClass("rect", "rect2")
//                .modifyComponent(TransformComp.class, transComp -> transComp.setXY(600, 300))
//                .modifyComponent(PhysicsBodyComp.class, comp -> comp.impulse.set(new Vector2f(-60, 0)));
//
//        world.instanciateEntityClass("circ", "circ1")
//                .modifyComponent(TransformComp.class, transComp -> transComp.setXY(450, 450))
//                .modifyComponent(PhysicsBodyComp.class, comp -> comp.impulse.set(new Vector2f(10, -60)));

//        world.instanciateEntityClass("rect", "rect2")
//                .modifyComponent(TransformComp.class, c -> c.setXY(600, 300))
//                .modifyComponent(RenderSquareComp.class, c -> c.material = MattMaterial.GREEN)
//                .modifyComponent(MoveCircularComp.class, c -> {
//                    c.centerX += 300;
//                    c.centerY += 300;
//                });

    }

    private Vector2f randomNormalizedVector2f() {
        float angle = MathF.random() * MathF.PI * 2;
        return new Vector2f(MathF.cos(angle), MathF.sin(angle));
    }
}
