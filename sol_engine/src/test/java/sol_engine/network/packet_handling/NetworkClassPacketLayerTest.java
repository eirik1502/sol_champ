package sol_engine.network.packet_handling;

import org.junit.Before;
import org.junit.Test;
import sol_engine.network.test_utils.TestPacketString;

import static org.junit.Assert.assertThat;

import java.util.Deque;

import static org.hamcrest.CoreMatchers.*;

public class NetworkClassPacketLayerTest {

    TestPacketString p1, p2, p3;
    RawPacketBuffer rawPacketLayer;
    NetworkClassPacketLayer classPacketLayer;

    @Before
    public void setUp() {
        rawPacketLayer = new RawPacketBuffer();
        classPacketLayer = new NetworkClassPacketLayer(rawPacketLayer);
        classPacketLayer.usePacketTypes(TestPacketString.class);

        p1 = new TestPacketString("Frank");
        p2 = new TestPacketString("Ingorf");
        p3 = new TestPacketString("Sutt");
    }

    private void assertPacketEquality(TestPacketString packet, TestPacketString target) {
        assertThat(packet, is(equalTo(target)));
        assertThat(packet, is(not(sameInstance(target))));
    }

    @Test
    public void testSinglePacket() {
        classPacketLayer.pushPacket(p1);
        classPacketLayer.pollAndParseRawPackets();
        Deque<TestPacketString> packets = classPacketLayer.pollPackets(TestPacketString.class);

        assertThat(packets.size(), is(1));
        TestPacketString firstPacket = packets.poll();

        assertPacketEquality(firstPacket, p1);
    }

    @Test
    public void testMultiplePackets() {
        classPacketLayer.pushPacket(p1);
        classPacketLayer.pushPacket(p2);
        classPacketLayer.pollAndParseRawPackets();
        Deque<TestPacketString> packets = classPacketLayer.pollPackets(TestPacketString.class);

        assertThat(packets.size(), is(2));
        TestPacketString firstPacket = packets.poll();
        assertPacketEquality(firstPacket, p1);

        TestPacketString secondPacket = packets.poll();
        assertPacketEquality(secondPacket, p2);

        classPacketLayer.pushPacket(p3);
        classPacketLayer.pollAndParseRawPackets();
        Deque<TestPacketString> packets2 = classPacketLayer.pollPackets(TestPacketString.class);
        assertPacketEquality(packets2.poll(), p3);
    }

}
