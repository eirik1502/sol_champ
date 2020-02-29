package sol_engine.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sol_engine.module.ModulesHandler;
import sol_engine.network.test_utils.TestPacketInt;
import sol_engine.network.test_utils.TestPacketString;
import sol_engine.network.test_utils.TestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;


public class NetworkModuleTest {

    private NetworkModule serverModule;
    private NetworkModule clientModule;
    private ModulesHandler modulesHandler;


    @Before
    public void setUp() {
        int port = 7654;
        serverModule = new NetworkModule(new NetworkModuleConfig(
                true,
                port,
                "localhost",
                new ArrayList<>(),
                Arrays.asList(TestPacketString.class)
        ));

        clientModule = new NetworkModule(new NetworkModuleConfig(
                false,
                port,
                "localhost",
                new ArrayList<>(),
                Arrays.asList(TestPacketString.class)
        ));

        modulesHandler = new ModulesHandler(); // will not do anything
    }

    @After
    public void tearDown() {
        clientModule.internalEnd();
        TestUtils.sleepShort();
        serverModule.internalEnd();
        TestUtils.sleepShort();
    }

    @Test
    public void testServerClientConnection() {
        serverModule.internalStart(modulesHandler);
        TestUtils.sleepShort();
        clientModule.internalStart(modulesHandler);
        TestUtils.sleepShort();
        assertThat(serverModule.isConnected(), is(true));
        assertThat(clientModule.isConnected(), is(true));
    }

    @Test
    public void testNetworkModulePackets() {
        serverModule.internalStart(modulesHandler);
        TestUtils.sleepShort();
        clientModule.internalStart(modulesHandler);

        TestUtils.sleepShort();
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
