package sol_engine.network_module.connection_handler;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionListener {

    private final Server kryonetServer;


    public ConnectionListener() {
        kryonetServer = new Server();
    }

    public void startListening(int port) {
        kryonetServer.start(); // running on another thread

        kryonetServer.addListener(new Listener() {
            public void connected(Connection conn) {
                System.out.println("Server: got connection_handler");
            }
        });

        try {
            kryonetServer.bind(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<ConnectedHost> pollNewConnections() {
        return new ArrayList<>();
    }

}
