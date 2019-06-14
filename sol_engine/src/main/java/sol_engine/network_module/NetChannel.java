package sol_engine.network_module;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class NetChannel {


    private List<NetInPacket> pendingPackets = new ArrayList<>();


//    public void broadcast()

    public void popForEach(Consumer<NetInPacket> action) {
        pendingPackets.forEach(action);
        pendingPackets.clear();
    }

    void receivePacket(NetInPacket packet) {
        pendingPackets.add(packet);
    }
}
