package sol_engine.archive.network_module.connection_handler;

import com.esotericsoftware.kryonet.Connection;

import java.util.HashSet;
import java.util.Set;

public class _SolConnectionEstablisher {

    public interface Listener {
        void onConnectionEstablished(ConnectedHost connHost, Connection conn);
    }

    private final String hostName;

    private Set<Connection> pendingSolConnections = new HashSet<>();


    public _SolConnectionEstablisher(String hostName, _ConnectionsHandler connHandler, Listener onConnectionEstablished) {
        this.hostName = hostName;

        connHandler.registerPacket(SolConnectPacket.class);

        connHandler.addListener(new com.esotericsoftware.kryonet.Listener() {

            public void received(Connection conn, Object packet) {
                if (packet instanceof SolConnectPacket) {
                    if (pendingSolConnections.contains(conn)) {

                        SolConnectPacket connPacket = (SolConnectPacket) packet;
                        ConnectedHost newHost = new ConnectedHost(conn.getRemoteAddressTCP().getHostName(), connPacket.hostName, -1);

                        onConnectionEstablished.onConnectionEstablished(newHost, conn);
                        pendingSolConnections.remove(conn);
                    }
                }
            }
        });
    }

    public void establishConnectionFor(Connection conn) {
        // listen for
        pendingSolConnections.add(conn);

        // send sol connection_handler packet
        SolConnectPacket connPacket = new SolConnectPacket(hostName);
        conn.sendTCP(connPacket);
    }

}
