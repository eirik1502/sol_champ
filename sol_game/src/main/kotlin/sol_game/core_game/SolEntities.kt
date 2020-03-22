package sol_game.core_game

import sol_engine.core.TransformComp
import sol_engine.ecs.EntityClass
import sol_engine.ecs.World
import sol_engine.game_utils.CollisionInteractionComp
import sol_engine.game_utils.DestroySelfTimedComp
import sol_engine.game_utils.MoveByVelocityComp
import sol_engine.graphics_module.RenderShapeComp
import sol_engine.graphics_module.graphical_objects.RenderableShape
import sol_engine.graphics_module.materials.MattMaterial
import sol_engine.input_module.InputComp
import sol_engine.physics_module.CollisionComp
import sol_engine.physics_module.NaturalCollisionResolutionComp
import sol_engine.physics_module.PhysicsBodyComp
import sol_engine.physics_module.PhysicsBodyShape
import sol_game.core_game.components.*


private fun createAbility(characterName: String, abConfig: AbilityConfig): Pair<EntityClass, Ability> {
    val abEntityClassName = "${characterName}:${abConfig.name}"
    val abEntityClass = EntityClass(abEntityClassName)
            .addBaseComponents(
                    TransformComp(),
                    RenderShapeComp(RenderableShape.CirclePointing(abConfig.radius, MattMaterial.BLUE())),
                    DestroySelfTimedComp(abConfig.activeHitboxTime),
                    CollisionComp(PhysicsBodyShape.Circ(abConfig.radius)),
                    HitboxComp(abConfig.damage, abConfig.baseKnockback, abConfig.knockbackRatio),
                    SceneChildComp()
            )
    val ab = Ability(
            abEntityClassName,
            abConfig.rechargeTime,
            abConfig.distanceFromChar,
            abConfig.speed,
            abConfig.startupTime + abConfig.activeHitboxTime + abConfig.endlagTime,
            abConfig.startupTime
    )
    return Pair(abEntityClass, ab)
}

fun createCharacterEntityClass(isServer: Boolean, config: CharacterConfig): List<EntityClass> {
    val abAbilities: List<Pair<EntityClass, Ability>> = config.abilities.map { createAbility(config.name, it) }

    val characterEntityClass = EntityClass(config.name)
            .addBaseComponents(
                    TransformComp(),
                    RenderShapeComp(RenderableShape.CirclePointing(32f, MattMaterial.RED())),
                    PhysicsBodyComp(10f, 0.05f, 0.5f),
                    MoveByVelocityComp(30f, "mvLeft", "mvRight", "mvUp", "mvDown"),
                    AbilityComp(abAbilities.map { it.second }),
                    CollisionComp(PhysicsBodyShape.Circ(32f)),
                    NaturalCollisionResolutionComp(),
                    CharacterComp("ability1", "ability2", "ability3"),
                    FaceCursorComp(),
                    HurtboxComp()
            )
    if (isServer) {
        characterEntityClass.addBaseComponents(
                InputComp(
                        setOf("mvLeft", "mvRight", "mvUp", "mvDown", "ability1", "ability2", "ability3"),
                        setOf("aimX", "aimY"),
                        setOf()
                )
        )
    } else {

    }

    return abAbilities.map { it.first } + characterEntityClass
}

fun instanciateCharacter(isServer: Boolean, world: World, name: String, teamIndex: Int, playerIndex: Int, startX: Float, startY: Float) {
    val inputGroup = "t${teamIndex}p${playerIndex}"
    val charEClass = world.instanciateEntityClass(name, name)
            .modifyComponent(TransformComp::class.java) { comp -> comp.setPosition(startX, startY) }
    if (isServer) {
        charEClass.modifyComponent(InputComp::class.java) { comp -> comp.inputGroup = inputGroup }
    }
}

fun createWalls(world: World) {
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