package sol_engine.network.server;

public interface ConnectionAcceptanceCriteria {

    public boolean accepted(NetworkServer server, Host host, String urlPath);
}
