package sol_game.core_game.entities_factory

import org.joml.Vector2f
import sol_engine.core.TransformComp
import sol_engine.ecs.EntityClass
import sol_engine.ecs.World
import sol_engine.game_utils.DestroySelfTimedComp
import sol_engine.graphics_module.RenderShapeComp
import sol_engine.graphics_module.graphical_objects.RenderableShape
import sol_engine.graphics_module.materials.MattMaterial
import sol_engine.input_module.InputComp
import sol_engine.network.network_ecs.host_managing.TeamPlayerComp
import sol_engine.network.network_ecs.world_syncing.NetSyncComp
import sol_engine.physics_module.CollisionComp
import sol_engine.physics_module.NaturalCollisionResolutionComp
import sol_engine.physics_module.PhysicsBodyComp
import sol_engine.physics_module.PhysicsBodyShape
import sol_game.core_game.Ability
import sol_game.core_game.AbilityConfig
import sol_game.core_game.CharacterConfig
import sol_game.core_game.components.*

object CharacterEntities {


    private fun createAbility(abConfig: AbilityConfig): Pair<EntityClass, Ability> {
        val abEntityClassName = abConfig.abilityId
        val abEntityClass = EntityClass(abEntityClassName)
                .addBaseComponents(
                        TransformComp(),
                        RenderShapeComp(RenderableShape.CirclePointing(abConfig.radius, MattMaterial.BLUE())),
                        DestroySelfTimedComp(abConfig.activeTime),
                        CollisionComp(PhysicsBodyShape.Circ(abConfig.radius)),
                        HitboxComp(
                                abConfig.damage,
                                abConfig.baseKnockback,
                                abConfig.knockbackRatio,
                                abConfig.knockbackPoint,
                                abConfig.knockbackTowardPoint
                        ),
                        PhysicsBodyComp(1f, 0f, 0.5f),
//                    SceneChildComp(),
                        NetSyncComp(true, true, setOf(TransformComp::class.java))
                )
        val ab = Ability(
                abEntityClassName,
                abConfig.name,
                abConfig.rechargeTime,
                abConfig.distanceFromChar,
                abConfig.speed,
                abConfig.startupTime + abConfig.executionTime + abConfig.endlagTime,
                abConfig.startupTime
        )
        return Pair(abEntityClass, ab)
    }

    fun addAllCharactersEntityClasses(isServer: Boolean, configs: List<CharacterConfig>, world: World) {
        configs.forEach { addCharacterEntityClass(isServer, it, world) }
    }

    fun addCharacterEntityClass(isServer: Boolean, config: CharacterConfig, world: World) {
        createCharacterEntityClass(isServer, config)
                .forEach { world.addEntityClass(it) }
    }

    fun createCharacterEntityClass(isServer: Boolean, config: CharacterConfig): List<EntityClass> {
        val abAbilities: List<Pair<EntityClass, Ability>> = config.abilities.map { createAbility(it) }

        val characterEntityClass = EntityClass(config.characterId)
                .addBaseComponents(
                        TransformComp(),
                        RenderShapeComp(RenderableShape.CirclePointing(config.radius, MattMaterial.RED())),
                        PhysicsBodyComp(10f, 5f, 0.5f),
                        MovementComp(listOf("mvLeft", "mvRight", "mvUp", "mvDown"), maxSpeed = config.moveVelocity, acceleration = config.moveVelocity / 5),
                        AbilityComp(abAbilities.map { it.second }),
                        CollisionComp(PhysicsBodyShape.Circ(config.radius)),
                        NaturalCollisionResolutionComp(),
                        CharacterComp("ability1", "ability2", "ability3"),
                        FaceAimComp(),
                        HurtboxComp(),
                        NetSyncComp(setOf(TransformComp::class.java)),
                        ControlDisabledComp(),
                        StockComp(startingStockCount = 3),
                        FallIntoHoleComp(),
                        SpawnPositionComp()
                )
        if (isServer) {
            characterEntityClass.addBaseComponents(
                    InputComp(
                            setOf("mvLeft", "mvRight", "mvUp", "mvDown", "ability1", "ability2", "ability3"),
                            setOf("aimX", "aimY")
                    )
            )
        } else {

        }

        return abAbilities.map { it.first } + characterEntityClass
    }

    fun instanciateCharacter(isServer: Boolean, world: World, name: String, entityClass: String, teamIndex: Int, playerIndex: Int, startPosition: Vector2f) {
        val inputGroup = "t${teamIndex}p${playerIndex}"
        val charEClass = world.addEntity(name, entityClass)
                .modifyComponent(TransformComp::class.java) { comp -> comp.setPosition(startPosition) }
                .modifyComponent(SpawnPositionComp::class.java) { comp -> comp.spawnPosition.set(startPosition) }
                .addComponentIfAbsent(TeamPlayerComp::class.java, { TeamPlayerComp() }, { comp ->
                    comp.teamIndex = teamIndex
                    comp.playerIndex = playerIndex
                })
        if (isServer) {
            charEClass.modifyComponent(InputComp::class.java) { comp -> comp.inputGroup = inputGroup }
        }
    }


}