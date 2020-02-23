package sol_engine.network.packet_handling;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;

import java.util.Deque;

import static org.hamcrest.CoreMatchers.*;

public class NetworkClassPacketLayerTest {

    public static class TestPacket implements NetworkPacket {
        public String name;
        public int age;
        public float height;

        public boolean equals(Object o) {
            if (o instanceof TestPacket) {
                TestPacket oPacket = (TestPacket) o;
                return oPacket.name.equals(name) && oPacket.age == age && oPacket.height == height;
            }
            return false;
        }
    }

    TestPacket p1, p2, p3;
    RawPacketBuffer rawPacketLayer;
    NetworkClassPacketLayer classPacketLayer;

    @Before
    public void setUp() {
        rawPacketLayer = new RawPacketBuffer();
        classPacketLayer = new NetworkClassPacketLayer(rawPacketLayer);
        classPacketLayer.usePacketTypes(TestPacket.class);

        p1 = new TestPacket();
        p1.name = "Frank";
        p1.height = 1.7f;
        p1.age = 33;

        p2 = new TestPacket();
        p2.name = "Ingorf";
        p2.height = 0.6f;
        p2.age = 20;

        p3 = new TestPacket();
        p3.name = "Sutt";
        p3.height = 3.46f;
        p3.age = 28;
    }

    private void assertPacketEquality(TestPacket packet, TestPacket target) {
        assertThat(packet, is(equalTo(target)));
        assertThat(packet, is(not(sameInstance(target))));
    }

    @Test
    public void testSinglePacket() {
        classPacketLayer.pushPacket(p1);

        Deque<TestPacket> packets = classPacketLayer.pollPackets(TestPacket.class);

        assertThat(packets.size(), is(1));
        TestPacket firstPacket = packets.poll();

        assertPacketEquality(firstPacket, p1);
    }

    @Test
    public void testMultiplePackets() {
        classPacketLayer.pushPacket(p1);
        classPacketLayer.pushPacket(p2);

        Deque<TestPacket> packets = classPacketLayer.pollPackets(TestPacket.class);

        assertThat(packets.size(), is(2));
        TestPacket firstPacket = packets.poll();
        assertPacketEquality(firstPacket, p1);

        TestPacket secondPacket = packets.poll();
        assertPacketEquality(secondPacket, p2);

        classPacketLayer.pushPacket(p3);
        Deque<TestPacket> packets2 = classPacketLayer.pollPackets(TestPacket.class);
        assertPacketEquality(packets2.poll(), p3);
    }

}
