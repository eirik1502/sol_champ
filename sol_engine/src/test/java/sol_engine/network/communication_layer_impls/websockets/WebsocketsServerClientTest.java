package sol_engine.network.communication_layer_impls.websockets;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.After;
import org.junit.Test;
import sol_engine.network.communication_layer.NetworkServer;
import sol_engine.network.communication_layer.Host;
import sol_engine.network.network_utils.NetworkUtils;
import sol_engine.network.network_test_utils.TestPacketString;
import sol_engine.network.network_test_utils.TestUtils;

import java.net.*;
import java.util.Set;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.fail;

public class WebsocketsServerClientTest {

    @After
    public void tearDown() {
        TestUtils.sleepShort();
    }

    @Test
    public void testServerClientConnection() {
        int port = NetworkUtils.findFreeSocketPort();
        NetworkWebsocketsServer server = new NetworkWebsocketsServer();
        MutableObject<Host> clientHost = new MutableObject<>();
        server.onOpen(host -> {
            clientHost.setValue(host);
            return true;  // accept
        });
        server.start(port);
        TestUtils.sleepShort();

        NetworkWebsocketsClient client = new NetworkWebsocketsClient();
        boolean clientIsConnected = client.connect("localhost", port);
        TestUtils.sleepShort();

        assertThat("Client is not connected by connect() return", clientIsConnected, is(true));
        assertThat("Client is not connected by isConnected()", client.isConnected(), is(true));
        Set<Host> serverConnectedHosts = server.getConnectedHosts();
        assertThat("Server does not have 1 registered connection", serverConnectedHosts.size(), is(1));
        Host serverConnectedHost = serverConnectedHosts.iterator().next();

        try {
            assertThat("Server registered client host address is not as expected: localhost",
                    InetAddress.getByName(serverConnectedHost.address), is(InetAddress.getByName("localhost")));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            fail("Server connected client url was invalid");
        }

        client.disconnect();
        TestUtils.sleepShort();
        server.stop();
    }

    @Test
    public void testSendPackets() {
        NetworkWebsocketsServer server = new NetworkWebsocketsServer();
        NetworkWebsocketsClient client1 = new NetworkWebsocketsClient();
        NetworkWebsocketsClient client2 = new NetworkWebsocketsClient();
        server.usePacketTypes(TestPacketString.class);
        client1.usePacketTypes(TestPacketString.class);
        client2.usePacketTypes(TestPacketString.class);

        TestPacketString packetFromServer = new TestPacketString("hello from server");
        TestPacketString packetFromClient1 = new TestPacketString("hello from client1");
        TestPacketString packetFromClient2 = new TestPacketString("hello from client2");

        MutableObject<TestPacketString>
                packetServerToClient1 = new MutableObject<>(),
                packetServerToClient2 = new MutableObject<>(),
                packetClient1ToServer = new MutableObject<>(),
                packetClient2ToServer = new MutableObject<>();

        server.onPacket((packet, host) -> {
            if (host.port == client1.getLocalPort()) {
                packetClient1ToServer.setValue((TestPacketString) packet);
            } else if (host.port == client2.getLocalPort()) {
                packetClient2ToServer.setValue((TestPacketString) packet);
            } else {
                fail("server got packet from a client where the host port did not match any client port");
            }
        });
        client1.onPacket(packet -> {
            packetServerToClient1.setValue((TestPacketString) packet);
        });
        client2.onPacket(packet -> {
            packetServerToClient2.setValue((TestPacketString) packet);
        });

        int port = NetworkUtils.findFreeSocketPort();
        server.start(port);
        client1.connect("localhost", port);
        client2.connect("localhost", port);

        client1.sendPacket(packetFromClient1);
        server.sendPacketAll(packetFromServer);
        client2.sendPacket(packetFromClient2);

        TestUtils.sleepShort();

        assertThat("packet from client1 to server did not match", packetFromClient1, is(packetClient1ToServer.getValue()));
        assertThat("packet from client2 to server did not match", packetFromClient2, is(packetClient2ToServer.getValue()));
        assertThat("packet from server to client1 did not match", packetFromServer, is(packetServerToClient1.getValue()));
        assertThat("packet from server to client2 did not match", packetFromServer, is(packetServerToClient2.getValue()));

        server.stop();
        TestUtils.sleepShort();
    }

