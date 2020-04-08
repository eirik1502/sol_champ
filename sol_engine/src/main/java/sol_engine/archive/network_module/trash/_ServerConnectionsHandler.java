package sol_engine.archive.network_module.connection_handler;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class _ServerConnectionsHandler extends _ConnectionsHandler {


    private Server server;

    private Set<Connection> pendingSolConnections = new HashSet<>();


    public _ServerConnectionsHandler(String hostName, int port) {
        super(hostName, port);

        server = new Server();
    }

    @Override
    public void onStart() {
        server.start();

        addListener(new Listener() {
            public void connected(Connection conn) {
                System.out.println("Server: got connection_handler");
                addConnection(conn);
            }
        });

        try {
            server.bind(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void addListener(Listener listener) {
        server.addListener(listener);
    }

    @Override
    protected EndPoint getEndpoint() {
        return server;
    }
}
