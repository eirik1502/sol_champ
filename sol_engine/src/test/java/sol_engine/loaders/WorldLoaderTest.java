package sol_engine.loaders;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import sol_engine.ecs.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class WorldLoaderTest {

    private static Gson gson = new Gson();

    private String ecCharacter = "character", ecFrank = "frank", ecBill = "bill";
    private String entityFrank1Name = "Frank1", entityFrank2Name = "Frank2";
    private List<String> ecNames = Arrays.asList(ecCharacter, ecFrank, ecBill);

    private World world;
    private WorldLoader worldLoader;


    @Before
    public void setUp() {
        world = new World();

        try {
            worldLoader = new WorldLoader();
            worldLoader.loadIntoWorld(world, "worldConfigTest.json");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCompsysPackagesLoading() {
        Set<String> targetCompsysPackages = new HashSet<>(Arrays.asList(
                "sol_engine",
                "sol_engine.loaders"));
        assertThat(worldLoader.getCompSysPackages().containsAll(targetCompsysPackages), is(true));
    }

    @Test
    public void testSystemsLoaded() {
        List<Class<? extends ComponentSystem>> targetSystems = new ArrayList<>(Arrays.asList(
                TestSys1.class,
                TestSys2.class));
        assertThat(world.getSystemTypes(), is(equalTo(targetSystems)));
    }

    @Test
    public void testEntityClassesLoaded() {
        assertThat(world.getEntityClasses().size(), is(3));
        // check that names equal ecs name
        world.getEntityClasses().forEach((ecName, ec) ->
                assertThat(ecName, equalTo(ec.className)));
        // check that all ecs exist
        ecNames.forEach(ecName ->
                assertThat(world.getEntityClasses().containsKey(ecName), is(true)));

        EntityClass characterClass = world.getEntityClasses().get(ecCharacter);
        EntityClass frankClass = world.getEntityClasses().get(ecFrank);
        EntityClass billClass = world.getEntityClasses().get(ecBill);

        // check extends classes
        assertThat(characterClass.getSuperclassesView(), equalTo(new ArrayList<>()));
        assertThat(frankClass.getSuperclassesView(), equalTo(Arrays.asList(ecCharacter)));
        assertThat(billClass.getSuperclassesView(), equalTo(Arrays.asList(ecFrank, ecCharacter)));

        // check components and their values
        // construct target comps
        TestPosComp characterPosComp = new TestPosComp();
        TestPosComp frankPosComp = new TestPosComp();
        frankPosComp.x = frankPosComp.y = 100;
        TestPosComp billPosComp = new TestPosComp();
        TestTextComp billTextComp = new TestTextComp();
        billTextComp.text = "hello there";

        assertThat(setsAreEqualByReflection(
                characterClass.getComponentsView().asSet(), asSet(characterPosComp)),
                is(true));
        assertThat(setsAreEqualByReflection(
                frankClass.getComponentsView().asSet(), asSet(frankPosComp)),
                is(true));
        assertThat(setsAreEqualByReflection(
                billClass.getComponentsView().asSet(), asSet(billTextComp, billPosComp)),
                is(true));
    }

    private <T> Set<T> asSet(T...elems) {
        return new HashSet<>(Stream.of(elems).collect(Collectors.toSet()));
    }

    private <T> boolean setsAreEqualByReflection(Set<T> set1, Set<T> set2) {
        List<String> set1AsJsonList = set1.stream().map(elem -> gson.toJson(elem)).collect(Collectors.toList());
        List<String> set2AsJsonList = set2.stream().map(elem -> gson.toJson(elem)).collect(Collectors.toList());
        Collections.sort(set1AsJsonList);
        Collections.sort(set2AsJsonList);
        return set1AsJsonList.equals(set2AsJsonList);
    }

    @Test
    public void testEntitiesLoaded() {
        assertThat(world.getEntities().size(), is(2));

        Entity entityFrank1 = world.getEntityByName(entityFrank1Name);
        Entity entityFrank2 = world.getEntityByName(entityFrank2Name);

        TestPosComp frank1PosCompTarget = new TestPosComp();
        frank1PosCompTarget.x = 100;
        frank1PosCompTarget.y = 100;
        TestPosComp frank2PosCompTarget = new TestPosComp();
        frank2PosCompTarget.x = 300;
        frank2PosCompTarget.y = 100;

        assertThat(componentsEqual(entityFrank1.getComponent(TestPosComp.class), frank1PosCompTarget), is(true));
        assertThat(componentsEqual(entityFrank2.getComponent(TestPosComp.class), frank2PosCompTarget), is(true));

    }

    private boolean componentsEqual(Component comp1, Component comp2) {
        return Component.areEqual(comp1, comp2);
    }
}
