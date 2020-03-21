package sol_engine.network.network_sol_module;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sol_engine.module.Module;
import sol_engine.module.ModulesHandler;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_game.PacketsQueueByType;
import sol_engine.network.network_game.game_client.ClientConfig;
import sol_engine.network.network_game.game_server.GameServerConfig;
import sol_engine.network.network_game.game_server.ServerConnectionData;
import sol_engine.network.network_test_utils.TestPacketInt;
import sol_engine.network.network_test_utils.TestPacketString;
import sol_engine.network.network_test_utils.TestUtils;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.fail;


public class NetworkModuleTest {
    int port = 55555;

    ModulesHandler serverModulesHandler;
    ModulesHandler client1ModulesHandler;
    ModulesHandler client2ModulesHandler;
    ModulesHandler client3ModulesHandler;
    ModulesHandler client4ModulesHandler;

    @Before
    public void setUp() {
        serverModulesHandler = new ModulesHandler();
        client1ModulesHandler = new ModulesHandler();
        client2ModulesHandler = new ModulesHandler();
        client3ModulesHandler = new ModulesHandler();
        client4ModulesHandler = new ModulesHandler();
    }

    @After
    public void tearDown() {
        endModulesHandlers(
                serverModulesHandler,
                client1ModulesHandler,
                client2ModulesHandler,
                client3ModulesHandler,
                client4ModulesHandler
        );
    }

    private NetworkServerModule create1v1ServerModule(ModulesHandler addTo, boolean waitForAllConnections) {
        NetworkServerModule sm = new NetworkServerModule(new NetworkServerModuleConfig(
                new GameServerConfig(port, List.of(1, 1), true),
                List.of(TestPacketString.class, TestPacketInt.class),
                waitForAllConnections
        ));
        addTo.addModule(sm);
        return sm;
    }


    private NetworkClientModule createClientModule(ServerConnectionData serverConnectionData, ModulesHandler addTo,
                                                   boolean isObserver, int teamIndex, int playerIndex) {
        NetworkClientModule cm = new NetworkClientModule(new NetworkClientModuleConfig(
                new ClientConfig(
                        "localhost",
                        serverConnectionData.port,
                        serverConnectionData.gameId,
                        isObserver
                                ? serverConnectionData.observerKey
                                : serverConnectionData.teamsPlayersKeys.get(teamIndex).get(playerIndex),
                        isObserver
                ),
                List.of(TestPacketString.class, TestPacketInt.class)
        ));
        addTo.addModule(cm);
        return cm;
    }

    private void applyToModuleHandlers(String applyingDescription, Consumer<ModulesHandler> apply, ModulesHandler... modulesHandlers) {
        for (ModulesHandler modulesHandler : modulesHandlers) {
            Iterator<Module> modulesIterator = modulesHandler.getAllModules().values().iterator();
            Module module = modulesIterator.hasNext() ? modulesIterator.next() : null;
            try {
                apply.accept(modulesHandler);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception occurred while " + applyingDescription + " the module: " + module + " exception: " + e);
            }
        }
    }

    private void setupModulesHandlers(ModulesHandler... enpointsModulesHandler) {
        applyToModuleHandlers("setting up", ModulesHandler::internalSetup, enpointsModulesHandler);
    }

    private void startModulesHandlers(ModulesHandler... enpointsModulesHandler) {
        applyToModuleHandlers("starting", ModulesHandler::internalStart, enpointsModulesHandler);
    }

    private void updateModulesHandlers(ModulesHandler... enpointsModulesHandler) {
        applyToModuleHandlers("updating", ModulesHandler::internalUpdate, enpointsModulesHandler);
    }

    private void endModulesHandlers(ModulesHandler... enpointsModulesHandler) {
        applyToModuleHandlers("ending", ModulesHandler::internalEnd, enpointsModulesHandler);
    }

    @Test
    public void testServerClientConnection() {
        NetworkServerModule serverModule = create1v1ServerModule(serverModulesHandler, false);
        setupModulesHandlers(serverModulesHandler);
        ServerConnectionData serverConnectionData = serverModule.getConnectionData();

        NetworkClientModule clientModule = createClientModule(serverConnectionData, client1ModulesHandler,
                false, 0, 0);

        setupModulesHandlers(client1ModulesHandler);

        assertThat(serverModule.isRunning(), is(false));
        assertThat(clientModule.isConnected(), is(false));

        startModulesHandlers(serverModulesHandler, client1ModulesHandler);
        TestUtils.sleepShort();

        assertThat(serverModule.isRunning(), is(true));
        assertThat(clientModule.isConnected(), is(true));
    }

    @Test
    public void testSendPackets() {
        NetworkServerModule serverModule = create1v1ServerModule(serverModulesHandler, false);
        setupModulesHandlers(serverModulesHandler);
        ServerConnectionData serverConnectionData = serverModule.getConnectionData();

        NetworkClientModule clientModule = createClientModule(serverConnectionData, client1ModulesHandler,
                false, 0, 0);

        setupModulesHandlers(client1ModulesHandler);
        startModulesHandlers(serverModulesHandler, client1ModulesHandler);
        TestUtils.sleepShort();

        TestPacketString packetFromClient = new TestPacketString("hei :)");

        clientModule.sendPacket(packetFromClient);
        TestUtils.sleepShort();
        updateModulesHandlers(serverModulesHandler, client1ModulesHandler);

        GameHost serversClientHost = serverModule.getGameServer().getAllConnectedHosts().iterator().next();
        PacketsQueueByType packetsByType = serverModule.peekPacketsForHost(serversClientHost);
        assertThat("There are no packets on the server from any hosts", packetsByType.totalPacketCount(), is(1));

        TestPacketString serverReceivedPacket = packetsByType.peek(TestPacketString.class);

        assertThat("Packet on server is null", serverReceivedPacket, is(notNullValue()));
        assertThat("server received packet did not match that sendt from client",
                serverReceivedPacket, both(equalTo(packetFromClient)).and(not(sameInstance(packetFromClient))));

        // Send from server to client
        TestPacketInt packetFromServer = new TestPacketInt(10);

        serverModule.sendPacketAll(packetFromServer);
        updateModulesHandlers(serverModulesHandler);
        TestUtils.sleepShort();
        updateModulesHandlers(client1ModulesHandler);

        Deque<TestPacketInt> clientPackets = clientModule.peekPacketsOfType(TestPacketInt.class);
        assertThat("client had no package after server sendt", clientPackets.size(), is(1));
        assertThat("client received packet was not equal to that sendt from the server",
                clientPackets.peek(), both(equalTo(packetFromServer)).and(not(sameInstance(packetFromServer))));

    }
}
