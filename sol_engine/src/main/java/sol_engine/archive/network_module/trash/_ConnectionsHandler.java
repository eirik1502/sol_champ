package sol_engine.network_module.connection_handler;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import sol_engine.network_module.NetPacket;
import sol_engine.utils.ImmutableSetView;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public abstract class _ConnectionsHandler {

    private class PendingPacket {
        final Class<? extends NetPacket> packetType;
        final NetPacket packet;
        final Connection conn;

        PendingPacket(Class<? extends NetPacket> packetType, NetPacket packet, Connection conn) {
            this.packetType = packetType;
            this.packet = packet;
            this.conn = conn;
        }
    }

    private class PendingConnection {
        final Connection connection;

        PendingConnection(Connection connection) {
            this.connection = connection;
        }
    }

    // buffers for kryonet
    private ConcurrentLinkedQueue<PendingPacket> pendingPackets = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<PendingConnection> pendingConnections = new ConcurrentLinkedQueue<>();


    private Map<ConnectedHost, Connection> connectedHosts = new HashMap<>();
    private Map<Connection, ConnectedHost> connectedHostsByConnection = new HashMap<>();


    private Set<_ConnectionListener> connectionListeners = new HashSet<>();
    private Map<Class<? extends NetPacket>, List<PacketListener>> packetListeners = new HashMap<>();


    protected final String hostName;
    protected final int port;

    private SolConnectionEstablisher connectionEstablisher;


    public _ConnectionsHandler(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public final void start() {
        connectionEstablisher = new SolConnectionEstablisher(hostName, this, (connHost, conn) -> {

            connectedHosts.put(connHost, conn);
            connectedHostsByConnection.put(conn, connHost);
            System.out.println("Calling connection_handler listeners");
        });

        // a kryonet listener to handle incoming packets
        addListener(new Listener() {
            public void received(Connection connection, Object packet) {
                if (!connectedHostsByConnection.containsKey(connection)) {
                    System.err.println("Recieved a packet from a not connected host");
                    return;
                }
                if (!(packet instanceof NetPacket)) {
                    System.err.println("Received an object that is not a NetPacket");
                    return;
                }
                NetPacket validPacket = (NetPacket) packet;
                Class<? extends NetPacket> validPacketClass = validPacket.getClass();

                PendingPacket pendingPacket = new PendingPacket(validPacketClass, validPacket, connectedHostsByConnection.get(connection));
                pendingPackets.add(pendingPacket);
            }
        });

        onStart();

    }

    /**
     * Polling will trigger the relevant listeners
     */
    public void pollConnections() {
        connectionListeners.forEach(cl -> cl.onConnection(connHost));
    }

    public void pollPackets() {
        pendingPackets.forEach(pendingPacket -> {
            if (packetListeners.containsKey(pendingPacket.packetType)) {
                packetListeners.get(pendingPacket.packetType).forEach(listener -> listener.onPacket(pendingPacket.fromHost, pendingPacket.packet));
            } else {
                System.err.println("Got a packet with no assigned listener: " + pendingPacket.packetType.getSimpleName());
            }
        });
    }

    protected abstract void onStart();

    public void terminate() {
        // close all connections
        connectedHosts.values().forEach(c -> c.close());

        getEndpoint().close();
    }

    public void registerPacket(Class<? extends NetPacket> packetType) {
        getEndpoint().getKryo().register(packetType);
    }

    public <T extends NetPacket> void registerPacketListener(Class<T> packetType, PacketListener<T> listener) {
        registerPacket(packetType);
        packetListeners.computeIfAbsent(packetType, newPacketType -> new ArrayList<>()).add(listener);
    }

    public void addConnectionListener(_ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public boolean removeConnectionListener(_ConnectionListener listener) {
        return connectionListeners.remove(listener);
    }

    public void sendToAll(NetPacket packet) {
        connectedHosts.values().forEach(c -> c.sendTCP(packet));
    }

    public void sendTo(ConnectedHost host, NetPacket packet) {
        connectedHosts.get(host).sendTCP(packet);
    }


    protected void addConnection(Connection conn) {
        connectionEstablisher.establishConnectionFor(conn);
    }

    protected abstract void addListener(Listener listener);

    protected abstract EndPoint getEndpoint();

    public ImmutableSetView<ConnectedHost> getConnectedHosts() {
        return new ImmutableSetView<>(connectedHosts.keySet());
    }

    public void printConnectedHosts() {
        System.out.println(connectedHosts.keySet().stream().map(ch -> ch.name).collect(Collectors.joining(" ")));
    }

}
