package sol_game

import sol_engine.ecs.SystemBase

class DamageSystem : SystemBase() {


    override fun onSetup() {
    }

    override fun onStart() {
        world.addSystem(DealDamageSystem::class.java)
        world.addSystem(TakeDamageSystem::class.java)
    }

    override fun onUpdate() {
    }
}

class DealDamageSystem : SystemBase() {
    override fun onSetup() {
        usingComponents(DealDamageComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(DealDamageComp::class.java) { entity, dealDamageComp ->
            dealDamageComp.currDamageDealt = 0f
        }
    }
}

class TakeDamageSystem : SystemBase() {
    override fun onSetup() {
        usingComponents(TakeDamageComp::class.java)
    }

    override fun onUpdate() {
        forEachWithComponents(TakeDamageComp::class.java) { entity, takeDamgeComp ->
            takeDamgeComp.totalDamageTaken += takeDamgeComp.currDamageTaken
            takeDamgeComp.currDamageTaken = 0f
        }
    }
}