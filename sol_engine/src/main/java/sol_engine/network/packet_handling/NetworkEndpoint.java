package sol_engine.network.packet_handling;

public interface NetworkEndpoint {

    boolean isConnected();

    void terminate();
}
