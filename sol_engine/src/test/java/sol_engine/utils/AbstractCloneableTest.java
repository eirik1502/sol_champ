package sol_engine.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class AbstractCloneableTest {

    public static class TestCloneable extends AbstractCloneable<TestCloneable> {
        public int a = 5;
        public String b = "hei";

        public boolean equals(Object o) {
            if (o instanceof TestCloneable) {
                TestCloneable oo = (TestCloneable) o;
                return a == oo.a && b.equals(oo.b);
            }
            return false;
        }
    }

    @Test
    public void testObjectCloned() {
        TestCloneable obj = new TestCloneable();
        TestCloneable objCloned = obj.clone();

        assertThat(obj, is(not(theInstance(objCloned))));
        assertThat(obj, is(equalTo(objCloned)));
    }
}
