package sol_engine.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sol_engine.module.ModulesHandler;
import sol_engine.network.client.ClientConfig;
import sol_engine.network.server.ServerConfig;
import sol_engine.network.test_utils.TestPacketInt;
import sol_engine.network.test_utils.TestPacketString;
import sol_engine.network.test_utils.TestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.fail;


public class NetworkModuleTest {

    private NetworkModule serverModule;
    private NetworkModule clientModule;
    private ModulesHandler serverModulesHandler;
    private ModulesHandler clientModulesHandler;


    @Before
    public void setUp() {
        int port = 7654;
        this.serverModule = new NetworkModule(new NetworkModuleConfig(
                new ServerConfig(port, List.of(1, 1), true, true),
                Arrays.asList(TestPacketString.class)
        ));

        this.clientModule = new NetworkModule(new NetworkModuleConfig(
                new ClientConfig("localhost", port, "", ""),
                Arrays.asList(TestPacketString.class)
        ));

        this.serverModulesHandler = new ModulesHandler();
        this.clientModulesHandler = new ModulesHandler();
        this.serverModulesHandler.addModule(this.serverModule);
        this.clientModulesHandler.addModule(this.clientModule);
    }

    @After
    public void tearDown() {
        clientModulesHandler.internalEnd();
        TestUtils.sleepShort();
        serverModulesHandler.internalEnd();
        TestUtils.sleepShort();
    }

    public void startServerClient() {
        try {
            serverModulesHandler.internalSetup();
        } catch (Exception e) {
            fail("Exception occurred while starting the server module: " + e);
        }
        try {
            clientModulesHandler.internalSetup();
        } catch (Exception e) {
            fail("Exception occurred while starting the client module: " + e);
        }
    }

    @Test
    public void testServerClientConnection() {
        startServerClient();
        assertThat(serverModule.isConnected(), is(true));
        assertThat(clientModule.isConnected(), is(true));
    }

    @Test
    public void testNetworkModulePackets() {
        startServerClient();

        TestPacketString packetFromClient = new TestPacketString("hei :)");
        clientModule.pushPacket(packetFromClient);
        TestUtils.sleepShort();
        serverModule.internalUpdate();
        clientModule.internalUpdate();

        List<TestPacketString> packets = serverModule.peekPackets(TestPacketString.class);
        assertThat(packets.size(), is(1));
        assertThat(packets.get(0), both(equalTo(packetFromClient)).and(not(sameInstance(packetFromClient))));

        TestPacketInt packetFromServer = new TestPacketInt(10);
        serverModule.pushPacket(packetFromServer);

        TestUtils.sleepShort();
        serverModule.internalUpdate();
        clientModule.internalUpdate();

        List<TestPacketString> clientPackets = clientModule.peekPackets(TestPacketString.class);
        assertThat(packets.size(), is(1));
        assertThat(packets.get(0), both(equalTo(packetFromClient)).and(not(sameInstance(packetFromClient))));

    }
}
