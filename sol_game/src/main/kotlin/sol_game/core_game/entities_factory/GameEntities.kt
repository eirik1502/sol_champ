package sol_game.core_game.entities_factory

import sol_engine.ecs.Entity
import sol_engine.ecs.EntityClass
import sol_engine.ecs.World
import sol_engine.network.network_ecs.world_syncing.NetSyncComp
import sol_game.core_game.components.SolGameComp

object GameEntities {

    fun addGameEntity(isServer: Boolean, world: World): Entity? {
        world.addEntityClass(
                EntityClass("sol-game-status").addBaseComponents(
                        SolGameComp(gameState = SolGameComp.GameState.BEFORE_START),
                        NetSyncComp(setOf(SolGameComp::class.java))
                )

        )
        if (isServer) {
            return world.addEntity("sol-game-status", "sol-game-status")
        } else {
            return null;
        }
    }


}