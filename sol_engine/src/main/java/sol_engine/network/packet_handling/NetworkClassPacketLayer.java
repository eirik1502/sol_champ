package sol_engine.network.packet_handling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class NetworkClassPacketLayer {
    private static final String PACKET_TYPE_FIELD_NAME = "_packetType";

    private final Logger logger = LoggerFactory.getLogger(NetworkClassPacketLayer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // packet classes in use
    private Map<String, Class<? extends NetworkPacket>> packetTypesByName = new HashMap<>();
    // all packet class in use must be present
    private Map<Class<? extends NetworkPacket>, ArrayDeque<NetworkPacket>> pendingPackets = new HashMap<>();

    private NetworkRawPacketLayer rawPacketLayer;


    public NetworkClassPacketLayer(NetworkRawPacketLayer rawPacketLayer) {
        this.rawPacketLayer = rawPacketLayer;
    }

    @SafeVarargs
    public final void usePacketTypes(Class<? extends NetworkPacket>... packetTypes) {
        Arrays.stream(packetTypes).forEach(packetType -> {
            packetTypesByName.put(packetType.getSimpleName(), packetType);
            pendingPackets.put(packetType, new ArrayDeque<>());
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends NetworkPacket> ArrayDeque<T> pollPackets(Class<T> packetType) {
        pollAndParseRawPackets();

        ArrayDeque<NetworkPacket> pendingPacketsOfType = pendingPackets.get(packetType);
        ArrayDeque<T> packets = new ArrayDeque<>((ArrayDeque<T>) pendingPacketsOfType);
        pendingPacketsOfType.clear();
        return packets;
    }

    public void pushPacket(NetworkPacket packet) {
        String packetTypeName = packet.getClass().getSimpleName();
        if (!packetTypesByName.containsKey(packetTypeName)) {
            logger.error("Trying to push a packet of a type that is not beeing used, type: " + packetTypeName);
            return;
        }

        String jsonStringPacket;
        try {
            ObjectNode jsonTreePacket = objectMapper.valueToTree(packet);
            jsonTreePacket.put(PACKET_TYPE_FIELD_NAME, packetTypeName);
            jsonStringPacket = objectMapper.writeValueAsString(jsonTreePacket);

        } catch (JsonProcessingException e) {
            logger.error("Could not write packet as json string, packet type: " + packetTypeName + " parsing exception: " + e);
            return;
        }

        rawPacketLayer.pushPacket(jsonStringPacket);

        logger.info("Packet pushed: " + packet);
        logger.debug("Packet pushed, raw: " + packet);
    }

    private void pollAndParseRawPackets() {
        Deque<NetworkPacketRaw> pendingRawPackets = rawPacketLayer.pollPackets();

        for (NetworkPacketRaw rawPacket : pendingRawPackets) {

            // parse raw packet as a json tree
            ObjectNode packetJsonRoot;
            try {
                packetJsonRoot = (ObjectNode) objectMapper.readTree(rawPacket.data);
            } catch (JsonProcessingException e) {
                logger.warn("raw packet could not be parsed as json, packet: " + rawPacket);
                continue;
            } catch (IOException e) {
                logger.error("IO exception when reading raw net packet: " + rawPacket);
                continue;
            }

            // retrieve packet type
            String packetTypeName;
            if (packetJsonRoot.has(PACKET_TYPE_FIELD_NAME)) {
                packetTypeName = packetJsonRoot.get(PACKET_TYPE_FIELD_NAME).asText();
            } else {
                logger.warn("raw packet did not include a " + PACKET_TYPE_FIELD_NAME + " field: " + rawPacket);
                continue;
            }

            // remove the type field before parsing as object
            packetJsonRoot.remove(PACKET_TYPE_FIELD_NAME);

            // parse packet to specified object type
            Class<? extends NetworkPacket> packetType = packetTypesByName.get(packetTypeName);
            NetworkPacket packet;
            try {
                packet = objectMapper.treeToValue(packetJsonRoot, packetType);
            } catch (JsonProcessingException e) {
                logger.error("Could not map json to the spesified object type: " + packetTypeName + " raw packet: " + rawPacket + " parsing exception: " + e);
                continue;
            }

            // check if the packet type is valid
            if (!packetTypesByName.containsKey(packetTypeName)) {
                logger.error("Trying to poll a packet of a type that is not beeing used: " + rawPacket);
                continue;
            }

            pendingPackets.get(packetType).add(packet);
            logger.debug("Raw packet parsed as object and added, type: " + packetTypeName + " packet raw: " + rawPacket);
            logger.info("Raw packet parsed as object and added, type: " + packetTypeName + " packet object: " + packet);
        }
    }
}
