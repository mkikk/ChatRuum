package networking;

import GUI.networking.ClientNetworkingManager;
import GUI.networking.ClientSession;
import networking.events.ConnectedEvent;
import networking.requests.DebugRequest;
import networking.responses.DebugResponse;
import org.junit.jupiter.api.Test;
import server.networking.ServerNetworkingManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class NettyClientServerTest {
    private static final int testPort = 5432;

    @Test
    void testMessaging() throws InterruptedException, TimeoutException, ExecutionException {
        // Ma ei tea kuidas mitmelõimelist koodi testida

        var pingCount = new AtomicInteger(0);
        var pongCount = new AtomicInteger(0);
        var serverCheck = new CompletableFuture<Boolean>();
        var clientCheck = new CompletableFuture<Boolean>();

        var server = new ServerNetworkingManager(testPort);

        server.onEvent(ConnectedEvent.class, (s, e) -> System.out.println("Server connected"));
        server.onRequest(DebugRequest.class, (s, r) -> {
            System.out.println("Server received request: " + r.data.message);
            r.sendResponse(new DebugResponse("Server siin!"));
            serverCheck.complete(true);
            pongCount.incrementAndGet();
        });

        server.start();


        var clientSession = new ClientNetworkingManager().connect("localhost", testPort);

        clientSession.onEvent(ConnectedEvent.class, (s, e) -> System.out.println("Client connected"));
        clientSession.onEvent(ConnectedEvent.class, (s, e) -> {
            var req = s.sendRequest(new DebugRequest("Klient siin!"));
            req.onResponse(DebugResponse.class, (ss, r) -> {
                System.out.println("Client received response: " + r.message);
                clientCheck.complete(true);
                pingCount.incrementAndGet();
            });
        });

        assertTrue(clientCheck.get(2, TimeUnit.SECONDS));
        assertTrue(serverCheck.get(2, TimeUnit.SECONDS));
        assertEquals(1, pingCount.get());
        assertEquals(1, pongCount.get());

        clientSession.close();
        server.stop();
    }

    private void closeThread(Thread thread) throws InterruptedException {
        if (thread != null) {
            thread.interrupt();
            thread.join();
        }
    }
}