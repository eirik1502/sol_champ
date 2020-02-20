package sol_engine.network.network_input;

import org.joml.Vector2f;
import sol_engine.input_module.InputSourceModule;
import sol_engine.network.NetworkModule;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NetworkInputSourceModule extends InputSourceModule {

    private Class<? extends NetInputPacket> packetType;
    private NetInputPacket inputPacket;
    private Map<String, Field> inputPacketFieldsByName;


    @Override
    public boolean checkAction(String label) {
        if (inputPacketFieldsByName.containsKey(label)) {
            Field field = inputPacketFieldsByName.get(label);
            if (field.getType() == boolean.class) {
                try {
                    return field.getBoolean(inputPacket);
                } catch (IllegalAccessException e) {
                }
            }
        }
        return false;
    }

    @Override
    public float floatInput(String label) {
        if (inputPacketFieldsByName.containsKey(label)) {
            Field field = inputPacketFieldsByName.get(label);
            if (field.getType() == float.class) {
                try {
                    return field.getFloat(inputPacket);
                } catch (IllegalAccessException e) {
                }
            }
        }
        return 0;
    }

    @Override
    public Vector2f vectorInput(String label) {
        return new Vector2f();
    }

    @Override
    public void onSetup() {
        usingModules(NetworkModule.class);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onUpdate() {
        Deque<NetInputPacket> packets = getModule(NetworkModule.class).getPackets(packetType);
        while (!packets.isEmpty()) {
            NetInputPacket currPacket = packets.pollLast();
            if (currPacket.getClass() != packetType) {
                // log
                continue;
            }

            inputPacketFieldsByName = Arrays.stream(packetType.getFields())
                    .collect(Collectors.toMap(
                            Field::getName,
                            Function.identity()
                    ));
            inputPacket = currPacket;
            break;
        }
    }
}
