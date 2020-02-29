package sol_engine.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sol_engine.module.ModulesHandler;
import sol_engine.network.packet_handling.NetworkPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;


public class NetworkModuleTest {

    private NetworkModule serverModule;
    private NetworkModule clientModule;
    private ModulesHandler modulesHandler;

    private final int shortTime = 50;

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
        sleepShort();
        serverModule.internalEnd();
        sleepShort();
    }

    @Test
    public void testServerClientConnection() {
        serverModule.internalStart(modulesHandler);
        sleepShort();
        clientModule.internalStart(modulesHandler);
        sleepShort();
        assertThat(serverModule.isConnected(), is(true));
        assertThat(clientModule.isConnected(), is(true));
    }

    @Test
    public void testNetworkModulePackets() {
        serverModule.internalStart(modulesHandler);
        sleep(100);
        clientModule.internalStart(modulesHandler);

        sleep(100);
        TestPacketString packetFromClient = new TestPacketString("hei :)");
        clientModule.pushPacket(packetFromClient);
        sleepShort();
        serverModule.internalUpdate();
        clientModule.internalUpdate();

        List<TestPacketString> packets = serverModule.peekPackets(TestPacketString.class);
        assertThat(packets.size(), is(1));
        assertThat(packets.get(0), both(equalTo(packetFromClient)).and(not(sameInstance(packetFromClient))));

        TestPacketInt packetFromServer = new TestPacketInt(10);
        serverModule.pushPacket(packetFromServer);

        sleepShort();
        serverModule.internalUpdate();
        clientModule.internalUpdate();

        List<TestPacketString> clientPackets = clientModule.peekPackets(TestPacketString.class);
        assertThat(packets.size(), is(1));
        assertThat(packets.get(0), both(equalTo(packetFromClient)).and(not(sameInstance(packetFromClient))));

    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sleepShort() {
        sleep(shortTime);
    }
}
