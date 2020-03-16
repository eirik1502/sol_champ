package sol_engine.network;

import org.apache.commons.lang3.mutable.MutableLong;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sol_engine.network.client.ClientConfig;
import sol_engine.network.communication_layer_impls.websockets.NetworkWebsocketsClient;
import sol_engine.network.communication_layer_impls.websockets.NetworkWebsocketsServer;
import sol_engine.network.server.ServerConfig;
import sol_engine.network.server.ServerConnectionData;
import sol_engine.network.test_utils.TestUtils;

import java.util.Deque;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class WebsocketsServerClientTest {
    private final int port = 7654;

    private NetworkWebsocketsServer createAndStartServer(int port) {

    }

    private NetworkWebsocketsClient createAndStartClient(int port) {

    }


    @Test
    public void testServerClientConnection() {
        NetworkWebsocketsServer server = new NetworkWebsocketsServer();
        MutableLong openTime = new MutableLong();

        server.onOpen();
        server.start(port);


        NetworkWebsocketsClient client = new NetworkWebsocketsClient();
        client.connect()
    }

    @Test
    public void testConnectionKeys() {
        ServerConnectionData connectionData = startServerClient(new ServerConfig(
                List.of(1, 1),
                true
        ));

        NetworkWebsocketsClient client2Fail = new NetworkWebsocketsClient();
        boolean client2FailConnected = connectClient(client2Fail, connectionData, 0, 0);
        assertThat("A second client could connect as player 1", client2FailConnected, is(false));
        client2Fail.terminate();

        NetworkWebsocketsClient client2 = new NetworkWebsocketsClient();
        boolean client2Connected = connectClient(client2, connectionData, 1, 0);
        assertThat("Client could not connect as player 2", client2Connected, is(true));
        client2.terminate();

        NetworkWebsocketsClient clientObserver = new NetworkWebsocketsClient();
        boolean clientObserverConnected = connectObserver(clientObserver, connectionData);
        assertThat("Client could not connect as observer with the observerKey", clientObserverConnected, is(true));
        clientObserver.terminate();

        NetworkWebsocketsClient clientObserverFail = new NetworkWebsocketsClient();
        boolean clientObserverFailConnected = connectObserver(clientObserverFail, connectionData, "123abc");
        assertThat("Client could connect as an observer with a random key", clientObserverFailConnected, is(false));
        clientObserverFail.terminate();
    }

    @Test
    public void testServerClientPackets() {
        startServerClient(new ServerConfig(
                -1,
                List.of(1, 1),
                false,
                true
        ));

        String p1 = "koko";
        String p2 = "knall";
        String p3 = "PRRRA";

        server.pushPacket(p1);
        server.pushPacket(p2);
        TestUtils.sleepShort();

        Deque<NetworkPacketRaw> clientPackets = client.pollPackets();
        assertThat(clientPackets.size(), is(2));
        assertThat(clientPackets.poll().data, both(equalTo(p1)).and(not(sameInstance(p1))));
        assertThat(clientPackets.poll().data, both(equalTo(p2)).and(not(sameInstance(p2))));

        assertThat(client.pollPackets().size(), is(0));

        // test packets from client to server
        client.pushPacket(p2);
        client.pushPacket(p3);
        TestUtils.sleepShort();

        Deque<NetworkPacketRaw> serverPackets = server.pollPackets();
        assertThat(serverPackets.size(), is(2));
        assertThat(serverPackets.poll().data, both(equalTo(p2)).and(not(sameInstance(p2))));
        assertThat(serverPackets.poll().data, both(equalTo(p3)).and(not(sameInstance(p3))));

        assertThat(server.pollPackets().size(), is(0));
    }


    @Test
    public void testServerWaitForConnections() {
        ServerConnectionData connData = server.start(new ServerConfig(
                List.of(1, 1),
                false
        ));

        Thread clientThread = new Thread(() -> {
            NetworkWebsocketsClient client1 = new NetworkWebsocketsClient();
            NetworkWebsocketsClient client2 = new NetworkWebsocketsClient();
            connectClient(client1, connData, 0, 0);
            connectClient(client2, connData, 0, 1);
        });

        clientThread.start();

        server.waitForConnections();

        assertThat(server.getConnectedHosts().size(), is(2));
    }
}
