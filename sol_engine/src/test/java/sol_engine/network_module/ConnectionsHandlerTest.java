package sol_engine.network_module;

public class ConnectionsHandlerTest {

//    private class TestPacketBox {
//        private TestPacket tp;
//
//        public TestPacketBox(TestPacket tp) {
//            setTp(tp);
//        }
//
//        public TestPacket getTp() {
//            return tp;
//        }
//
//        public void setTp(TestPacket tp) {
//            this.tp = tp;
//        }
//    }
//
//    private static int port = 7779;
//
//    _ConnectionsHandler serverHandler;
//    _ConnectionsHandler clientHandler;
//
//    @Before
//    public void setup() {
//        serverHandler = new _ServerConnectionsHandler("server", port);
//        clientHandler = new _ClientConnectionHandler("client", "localhost", port);
//    }
//
//    @After
//    public void tearDown() {
//        serverHandler.terminate();
//        clientHandler.terminate();
//    }
//
//    @Test
//    public void testSolConnection() {
//        serverHandler.start();
//        clientHandler.start();
//
//        sleep(50);
//
//        assertThat(clientHandler.getConnectedHosts().size(), is(1));
//        assertThat(clientHandler.getConnectedHosts().iterator().next().name, equalTo("server"));
//
//        assertThat(serverHandler.getConnectedHosts().size(), is(1));
//        assertThat(serverHandler.getConnectedHosts().iterator().next().name, equalTo("client"));
//    }
//
//    @Test
//    public void testConnection() {
//
//        TestPacketBox clientReceivePacket = new TestPacketBox(null);
//        TestPacketBox serverReceivePacket = new TestPacketBox(null);
//
//
//        clientHandler.registerPacketListener(TestPacket.class, (host, packet) -> clientReceivePacket.setTp(packet));
//        serverHandler.registerPacketListener(TestPacket.class, (host, packet) -> serverReceivePacket.setTp(packet));
//
//        serverHandler.start();
//        sleep(50);
//        clientHandler.start();
//
//
//        sleep(50);
//
//        clientHandler.sendToAll(new TestPacket("Hello server!"));
////        clientHandler.sendToAll(new TestPacket("Hello server! 2"));
////        clientHandler.sendToAll(new TestPacket("Hello server! 3"));
////
//        serverHandler.sendToAll(new TestPacket("COCO"));
//
//        sleep(100);
//
//        System.out.println("Client got package: " + clientReceivePacket.getTp());
//        System.out.println("Server got package: " + serverReceivePacket.getTp());
//    }
//
//    private void sleep(int millis) {
//        try {
//            Thread.sleep(millis);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

}
