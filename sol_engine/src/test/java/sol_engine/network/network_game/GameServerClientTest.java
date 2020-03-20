package sol_engine.network.network_game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_game.game_client.ClientConfig;
import sol_engine.network.network_game.game_client.ClientConnectionData;
import sol_engine.network.network_game.game_client.NetworkGameClient;
import sol_engine.network.communication_layer.Host;
import sol_engine.network.network_game.game_server.NetworkGameServer;
import sol_engine.network.network_game.game_server.ServerConfig;
import sol_engine.network.network_game.game_server.ServerConnectionData;
import sol_engine.network.network_game.game_server.TeamPlayerHosts;
import sol_engine.network.test_utils.TestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameServerClientTest {

    NetworkGameServer server;
    NetworkGameClient client1;
    NetworkGameClient client2;
    NetworkGameClient clientObserver;
    NetworkGameClient client1Fail;


    @Before
    public void setUp() {
        server = new NetworkGameServer();
        client1 = new NetworkGameClient();
        client2 = new NetworkGameClient();
        clientObserver = new NetworkGameClient();
        client1Fail = new NetworkGameClient();
    }

    @After
    public void tearDown() {
        client1.disconnect();
        client2.disconnect();
        clientObserver.disconnect();
        client1Fail.disconnect();
        server.stop();
        TestUtils.sleepShort();
    }


    private ClientConnectionData connectClient(NetworkGameClient client, ServerConnectionData connectData, boolean isObserver, int teamIndex, int playerIndex) {
        return client.connect(new ClientConfig(
                "localhost",
                connectData.port,
                connectData.gameId,
                isObserver ? connectData.observerKey : connectData.teamsPlayersKeys.get(teamIndex).get(playerIndex),
                isObserver
        ));
    }

    @Test
    public void testConnectionEstablishment() {
        ServerConnectionData serverConnectionData = server.start(new ServerConfig(
                List.of(1, 1),
                true
        ));

        TestUtils.sleepShort();

        ClientConnectionData client1ConnectionData = connectClient(
                client1,
                serverConnectionData,
                false,
                0,
                0
        );
        ClientConnectionData client2ConnectionData = connectClient(
                client2,
                serverConnectionData,
                false,
                1,
                0
        );

        ClientConnectionData clientObserverConnectionData = connectClient(
                clientObserver,
                serverConnectionData,
                true,
                -1, -1
        );

        TestUtils.sleepShort();

        assertThat("client1ConnectionData said not connected", client1ConnectionData.isConnected, is(true));
        assertThat("client1 isConnected() returned false", client1.isConnected(), is(true));

        assertThat("client2ConnectionData said not connected", client2ConnectionData.isConnected, is(true));
        assertThat("client2 isConnected() returned false", client2.isConnected(), is(true));

        assertThat("clientObserver said not connected", clientObserverConnectionData.isConnected, is(true));
        assertThat("clientObserver isConnected() returned false", clientObserver.isConnected(), is(true));
    }

    @Test
    public void testStateAfterConnection() {
        ServerConnectionData serverConnectionData = server.start(new ServerConfig(
                List.of(1, 1),
                true
        ));

        checkServerState(server, List.of(), List.of());

        ClientConnectionData client1ConnectionData = connectClient(
                client1,
                serverConnectionData,
                false,
                0,
                0
        );
        ClientConnectionData client2ConnectionData = connectClient(
                client2,
                serverConnectionData,
                false,
                1,
                0
        );
        ClientConnectionData clientObserverConnectionData = connectClient(
                clientObserver,
                serverConnectionData,
                true,
                -1, -1
        );

        TestUtils.sleepShort();

        assertThat("client1 received teamIndex that is incorrect given the connectionKey",
                client1ConnectionData.teamIndex, is(0));
        assertThat("client1 received playerIndex that is incorrect given the connectionKey",
                client1ConnectionData.playerIndex, is(0));

        assertThat("client2 received teamIndex that is incorrect given the connectionKey",
                client2ConnectionData.teamIndex, is(1));
        assertThat("client2 received playerIndex that is incorrect given the connectionKey",
                client2ConnectionData.playerIndex, is(0));

        checkServerState(
                server,
                List.of(client1ConnectionData, client2ConnectionData),
                List.of(clientObserverConnectionData)
        );

        client1.disconnect();
        TestUtils.sleepShort();

        checkServerState(
                server,
                List.of(client2ConnectionData),
                List.of(clientObserverConnectionData)
        );

        clientObserver.disconnect();
        TestUtils.sleepShort();

        checkServerState(
                server,
                List.of(client2ConnectionData),
                List.of()
        );

        client2.disconnect();
        TestUtils.sleepShort();

        checkServerState(
                server,
                List.of(),
                List.of()
        );
    }

    @Test
    public void testSendPackets() {
        // server and client should send and receive packets, observers should only receive
    }

    private void checkServerState(
            NetworkGameServer server,
            List<ClientConnectionData> playerClientsConnectionData,
            List<ClientConnectionData> observerClientsConnectionData
    ) {
        int clientsCount = playerClientsConnectionData.size() + observerClientsConnectionData.size();
        int playerClientsCount = playerClientsConnectionData.size();
        int observerClientsCount = observerClientsConnectionData.size();

        assertThat("Incorrect amount of total hosts registered on the server",
                server.getAllConnectedHosts().size(), is(clientsCount));

        assertThat("Incorrect amount of player hosts registered on the server",
                server.getAllPlayerHosts().size(), is(playerClientsCount));

        assertThat("Incorrect amount of observer hosts registered on the server",
                server.getObserverHosts().size(), is(observerClientsCount));

        TeamPlayerHosts serverHosts = server.getTeamPlayerHosts();

        int i = 0;
        for (ClientConnectionData playerClientConnectionData : playerClientsConnectionData) {
            GameHost serverClientHost = serverHosts.getHost(
                    playerClientConnectionData.teamIndex,
                    playerClientConnectionData.playerIndex
            );

            assertThat("client " + i + " sessionId received from server does not match the one registered in the server",
                    serverClientHost.sessionId, is(equalTo(serverClientHost.sessionId)));
            ++i;
        }

        i = 0;
        for (ClientConnectionData observerClientConnectionData : observerClientsConnectionData) {

            GameHost serverObserverClientHost = server.getObserverHosts().iterator().next();
            List<Integer> serverObserverHostSessionIds = server.getObserverHosts().stream()
                    .map(serverObserverHost -> serverObserverHost.sessionId)
                    .collect(Collectors.toList());
            assertThat(
                    "clientObserver seessionId received from server does not match the one registered in the server",
                    serverObserverHostSessionIds.contains(observerClientConnectionData.sessionId),
                    is(true)
            );

            ++i;
        }
    }
}
