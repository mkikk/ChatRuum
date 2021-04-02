package server;

import static org.junit.jupiter.api.Assertions.*;

import client.networking.ClientNetworkingManager;
import client.networking.ClientSession;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import networking.events.ConnectionEvent;
import networking.events.ConnectionState;
import networking.persistentrequests.ViewChannelRequest;
import networking.requests.*;
import networking.responses.CheckUsernameResponse;
import networking.responses.GenericResponse;
import networking.responses.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.concurrent.FutureTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

class ChatRuumServerTest {
    private static final int testPort = 5430;
    private static ChatRuumServer server;
    private static ClientNetworkingManager client;
    private static ClientSession session;

    @BeforeAll
    public static void setupServer() throws InterruptedException, ExecutionException {
        System.out.println("Setting up test");

        server = new ChatRuumServer(testPort);
        server.users.put("albert1", new User("albert1", "parool"));
        server.channels.put("yldine", new Channel("yldine", "321"));
        server.startServer();
        System.out.println("Server started");

        client = new ClientNetworkingManager("localhost", testPort);
        var clientStartTask = new CompletableFuture<ClientSession>();
        client.onEvent(ConnectionEvent.class, (s, e) -> {
            clientStartTask.complete(session);
        });
        session = clientStartTask.get();
        System.out.println("Client started");
    }

    @AfterAll
    public static void stopServer() throws InterruptedException {
        server.stopServer();
        client.stop();
    }

    @Test
    public void testLogin() {
        String username = "albert1";
        String userPassword = "valeparool";
        session.sendRequest(new PasswordLoginRequest(username, userPassword))
                .onResponse(GenericResponse.class, (ss, r) -> {
                    System.out.println(r.response.name());
                    if (r.response == Response.OK) {
                        System.out.println("Login successful. Hello, " + username);
                    } else if (r.response == Response.FORBIDDEN){
                        System.out.println("Failed to login");
                    }
                });
    }
    @Test
    public void testRegister() {
        String username = "albert";
        String userPassword = "321";
        session.sendRequest(new RegisterRequest(username, userPassword))
                .onResponse(GenericResponse.class, (ss, r) -> {
                    System.out.println(r.response.name());
                    if (r.response == Response.OK) {
                        System.out.println("New user registered: " + username);
                    } else if (r.response == Response.FORBIDDEN){
                        System.out.println("Failed to register");
                    }
                });
    }
    @Test
    public void testCheckUsername() {
        String username = "albert";
        session.sendRequest(new CheckUsernameRequest(username))
                .onResponse(CheckUsernameResponse.class, (ss, r) -> {
                    System.out.println(r.result);
                    if (r.result == CheckUsernameResponse.Result.NAME_FREE) {
                        System.out.println("The name " + username + " is free");
                    } else if (r.result == CheckUsernameResponse.Result.NAME_IN_USE){
                        System.out.println("The name " + username + " is already used");
                    }
                });
    }
    @Test
    public void testJoinChannel() {
        String channelName = "yldine";
        String channelPassword = "321";
        session.sendRequest(new JoinChannelRequest(channelName, channelPassword))
                .onResponse(GenericResponse.class, (ss, r) -> {
                    System.out.println(r.response.name());
                    if (r.response == Response.OK) {
                        System.out.println("Joined channel: " + channelName);
                    } else if (r.response == Response.FORBIDDEN){
                        System.out.println("Failed to join channel");
                    }
                });
    }
    @Test
    public void testViewChannel() {
        String channelName = "yldine";
        session.sendPersistentRequest(new ViewChannelRequest(channelName))
                .onResponse(GenericResponse.class, (ss, r) -> {
                    System.out.println(r.response.name());
                    if (r.response == Response.OK) {
                        System.out.println("Viewing channel: " + channelName);
                    } else if (r.response == Response.FORBIDDEN){
                        System.out.println("Failed to view channel");
                    }
                });
    }
    @Test
    public void testSendMessage() {
        String channelName = "yldine";
        String text = "Tere, maailm";
        session.sendRequest(new SendMessageRequest(channelName, text))
                .onResponse(GenericResponse.class, (ss, r) -> {
                    System.out.println(r.response.name());
                    if (r.response == Response.OK) {
                        System.out.println("Sent message: " + text + " to " + channelName);
                    } else if (r.response == Response.FORBIDDEN){
                        System.out.println("Failed to send message");
                    }
                });
    }
    @Test
    public void testCreateChannel() {
        String channelName = "abi";
        String channelPassword = "iba";
        session.sendRequest(new SendMessageRequest(channelName, channelPassword))
                .onResponse(GenericResponse.class, (ss, r) -> {
                    System.out.println(r.response.name());
                    if (r.response == Response.OK) {
                        System.out.println("Created channel: " + channelName);
                    } else if (r.response == Response.FORBIDDEN){
                        System.out.println("Failed to create channel");
                    }
                });
    }

}