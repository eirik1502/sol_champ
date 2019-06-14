package sol_engine.network_module;

import sol_engine.modules.Module;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NetworkModule implements Module {



    private Map<Integer, Consumer<NetInPacket>> channels = new HashMap<>();


    public NetworkModule(NetworkModuleConfig config) {

    }

    public void registerChannel(int channelId, Consumer<NetInPacket> callback) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onUpdate() {

    }
}
