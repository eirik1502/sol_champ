package sol_engine.network.communication_layer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sol_engine.network.packet_handling.NetworkPacket;

import java.io.IOException;
import java.util.*;

public class PacketClassStringConverter {
    private final Logger logger = LoggerFactory.getLogger(PacketClassStringConverter.class);

    private static final String PACKET_TYPE_FIELD_NAME = "_packetType";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Class<? extends NetworkPacket>> registeredPacketTypesByName = new HashMap<>();


    @SafeVarargs
    public final void usePacketTypes(Class<? extends NetworkPacket>... packetTypes) {
        usePacketTypes(Arrays.asList(packetTypes));
    }

    public final void usePacketTypes(List<Class<? extends NetworkPacket>> packetTypes) {
        packetTypes.forEach(packetType -> {
            registeredPacketTypesByName.put(packetType.getSimpleName(), packetType);
        });
    }

    public String packetToString(NetworkPacket packet) {
        String packetTypeName = packet.getClass().getSimpleName();
        if (!registeredPacketTypesByName.containsKey(packetTypeName)) {
            logger.error("Trying to convert a packet of a type that is not beeing used, type: " + packetTypeName);
            return null;
        }

        String jsonStringPacket;
        try {
            ObjectNode jsonTreePacket = objectMapper.valueToTree(packet);
            jsonTreePacket.put(PACKET_TYPE_FIELD_NAME, packetTypeName);
            jsonStringPacket = objectMapper.writeValueAsString(jsonTreePacket);

        } catch (JsonProcessingException e) {
            logger.error("Could not write packet as json string, packet type: " + packetTypeName + " parsing exception: " + e);
            return null;
        }

        logger.info("Packet converted: " + packet);
        logger.debug("Packet converted as string: " + jsonStringPacket);

        return jsonStringPacket;
    }

    public NetworkPacket stringToPacket(String packetString) {

        // parse raw packet as a json tree
        ObjectNode packetJsonRoot;
        try {
            packetJsonRoot = (ObjectNode) objectMapper.readTree(packetString);
        } catch (JsonProcessingException e) {
            logger.warn("raw packet could not be parsed as json, packet data: " + packetString);
            return null;
        } catch (IOException e) {
            logger.error("IO exception when reading raw packet: " + packetString);
            return null;
        }

        // retrieve packet type
        String packetTypeName;
        if (packetJsonRoot.has(PACKET_TYPE_FIELD_NAME)) {
            packetTypeName = packetJsonRoot.get(PACKET_TYPE_FIELD_NAME).asText();
        } else {
            logger.warn("raw packet did not include a " + PACKET_TYPE_FIELD_NAME + ", packet: " + packetString);
            return null;
        }

        // check if the packet type is valid
        if (!registeredPacketTypesByName.containsKey(packetTypeName)) {
            logger.error("Trying to poll a packet of a type that is not beeing used: " + packetString);
            return null;
        }

        // remove the type field before parsing as object
        packetJsonRoot.remove(PACKET_TYPE_FIELD_NAME);

        // parse packet to specified object type
        Class<? extends NetworkPacket> packetType = registeredPacketTypesByName.get(packetTypeName);
        NetworkPacket packet;
        try {
            packet = objectMapper.treeToValue(packetJsonRoot, packetType);
        } catch (JsonProcessingException e) {
            logger.error("Could not map json to the spesified object type: " + packetTypeName + " packet_json: " + packetJsonRoot + " parsing exception: " + e);
            return null;
        }

        logger.debug("String packet parsed as object, type: " + packetTypeName + " packet string: " + packetString);
        logger.info("Packet parsed as object, type: " + packetTypeName + " packet object: " + packet);

        return packet;
    }
}
