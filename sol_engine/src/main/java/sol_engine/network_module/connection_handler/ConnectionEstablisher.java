package sol_engine.network_module.connection_handler;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import sol_engine.network_module.NetPacket;

import java.io.IOException;

public class ConnectionEstablisher {

    interface Callback {
        void onConnectedToServer(ConnectedHost connectedHost);
    }

    static class ConnectPacket extends NetPacket {
        public String hostName;

        public ConnectPacket(String hostName) {
            this.hostName = hostName;
        }
    }


    private final Client kryonetClient;


    public ConnectionEstablisher() {
        kryonetClient = new Client();
    }

    public ConnectedHost connectToServer(String address, int port) {
//        ConnectedHostContainer newHost = null;
//        connectToServerAsync(address, port, host -> {
//            newHost = host;
//        });
//        while (!newHost) {
//
//        }
        return null;
    }

    public void connectToServerAsync(String address, int port, Callback callback) {
        kryonetClient.start();

        kryonetClient.getKryo().register(ConnectionEstablisher.ConnectPacket.class);

        kryonetClient.addListener(new Listener() {
            public void connected(Connection conn) {
                establishConnectedHost(conn, callback);
            }
        });

        new Thread(() -> {
            try {
                kryonetClient.connect(3000, address, port);
            } catch (IOException e) {
                System.err.println("Could not connect to server");
                e.printStackTrace();
            }
        }, "kryonet connect").start();
    }

    static void establishConnectedHost(final Connection conn, final Callback callback) {

        conn.addListener(new Listener() {
            public void received(Connection connInner, Object packet) {
                if (packet instanceof ConnectionEstablisher.ConnectPacket) {
                    ConnectedHost newHost = new ConnectedHost(connInner.getRemoteAddressTCP().getHostName(), ((ConnectPacket) packet).hostName);
                    callback.onConnectedToServer(newHost);
                    conn.removeListener(this);
                }
            }
        });

    }
}
