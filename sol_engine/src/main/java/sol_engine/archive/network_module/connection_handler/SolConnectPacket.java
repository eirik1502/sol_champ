package sol_engine.archive.network_module.connection_handler;

import sol_engine.archive.network_module.NetPacket;

public class SolConnectPacket extends NetPacket {

    public String hostName;


    public SolConnectPacket() {

    }

    public SolConnectPacket(String hostName) {
        this.hostName = hostName;
    }
}
