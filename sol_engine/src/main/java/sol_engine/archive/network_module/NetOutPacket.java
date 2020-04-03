package sol_engine.archive.network_module;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NetOutPacket {


    private DataOutputStream dataOut;
    private ByteArrayOutputStream byteOut;


    public NetOutPacket() {
        byteOut = new ByteArrayOutputStream();
        dataOut = new DataOutputStream(byteOut);
    }


    public NetOutPacket writeInt(int d) {
        try {
            dataOut.writeInt(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
    public NetOutPacket writeFloat(float d) {
        try {
            dataOut.writeFloat(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
    public NetOutPacket writeBool(boolean d) {
        try {
            dataOut.writeBoolean(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public byte[] getBytes() {
        return byteOut.toByteArray();
    }
}
