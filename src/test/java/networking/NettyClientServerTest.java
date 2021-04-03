package networking;

import GUI.networking.ClientNetworkingManager;
import networking.events.ConnectedEvent;
import networking.requests.DebugRequest;
import networking.responses.DebugResponse;
import org.junit.jupiter.api.Test;
import server.networking.ServerNetworkingManager;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class NettyClientServerTest {
    private static final int testPort = 5432;

    @Test
    void testMessaging() throws InterruptedException {
        // Ma ei tea kuidas mitmelõimelist koodi testida

        AtomicBoolean serverCheck = new AtomicBoolean(false);
        AtomicBoolean clientCheck = new AtomicBoolean(false);
        Thread clientThread = null;
        Thread serverThread = null;
        try {
            var server = new ServerNetworkingManager(testPort);

            server.onEvent(ConnectedEvent.class, (s, e) -> System.out.println("Server connected"));
            server.onRequest(DebugRequest.class, (s, r) -> {
                System.out.println("Server received request: " + r.data.message);
                r.sendResponse(new DebugResponse("Server siin!"));
                serverCheck.set(true);
            });

            serverThread = new Thread(server);
            serverThread.start();


            var client = new ClientNetworkingManager("localhost", testPort);

            client.onEvent(ConnectedEvent.class, (s, e) -> System.out.println("Client connected"));
            client.onEvent(ConnectedEvent.class, (s, e) -> {
                var req = s.sendRequest(new DebugRequest("Klient siin!"));
                req.onResponse(DebugResponse.class, (ss, r) -> {
                    System.out.println("Client received response: " + r.message);
                    clientCheck.set(true);
                });
            });

            clientThread = new Thread(client);
            clientThread.start();

            Thread.sleep(5000);
        } finally {
            closeThread(clientThread);
            closeThread(serverThread);
        }

        assertTrue(clientCheck.get());
        assertTrue(serverCheck.get());
    }

    private void closeThread(Thread thread) throws InterruptedException {
        if (thread != null) {
            thread.interrupt();
            thread.join();
        }
    }
}