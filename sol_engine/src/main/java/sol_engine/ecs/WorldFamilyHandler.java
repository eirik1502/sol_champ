package sol_engine.ecs;

import sol_engine.utils.collections.ImmutableListView;
import sol_engine.utils.collections.SetUtils;

import java.util.*;
import java.util.stream.Collectors;

public class WorldFamilyHandler {

    Map<Class<? extends Component>, Set<Entity>> entitiesWithCompType = new HashMap<>();
    Map<ComponentFamily, List<Entity>> entitiesOfFamilies = new HashMap<>();


    WorldFamilyHandler() {
    }

    // TODO: when components are added or removed, this is not updated
    void addEntity(Entity entity) {
        // add the entity to component types mapping
        entity.getComponents().forEach((compType, comp) ->
                entitiesWithCompType.computeIfAbsent(compType, key -> new HashSet<>()).add(entity)
        );

        // update entity groups
        final ComponentFamily entityGroup = entity.getComponentTypeGroup();
        entitiesOfFamilies.entrySet().stream()
                // filter groups that match the new entity
                .filter(group -> entityGroup.contains(group.getKey()))
                // add the new entity to the relevant groups
                .forEach(existingEntityGroup -> existingEntityGroup.getValue().add(entity));
    }

    void removeEntity(Entity entity) {
        // remove the entity to component types mapping
        entity.getComponents().forEach((compType, comp) -> {
            entitiesWithCompType.get(compType).remove(entity);
        });

        // update entity groups
        final ComponentFamily entityGroup = entity.getComponentTypeGroup();
        entitiesOfFamilies.entrySet().stream()
                // filter groups that match the entity
                .filter(group -> entityGroup.contains(group.getKey()))
                // remove the new entity from the relevant groups
                .forEach(group -> group.getValue().remove(entity));
    }

    ImmutableListView<Entity> getEntitiesOfFamily(ComponentFamily compFamily) {
        List<Entity> entityGroupList = entitiesOfFamilies.computeIfAbsent(compFamily, newCompFamily -> {

            // find all entities associated with the comp group
            Set<Set<Entity>> entitiesInGroups = newCompFamily.stream()
                    .map(entitiesWithCompType::get)
                    .collect(Collectors.toSet());

            // if some comp types weren't present, there are no entities that matches the group
            if (entitiesInGroups.isEmpty() || entitiesInGroups.contains(null)) {
                return new ArrayList<>();
            }

            // take the intersection of entities by comp
            final Set<Entity> entitiesInGroup = SetUtils.intersection(entitiesInGroups);

            return new ArrayList<>(entitiesInGroup);
        });

        return new ImmutableListView<>(entityGroupList);
    }
}
