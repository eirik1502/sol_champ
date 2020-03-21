package sol_engine.network.network_input;

import org.junit.Test;
import sol_engine.module.ModulesHandler;
import sol_engine.network.network_input.network_input_utils.TestInputPacket;
import sol_engine.network.network_test_utils.TestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class NetworkInputSourceModuleTest {


    private void applyPackets(Map<String, TestInputPacket> packetsByGroup, NetworkInputSourceModule inputModule) {
        packetsByGroup.forEach((group, packet) -> {
            int teamIndex = Integer.parseInt("" + group.charAt(1));  // retrieve teamIndex from group
            int playerIndex = Integer.parseInt("" + group.charAt(3));  // retrieve playerIndex from group
            TestUtils.callPrivateMethod(inputModule, "parseAndPutInputsFromPacket",
                    NetInputPacket.class, packet,
                    int.class, teamIndex,
                    int.class, playerIndex
            );
        });
    }

    private void checkInputCorrect(String testingLabel, Map<String, TestInputPacket> packetsByGroup, NetworkInputSourceModule inputModule) {
        // the inputs should be set for the group given by teamIndex and playerIndex: group t{teamIndex}p{playerIndex}
        packetsByGroup.forEach((inputGroup, packet) -> {
            assertThat(testingLabel + ": " + inputGroup + " mvLeft incorrect value",
                    inputModule.checkAction(inputGroup + ":mvLeft"), is(packet.mvLeft));
            assertThat(testingLabel + ": " + inputGroup + " mvRight incorrect value",
                    inputModule.checkAction(inputGroup + ":mvRight"), is(packet.mvRight));
            assertThat(testingLabel + ": " + inputGroup + " aimX incorrect value",
                    inputModule.floatInput(inputGroup + ":aimX"), is(packet.aimX));
        });
    }

    private void checkInputNotCorrect(String testingLabel, Map<String, TestInputPacket> packetsByGroup, NetworkInputSourceModule inputModule) {
        // the inputs should be set for the group given by teamIndex and playerIndex: group t{teamIndex}p{playerIndex}
        packetsByGroup.forEach((inputGroup, packet) -> {
            assertThat(testingLabel + ": " + inputGroup + " mvLeft incorrect value",
                    inputModule.checkAction(inputGroup + ":mvLeft"), is(not(packet.mvLeft)));
            assertThat(testingLabel + ": " + inputGroup + " mvRight incorrect value",
                    inputModule.checkAction(inputGroup + ":mvRight"), is(not(packet.mvRight)));
            assertThat(testingLabel + ": " + inputGroup + " aimX incorrect value",
                    inputModule.floatInput(inputGroup + ":aimX"), is(not(packet.aimX)));
        });
    }

    @Test
    public void testNetworkInputSource() {


        NetworkInputSourceModule inputModule = new NetworkInputSourceModule(new NetworkInputSourceModuleConfig(
                TestInputPacket.class
        ));

        Map<String, TestInputPacket> inputPacketsByGroup = Map.of(
                "t0p0", new TestInputPacket(true, false, 54.67f),
                "t1p0", new TestInputPacket(false, true, -1f)
        );

        applyPackets(inputPacketsByGroup, inputModule);
        checkInputCorrect("Initial packets", inputPacketsByGroup, inputModule);

        // test that invalid packets does not match
        Map<String, TestInputPacket> otherInputPacketsByGroup = Map.of(
                "t0p0", new TestInputPacket(false, true, 555.67f),
                "t1p0", new TestInputPacket(true, false, 10f)
        );

        checkInputNotCorrect("Compare initial packets with other packets",
                otherInputPacketsByGroup, inputModule);

        // test that new packets override old ones
        applyPackets(otherInputPacketsByGroup, inputModule);
        checkInputNotCorrect("Applied new packets, comparing to old ones",
                inputPacketsByGroup, inputModule);

        checkInputCorrect("Applied new packets, comparing to new packets",
                otherInputPacketsByGroup, inputModule);
    }
}
