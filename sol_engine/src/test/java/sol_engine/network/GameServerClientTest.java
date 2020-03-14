package sol_engine.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import sol_engine.network.client.ClientConfig;
import sol_engine.network.client.NetworkGameClient;
import sol_engine.network.server.Host;
import sol_engine.network.server.NetworkGameServer;
import sol_engine.network.server.ServerConfig;
import sol_engine.network.server.ServerConnectionData;

import java.util.List;

public class GameServerClientTest {


    @Test
    public void testServerClientConnect() {
        NetworkGameServer server = new NetworkGameServer();
        NetworkGameClient client = new NetworkGameClient();

        ServerConnectionData connectData = server.start(new ServerConfig(
                List.of(1, 1),
                false
        ));

        String clientConnectionKey = connectData.teamsPlayersKeys.get(0).get(0);
        boolean clientConnected = client.connect(new ClientConfig(
                "localhost",
                connectData.port,
                connectData.gameId,
                clientConnectionKey
        ));

        assertThat("return value of client connect gave false", clientConnected, is(true));
        assertThat("client isConnected() returned false", client.isConnected(), is(true));

        assertThat("server have no registered client after client connected",
                server.getConnectedHosts().size(), is(1));

        Host serverConnectedHost = server.getConnectedHosts().iterator().next();

        assertThat("client did not connect with the right connectionKey",
                serverConnectedHost.connectionKey, is(equalTo(clientConnectionKey)));
    }
}
