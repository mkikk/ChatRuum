package client.networking;

import networking.events.ConnectionEvent;
import networking.events.ConnectionState;
import networking.requests.DebugRequest;
import networking.responses.DebugResponse;
import server.User;

public class ChatRuumClient {
    private final User user = null;
    private final ClientNetworkingManager client;
    private Thread clientThread;

    public ChatRuumClient(int port) {
        client = new ClientNetworkingManager("localhost", port);
        setupClient();
    }

    private void setupClient() {
        client.onEvent(ConnectionEvent.class, (s, e) -> System.out.println("Client: " + e.state.name()));

    }

    public synchronized void startServer() {
        if (clientThread != null) return; // Server already running

        clientThread = new Thread(client);
        clientThread.start();
    }

    public synchronized void stopServer() throws InterruptedException {
        if (clientThread == null) return; // Server not running

        clientThread.interrupt();
        clientThread.join();
        clientThread = null;
    }
}
