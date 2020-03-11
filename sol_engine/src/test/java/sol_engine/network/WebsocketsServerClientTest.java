package sol_engine.network;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sol_engine.network.client.NetworkWebsocketsClient;
import sol_engine.network.packet_handling.NetworkPacketRaw;
import sol_engine.network.server.NetworkWebsocketsServer;
import sol_engine.network.test_utils.TestPacketString;
import sol_engine.network.test_utils.TestUtils;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class WebsocketsServerClientTest {
    NetworkWebsocketsServer server;
    NetworkWebsocketsClient client;

    @Before
    public void setUp() {
        server = new NetworkWebsocketsServer();
        client = new NetworkWebsocketsClient();
    }

    @After
    public void tearDown() {
        client.terminate();
        TestUtils.sleepShort();
        server.terminate();
    }

    private boolean startServerClient() {
        int port = server.start().port;
        TestUtils.sleepShort();
        boolean connected = client.connect("localhost", port);
        TestUtils.sleepShort();
        return connected;
    }

    @Test
    public void testServerClientConnection() {
        try {
            boolean clientConnected = startServerClient();
            assertThat(clientConnected, is(true));

            assertThat(server.isConnected(), is(true));
            assertThat(client.isConnected(), is(true));

            client.terminate();
            server.terminate();
        } catch (Exception e) {
            Assert.fail("server and client could not make a connection due to an exception: " + e);
        }
    }

    @Test
    public void testServerClientPackets() {
        startServerClient();

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

}
