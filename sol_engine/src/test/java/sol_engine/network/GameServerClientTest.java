package sol_engine.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_game.game_client.ClientConfig;
import sol_engine.network.network_game.game_client.NetworkGameClient;
import sol_engine.network.communication_layer.Host;
import sol_engine.network.network_game.game_server.NetworkGameServer;
import sol_engine.network.network_game.game_server.ServerConfig;
import sol_engine.network.network_game.game_server.ServerConnectionData;
import sol_engine.network.test_utils.TestUtils;

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
        TestUtils.sleepShort();

        assertThat("return value of client connect gave false", clientConnected, is(true));
        assertThat("client isConnected() returned false", client.isConnected(), is(true));

        assertThat("server have no registered client after client connected",
                server.getAllConnectedHosts().size(), is(1));

        GameHost serverConnectedHost = server.getAllConnectedHosts().iterator().next();

        assertThat("client did not connect with the right connectionKey",
                serverConnectedHost.connectionKey, is(equalTo(clientConnectionKey)));
    }
}
