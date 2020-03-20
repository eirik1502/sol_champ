package sol_engine.network.network_sol_module;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sol_engine.module.ModulesHandler;
import sol_engine.network.network_game.GameHost;
import sol_engine.network.network_game.game_client.ClientConfig;
import sol_engine.network.network_game.game_server.ServerConfig;
import sol_engine.network.network_sol_module.NetworkClientModule;
import sol_engine.network.network_sol_module.NetworkClientModuleConfig;
import sol_engine.network.network_sol_module.NetworkServerModule;
import sol_engine.network.network_sol_module.NetworkServerModuleConfig;
import sol_engine.network.test_utils.TestPacketInt;
import sol_engine.network.test_utils.TestPacketString;
import sol_engine.network.test_utils.TestUtils;

import java.util.Deque;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.fail;


public class NetworkModuleTest {

    private NetworkServerModule serverModule;
    private NetworkClientModule clientModule;
    private ModulesHandler serverModulesHandler;
    private ModulesHandler clientModulesHandler;


    @Before
    public void setUp() {
        int port = 7654;
        this.serverModule = new NetworkServerModule(new NetworkServerModuleConfig(
                new ServerConfig(port, List.of(1, 1), true, true),
                List.of(TestPacketString.class)
        ));

        this.clientModule = new NetworkClientModule(new NetworkClientModuleConfig(
                new ClientConfig("localhost", port, "", ""),
                List.of(TestPacketString.class)
        ));

        this.serverModulesHandler = new ModulesHandler();
        this.clientModulesHandler = new ModulesHandler();
        this.serverModulesHandler.addModule(this.serverModule);
        this.clientModulesHandler.addModule(this.clientModule);
    }

    @After
    public void tearDown() {
        clientModulesHandler.internalEnd();
        serverModulesHandler.internalEnd();
    }

    public void startServerClient() {
        try {
            serverModulesHandler.internalSetup();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("YEEE");
            fail("Exception occurred while starting the server module: " + e);
        }
        try {
            clientModulesHandler.internalSetup();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred while starting the client module: " + e);
        }
    }

    @Test
    public void testServerClientConnection() {
        startServerClient();
        assertThat(serverModule.isRunning(), is(true));
        assertThat(clientModule.isConnected(), is(true));
    }

    @Test
    public void testNetworkModulePackets() {
        startServerClient();

        TestPacketString packetFromClient = new TestPacketString("hei :)");
        clientModule.sendPacket(packetFromClient);
        TestUtils.sleepShort();
        serverModule.internalUpdate();
        clientModule.internalUpdate();

        Map<GameHost, Deque<TestPacketString>> packetsByHost = serverModule.peekPacketsOfType(TestPacketString.class);
        assertThat("There are no packets from any hosts", packetsByHost.size(), is(1));

        TestPacketString serverReceivedPacket = packetsByHost.values().iterator().next().peek();

        assertThat("There are no packets assigned for the given host", serverReceivedPacket, is(notNullValue()));
        assertThat("server received packet did not match that sendt from client",
                serverReceivedPacket, both(equalTo(packetFromClient)).and(not(sameInstance(packetFromClient))));


        TestPacketInt packetFromServer = new TestPacketInt(10);
        serverModule.sendPacketAll(packetFromServer);

        TestUtils.sleepShort();
        serverModule.internalUpdate();
        clientModule.internalUpdate();

        Deque<TestPacketInt> clientPackets = clientModule.peekPacketsOfType(TestPacketInt.class);
        assertThat("client had no package after server sendt", clientPackets.size(), is(1));
        assertThat("client received packet was not equal to that sendt from the server",
                clientPackets.peek(), both(equalTo(packetFromServer)).and(not(sameInstance(packetFromServer))));

    }
}
