package sol_game.core_game.entities_factory

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.Entity
import sol_engine.ecs.World
import sol_engine.graphics_module.RenderShapeComp
import sol_engine.graphics_module.graphical_objects.RenderableShape
import sol_engine.graphics_module.materials.Material
import sol_engine.graphics_module.materials.MattMaterial
import sol_engine.physics_module.CollisionComp
import sol_engine.physics_module.NaturalCollisionResolutionComp
import sol_engine.physics_module.PhysicsBodyComp
import sol_engine.physics_module.PhysicsBodyShape
import sol_game.core_game.components.HoleComp
import sol_game.core_game.components.WallComp

object StaticEntities {
    fun addStaticCollideableRectObject(
            world: World, name: String, position: Vector2f, size: Vector2f, positionInCenter: Boolean, material: Material
    ): Entity =
            world.addEntity(name).addComponents(
                    TransformComp(if (positionInCenter) position else position.add(size.mul(0.5f, Vector2f()))),
                    RenderShapeComp(
                            RenderableShape.Rectangle(size.x, size.y, material),
                            (-size.x) / 2,
                            (-size.y) / 2
                    ),
                    PhysicsBodyComp(PhysicsBodyComp.INF_MASS, 1f, 0.7f),
                    CollisionComp(PhysicsBodyShape.Rect(size.x, size.y))
            )

    fun addStaticCollideableCircObject(
            world: World, name: String, position: Vector2f, radius: Float, material: Material
    ): Entity =
            world.addEntity(name).addComponents(
                    TransformComp(position),
                    RenderShapeComp(
                            RenderableShape.Circle(radius, material)
                    ),
                    PhysicsBodyComp(PhysicsBodyComp.INF_MASS, 1f, 0.7f),
                    CollisionComp(PhysicsBodyShape.Circ(radius))
            )

    fun addRectWall(world: World, name: String, position: Vector2f, size: Vector2f, positionInCenter: Boolean): Entity =
            addStaticCollideableRectObject(world, name, position, size, positionInCenter, MattMaterial.BLUE())
                    .addComponents(
                            NaturalCollisionResolutionComp(),
                            WallComp()
                    )

    fun addCircWall(world: World, name: String, position: Vector2f, radius: Float): Entity =
            addStaticCollideableCircObject(world, name, position, radius, MattMaterial.BLUE())
                    .addComponents(
                            NaturalCollisionResolutionComp(),
                            WallComp()
                    )

    fun addRectHole(world: World, name: String, position: Vector2f, size: Vector2f, positionInCenter: Boolean): Entity =
            addStaticCollideableRectObject(world, name, position, size, positionInCenter, MattMaterial.RED())
                    .addComponents(
                            HoleComp()
                    )
                    .modifyComponent(RenderShapeComp::class.java) { comp -> comp.depth = 1f }

    fun addCircHole(world: World, name: String, position: Vector2f, radius: Float): Entity =
            addStaticCollideableCircObject(world, name, position, radius, MattMaterial.RED())
                    .addComponents(
                            HoleComp()
                    )
                    .modifyComponent(RenderShapeComp::class.java) { comp -> comp.depth = 1f }

    fun addStaticMapEntities(world: World, worldSize: Vector2f) {

        val wallThickness = 64f
        val worldWidth = worldSize.x
        val worldHeight = worldSize.y

        val sideWallDistance = 1500f

        addCircWall(world, "wall-right",
                Vector2f(worldWidth + sideWallDistance, worldHeight / 2f),
                sideWallDistance + wallThickness)

        addCircWall(world, "wall-left",
                Vector2f(-sideWallDistance, worldHeight / 2f),
                sideWallDistance + wallThickness)


        addRectHole(world, "hole-top",
                Vector2f(0f, 0f),
                Vector2f(worldWidth, wallThickness),
                false)

        addRectHole(world, "hole-bottom",
                Vector2f(0f, worldHeight - wallThickness),
                Vector2f(worldWidth, wallThickness),
                false)

        addCircWall(world, "center-hole",
                Vector2f(worldWidth / 2f, worldHeight / 2f),
                48f
        )
    }
}