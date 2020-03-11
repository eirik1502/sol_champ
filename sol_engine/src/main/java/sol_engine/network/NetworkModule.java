package sol_engine.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.module.Module;
import sol_engine.network.client.NetworkClient;
import sol_engine.network.client.NetworkWebsocketsClient;
import sol_engine.network.packet_handling.NetworkClassPacketLayer;
import sol_engine.network.packet_handling.NetworkPacket;
import sol_engine.network.packet_handling.NetworkRawPacketLayer;
import sol_engine.network.server.NetworkServer;
import sol_engine.network.server.NetworkWebsocketsServer;
import sol_engine.network.server.ConnectionData;

import java.util.*;

public class NetworkModule extends Module {
    private final Logger logger = LoggerFactory.getLogger(NetworkModule.class);

    private NetworkModuleConfig config;
    private ConnectionData connectionData = null;

    private NetworkServer server = null;
    private NetworkClient client = null;
    private NetworkRawPacketLayer rawPacketLayer;
    private NetworkClassPacketLayer classPacketLayer;

    public NetworkModule(NetworkModuleConfig config) {
        this.config = config;
    }


    @SafeVarargs
    public final void usePacketTypes(Class<? extends NetworkPacket>... packetTypes) {
        usePacketTypes(Arrays.asList(packetTypes));
    }

    public final void usePacketTypes(List<Class<? extends NetworkPacket>> packetTypes) {
        classPacketLayer.usePacketTypes(packetTypes);
    }


    public <T extends NetworkPacket> List<T> peekPackets(Class<T> packetType) {
        return new ArrayList<>(classPacketLayer.peekPackets(packetType));
    }

    public void pushPacket(NetworkPacket packet) {
        classPacketLayer.pushPacket(packet);
    }

    public boolean isConnected() {
        return rawPacketLayer.isConnected();
    }

    public ConnectionData getConnectionData() {
        if (connectionData == null) {
            logger.error("Cannot get connection data before internalSetup() is called");
        }
        return connectionData;
    }

    @Override
    public void onSetup() {
        if (config.isServer) {
            server = new NetworkWebsocketsServer();
            rawPacketLayer = server;
            classPacketLayer = new NetworkClassPacketLayer(server);
            server.start();
            this.connectionData = connectionData;

            // wait for connections
            server.waitForConnections();

        } else {
            client = new NetworkWebsocketsClient();
            rawPacketLayer = client;
            classPacketLayer = new NetworkClassPacketLayer(client);

            client.connect(config.address, config.port);
        }
    }

    @Override
    public void onStart() {
        usePacketTypes(config.packetTypes);
    }

    @Override
    public void onEnd() {
        rawPacketLayer.terminate();
    }

    @Override
    public void onUpdate() {
        classPacketLayer.clearAllPackets();
        classPacketLayer.pollAndParseRawPackets();
    }
}
