package server;

import static org.junit.jupiter.api.Assertions.*;

import networking.client.ClientNetworkingManager;
import networking.client.ClientSession;
import networking.events.ConnectedEvent;
import networking.events.ErrorEvent;
import networking.persistentrequests.ViewChannelRequest;
import networking.requests.*;
import networking.responses.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChatRuumServerTest {
    private static final Logger logger = LogManager.getLogger();

    private static final int testPort = 5430;
    private static ChatRuumServer server;
    private static ClientSession session;
    private static AtomicInteger exceptionCount;

    @BeforeAll
    public static void setupServer() throws Exception {
        logger.info("Setting up test");

        server = new ChatRuumServer(testPort);
        final User robert = new User("roobert", "jfkdsfjkdkjfa");
        server.users.put("roobert", robert);
        final Channel yldine = new Channel("yldine", "321");
        server.channels.put("yldine", yldine);
        server.startServer();
        logger.info("Server started");

        var connectFuture = new CompletableFuture<Void>();
        exceptionCount = new AtomicInteger(0);
        session = new ClientNetworkingManager(testPort).connect("localhost");
        session.onEvent(ConnectedEvent.class, (s, e) -> connectFuture.complete(null));
        session.onEvent(ErrorEvent.class, (s, e) -> exceptionCount.incrementAndGet());
        connectFuture.get(2, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void stopServer() {
        session.close();
        server.stopServer();
    }

    @BeforeEach
    public void resetExceptionCount() {
        exceptionCount.set(0);
    }

    @AfterEach
    public void checkExceptionCount() {
        assertEquals(0, exceptionCount.get(), "Exception count in simulated client should be 0");
    }

    /**
     * Helper method to wait for a completable future to complete.
     * Provides custom error message and default timeout.
     *
     * @return result of given future
     */
    public <T> T await(CompletableFuture<T> future) {
        try {
            return future.get(2, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            return fail("Timeout while waiting for result", e);
        } catch (Exception e) {
            return fail("Error while waiting for result", e);
        }
    }

    @Test
    @Order(0)
    public void testCheckUsername() {
        var result = new CompletableFuture<Result>();

        String username = "albert";
        session.sendRequest(new CheckUsernameRequest(username))
                .onResponse((s, r) -> {
                    logger.info(r.result);
                    if (r.result == Result.NAME_FREE) {
                        logger.info("The name " + username + " is free");
                    } else if (r.result == Result.NAME_IN_USE) {
                        logger.info("The name " + username + " is already used");
                    }
                    result.complete(r.result);
                });

        assertEquals(Result.NAME_FREE, await(result));
    }

    @Test
    @Order(1)
    public void testRegister() {
        var response = new CompletableFuture<Response>();

        session.sendRequest(new RegisterRequest("albert1", "parool"))
                .onResponse((s, r) -> {
                    logger.info(r.response);
                    if (r.response == Response.OK) {
                        logger.info("New user registered");
                    } else if (r.response == Response.FORBIDDEN) {
                        logger.info("Failed to register");
                    }
                    response.complete(r.response);
                });

        assertEquals(Response.OK, await(response));
    }

    @Test
    @Order(2)
    public void testLoginWrongPassword() {
        var response = new CompletableFuture<Response>();

        String username = "albert1";
        String userPassword = "valeparool";
        session.sendRequest(new PasswordLoginRequest(username, userPassword))
                .onResponse((s, r) -> {
                    logger.info(r.response.name());
                    if (r.response == Response.OK) {
                        logger.info("Login successful. Hello, " + username);
                    } else if (r.response == Response.FORBIDDEN) {
                        logger.info("Failed to login");
                    }
                    response.complete(r.response);
                });

        assertEquals(Response.FORBIDDEN, await(response));
    }

    @Test
    @Order(3)
    public void testLoginCorrectPassword() {
        var response = new CompletableFuture<Response>();

        String username = "albert1";
        String userPassword = "parool";
        session.sendRequest(new PasswordLoginRequest(username, userPassword))
                .onResponse((s, r) -> {
                    logger.info(r.response.name());
                    if (r.response == Response.OK) {
                        logger.info("Login successful. Hello, " + username);
                    } else if (r.response == Response.FORBIDDEN) {
                        logger.info("Failed to login");
                    }
                    response.complete(r.response);
                });

        assertEquals(Response.OK, await(response));
    }

    @Test
    @Order(4)
    public void testJoinChannel() {
        var response = new CompletableFuture<Response>();

        String channelName = "yldine";
        String channelPassword = "321";
        session.sendRequest(new JoinChannelRequest(channelName, channelPassword))
                .onResponse((s, r) -> {
                    logger.info(r.response.name());
                    if (r.response == Response.OK) {
                        logger.info("Joined channel: " + channelName);
                    } else if (r.response == Response.FORBIDDEN) {
                        logger.info("Failed to join channel");
                    }
                    response.complete(r.response);
                });

        assertEquals(Response.OK, await(response));
    }

    @Test
    @Order(6)
    public void testViewChannel() {
        var response = new CompletableFuture<Response>();

        String channelName = "yldine";
        var view = session.sendPersistentRequest(new ViewChannelRequest(channelName));
        view.onResponse(ViewChannelResponse.class, (s, r) -> {
            logger.info(r.response.name());
            if (r.response == Response.OK) {
                logger.info("Viewing channel: " + channelName);
                logger.info("Channel messages: " + r.messages.toString());
            } else if (r.response == Response.FORBIDDEN) {
                logger.info("Failed to view channel");
            }
            view.close();
            response.complete(r.response);
        });

        assertEquals(Response.OK, await(response));
    }

    @Test
    @Order(5)
    public void testSendMessage() {
        var message = new CompletableFuture<String>();
        var sendResponse = new CompletableFuture<Response>();

        String channelName = "yldine";
        String text = "Tere, maailm";

        var view = session.sendPersistentRequest(new ViewChannelRequest(channelName));
        view.onResponse(ViewChannelResponse.class, (s, r) -> {
            logger.info(r.response.name());
            if (r.response == Response.OK) {
                logger.info("Viewing channel: " + channelName);
            } else if (r.response == Response.FORBIDDEN) {
                logger.info("Failed to view channel");
            }
        });
        view.onResponse(NewMessageResponse.class, (s, r) -> {
            logger.info("Received message: " + r.data.text + " from " + r.data.senderName);
            message.complete(r.data.text);
        });

        session.sendRequest(new SendMessageRequest(channelName, text))
                .onResponse((s, r) -> {
                    logger.info(r.response.name());
                    if (r.response == Response.OK) {
                        logger.info("Sent message: " + text + " to " + channelName);
                    } else if (r.response == Response.FORBIDDEN) {
                        logger.info("Failed to send message");
                    }
                    sendResponse.complete(r.response);
                });

        try {
            assertEquals(Response.OK, await(sendResponse));
            assertEquals(text, await(message));
        } finally {
            view.close();
        }
    }

    @Test
    @Order(7)
    public void testCreateChannel() {
        var response = new CompletableFuture<Response>();

        String channelName = "abi";
        String channelPassword = "iba";
        session.sendRequest(new CreateChannelRequest(channelName, channelPassword))
                .onResponse((s, r) -> {
                    logger.info(r.response.name());
                    if (r.response == Response.OK) {
                        logger.info("Created channel: " + channelName);
                    } else if (r.response == Response.FORBIDDEN) {
                        logger.info("Failed to create channel");
                    }
                    response.complete(r.response);
                });

        assertEquals(Response.OK, await(response));
    }

}