package sol_engine.network.network_test_utils;

import sol_engine.network.packet_handling.NetworkPacket;

import java.util.Objects;

public class TestPacketInt implements NetworkPacket {
    public int value;

    public TestPacketInt() {

    }

    public TestPacketInt(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TestPacketInt{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestPacketInt)) return false;
        TestPacketInt that = (TestPacketInt) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
