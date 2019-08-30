package sol_engine.network_module;

import com.esotericsoftware.kryonet.Server;
import sol_engine.network_module.connection_handler.ConnectedHost;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerConnectionHandlerOld {


    public Map<ConnectedHost, Socket> pollConnections() {
        return null;
    }

    private ConcurrentLinkedQueue<Socket> newSockets = new ConcurrentLinkedQueue<>();

    private ServerSocket serverSocket;
    private Thread connectionListenThread = null;
    private boolean running = false;
    private final Object threadLock = new Object();

    private Server server;

    public ServerConnectionHandlerOld(int port) {
        server = new Server();
        server.start();
        try {
            server.bind(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void terminate() {

        setRunningSynchronized(false);
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connectionListenThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startListenThread() {
        running = true;

        connectionListenThread = new Thread(() -> {

            while(true) {

                synchronized (threadLock) {
                    if (!running) return;
                }

                try {

                    Socket socket = serverSocket.accept();
                    newSockets.add(socket);

                } catch (IOException e) {
                    System.err.println("Server socket was closed or something. terminating");

                    setRunningSynchronized(false);
                }
            }
        });
    }

    private void setRunningSynchronized(boolean value) {
        synchronized (threadLock) {
            running = value;
        }
    }

}
