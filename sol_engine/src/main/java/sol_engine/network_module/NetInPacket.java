package sol_engine.network_module;

import java.io.*;

public class NetInPacket {

    private DataInputStream dataIn;
    private ByteArrayInputStream byteIn;


    public NetInPacket(byte[] data) {
        byteIn = new ByteArrayInputStream(data);
        dataIn = new DataInputStream(byteIn);
    }


    public int readInt() {
        try {
            return dataIn.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public float readFloat() {
        try {
            return dataIn.readFloat();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public boolean readBool() {
        try {
            return dataIn.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
