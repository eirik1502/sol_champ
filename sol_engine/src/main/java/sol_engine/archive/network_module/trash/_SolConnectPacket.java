package sol_engine.archive.network_module.connection_handler;

import sol_engine.archive.network_module.NetPacket;

public class _SolConnectPacket extends NetPacket {

    public String hostName;


    public _SolConnectPacket() {

    }

    public _SolConnectPacket(String hostName) {
        this.hostName = hostName;
    }
}
