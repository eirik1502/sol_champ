package sol_engine.archive.network_module.network_ecs;

import sol_engine.archive.network_module.NetPacket;

public class EntityClassInstanciatedPacket extends NetPacket {

    public String entityClass;

    public EntityClassInstanciatedPacket(String entityClass) {
        this.entityClass = entityClass;
    }

}
