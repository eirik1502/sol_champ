package sol_engine.network_module.connection_handler;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public class _ClientConnectionHandler extends _ConnectionsHandler {


    private String connectAddr;
    private Client client;

    private boolean connectionEstablished = false;

    public _ClientConnectionHandler(String hostName, String connectAddr, int port) {
        super(hostName, port);
        this.connectAddr = connectAddr;
        client = new Client();
    }


    @Override
    public void onStart() {
        client.start();

        addListener(new Listener() {
            public void connected(Connection conn) {
                addConnection(conn);
            }
        });

        _ConnectionListener connListener = (connHost) -> {
            connectionEstablished = true;
            System.out.println("connection_handler established");
        };
        addConnectionListener(connListener);

        try {
            client.connect(3000, connectAddr, port);
        } catch (IOException e) {
            System.err.println("Could not connect to server");
            e.printStackTrace();
        }

        // block until sol connection_handler is established

        while (!connectionEstablished) {
        }
        System.out.println("return to life");
        removeConnectionListener(connListener);
    }

    @Override
    protected void addListener(Listener listener) {
        client.addListener(listener);
    }

    @Override
    protected EndPoint getEndpoint() {
        return client;
    }
}
