package sol_engine.network;

import org.junit.Assert;
import org.junit.Test;
import sol_engine.network.client.NetworkWebsocketsClient;
import sol_engine.network.server.NetworkWebsocketsServer;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class WebsocketsServerClientTest {

    @Test
    public void testServerClientConnection() {
        int port = 7774;
        NetworkWebsocketsServer server = new NetworkWebsocketsServer();
        NetworkWebsocketsClient client = new NetworkWebsocketsClient();

        try {
            server.start(port);
            boolean connected = client.connect("localhost", port);

            assertThat(connected, is(true));

            client.terminate();
            server.terminate();
        } catch (Exception e) {
            Assert.fail("server and client could not make a connection due to an exception: " + e);
        }
    }

}
