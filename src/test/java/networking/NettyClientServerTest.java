package networking;

import networking.events.ConnectionEvent;
import networking.events.ConnectionEvent.State;
import networking.messages.TestMessage;
import org.junit.jupiter.api.Test;

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
            var server = new NettyServer(testPort);
            server.on(TestMessage.class, (s, tm) -> System.out.println("Server: " + tm.text));
            server.on(ConnectionEvent.class, (s, e) -> System.out.println("Server: " + e.state.name()));
            server.on(ConnectionEvent.class, (s, e) -> {
                if (e.state == State.CONNECTED) {
                    s.sendMessage(new TestMessage("server siin"));
                }
            });
            server.on(TestMessage.class, (s, tm) -> {
                assertEquals(tm.text, "klient siin");
                serverCheck.set(true);
            });
            serverThread = new Thread(server);
            serverThread.start();

            var client = new NettyClient("localhost", testPort);
            client.on(TestMessage.class, (s, tm) -> System.out.println("Client: " + tm.text));
            client.on(ConnectionEvent.class, (s, e) -> System.out.println("Client: " + e.state.name()));
            client.on(ConnectionEvent.class, (s, e) -> {
                if (e.state == State.CONNECTED) {
                    s.sendMessage(new TestMessage("klient siin"));
                }
            });
            client.on(TestMessage.class, (s, tm) -> {
                assertEquals(tm.text, "server siin");
                clientCheck.set(true);
            });
            clientThread = new Thread(client);
            clientThread.start();

            Thread.sleep(1000);
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