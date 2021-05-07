package server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import networking.events.ConnectedEvent;
import networking.events.DisconnectedEvent;
import networking.requests.*;
import networking.persistentrequests.ViewChannelRequest;
import networking.responses.*;
import networking.server.ServerNetworkingManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChatRuumServer {
    protected static final int DEFAULT_PORT = 5050;
    @JsonProperty(value = "users")
    protected final Map<String, User> users = new HashMap<>();
    @JsonProperty(value = "channels")
    protected final Map<String, Channel> channels = new HashMap<>();
    @JsonIgnore
    private final ServerNetworkingManager<User> server;
    @JsonIgnore
    private final String saveFile;

    public ChatRuumServer(int port) {
        saveFile = null;
        server = new ServerNetworkingManager<>(port);
        setupServer();
    }

    public ChatRuumServer(int port, String saveFile) throws IOException {
        this.saveFile = saveFile;
        ReadWrite.readServer(saveFile, this);
        server = new ServerNetworkingManager<>(port);
        setupServer();
    }

    private void setupServer() {
        server.onEvent(ConnectedEvent.class, (s, e) -> System.out.println("Client connected: " + s.getInternalChannel().remoteAddress()));
        server.onEvent(DisconnectedEvent.class, (s, e) -> {
//            try {
//                ReadWrite.writeServer("server\\src\\main\\java\\data\\server.json", this);
//            } catch (Exception exception) {
//                exception.printStackTrace();
//            }
            System.out.println("Client disconnected: " + s.getInternalChannel().remoteAddress());
        });

        server.onRequest(RegisterRequest.class, (session, req) -> {
            System.out.println("Registering...");
            final User user = users.get(req.data.username);
            if (user == null) {
                final User newUser = new User(req.data.username, req.data.password);
                users.put(req.data.username, newUser);
                req.sendResponse(new GenericResponse(Response.OK));
            } else {
                req.sendResponse(new GenericResponse(Response.FORBIDDEN));
            }
        });

        server.onRequest(CheckUsernameRequest.class, (session, req) -> {
            System.out.println("Checking username: " + req.data.username);
            final User user = users.get(req.data.username);
            if (user != null) {
                req.sendResponse(new CheckNameResponse(Result.NAME_IN_USE));
            } else {
                req.sendResponse(new CheckNameResponse(Result.NAME_FREE));
            }
        });

        server.onRequest(CheckChannelNameRequest.class, (session, req) -> {
            System.out.println("Checking channel: " + req.data.channelName);

            final Channel channel = channels.get(req.data.channelName);
            if (channel != null) {
                req.sendResponse(new CheckNameResponse(Result.NAME_IN_USE));
            } else {
                req.sendResponse(new CheckNameResponse(Result.NAME_FREE));
            }
        });

        server.onRequest(PasswordLoginRequest.class, (session, req) -> {
            System.out.println("User logging in: " + req.data.username);

            final User user = users.get(req.data.username);
            if (user != null && user.checkPassword(req.data.password)) {
                session.setUser(user);
                req.sendResponse(new LoginResponse(Response.OK, user.getFavoriteChannels()));
            } else {
                req.sendResponse(new LoginResponse(Response.FORBIDDEN, null));
            }
        });

        server.onRequest(CreateChannelRequest.class, (session, req) -> {
            System.out.println("Trying to create channel: " + req.data.channelName);

            final Channel channel = channels.get(req.data.channelName);
            if (channel == null) {
                final Channel newChannel = new Channel(req.data.channelName, req.data.channelPassword);
                channels.put(newChannel.getName(), newChannel);
                req.sendResponse(new GenericResponse(Response.OK));
            } else {
                req.sendResponse(new GenericResponse(Response.FORBIDDEN));
            }
        });

        server.onRequest(JoinChannelRequest.class, (session, req) -> {
            System.out.println("Joining channel: " + req.data.channelName);

            final Channel channel = channels.get(req.data.channelName);
            if (channel != null && channel.checkPassword(req.data.channelPassword)) {
                final User user = session.getUser();
                channel.join(user);
                req.sendResponse(new GenericResponse(Response.OK));
            } else {
                req.sendResponse(new GenericResponse(Response.FORBIDDEN));
            }
        });

        server.onPersistentRequest(ViewChannelRequest.class, (session, req) -> {
            System.out.println("Viewing channel: " + req.data.channelName);

            final Channel channel = channels.get(req.data.channelName);
            final User user = session.getUser();
            if (channel != null && channel.isJoined(user)) {
                channel.addViewingRequest(req);
                user.addVisitChannel(channel.getName());
                req.sendResponse(new ViewChannelResponse(Response.OK, channel.convertToMessageData()));
            } else {
                req.sendResponse(new ViewChannelResponse(Response.FORBIDDEN, null));
            }
        });

        server.onRequest(SendMessageRequest.class, (session, req) -> {
            final Channel channel = channels.get(req.data.channelName);
            if (channel == null) {
                req.sendResponse(new GenericResponse(Response.ERROR));
                return;
            }

            channel.sendMessage(new Message(req.data.text, session.getUser()));
            req.sendResponse(new GenericResponse(Response.OK));
        });

        if (saveFile != null) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("In shutdown hook");
                try {
                    ReadWrite.writeServer(saveFile, this);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }, "Shutdown-thread"));
        }
    }

    public synchronized void startServer() {
        server.start();
    }

    public synchronized void stopServer() {
        server.stop();
    }

    public static void main(String[] args) throws IOException {
        var server = new ChatRuumServer(DEFAULT_PORT, "server.json");
        server.startServer();
    }
}
