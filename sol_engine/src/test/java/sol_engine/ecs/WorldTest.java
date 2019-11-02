package sol_engine.ecs;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sol_engine.utils.Repeat;
import sol_engine.utils.math.MathF;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

//import static org.hamcrest.CoreMatchers.*;

public class WorldTest {

    private static class WorldState {
        private static Map<World, WorldState> retrievedWorldStates = new HashMap<>();

        public static WorldState getFor(World world) {
            return retrievedWorldStates.computeIfAbsent(world, key -> {
                try {
                    WorldState ws = new WorldState();

                    Field entitiesOfComponentTypeField = World.class.getDeclaredField("entitiesWithCompType");
                    Field entityGroupsField = World.class.getDeclaredField("entityGroups");
                    entitiesOfComponentTypeField.setAccessible(true);
                    entityGroupsField.setAccessible(true);

                    ws.entitiesOfCompType = (Map<Class<? extends Component>, Set<Entity>>) entitiesOfComponentTypeField.get(world);
                    ws.entityGroups = (Map<ComponentFamily, List<Entity>>) entityGroupsField.get(world);

                    return ws;

                } catch (NoSuchFieldException | IllegalAccessException e1) {
                    e1.printStackTrace();
                    Assert.fail();
                    return null;
                }
            });
        }

        public Map<Class<? extends Component>, Set<Entity>> entitiesOfCompType;
        public Map<ComponentFamily, List<Entity>> entityGroups;
    }


    private World world;


    @Before
    public void setUp() {
        world = new World();
    }

    @After
    public void tearDown() {
        world.end();
    }

    private Entity createTextEntity(World world, String name) {
        Entity e = world.createEntity(name);
        e.addComponent(new TextComp("hei"));
        return e;
    }

    @Test
    public void testAddEntity() {
        Entity e = createTextEntity(world, "te");
        world.addEntity(e);
        world.update();
        assertThat(world.getEntities(), containsInAnyOrder(e));

        Set<Entity> createdEntities = new HashSet<>();
        createdEntities.add(e);
        Repeat.repeat(10, i -> {
            Entity e2 = createTextEntity(world, "te" + i);
            world.addEntity(e2);
            createdEntities.add(e2);
        });
        world.update();

        assertThat("entities in world match", world.getEntities(),
                containsInAnyOrder(createdEntities.toArray(new Entity[0])));

        assertThat("entity names match", world.getEntities().stream().map(Entity::getName).collect(Collectors.toSet()),
                containsInAnyOrder("te", "te0", "te1", "te2", "te3", "te4", "te5", "te6", "te7", "te8", "te9"));
    }

