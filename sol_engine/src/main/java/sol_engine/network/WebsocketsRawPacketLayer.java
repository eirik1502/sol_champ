package sol_engine.network;

import java.util.Deque;

public class WebsocketsRawPacketLayer implements NetworkRawPacketLayer {
    @Override
    public Deque<String> pollPacket() {
        return null;
    }

    @Override
    public void pushPacket(String packet) {

    }
}
