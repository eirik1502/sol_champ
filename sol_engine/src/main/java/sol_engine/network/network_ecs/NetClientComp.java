package sol_engine.network.network_ecs;

import sol_engine.ecs.Component;
import sol_engine.network.packet_handling.NetworkPacket;

import java.util.ArrayList;
import java.util.List;

public class NetClientComp extends Component {

    public Class<? extends NetworkPacket> staticConnectionPacketType = null;
    public Class<? extends StaticConnectionPacketHandler> staticConnectionPacketHandler = null;


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
