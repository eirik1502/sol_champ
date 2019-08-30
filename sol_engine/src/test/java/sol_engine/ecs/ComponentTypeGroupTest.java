package sol_engine.ecs;

import org.junit.Assert;
import org.junit.Test;

public class ComponentTypeGroupTest {

    private class PosComp extends Component {
        public int x, y;
    }
    private class TextComp extends Component {
        public String text;
    }

    @Test
    public void testComponentTypeGroup() {
        ComponentTypeGroup g1 = new ComponentTypeGroup(PosComp.class);
        ComponentTypeGroup g2 = new ComponentTypeGroup(PosComp.class);
        ComponentTypeGroup g3 = new ComponentTypeGroup(PosComp.class, TextComp.class);
        ComponentTypeGroup g4 = new ComponentTypeGroup(TextComp.class, PosComp.class);
        ComponentTypeGroup g5 = new ComponentTypeGroup(TextComp.class);

        // check equals
        Assert.assertEquals(g1, g2);
        Assert.assertEquals(g3, g4);
        Assert.assertEquals(g5, g5);
        Assert.assertNotEquals(g1, g3);
        Assert.assertNotEquals(g1, g5);
        Assert.assertNotEquals(g3, g5);

        // test containes
        Assert.assertTrue(g3.contains(g1));
        Assert.assertTrue(g3.contains(g5));
        Assert.assertTrue(g3.contains(g4));

        Assert.assertTrue(g1.contains(g1));
        Assert.assertTrue(g4.contains(g4));

        Assert.assertFalse(g1.contains(g3));
        Assert.assertFalse(g5.contains(g4));
    }
}
