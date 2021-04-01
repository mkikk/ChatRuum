package networking;

import client.networking.ClientEventHandlerGroup;
import client.networking.ClientNetworkingManager;
import networking.events.ConnectionEvent;
import networking.events.ConnectionState;
import networking.messages.TestMessage;
import org.junit.jupiter.api.Test;
import server.networking.ServerEventHandlerGroup;
import server.networking.ServerNetworkingManager;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class NettyClientServerTest {
    private static final int testPort = 5432;

    ServerEventHandlerGroup createServerDebugHandlers() {
        var handlers = new ServerEventHandlerGroup();
        handlers.on(TestMessage.class, (s, tm) -> System.out.println("Server: " + tm.text));
        handlers.on(ConnectionEvent.class, (s, e) -> System.out.println("Server: " + e.state.name()));
        return handlers;
    }

    ClientEventHandlerGroup createClientDebugHandlers() {
        var handlers = new ClientEventHandlerGroup();
        handlers.on(TestMessage.class, (s, tm) -> System.out.println("Client: " + tm.text));
        handlers.on(ConnectionEvent.class, (s, e) -> System.out.println("Client: " + e.state.name()));
        return handlers;
    }

    @Test
    void testMessaging() throws InterruptedException {
        // Ma ei tea kuidas mitmelõimelist koodi testida

        AtomicBoolean serverCheck = new AtomicBoolean(false);
        AtomicBoolean clientCheck = new AtomicBoolean(false);
        Thread clientThread = null;
        Thread serverThread = null;
        try {
            var server = new ServerNetworkingManager(testPort);
            var handlers = new ServerEventHandlerGroup();
            handlers.on(ConnectionEvent.class, (s, e) -> {
                if (e.state == ConnectionState.CONNECTED) {
                    s.sendMessage(new TestMessage("server siin 1"));
                    s.sendMessage(new TestMessage("server siin 2"));
                    s.sendMessage(new TestMessage("server siin 3"));
                    s.sendMessage(new TestMessage("server siin 4"));
                }
            });
            handlers.on(TestMessage.class, (s, tm) -> {
                assertEquals(tm.text, "klient siin");
                serverCheck.set(true);
            });
            server.addHandlerGroup(createServerDebugHandlers());
            server.addHandlerGroup(handlers);
            serverThread = new Thread(server);
            serverThread.start();

            var client = new ClientNetworkingManager("localhost", testPort);
            var clientHandlers = new ClientEventHandlerGroup();
            clientHandlers.on(ConnectionEvent.class, (s, e) -> {
                if (e.state == ConnectionState.CONNECTED) {
                    s.sendMessage(new TestMessage("klient siin"));
                }
            });
            clientHandlers.on(TestMessage.class, (s, tm) -> {
                assertTrue(tm.text.startsWith("server siin"));
                clientCheck.set(true);
            });
            client.addHandlerGroup(clientHandlers);
            client.addHandlerGroup(createClientDebugHandlers());
            System.out.println(clientCheck.get());
            clientThread = new Thread(client);
            clientThread.start();

            Thread.sleep(5000);
        } finally {
            closeThread(clientThread);
            closeThread(serverThread);
        }

        System.out.println(clientCheck.get());
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