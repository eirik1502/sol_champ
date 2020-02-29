package sol_engine.network.network_input.network_input_utils;

import sol_engine.network.network_input.NetInputPacket;

public class TestInputPacket extends NetInputPacket {

    public boolean mvLeft, mvRight;
    public float aimX;

    public TestInputPacket(boolean mvLeft, boolean mvRight, float aimX) {
        this.mvLeft = mvLeft;
        this.mvRight = mvRight;
        this.aimX = aimX;
    }
}
