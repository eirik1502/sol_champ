package sol_engine.ecs;

import org.junit.Assert;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorldTest {

    private class PosComp extends Component {
        public int x, y;
    }
    private class TextComp extends Component {
        public String text;
    }

    public static class MoveRightSys extends SystemBase {
        public void onStart() {
            super.usingComponents(PosComp.class);
        }
        public void onUpdate() {
            super.entities.stream().forEach(e -> {
                e.getComponent(PosComp.class).x += 1;
            });
        }
        public void onEnd() {

        }
    }

    public static class TextRenderSys extends SystemBase {
        private String space = ".";

        public String output = "";


        public TextRenderSys() {
        }

        public void onStart() {
            super.usingComponents(TextComp.class, PosComp.class);
        }
        public void onUpdate() {
            output = "";
            super.entities.stream().forEach(e -> {
                final PosComp posComp = e.getComponent(PosComp.class);
                final TextComp textComp = e.getComponent(TextComp.class);

                output += ""+posComp.x + " " + posComp.y + " " + textComp.text + "\n";
            });
        }
        public void onEnd() {

        }
    }

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

    private Entity createEntity() {
        Entity e = world.createEntity("hello");
        PosComp posComp = new PosComp();
        TextComp textComp = new TextComp();

        e.addComponent(posComp);
        e.addComponent(textComp);
        return e;
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

    @Test
    public void testEntities() {

        Entity e = createEntity();

        e.getComponent(PosComp.class).x = 0;
        e.getComponent(PosComp.class).y = 3;

        e.getComponent(TextComp.class).text = "SomeText";


        TextRenderSys textRenderSys = world.addSystem(TextRenderSys.class);
        world.addEntity(e);

        checkWorldState("first", world, Arrays.asList(e), Arrays.asList(textRenderSys));


//        world.setup();
        world.update();

        MoveRightSys moveRightSys = world.addSystem(MoveRightSys.class);

        checkWorldState("second", world, Arrays.asList(e), Arrays.asList(textRenderSys, moveRightSys));
        checkWorldState("second", world, Arrays.asList(e), Arrays.asList(moveRightSys, textRenderSys));

        IntStream.range(0, 5).forEach(i -> world.update());

        // check that systems executed
        Assert.assertEquals(e.getComponent(PosComp.class).x, 5);
        Assert.assertEquals(e.getComponent(PosComp.class).y, 3);

        assertThat(textRenderSys.output, is(equalTo("4 3 SomeText\n")));

        Entity e2 = createEntity();
        world.addEntity(e2);

        checkWorldState("third", world, Arrays.asList(e, e2), Arrays.asList(textRenderSys, moveRightSys));

        // TODO: check remove entities and remove systems. In checkWorld, need to account for componentTypeGroup keys with no systems

        world.end();
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