    @Test
    public void testServerLifecycleHandlers() {
        int port = NetworkUtils.findFreeSocketPort();
        NetworkWebsocketsServer server = new NetworkWebsocketsServer();
        MutableBoolean handshake = new MutableBoolean(false);
        MutableBoolean opened = new MutableBoolean(false);
        MutableBoolean close = new MutableBoolean(false);

        server.onHandshake((host, params) -> {
            handshake.setValue(true);
            return new NetworkServer.HandshakeResponse(true, null);  // accept
        });
        server.onOpen(host -> {
            opened.setValue(true);
            return true;  // accept
        });
        server.onClose(host -> {
            close.setValue(true);
            return true;
        });

        server.start(port);
        TestUtils.sleepShort();

        NetworkWebsocketsClient client = new NetworkWebsocketsClient();
        client.connect("localhost", port);
        TestUtils.sleepShort();
        client.disconnect();
        TestUtils.sleepShort();

        assertThat("server handshake handler not called", handshake.getValue(), is(true));
        assertThat("server open handler not called", opened.getValue(), is(true));
        assertThat("server close handler not called", close.getValue(), is(true));

        server.stop();
    }

    @Test
    public void testClientLifecycleHandlers() {
        int port = NetworkUtils.findFreeSocketPort();
        NetworkWebsocketsServer server = new NetworkWebsocketsServer();
        server.start(port);

        NetworkWebsocketsClient client = new NetworkWebsocketsClient();
        MutableBoolean opened = new MutableBoolean(false);
        MutableBoolean close = new MutableBoolean(false);
        client.onOpen((params) -> {
            opened.setValue(true);
        });
        client.onClose(() -> {
            close.setValue(true);
        });
        client.connect("localhost", port);

        TestUtils.sleepShort();
        client.disconnect();
        TestUtils.sleepShort();

        assertThat("client open handler not called", opened.getValue(), is(true));
        assertThat("client close handler not called", close.getValue(), is(true));

        server.stop();
    }

//    @Test
//    public void testConnectionKeys() {
//        ServerConnectionData connectionData = startServerClient(new ServerConfig(
//                List.of(1, 1),
//                true
//        ));
//
//        NetworkWebsocketsClient client2Fail = new NetworkWebsocketsClient();
//        boolean client2FailConnected = connectClient(client2Fail, connectionData, 0, 0);
//        assertThat("A second client could connect as player 1", client2FailConnected, is(false));
//        client2Fail.terminate();
//
//        NetworkWebsocketsClient client2 = new NetworkWebsocketsClient();
//        boolean client2Connected = connectClient(client2, connectionData, 1, 0);
//        assertThat("Client could not connect as player 2", client2Connected, is(true));
//        client2.terminate();
//
//        NetworkWebsocketsClient clientObserver = new NetworkWebsocketsClient();
//        boolean clientObserverConnected = connectObserver(clientObserver, connectionData);
//        assertThat("Client could not connect as observer with the observerKey", clientObserverConnected, is(true));
//        clientObserver.terminate();
//
//        NetworkWebsocketsClient clientObserverFail = new NetworkWebsocketsClient();
//        boolean clientObserverFailConnected = connectObserver(clientObserverFail, connectionData, "123abc");
//        assertThat("Client could connect as an observer with a random key", clientObserverFailConnected, is(false));
//        clientObserverFail.terminate();
//    }
//
//    @Test
//    public void testServerClientPackets() {
//        startServerClient(new ServerConfig(
//                -1,
//                List.of(1, 1),
//                false,
//                true
//        ));
//
//        String p1 = "koko";
//        String p2 = "knall";
//        String p3 = "PRRRA";
//
//        server.pushPacket(p1);
//        server.pushPacket(p2);
//        TestUtils.sleepShort();
//
//        Deque<NetworkPacketRaw> clientPackets = client.pollPackets();
//        assertThat(clientPackets.size(), is(2));
//        assertThat(clientPackets.poll().data, both(equalTo(p1)).and(not(sameInstance(p1))));
//        assertThat(clientPackets.poll().data, both(equalTo(p2)).and(not(sameInstance(p2))));
//
//        assertThat(client.pollPackets().size(), is(0));
//
//        // test packets from client to server
//        client.pushPacket(p2);
//        client.pushPacket(p3);
//        TestUtils.sleepShort();
//
//        Deque<NetworkPacketRaw> serverPackets = server.pollPackets();
//        assertThat(serverPackets.size(), is(2));
//        assertThat(serverPackets.poll().data, both(equalTo(p2)).and(not(sameInstance(p2))));
//        assertThat(serverPackets.poll().data, both(equalTo(p3)).and(not(sameInstance(p3))));
//
//        assertThat(server.pollPackets().size(), is(0));
//    }
//
//
//    @Test
//    public void testServerWaitForConnections() {
//        ServerConnectionData connData = server.start(new ServerConfig(
//                List.of(1, 1),
//                false
//        ));
//
//        Thread clientThread = new Thread(() -> {
//            NetworkWebsocketsClient client1 = new NetworkWebsocketsClient();
//            NetworkWebsocketsClient client2 = new NetworkWebsocketsClient();
//            connectClient(client1, connData, 0, 0);
//            connectClient(client2, connData, 0, 1);
//        });
//
//        clientThread.start();
//
//        server.waitForConnections();
//
//        assertThat(server.getConnectedHosts().size(), is(2));
//    }
}
