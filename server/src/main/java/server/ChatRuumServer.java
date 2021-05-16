package server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import networking.events.ConnectedEvent;
import networking.events.DisconnectedEvent;
import networking.requests.*;
import networking.persistentrequests.ViewChannelRequest;
import networking.responses.*;
import networking.server.ServerNetworkingManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRuumServer {
    private static final Logger logger = LogManager.getLogger();

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
        server.onEvent(ConnectedEvent.class, (s, e) -> logger.info("Client connected: " + s.getTargetAddress()));
        server.onEvent(DisconnectedEvent.class, (s, e) -> {
//            try {
//                ReadWrite.writeServer("server\\src\\main\\java\\data\\server.json", this);
//            } catch (Exception exception) {
//                logger.error("Error saving server data", exception);
//            }
            logger.info("Client disconnected: " + s.getTargetAddress());
        });

        server.onRequest(RegisterRequest.class, (session, req) -> {
            logger.debug("Registering...");
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
            logger.debug("Checking username: " + req.data.username);
            final User user = users.get(req.data.username);
            if (user != null) {
                req.sendResponse(new CheckNameResponse(Result.NAME_IN_USE));
            } else {
                req.sendResponse(new CheckNameResponse(Result.NAME_FREE));
            }
        });

        server.onRequest(CheckChannelNameRequest.class, (session, req) -> {
            logger.debug("Checking channel: " + req.data.channelName);

            final Channel channel = channels.get(req.data.channelName);
            if (channel != null) {
                req.sendResponse(new CheckNameResponse(Result.NAME_IN_USE));
            } else {
                req.sendResponse(new CheckNameResponse(Result.NAME_FREE));
            }
        });

        server.onRequest(PasswordLoginRequest.class, (session, req) -> {
            logger.debug("User logging in: " + req.data.username);

            final User user = users.get(req.data.username);
            if (user != null && user.checkPassword(req.data.password)) {
                session.setUser(user);
                req.sendResponse(new LoginResponse(Response.OK, user.getFavoriteChannels()));
            } else {
                req.sendResponse(new LoginResponse(Response.FORBIDDEN, null));
            }
        });

        server.onRequest(CreateChannelRequest.class, (session, req) -> {
            logger.debug("Trying to create channel: " + req.data.channelName);

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
            logger.debug("Joining channel: " + req.data.channelName);

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
            logger.debug("Viewing channel: " + req.data.channelName);

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
        server.onRequest(EditMessageRequest.class, (session, req) -> {
            logger.debug("hi");
            final Channel channel = channels.get(req.data.channelName);
            if (channel == null) {
                req.sendResponse(new EditChannelResponse(Response.ERROR, new ArrayList<>()));
                return;
            }
            channel.editMessage(req.data.textAfter, req.data.time);
            logger.debug(channel.convertToMessageData());
            req.sendResponse(new EditChannelResponse(Response.OK, channel.convertToMessageData()));
        });

        if (saveFile != null) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Running shutdown hook...");
                try {
                    ReadWrite.writeServer(saveFile, this);
                } catch (Exception exception) {
                    logger.error("Error writing server data during shutdown hook:", exception);
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
