package sol_engine.network.network_ecs;

import org.joml.Vector2f;
import sol_engine.ecs.Component;
import sol_engine.utils.collections.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NetServerComp extends Component {

    public List<List<Pair<String, Vector2f>>> hostEntitiesStartData = new ArrayList<>();  // given by teamIndex and playerIndex

    public NetServerComp() {
    }

    public NetServerComp(List<List<Pair<String, Vector2f>>> hostEntitiesStartData) {
        this.hostEntitiesStartData = hostEntitiesStartData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetServerComp)) return false;
        NetServerComp that = (NetServerComp) o;
        return Objects.equals(hostEntitiesStartData, that.hostEntitiesStartData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostEntitiesStartData);
    }
}