    @Test
    public void testRemoveEntity() {
        Entity e1 = createTextEntity(world, "e1");
        Entity e2 = createTextEntity(world, "e2");
        Entity e3 = createTextEntity(world, "e3");

        world.addEntity(e1);
        world.addEntity(e2);
        world.addEntity(e3);
        world.update();

        world.removeEntity(e2);
        world.update();
        assertThat("remove entity that is addedand world updated",
                world.getEntities(), containsInAnyOrder(e1, e3));

        world.removeEntityByName("e3");
        world.update();
        assertThat("remove entity by name", world.getEntities(), containsInAnyOrder(e1));

        // remove entity without updating world after add
        Entity e4 = createTextEntity(world, "e4");
        world.addEntity(e4);
        world.removeEntity(e4);
        world.update();
        assertThat("remove entity that is added and removed without world update between",
                world.getEntities(), containsInAnyOrder(e1));

        world.removeEntityByName("e1");
        world.update();
        assertThat(world.getEntities(), empty());

        // stress test
        int testWithEntitiesCount = 200;
        Set<Entity> removedEntities = IntStream.range(0, testWithEntitiesCount)
                .peek(i -> world.addEntity(createTextEntity(world, "e" + i)))
                .peek(i -> {
                    if (MathF.random() > 0.5) world.update();
                })
                .mapToObj(i -> {
                    // remove an entity by chance
                    if (MathF.random() > 0.5) {
                        List<Entity> entities = new ArrayList<>(world.getEntities());
                        if (!entities.isEmpty()) {
                            Entity rande = entities.get(MathF.floori(MathF.random() * entities.size()));
                            world.removeEntity(rande);
                            return rande;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        world.update();

        int removedEntitiesCount = removedEntities.size();
        assertThat(world.getEntities(), hasSize(testWithEntitiesCount - removedEntitiesCount));
        assertThat(world.getEntities(), not(hasItems(removedEntities.toArray(new Entity[0]))));
    }


    private void checkWorldState(String checkLabel, World world, List<Entity> targetEntities, List<SystemBase> targetSystems) {
//        System.out.println(checkLabel + " num entities: " + targetEntities.size() + " num systems: " + targetSystems.size());

        WorldState ws = WorldState.getFor(world);

        //
        // get all comp types
        Set<Class<? extends Component>> allCompTypes = targetEntities.stream()
                .flatMap(e -> e.getComponentTypeGroup().stream())
                .collect(Collectors.toSet());

        // get all entities of comp types
        Map<Class<? extends Component>, Set<Entity>> targetEntitiesOfCompType = new HashMap<>();
        targetEntities.forEach(e ->
                e.getComponentTypeGroup().stream().forEach(compType ->
                        targetEntitiesOfCompType.computeIfAbsent(compType, key -> new HashSet<>()).add(e)
                )
        );

        // test entitiesOfCompType
        Assert.assertEquals(ws.entitiesOfCompType, targetEntitiesOfCompType);
        Assert.assertNotSame(targetEntitiesOfCompType, ws.entitiesOfCompType);  // make sure the collections are not the same

        // these might be redundant
        Assert.assertEquals(ws.entitiesOfCompType.size(), allCompTypes.size());  // test that no more comps exists, and one of each
        allCompTypes.forEach(ct -> Assert.assertTrue(ws.entitiesOfCompType.containsKey(ct)));  // test that all comp types are registered


        //test groupEntities
        Map<ComponentFamily, List<Entity>> targetEntityGroups = new HashMap<>();
        targetSystems.forEach(s ->
                targetEntityGroups.computeIfAbsent(s.compFamily, key -> new ArrayList<>())
                        .addAll(
                                targetEntities.stream()
                                        .filter(e -> e.getComponentTypeGroup().contains(s.compFamily))
                                        .collect(Collectors.toList())
                        )
        );

        Assert.assertEquals(ws.entityGroups, targetEntityGroups);
        Assert.assertNotSame(targetEntityGroups, ws.entityGroups);  // make sure the collections are not the same
    }


}


//        // test entitiesOfCompType has not changed
//        Assert.assertEquals(entitiesOfCompType.size(), 2);
//        Assert.assertNotNull(entitiesOfCompType.get(PosComp.class));
//        Assert.assertNotNull(entitiesOfCompType.get(TextComp.class));
//        Assert.assertEquals(entitiesOfCompType.get(PosComp.class).size(), 1);
//        Assert.assertTrue(entitiesOfCompType.get(PosComp.class).contains(e));
//        Assert.assertEquals(entitiesOfCompType.get(TextComp.class).size(), 1);
//        Assert.assertTrue(entitiesOfCompType.get(TextComp.class).contains(e));
//
//        //test groupEntities has included the new system group
//        Assert.assertEquals(groupEntities.size(), 2);
//        Assert.assertTrue(groupEntities.containsKey(new ComponentTypeGroup(PosComp.class, TextComp.class)));
//        Assert.assertTrue(groupEntities.containsKey(new ComponentTypeGroup(PosComp.class)));
//        Assert.assertFalse(groupEntities.containsKey(new ComponentTypeGroup(TextComp.class)));
