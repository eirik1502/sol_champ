package sol_engine.network.network_input;

import org.junit.Test;
import sol_engine.module.ModulesHandler;
import sol_engine.network.network_input.network_input_utils.TestInputPacket;
import sol_engine.network.test_utils.TestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class NetworkInputSourceModuleTest {

    @Test
    public void testNetworkInputSource() {


        NetworkInputSourceModule inputModule = new NetworkInputSourceModule(new NetworkInputSourceModuleConfig(
                TestInputPacket.class
        ));
        inputModule.internalSetup(new ModulesHandler());

        List<? extends NetInputPacket> packets = Arrays.asList(
                new TestInputPacket(true, false, 54.67f)
        );

        TestUtils.callPrivateMethod(inputModule, "updateWithPackets",
                List.class, packets
        );

        assertThat(inputModule.checkAction("mvLeft"), is(true));
        assertThat(inputModule.checkAction("mvRight"), is(false));
        assertThat(inputModule.floatInput("aimX"), is(54.67f));

    }
}
