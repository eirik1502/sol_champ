package sol_engine.network.network_ecs.host_managing;

import sol_engine.ecs.Component;
import sol_engine.network.packet_handling.NetworkPacket;

public class NetClientComp extends Component {

    public Class<? extends NetworkPacket> staticConnectionPacketType = null;
    public Class<? extends StaticConnectionPacketHandler> staticConnectionPacketHandler = null;

    public boolean requestDisconnect = false;

    public NetClientComp() {
    }

    public NetClientComp(
            Class<? extends NetworkPacket> staticConnectionPacketType,
            Class<? extends StaticConnectionPacketHandler> staticConnectionPacketHandler
    ) {
        this.staticConnectionPacketType = staticConnectionPacketType;
        this.staticConnectionPacketHandler = staticConnectionPacketHandler;
    }
}
