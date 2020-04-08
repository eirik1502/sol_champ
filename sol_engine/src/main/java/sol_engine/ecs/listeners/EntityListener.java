package sol_engine.ecs.listeners;

import sol_engine.ecs.Entity;
import sol_engine.ecs.World;

public interface EntityListener {

    /**
     * Called when an entity is added through addEntity(...)
     * This happens before the Entity is actually added so systems will recognize it
     */
    interface WillBeAdded extends EntityListener {
        void onEntityWillBeAdded(Entity entity, World world);
    }

    interface WillBeRemoved extends EntityListener {
        void onEntityWillBeRemoved(Entity entity, World world);
    }

    /**
     * Called when an Entity is added to World and systems will recognize it,
     * which happens at the end of an update after all systems have been updated
     */
    interface Added extends EntityListener {
        void onEntityAdded(Entity entity, World world);
    }

    interface Removed extends EntityListener {
        void onEntityRemoved(Entity entity, World world);
    }
}
