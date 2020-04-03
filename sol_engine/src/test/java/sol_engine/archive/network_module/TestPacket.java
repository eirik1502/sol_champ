package sol_engine.archive.network_module;

public class TestPacket extends NetPacket {

    public String val;

    public TestPacket() {

    }
    public TestPacket(String val) {
        this.val = val;
    }

    public String toString() {
        return val;
    }
}