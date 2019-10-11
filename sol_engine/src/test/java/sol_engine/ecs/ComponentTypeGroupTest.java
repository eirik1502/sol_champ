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
        ComponentFamily g1 = new ComponentFamily(PosComp.class);
        ComponentFamily g2 = new ComponentFamily(PosComp.class);
        ComponentFamily g3 = new ComponentFamily(PosComp.class, TextComp.class);
        ComponentFamily g4 = new ComponentFamily(TextComp.class, PosComp.class);
        ComponentFamily g5 = new ComponentFamily(TextComp.class);

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
