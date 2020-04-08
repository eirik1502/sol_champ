package sol_engine.network.network_test_utils;

import sol_engine.network.packet_handling.NetworkPacket;

import java.util.Objects;

public class TestPacketString implements NetworkPacket {
    public String message;

    public TestPacketString(String message) {
        this.message = message;
    }

    public TestPacketString() {
    }

    @Override
    public String toString() {
        return "TestPacketString{" +
                "message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestPacketString)) return false;
        TestPacketString that = (TestPacketString) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}
