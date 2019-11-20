package sol_engine.ecs;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldInsight {

    private World world;


    WorldInsight(World world) {
        this.world = world;
    }


    public List<SystemBase> getSystems() {
        return new ArrayList<>(world.systems.values());
    }

    public List<Class<? extends SystemBase>> getSystemTypes() {
        return getSystems().stream().map(SystemBase::getClass).collect(Collectors.toList());
    }

    public Set<Entity> getEntities() {
        return world.entities;
    }

    public Set<Entity> getEntitiesScheduledForAdd() {
        return world.entitiesScheduledForAdd;
    }

    public Set<Entity> getEntitiesScheduledForRemove() {
        return world.entitiesScheduledForRemove;
    }

    public Map<String, EntityClass> getEntityClasses() {
        return world.entityClasses;
    }

    public String toString() {
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        sb.append("---Entity classes---\n");
        getEntityClasses().values().forEach(ec -> {
            sb.append(ec.className).append(' ');
            sb.append(gson.toJson(ec.getComponentsView())).append('\n');
        });
        sb.append("---Component systems---\n");
        getSystems().forEach(cs -> {
            sb.append(cs.getClass().getSimpleName()).append(' ');
            sb.append(gson.toJson(cs.getCompFamily())).append('\n');
        });
        sb.append("---Entities---\n");
        getEntities().forEach(e -> {
            sb.append(e.name).append(' ');
            sb.append(gson.toJson(e.getComponents().values())).append('\n');
        });
        return sb.toString();
    }
}
