package sol_engine.network_module;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class NetChannel {

    private final int id;
    private List<NetInPacket> pendingPacketsIn = new ArrayList<>();
    private List<NetOutPacket> pendingPacketsOut = new ArrayList<>();


    public NetChannel(int id) {
        this.id = id;
    }

    public void broadcast(NetOutPacket outPacket) {
        pendingPacketsOut.add(outPacket);
    }

    public void popForEach(Consumer<NetInPacket> action) {
        pendingPacketsIn.forEach(action);
        pendingPacketsIn.clear();
    }


    void addInPacket(NetInPacket packet) {
        pendingPacketsIn.add(packet);
    }
    List<NetOutPacket> popOutPackets() {
        List<NetOutPacket> returnPackets = new ArrayList<>(pendingPacketsOut);
        pendingPacketsOut.clear();
        return returnPackets;
    }
}
